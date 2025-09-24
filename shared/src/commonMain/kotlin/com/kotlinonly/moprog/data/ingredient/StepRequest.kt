package com.kotlinonly.moprog.data.ingredient

import kotlinx.serialization.Serializable

@Serializable
data class StepRequest(
    val id: Long,
    val content: String
)