package com.kotlinonly.moprog.database

import com.kotlinonly.moprog.database.bookmarks.Bookmarks
import com.kotlinonly.moprog.database.comments.Comments
import com.kotlinonly.moprog.database.comments_images.CommentsImages
import com.kotlinonly.moprog.database.images.Images
import com.kotlinonly.moprog.database.ingredients.Ingredients
import com.kotlinonly.moprog.database.ratings.Ratings
import com.kotlinonly.moprog.database.reactions.Reactions
import com.kotlinonly.moprog.database.recipes.Recipes
import com.kotlinonly.moprog.database.recipes_images.RecipesImages
import com.kotlinonly.moprog.database.steps.Steps
import com.kotlinonly.moprog.database.users.Users
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.SchemaUtils

data class Migration(
    val version: Int,
    val run: JdbcTransaction.() -> Unit
)

val migrations = listOf(
    Migration(1) {
        SchemaUtils.create(
            Users
        )
    },
    Migration(2) {
        SchemaUtils.create(
            Bookmarks,
            Comments,
            CommentsImages,
            Images,
            Ingredients,
            Ratings,
            Reactions,
            Recipes,
            RecipesImages,
            Steps
        )
    }
)
