package com.kotlinonly.moprog.data.recipes

import com.kotlinonly.moprog.data.ingredient.IngredientRequest
import kotlinx.serialization.Serializable

@Serializable
data class CreateRecipeRequest(
    val name: String,
    val description: String? = null,
    val estTimeInMinutes: Int? = null,
    val isPublic: Boolean,
    val ingredients: List<IngredientRequest>,
    val steps: List<String>
)