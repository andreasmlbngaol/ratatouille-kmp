package com.kotlinonly.moprog.users

import com.kotlinonly.moprog.auth.config.userId
import com.kotlinonly.moprog.data.users.UpdateUserNameRequest
import com.kotlinonly.moprog.database.users.UsersRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.patch
import io.ktor.server.routing.route

fun Route.userRoute() {
    route("/users") {
        patch("/name") {
            val userId = call.principal<JWTPrincipal>()!!.userId
            val payload = call.receive<UpdateUserNameRequest>()

            UsersRepository.updateName(userId, payload.name).let {
                if(it > 0) return@patch call.respond(HttpStatusCode.OK)
            }
        }
    }
}