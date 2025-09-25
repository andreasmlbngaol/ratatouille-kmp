package com.kotlinonly.moprog.recipes

import com.kotlinonly.moprog.MY_DOMAIN
import com.kotlinonly.moprog.auth.config.userId
import com.kotlinonly.moprog.core.utils.respondJson
import com.kotlinonly.moprog.data.core.logE
import com.kotlinonly.moprog.data.recipes.DeleteRecipeBaseImageRequest
import com.kotlinonly.moprog.data.recipes.UpdateRecipeBaseRequest
import com.kotlinonly.moprog.database.images.ImagesRepository
import com.kotlinonly.moprog.database.recipes.RecipesRepository
import com.kotlinonly.moprog.database.recipes_images.RecipesImagesRepository
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
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.io.File
import java.io.FileOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

@Suppress("DEPRECATION")
fun Route.recipeDraftBaseRoute() {
    route("/base") {
        get {
            val userId = call.principal<JWTPrincipal>()!!.userId
            val draftBaseRecipe = RecipesRepository.findDraftBaseByAuthorId(userId) ?: run {
                logE("/recipes/draft", "User has no draft recipe")

                val newId = RecipesRepository.createDraft(userId)
                RecipesRepository.findBaseById(newId)
            } ?: return@get call.respondJson(HttpStatusCode.InternalServerError, "Database error")

            call.respond(draftBaseRecipe)
        }

        route("/{id}") {
            patch {
                val userId = call.principal<JWTPrincipal>()!!.userId
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respondJson(HttpStatusCode.BadRequest, "Invalid id")
                val payload = call.receive<UpdateRecipeBaseRequest>()

                if (!RecipesRepository.existByIdAndAuthorId(id, userId))
                    return@patch call.respondJson(HttpStatusCode.NotFound, "Recipe not found")

                if (payload.name.isBlank()) return@patch call.respondJson(
                    HttpStatusCode.BadRequest,
                    "Invalid name"
                )

                if (RecipesRepository.saveBase(
                        id = id,
                        name = payload.name,
                        description = payload.description,
                        category = payload.category,
                        estTimeInMinutes = payload.estTimeInMinutes,
                        isPublic = payload.isPublic
                    ) < 1
                ) return@patch call.respondJson(HttpStatusCode.InternalServerError, "Database error")
                call.respond(HttpStatusCode.OK)
            }

            route("/images") {
                post {
                    val userId = call.principal<JWTPrincipal>()!!.userId
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid id")

                    // Check if the recipe exists
                    if (!RecipesRepository.existByIdAndAuthorId(id, userId))
                        return@post call.respondJson(HttpStatusCode.NotFound, "Recipe not found")

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

                    if (savedImages.isEmpty()) return@post call.respondJson(
                        HttpStatusCode.BadRequest,
                        "No image uploaded"
                    )

                    call.respond(RecipesImagesRepository.findAllByRecipeId(id))
                }

                delete {
                    val userId = call.principal<JWTPrincipal>()!!.userId
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@delete call.respondJson(HttpStatusCode.BadRequest, "Invalid id")
                    val payload = call.receive<DeleteRecipeBaseImageRequest>()
                    val imageId = payload.imageId

                    if (!RecipesRepository.existByIdAndAuthorId(id, userId))
                        return@delete call.respondJson(HttpStatusCode.NotFound, "Recipe not found")

                    if (RecipesImagesRepository.deleteByImageId(imageId) < 1)
                        return@delete call.respondJson(HttpStatusCode.InternalServerError, "Database error")

                    val filePath = payload.url.replace("$MY_DOMAIN/", "")
                    val file = File(filePath)
                    if (file.exists()) {
                        if (!file.delete()) return@delete call.respondJson(
                            HttpStatusCode.InternalServerError,
                            "File delete failed"
                        )
                    }

                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}