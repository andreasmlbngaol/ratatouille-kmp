package com.kotlinonly.moprog.recipes

import com.kotlinonly.moprog.auth.config.userId
import com.kotlinonly.moprog.core.utils.respondJson
import com.kotlinonly.moprog.database.bookmarks.BookmarksRepository
import com.kotlinonly.moprog.database.recipes.RecipesRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.route

@Suppress( "DuplicatedCode")
fun Route.recipeBookmarkRoute() {
    route("/bookmarks") {
        // Save to bookmarks
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

            BookmarksRepository.isBookmarked(recipeId, userId).let {
                if (it) return@post call.respondJson(HttpStatusCode.OK, "Already bookmarked")
            }
            BookmarksRepository.save(recipeId, userId)
            call.respond(HttpStatusCode.Created)
        }

        // Delete from bookmarks
        delete {
            val recipeId = call.parameters["id"]?.toLongOrNull()
                ?: return@delete call.respondJson(HttpStatusCode.BadRequest, "Invalid id")

            val userId = call.principal<JWTPrincipal>()!!.userId

            BookmarksRepository.delete(recipeId, userId)
            call.respond(HttpStatusCode.OK)
        }
    }

}