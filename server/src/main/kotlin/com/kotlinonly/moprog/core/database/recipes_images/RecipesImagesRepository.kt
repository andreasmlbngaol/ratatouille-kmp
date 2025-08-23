package com.kotlinonly.moprog.core.database.recipes_images

import com.kotlinonly.moprog.core.database.images.Images
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction


object RecipesImagesRepository {
    fun findAllByRecipeId(recipeId: Long) = transaction {
        (RecipesImages innerJoin Images)
            .select(Images.url)
            .where { RecipesImages.recipeId eq recipeId }
            .map { it[Images.url] }
    }

    fun save(
        recipeId: Long,
        imageId: Long
    ) = transaction {
        RecipesImages
            .insertAndGetId {
                it[RecipesImages.recipeId] = recipeId
                it[RecipesImages.imageId] = imageId
            }
    }
}