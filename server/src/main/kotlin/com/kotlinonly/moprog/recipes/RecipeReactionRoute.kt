package com.kotlinonly.moprog.recipes

import com.kotlinonly.moprog.auth.config.userId
import com.kotlinonly.moprog.core.utils.respondJson
import com.kotlinonly.moprog.data.reactions.CreateReactionRequest
import com.kotlinonly.moprog.database.reactions.ReactionsRepository
import com.kotlinonly.moprog.database.recipes.RecipesRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.route

@Suppress( "DuplicatedCode")
fun Route.recipeReactionRoute() {
    route("/reactions") {
        // Add reader reaction
        post("/reactions") {
            val recipeId = call.parameters["id"]?.toLongOrNull()
                ?: return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid id")

            val userId = call.principal<JWTPrincipal>()!!.userId

            RecipesRepository.isAuthor(recipeId, userId).let {
                if (it) return@post call.respondJson(
                    HttpStatusCode.Forbidden,
                    "You are the author of this recipe"
                )
            }

            val payload = call.receive<CreateReactionRequest>()

            if (payload.reaction == null) {
                ReactionsRepository.delete(recipeId, userId)
                return@post call.respond(HttpStatusCode.OK)
            }
            ReactionsRepository.save(recipeId, userId, payload.reaction!!)
            call.respond(HttpStatusCode.OK)
        }

        // Delete reaction
        delete("/reactions") {
            // TODO() Note: Split from the above post method later
        }
    }
}