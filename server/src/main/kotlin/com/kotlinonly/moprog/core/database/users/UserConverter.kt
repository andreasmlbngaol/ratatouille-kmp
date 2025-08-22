package com.kotlinonly.moprog.core.database.users

import com.kotlinonly.moprog.data.auth.User
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toUser() = User(
    id = this[Users.id],
    name = this[Users.name],
    email = this[Users.email],
    profilePictureUrl = this[Users.profilePictureUrl],
    method = this[Users.method],
    isEmailVerified = this[Users.isEmailVerified],
    createdAt = this[Users.createdAt]
)