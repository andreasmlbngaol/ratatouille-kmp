package com.kotlinonly.moprog.recipes

import com.kotlinonly.moprog.core.config.userId
import com.kotlinonly.moprog.core.database.comments.CommentsRepository
import com.kotlinonly.moprog.core.database.images.ImagesRepository
import com.kotlinonly.moprog.core.database.ingredients.IngredientsRepository
import com.kotlinonly.moprog.core.database.ratings.RatingsRepository
import com.kotlinonly.moprog.core.database.reactions.ReactionsRepository
import com.kotlinonly.moprog.core.database.recipes.RecipesRepository
import com.kotlinonly.moprog.core.database.recipes_images.RecipesImagesRepository
import com.kotlinonly.moprog.core.database.steps.StepsRepository
import com.kotlinonly.moprog.core.utils.respondJson
import com.kotlinonly.moprog.data.comments.CreateCommentRequest
import com.kotlinonly.moprog.data.ratings.CreateRatingRequest
import com.kotlinonly.moprog.data.reactions.CreateReactionRequest
import com.kotlinonly.moprog.data.recipes.CreateRecipeRequest
import com.kotlinonly.moprog.data.recipes.RecipeDetailSummary
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.io.File

@Suppress("DEPRECATION")
fun Route.recipeRoute() {
    route("/recipes") {
        // Create a new recipe
        post {
            val userId = call.principal<JWTPrincipal>()!!.userId
            val payload = call.receive<CreateRecipeRequest>()

            val recipeId = RecipesRepository.save(
                name = payload.name,
                authorId = userId,
                estTimeInMinutes = payload.estTimeInMinutes,
                description = payload.description,
                isPublic = payload.isPublic
            ).value

            IngredientsRepository.saveAll(
                recipeId = recipeId,
                ingredients = payload.ingredients
            )

            StepsRepository.saveAll(
                recipeId = recipeId,
                steps = payload.steps
            )

            call.respond(HttpStatusCode.Created)
        }

        route("/{id}") {
            // Find recipe by id
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respondJson(HttpStatusCode.BadRequest, "Invalid id")

                val recipe: RecipeDetailSummary = RecipesRepository.findById(id)
                    ?: return@get call.respondJson(HttpStatusCode.NotFound, "Recipe not found")

                if(!recipe.isPublic) {
                    val userId = call.principal<JWTPrincipal>()!!.userId
                    RecipesRepository.isAuthor(id, userId).let {
                        if(!it) return@get call.respondJson(HttpStatusCode.Forbidden, "You are not the author of this recipe")
                    }
                }
                call.respond(recipe)
            }

            // Add recipe image upload later
            post("/images") {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid id")

                val userId = call.principal<JWTPrincipal>()!!.userId

                // Check if the user is the author of the recipe
                RecipesRepository.isAuthor(id, userId).let {
                    if(!it) return@post call.respondJson(HttpStatusCode.Forbidden, "You are not the author of this recipe")
                }

                val multipart = call.receiveMultipart()
                var filePath: String? = null

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            val originalFileName = part.originalFileName as String
                            val extension = originalFileName.substring(originalFileName.lastIndexOf("."))

                            val fileName = "image-${System.currentTimeMillis()}$extension"

                            filePath = "uploads/recipes/$id/$fileName"
                            val file = File(filePath)
                            file.parentFile.mkdirs()

                            part.streamProvider().use { input ->
                                file.outputStream().buffered().use { output ->
                                    input.copyTo(output)
                                }
                            }

                        }
                        else -> {}
                    }
                    part.dispose()
                }

                if (filePath == null) return@post call.respondJson(HttpStatusCode.BadRequest, "No file uploaded")

                val imageId = ImagesRepository.save(filePath)
                RecipesImagesRepository.save(id, imageId)

                call.respond(HttpStatusCode.OK)
            }

            // Add reader comment
            post("/comments") {
                val recipeId = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid id")

                val userId = call.principal<JWTPrincipal>()!!.userId

                RecipesRepository.isAuthor(recipeId, userId).let {
                    if(it) return@post call.respondJson(HttpStatusCode.Forbidden, "You are the author of this recipe")
                }

                val payload = call.receive<CreateCommentRequest>()

                CommentsRepository.save(recipeId, userId, payload.content)
                call.respond(HttpStatusCode.Created)
            }

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

            // Add reader reaction
            post("/reactions") {
                val recipeId = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid id")

                val userId = call.principal<JWTPrincipal>()!!.userId

                RecipesRepository.isAuthor(recipeId, userId).let {
                    if(it) return@post call.respondJson(HttpStatusCode.Forbidden, "You are the author of this recipe")
                }

                val payload = call.receive<CreateReactionRequest>()

                if(payload.reaction == null) {
                    ReactionsRepository.delete(recipeId, userId)
                    return@post call.respond(HttpStatusCode.OK)
                }
                ReactionsRepository.save(recipeId, userId, payload.reaction!!)
                call.respond(HttpStatusCode.OK)
            }
        }

    }
}