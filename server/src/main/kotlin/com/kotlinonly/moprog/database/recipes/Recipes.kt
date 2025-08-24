package com.kotlinonly.moprog.database.recipes

import com.kotlinonly.moprog.database.LongBaseTable
import com.kotlinonly.moprog.database.users.Users
import org.jetbrains.exposed.v1.core.ReferenceOption

object Recipes: LongBaseTable("recipes") {
    val name = varchar("name", 100)
    val authorId = reference("author", Users.id, onDelete = ReferenceOption.CASCADE)
    val description = text("description").nullable()
    val estTimeInMinutes = integer("est_time_in_minutes").nullable()
    val isPublic = bool("is_public").default(true)
}