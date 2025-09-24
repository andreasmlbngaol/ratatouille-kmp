package com.kotlinonly.moprog.data.ingredient

import kotlinx.serialization.Serializable

@Serializable
data class IngredientTag(
    val id: Long = 0L,
    val name: String = ""
)
