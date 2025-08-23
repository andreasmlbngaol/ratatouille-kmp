package com.kotlinonly.moprog.core.database.ratings

import com.kotlinonly.moprog.core.database.recipes.Recipes
import com.kotlinonly.moprog.core.database.users.Users
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object Ratings: LongIdTable("ratings") {
    val recipeId = reference("recipe_id", Recipes.id)
    val authorId = reference("author_id", Users.id)
    val value = double("value")
}