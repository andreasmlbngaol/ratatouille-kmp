package com.kotlinonly.moprog.core.database.recipes_images

import com.kotlinonly.moprog.core.database.images.Images
import com.kotlinonly.moprog.core.database.recipes.Recipes
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object RecipesImages: LongIdTable("recipes_images") {
    val recipeId = reference("recipe_id", Recipes.id)
    val imageId = reference("image_id", Images.id)
}