package com.kotlinonly.moprog.users

import com.kotlinonly.moprog.auth.config.userId
import com.kotlinonly.moprog.core.utils.respondJson
import com.kotlinonly.moprog.data.recipes.RecipeFilter
import com.kotlinonly.moprog.data.users.UpdateUserNameRequest
import com.kotlinonly.moprog.database.users.UsersRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.route

fun Route.myRoute() {
    route("/me") {
        get {
            val userId = call.principal<JWTPrincipal>()!!.userId
            val userDetail = UsersRepository.findDetailById(userId, RecipeFilter())
                ?: return@get call.respondJson(HttpStatusCode.NotFound, "User not found")

            call.respond(
                userDetail.copy(
                    isMe = true,
                    isFollowed = null
                )
            )
        }

        patch("/name") {
            val userId = call.principal<JWTPrincipal>()!!.userId
            val payload = call.receive<UpdateUserNameRequest>()

            UsersRepository.updateName(userId, payload.name).let {
                if(it < 1) return@patch call.respondJson(
                    HttpStatusCode.InternalServerError,
                    "Database error"
                )
                val user = UsersRepository.findById(userId)
                    ?: return@patch call.respondJson(HttpStatusCode.NotFound, "User not found")

                call.respond(user)
            }
        }
    }
}