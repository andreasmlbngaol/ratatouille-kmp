package com.kotlinonly.moprog.data.recipes

import kotlinx.serialization.Serializable

@Serializable
data class UpdateRecipeBaseRequest(
    val name: String,
    val description: String? = null,
    val category: RecipeCategory,
    val estTimeInMinutes: Int? = null,
    val isPublic: Boolean
)
