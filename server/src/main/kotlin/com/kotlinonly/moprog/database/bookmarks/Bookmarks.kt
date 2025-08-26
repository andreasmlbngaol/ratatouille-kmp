package com.kotlinonly.moprog.database.bookmarks

import com.kotlinonly.moprog.database.utils.LongBaseTable
import com.kotlinonly.moprog.database.recipes.Recipes
import com.kotlinonly.moprog.database.users.Users
import org.jetbrains.exposed.v1.core.ReferenceOption

object Bookmarks: LongBaseTable("bookmarks") {
    val userId = reference("user_id", Users.id, onDelete = ReferenceOption.CASCADE)
    val recipeId = reference("recipe_id", Recipes.id, onDelete = ReferenceOption.CASCADE)

    init {
        uniqueIndex(userId, recipeId)
    }
}