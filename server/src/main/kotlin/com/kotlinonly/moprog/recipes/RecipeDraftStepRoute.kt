package com.kotlinonly.moprog.recipes

import com.kotlinonly.moprog.auth.config.userId
import com.kotlinonly.moprog.core.utils.respondJson
import com.kotlinonly.moprog.data.ingredient.CreateEmptyStepRequest
import com.kotlinonly.moprog.data.ingredient.SaveStepsRequest
import com.kotlinonly.moprog.database.images.ImagesRepository
import com.kotlinonly.moprog.database.recipes.RecipesRepository
import com.kotlinonly.moprog.database.recipes_images.RecipesImagesRepository
import com.kotlinonly.moprog.database.steps.StepsRepository
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
fun Route.recipeDraftStepRoute() {
    route("/steps") {

        route("/{id}") {
            // Get all steps
            get {
                val userId = call.principal<JWTPrincipal>()!!.userId
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respondJson(HttpStatusCode.BadRequest, "Invalid id")
                RecipesRepository.existByIdAndAuthorId(id, userId).let {
                    if(!it) return@get call.respondJson(HttpStatusCode.NotFound, "Recipe with this id and author id not found")
                }
                call.respond(StepsRepository.findAllByRecipeId(id))
            }

            // Create Empty Step
            post {
                val userId = call.principal<JWTPrincipal>()!!.userId
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid id")
                RecipesRepository.existByIdAndAuthorId(id, userId).let {
                    if(!it) return@post call.respondJson(HttpStatusCode.NotFound, "Recipe with this id and author id not found")
                }
                val payload = call.receive<CreateEmptyStepRequest>()
                StepsRepository.createEmptyStep(
                    recipeId = id,
                    stepNumber = payload.stepNumber
                )
                call.respond(HttpStatusCode.Created)
            }

            // Save All Steps
            patch {
                val userId = call.principal<JWTPrincipal>()!!.userId
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respondJson(HttpStatusCode.BadRequest, "Invalid id")
                RecipesRepository.existByIdAndAuthorId(id, userId).let {
                    if(!it) return@patch call.respondJson(HttpStatusCode.NotFound, "Recipe with this id and author id not found")
                }
                val payload = call.receive<SaveStepsRequest>()
                if(payload.steps.isEmpty()) return@patch call.respondJson(HttpStatusCode.BadRequest, "No steps provided")

                StepsRepository.saveAll(payload.steps)
                call.respond(HttpStatusCode.OK)
            }

            route("/{stepId}") {
                delete {
                    val userId = call.principal<JWTPrincipal>()!!.userId
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@delete call.respondJson(HttpStatusCode.BadRequest, "Invalid id")
                    RecipesRepository.existByIdAndAuthorId(id, userId).let {
                        if (!it) return@delete call.respondJson(
                            HttpStatusCode.NotFound,
                            "Recipe with this id and author id not found"
                        )
                    }
                    val stepId = call.parameters["stepId"]?.toLongOrNull()
                        ?: return@delete call.respondJson(HttpStatusCode.BadRequest, "Invalid step id")

                    StepsRepository.delete(stepId)
                    call.respond(HttpStatusCode.OK)
                }

                route("/images") {
                    post {
                        val userId = call.principal<JWTPrincipal>()!!.userId
                        val id = call.parameters["id"]?.toLongOrNull()
                            ?: return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid id")
                        RecipesRepository.existByIdAndAuthorId(id, userId).let {
                            if (!it) return@post call.respondJson(
                                HttpStatusCode.NotFound,
                                "Recipe with this id and author id not found"
                            )
                        }
                        val stepId = call.parameters["stepId"]?.toLongOrNull()
                            ?: return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid step id")

                        val multipart = call.receiveMultipart()
                        val savedImages = mutableListOf<Long>()

                        multipart.forEachPart { part ->
                            when (part) {
                                is PartData.FileItem -> {
                                    val fileName = "image-${System.currentTimeMillis()}.webp"
                                    val filePath = "uploads/recipes/$id/steps/$stepId/$fileName"
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

                    delete("/{imageId}") {
                        val userId = call.principal<JWTPrincipal>()!!.userId
                        val id = call.parameters["id"]?.toLongOrNull()
                            ?: return@delete call.respondJson(HttpStatusCode.BadRequest, "Invalid id")
                        RecipesRepository.existByIdAndAuthorId(id, userId).let {
                            if (!it) return@delete call.respondJson(
                                HttpStatusCode.NotFound,
                                "Recipe with this id and author id not found"
                            )
                        }
                        val imageId = call.parameters["imageId"]?.toLongOrNull()
                            ?: return@delete call.respondJson(HttpStatusCode.BadRequest, "Invalid image id")

                        val filePath = ImagesRepository.findUrlById(imageId)
                            ?: return@delete call.respondJson(HttpStatusCode.NotFound, "Image not found")

                        if(ImagesRepository.deleteById(imageId) < 1)
                            return@delete call.respondJson(HttpStatusCode.InternalServerError, "Database error")

                        val file = File(filePath)
                        if(file.exists()) {
                            file.delete()
                        }

                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }
    }
}