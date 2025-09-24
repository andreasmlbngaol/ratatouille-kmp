package com.kotlinonly.moprog.data.ingredient

import kotlinx.serialization.Serializable

@Serializable
data class SaveStepsRequest(
    val steps: List<StepRequest>
)
