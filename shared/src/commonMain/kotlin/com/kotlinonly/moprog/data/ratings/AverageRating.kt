package com.kotlinonly.moprog.data.ratings

import kotlinx.serialization.Serializable

@Serializable
data class AverageRating(
    val count: Int,
    val value: Double
)