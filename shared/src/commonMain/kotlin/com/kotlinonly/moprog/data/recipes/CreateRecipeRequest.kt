package com.kotlinonly.moprog.data.recipes

import com.kotlinonly.moprog.data.ingredient.IngredientRequest
import kotlinx.serialization.Serializable

@Serializable
data class CreateRecipeRequest(
    val category: RecipeCategory = RecipeCategory.OTHERS,
    val description: String? = null,
    val estTimeInMinutes: Int? = null,
    val isPublic: Boolean,
    val ingredients: List<IngredientRequest>,
    val name: String,
    val steps: List<String>
)