package com.kotlinonly.moprog.recipes

import com.kotlinonly.moprog.auth.config.userId
import com.kotlinonly.moprog.core.utils.respondJson
import com.kotlinonly.moprog.core.utils.uppercaseEachWord
import com.kotlinonly.moprog.data.ingredient.CreateIngredientRequest
import com.kotlinonly.moprog.data.ingredient.IngredientTag
import com.kotlinonly.moprog.data.ingredient.IngredientTagRequest
import com.kotlinonly.moprog.database.ingredient_tags.IngredientTagsRepository
import com.kotlinonly.moprog.database.ingredients.IngredientsRepository
import com.kotlinonly.moprog.database.recipes.RecipesRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.recipeDraftIngredientRoute() {
    route("/ingredients") {
        route("/tags") {
            get {
                val payload = call.receive<IngredientTagRequest>()
                call.respond(
                    IngredientTagsRepository.findByNameILikeWithLimit(
                        payload.name,
                        payload.limit ?: 4
                    )
                )
            }

            post {
                val payload = call.receive<IngredientTagRequest>()
                val name = payload.name.trim().uppercase()
                if (name.isBlank()) return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid name")
                IngredientTagsRepository.findByName(name)?.let {
                    return@post call.respondJson(HttpStatusCode.Conflict, "Ingredient tag already exists")
                }

                val id = IngredientTagsRepository.save(name).value
                call.respond(
                    IngredientTag(
                        id = id,
                        name = name.uppercaseEachWord()
                    )
                )
            }
        }

        route("/{id}") {
            get {
                val userId = call.principal<JWTPrincipal>()!!.userId
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respondJson(HttpStatusCode.BadRequest, "Invalid id")
                RecipesRepository.existByIdAndAuthorId(id, userId).let {
                    if(!it) return@get call.respondJson(HttpStatusCode.NotFound, "Recipe with this id and author id not found")
                }
                call.respond(IngredientsRepository.findAllByRecipeId(id))
            }
            post {
                val userId = call.principal<JWTPrincipal>()!!.userId
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid id")
                RecipesRepository.existByIdAndAuthorId(id, userId).let {
                    if(!it) return@post call.respondJson(HttpStatusCode.NotFound, "Recipe with this id and author id not found")
                }

                val payload = call.receive<CreateIngredientRequest>()
                if(payload.ingredients.isEmpty()) return@post call.respondJson(HttpStatusCode.BadRequest, "No ingredients provided")

                IngredientsRepository.saveAll(id, payload.ingredients)
                call.respond(HttpStatusCode.Created)
            }
        }
    }

}