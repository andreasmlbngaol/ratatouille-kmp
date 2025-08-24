package com.kotlinonly.moprog.database.recipes_images

import com.kotlinonly.moprog.database.images.Images
import com.kotlinonly.moprog.database.recipes.Recipes
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object RecipesImages: LongIdTable("recipes_images") {
    val recipeId = reference("recipe_id", Recipes.id, onDelete = ReferenceOption.CASCADE)
    val imageId = reference("image_id", Images.id)
}