package com.kotlinonly.moprog.data.ingredient

import kotlinx.serialization.Serializable

@Serializable
data class IngredientTagRequest(
    val name: String,
    val limit: Int
)
