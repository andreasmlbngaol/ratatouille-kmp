package com.kotlinonly.moprog.core.database

import com.kotlinonly.moprog.core.database.bookmarks.Bookmarks
import com.kotlinonly.moprog.core.database.comments.Comments
import com.kotlinonly.moprog.core.database.comments_images.CommentsImages
import com.kotlinonly.moprog.core.database.images.Images
import com.kotlinonly.moprog.core.database.ingredients.Ingredients
import com.kotlinonly.moprog.core.database.ratings.Ratings
import com.kotlinonly.moprog.core.database.reactions.Reactions
import com.kotlinonly.moprog.core.database.recipes.Recipes
import com.kotlinonly.moprog.core.database.recipes_images.RecipesImages
import com.kotlinonly.moprog.core.database.steps.Steps
import com.kotlinonly.moprog.core.database.users.Users
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
