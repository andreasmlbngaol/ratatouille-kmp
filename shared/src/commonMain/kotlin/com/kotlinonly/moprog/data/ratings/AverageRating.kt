package com.kotlinonly.moprog.data.ratings

import kotlinx.serialization.Serializable

@Serializable
data class AverageRating(
    val value: Double,
    val count: Int
)