package com.kotlinonly.moprog.database.recipes_images

import com.kotlinonly.moprog.MY_DOMAIN
import com.kotlinonly.moprog.database.images.Images
import com.kotlinonly.moprog.database.images.toImage
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction


object RecipesImagesRepository {
    fun findAllByRecipeId(recipeId: Long) = transaction {
        (RecipesImages innerJoin Images)
            .select(Images.url, Images.id)
            .where { RecipesImages.recipeId eq recipeId }
            .map { it.toImage() }
    }

    fun deleteByImageId(imageId: Long) = transaction {
        RecipesImages
            .deleteWhere { RecipesImages.imageId eq imageId }
    }



    fun findFirstByRecipeId(recipeId: Long) = transaction {
        (RecipesImages innerJoin Images)
            .select(Images.url)
            .where { RecipesImages.recipeId eq recipeId }
            .limit(1)
            .map { "${MY_DOMAIN}/${it[Images.url]}" }
            .firstOrNull()
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