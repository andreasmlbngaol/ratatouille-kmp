package com.kotlinonly.moprog.data.ingredient

import kotlinx.serialization.Serializable

@Serializable
data class IngredientRequest(
    val name: String,
    val amount: Double? = null,
    val unit: String? = null
)
