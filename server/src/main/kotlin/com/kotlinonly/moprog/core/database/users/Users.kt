package com.kotlinonly.moprog.core.database.users

import com.kotlinonly.moprog.data.auth.AuthMethod
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object Users: Table("users") {
    val id = varchar("id", 40)
    val name = varchar("name", 100)
    val email = varchar("email", 100).uniqueIndex().nullable()
    val profilePictureUrl = varchar("image_url", 255).nullable()
    val method = enumerationByName<AuthMethod>("method", 20)
    val isEmailVerified = bool("is_email_verified")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)

}