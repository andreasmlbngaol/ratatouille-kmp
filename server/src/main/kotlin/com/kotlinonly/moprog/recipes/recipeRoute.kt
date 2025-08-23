package com.kotlinonly.moprog.recipes

import com.kotlinonly.moprog.core.config.userId
import com.kotlinonly.moprog.core.database.comments.CommentsRepository
import com.kotlinonly.moprog.core.database.comments_images.CommentsImagesRepository
import com.kotlinonly.moprog.core.database.images.ImagesRepository
import com.kotlinonly.moprog.core.database.ingredients.IngredientsRepository
import com.kotlinonly.moprog.core.database.ratings.RatingsRepository
import com.kotlinonly.moprog.core.database.reactions.ReactionsRepository
import com.kotlinonly.moprog.core.database.recipes.RecipesRepository
import com.kotlinonly.moprog.core.database.recipes_images.RecipesImagesRepository
import com.kotlinonly.moprog.core.database.steps.StepsRepository
import com.kotlinonly.moprog.core.utils.respondJson
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
import java.io.FileOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

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
                            val fileName = "image-${System.currentTimeMillis()}.webp"
                            filePath = "uploads/recipes/$id/$fileName"
                            val file = File(filePath)
                            file.parentFile.mkdirs()

                            // Convert BufferedImage
                            part.streamProvider().use { input ->
                                val image = ImageIO.read(input)

                                FileOutputStream(file).use { fos ->
                                    val writers = ImageIO.getImageWritersByFormatName("webp")

                                    if (!writers.hasNext()) {
                                        throw RuntimeException("No WebP writer found")
                                    }

                                    val writer = writers.next()
                                    val ios = ImageIO.createImageOutputStream(fos)
                                    writer.output = ios

                                    val param = writer.defaultWriteParam
                                    if(param.canWriteCompressed()) {
                                        param.compressionMode = ImageWriteParam.MODE_EXPLICIT

                                        println("Available compression types = ${param.compressionTypes?.joinToString()}")
                                        param.compressionType = param.compressionTypes[0]

                                        param.compressionQuality = 0.8f // Compress to 80% quality
                                    }

                                    writer.write(null, IIOImage(image, null, null), param)
                                    ios.close()
                                    writer.dispose()
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

                val multipart = call.receiveMultipart()

                var content: String? = null
                var imagePath: String? = null

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            val fileName = "image-${System.currentTimeMillis()}.webp"
                            imagePath = "uploads/recipes/$recipeId/comments/$fileName"
                            val file = File(imagePath)
                            file.parentFile.mkdirs()

                            // Convert BufferedImage
                            part.streamProvider().use { input ->
                                val image = ImageIO.read(input)

                                FileOutputStream(file).use { fos ->
                                    val writers = ImageIO.getImageWritersByFormatName("webp")

                                    val writer = writers.next()
                                    val ios = ImageIO.createImageOutputStream(fos)
                                    writer.output = ios

                                    val param = writer.defaultWriteParam
                                    if (param.canWriteCompressed()) {
                                        param.compressionMode = ImageWriteParam.MODE_EXPLICIT

                                        println("Available compression types = ${param.compressionTypes?.joinToString()}")
                                        param.compressionType = param.compressionTypes[0]

                                        param.compressionQuality = 0.8f // Compress to 80% quality
                                    }

                                    writer.write(null, IIOImage(image, null, null), param)
                                    ios.close()
                                    writer.dispose()
                                }
                            }
                        }
                        is PartData.FormItem -> {
                            if (part.name == "content") content = part.value
                        }
                        else -> {}
                    }
                }

                if(content.isNullOrBlank()) {
                    return@post call.respondJson(HttpStatusCode.BadRequest, "Content is required")
                }

                val commentId = CommentsRepository.save(recipeId, userId, content)

                if(imagePath != null) {
                    val imageId = ImagesRepository.save(imagePath)
                    CommentsImagesRepository.save(commentId, imageId)
                }

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