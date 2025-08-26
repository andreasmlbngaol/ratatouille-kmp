package com.kotlinonly.moprog.database.comments

import com.kotlinonly.moprog.database.utils.LongBaseTable
import com.kotlinonly.moprog.database.recipes.Recipes
import com.kotlinonly.moprog.database.users.Users
import org.jetbrains.exposed.v1.core.ReferenceOption

object Comments: LongBaseTable("comments") {
    val recipeId = reference("recipe_id", Recipes.id, onDelete = ReferenceOption.CASCADE)
    val userId = optReference("user_id", Users.id, onDelete = ReferenceOption.SET_NULL)
    val content = text("content")
}