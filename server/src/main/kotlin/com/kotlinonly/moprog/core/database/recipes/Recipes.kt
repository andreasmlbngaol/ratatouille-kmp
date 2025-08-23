package com.kotlinonly.moprog.core.database.recipes

import com.kotlinonly.moprog.core.database.users.Users
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object Recipes: LongIdTable("recipes") {
    val name = varchar("name", 100)
    val authorId = reference("author", Users.id)
    val description = text("description").nullable()
    val estTimeInMinutes = integer("est_time_in_minutes").nullable()
    val isPublic = bool("is_public").default(true)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}