package com.kotlinonly.moprog.database.ratings

import com.kotlinonly.moprog.database.utils.LongBaseTable
import com.kotlinonly.moprog.database.recipes.Recipes
import com.kotlinonly.moprog.database.users.Users
import org.jetbrains.exposed.v1.core.ReferenceOption

object Ratings: LongBaseTable("ratings") {
    val recipeId = reference("recipe_id", Recipes.id, onDelete = ReferenceOption.CASCADE)
    val authorId = optReference("author_id", Users.id, onDelete = ReferenceOption.SET_NULL)
    val value = double("value")
}