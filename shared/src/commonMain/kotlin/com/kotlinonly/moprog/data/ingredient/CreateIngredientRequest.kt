package com.kotlinonly.moprog.data.ingredient

import kotlinx.serialization.Serializable

@Serializable
data class CreateIngredientRequest(
    val ingredients: List<IngredientRequest>
)
