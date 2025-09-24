package com.kotlinonly.moprog.data.ratings

import kotlinx.serialization.Serializable

@Serializable
data class CreateRatingRequest(
    val rating: Double
)
