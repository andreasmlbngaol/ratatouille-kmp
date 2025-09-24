package com.kotlinonly.moprog.data.ingredient

import kotlinx.serialization.Serializable

@Serializable
data class IngredientRequest(
    val tagId: Long,
    val alternative: String? = null,
    val amount: Double? = null,
    val unit: String? = null
)
