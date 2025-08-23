package com.kotlinonly.moprog.core.database.comments

import com.kotlinonly.moprog.core.database.recipes.Recipes
import com.kotlinonly.moprog.core.database.users.Users
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object Comments: LongIdTable("comments") {
    val recipeId = reference("recipe_id", Recipes.id)
    val userId = reference("user_id", Users.id)
    val content = text("content")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}