package com.kotlinonly.moprog.database.users

import com.kotlinonly.moprog.database.utils.StringBaseTable
import com.kotlinonly.moprog.data.auth.AuthMethod

object Users: StringBaseTable("users") {
    val name = varchar("name", 100)
    val email = varchar("email", 100).uniqueIndex()
    val profilePictureUrl = varchar("image_url", 255).nullable()
    val coverPictureUrl = varchar("cover_url", 255).nullable()
    val method = enumerationByName<AuthMethod>("method", 20)
    val bio = varchar("bio", 100).nullable()
    val isEmailVerified = bool("is_email_verified")
}