package com.kotlinonly.moprog.users

import io.ktor.server.routing.*

fun Route.userRoute() {
    route("/users") {
        otherUserRoute()
        myRoute()
    }
}