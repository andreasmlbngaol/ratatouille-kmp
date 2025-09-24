package com.kotlinonly.moprog.recipes

import com.kotlinonly.moprog.auth.config.userId
import com.kotlinonly.moprog.core.utils.respondJson
import com.kotlinonly.moprog.data.ratings.CreateRatingRequest
import com.kotlinonly.moprog.data.recipes.RecipeCategory
import com.kotlinonly.moprog.data.recipes.RecipeDetailSummary
import com.kotlinonly.moprog.data.recipes.RecipeFilter
import com.kotlinonly.moprog.data.recipes.SortType
import com.kotlinonly.moprog.database.ratings.RatingsRepository
import com.kotlinonly.moprog.database.recipes.RecipesRepository
import com.kotlinonly.moprog.utils.enumValueOfOrNull
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@Suppress( "DuplicatedCode")
fun Route.recipeRoute() {
    route("/recipes") {
        // 3 Step creating a recipe
        route("/draft") {
            recipeDraftBaseRoute()
            recipeDraftIngredientRoute()
            recipeDraftStepRoute()
        }

        // Get simple response recipes with filters
        get {
            val searchQuery = call.request.queryParameters["search"]

            val categoryAsText = call.request.queryParameters["category"] ?: RecipeCategory.ALL.name
            val category = enumValueOfOrNull<RecipeCategory>(categoryAsText)
                ?: return@get call.respondJson(HttpStatusCode.BadRequest, "Invalid category")

            val sortAsText = call.request.queryParameters["sort"]
            val sort = enumValueOfOrNull<SortType>(sortAsText)
                ?: SortType.POPULAR

            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10

            val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0

            val filter = RecipeFilter(
                name = searchQuery,
                category = category,
                sort = sort,
                limit = limit,
                offset = offset
            )

            val recipes = RecipesRepository.findAll(filter)
            call.respond(recipes)
        }

        route("/{id}") {
            // Find recipe by id
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respondJson(HttpStatusCode.BadRequest, "Invalid id")

                val userId = call.principal<JWTPrincipal>()!!.userId

                val recipe: RecipeDetailSummary = RecipesRepository.findById(id, userId)
                    ?: return@get call.respondJson(HttpStatusCode.NotFound, "Recipe not found")

                if(!recipe.isPublic) {
                    val userId = call.principal<JWTPrincipal>()!!.userId
                    RecipesRepository.isAuthor(id, userId).let {
                        if(!it) return@get call.respondJson(HttpStatusCode.Forbidden, "You are not the author of this recipe")
                    }
                }
                call.respond(recipe)
            }

            recipeImageRoute()
            recipeCommentRoute()
            recipeReactionRoute()
            recipeBookmarkRoute()

            // Add reader rating
            post("/ratings") {
                val recipeId = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid id")

                val userId = call.principal<JWTPrincipal>()!!.userId

                RecipesRepository.isAuthor(recipeId, userId).let {
                    if(it) return@post call.respondJson(HttpStatusCode.Forbidden, "You are the author of this recipe")
                }

                val payload = call.receive<CreateRatingRequest>()
                if(payload.rating !in 1.0..5.0) return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid rating")

                RatingsRepository.save(recipeId, userId, payload.rating)
                call.respond(HttpStatusCode.Created)
            }
        }
    }
}