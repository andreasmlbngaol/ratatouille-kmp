package com.kotlinonly.moprog.recipes

import com.kotlinonly.moprog.MY_DOMAIN
import com.kotlinonly.moprog.auth.config.userId
import com.kotlinonly.moprog.core.utils.respondJson
import com.kotlinonly.moprog.data.core.logE
import com.kotlinonly.moprog.data.ratings.CreateRatingRequest
import com.kotlinonly.moprog.data.recipes.CreateRecipeRequest
import com.kotlinonly.moprog.data.recipes.DeleteRecipeBaseImageRequest
import com.kotlinonly.moprog.data.recipes.RecipeCategory
import com.kotlinonly.moprog.data.recipes.RecipeDetailSummary
import com.kotlinonly.moprog.data.recipes.RecipeFilter
import com.kotlinonly.moprog.data.recipes.SortType
import com.kotlinonly.moprog.data.recipes.UpdateRecipeBaseRequest
import com.kotlinonly.moprog.database.images.ImagesRepository
import com.kotlinonly.moprog.database.ingredients.IngredientsRepository
import com.kotlinonly.moprog.database.ratings.RatingsRepository
import com.kotlinonly.moprog.database.recipes.RecipesRepository
import com.kotlinonly.moprog.database.recipes_images.RecipesImagesRepository
import com.kotlinonly.moprog.database.steps.StepsRepository
import com.kotlinonly.moprog.utils.enumValueOfOrNull
import io.ktor.http.*
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.io.FileOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

@Suppress( "DuplicatedCode")
fun Route.recipeRoute() {
    route("/recipes") {
        // 3 Step creating recipe
        route("/draft") {
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

                            call.respond(HttpStatusCode.OK)
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

        // Create a new recipe
        post {
            val userId = call.principal<JWTPrincipal>()!!.userId
            val payload = call.receive<CreateRecipeRequest>()

            val recipeId = RecipesRepository.save(
                category = payload.category,
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