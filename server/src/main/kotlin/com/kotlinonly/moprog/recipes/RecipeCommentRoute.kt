package com.kotlinonly.moprog.recipes

import com.kotlinonly.moprog.auth.config.userId
import com.kotlinonly.moprog.core.utils.respondJson
import com.kotlinonly.moprog.database.comments.CommentsRepository
import com.kotlinonly.moprog.database.comments_images.CommentsImagesRepository
import com.kotlinonly.moprog.database.images.ImagesRepository
import com.kotlinonly.moprog.database.recipes.RecipesRepository
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.io.File
import java.io.FileOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

@Suppress( "DEPRECATION", "DuplicatedCode")
fun Route.recipeCommentRoute() {
    // TODO() Note: Maybe add the replying feature to a comment later
    route("/comments") {
        // Add reader comment
        post {
            val recipeId = call.parameters["id"]?.toLongOrNull()
                ?: return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid id")

            val userId = call.principal<JWTPrincipal>()!!.userId

            RecipesRepository.isAuthor(recipeId, userId).let {
                if (it) return@post call.respondJson(
                    HttpStatusCode.Forbidden,
                    "You are the author of this recipe"
                )
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

            if (content.isNullOrBlank()) {
                return@post call.respondJson(HttpStatusCode.BadRequest, "Content is required")
            }

            val commentId = CommentsRepository.save(recipeId, userId, content)

            if (imagePath != null) {
                val imageId = ImagesRepository.save(imagePath)
                CommentsImagesRepository.save(commentId, imageId)
            }

            call.respond(HttpStatusCode.Created)
        }

        // Delete comment
        delete("/{commentId}") {
            val commentId = call.parameters["commentId"]?.toLongOrNull()
                ?: return@delete call.respondJson(HttpStatusCode.BadRequest, "Invalid id")

            val userId = call.principal<JWTPrincipal>()!!.userId

            CommentsRepository.existById(commentId).let {
                if(!it) return@delete call.respondJson(HttpStatusCode.NotFound, "Comment not found")
            }

            CommentsRepository.isAuthor(commentId, userId).let {
                if(!it) return@delete call.respondJson(HttpStatusCode.Forbidden, "You are not the author of this comment")
            }

            CommentsRepository.delete(commentId)
            call.respond(HttpStatusCode.OK)
        }
    }
}