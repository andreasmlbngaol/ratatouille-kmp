package com.kotlinonly.moprog.data.ingredient

import kotlinx.serialization.Serializable

@Serializable
data class CreateEmptyStepRequest(
    val stepNumber: Int
)
