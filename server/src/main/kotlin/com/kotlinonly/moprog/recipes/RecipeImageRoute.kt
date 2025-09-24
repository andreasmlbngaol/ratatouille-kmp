package com.kotlinonly.moprog.recipes

import com.kotlinonly.moprog.auth.config.userId
import com.kotlinonly.moprog.core.utils.respondJson
import com.kotlinonly.moprog.database.images.ImagesRepository
import com.kotlinonly.moprog.database.recipes.RecipesRepository
import com.kotlinonly.moprog.database.recipes_images.RecipesImagesRepository
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
fun Route.recipeImageRoute() {
    route("/images") {
        // Add recipe image upload later
        post {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid id")

            val userId = call.principal<JWTPrincipal>()!!.userId

            // Check if the user is the author of the recipe
            RecipesRepository.isAuthor(id, userId).let {
                if (!it) return@post call.respondJson(
                    HttpStatusCode.Forbidden,
                    "You are not the author of this recipe"
                )
            }

            val multipart = call.receiveMultipart()
            val savedImages = mutableListOf<Long>()

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val fileName = "image-${System.currentTimeMillis()}.webp"
                        val filePath = "uploads/recipes/$id/$fileName"
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
                                if (param.canWriteCompressed()) {
                                    param.compressionMode = ImageWriteParam.MODE_EXPLICIT
                                    param.compressionType = param.compressionTypes[0]
                                    param.compressionQuality = 0.8f // Compress to 80% quality
                                }

                                writer.write(null, IIOImage(image, null, null), param)
                                ios.close()
                                writer.dispose()
                            }
                        }

                        val imageId = ImagesRepository.save(filePath)
                        RecipesImagesRepository.save(id, imageId)
                        savedImages.add(imageId)
                    }

                    else -> {}
                }
                part.dispose()
            }

            if(savedImages.isEmpty()) return@post call.respondJson(HttpStatusCode.BadRequest, "No image uploaded")

            call.respond(HttpStatusCode.OK)
        }

        // Delete recipe image
        delete("/{imageId}") {
            TODO()
        }
    }
}