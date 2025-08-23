package com.kotlinonly.moprog.core.database.bookmarks

import com.kotlinonly.moprog.core.database.users.Users
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object Bookmarks: LongIdTable("bookmarks") {
    val userId = reference("user_id", Users.id)
    val recipeId = reference("recipe_id", Users.id)
}