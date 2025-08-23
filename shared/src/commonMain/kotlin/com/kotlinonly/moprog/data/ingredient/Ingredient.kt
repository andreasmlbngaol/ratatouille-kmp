package com.kotlinonly.moprog.data.ingredient

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    val id: Long = 0L,
    val name: String = "",
    val amount: Double? = null,
    val unit: String? = null
)