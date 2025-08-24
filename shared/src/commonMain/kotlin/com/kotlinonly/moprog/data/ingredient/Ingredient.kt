package com.kotlinonly.moprog.data.ingredient

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    val amount: Double? = null,
    val id: Long = 0L,
    val name: String = "",
    val unit: String? = null
)