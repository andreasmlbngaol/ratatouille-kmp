package com.kotlinonly.moprog.database.steps

import com.kotlinonly.moprog.data.Step
import com.kotlinonly.moprog.database.steps_images.StepsImagesRepository
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toStep(): Step {
    val id = this[Steps.id].value
    val images = StepsImagesRepository.findByStepId(this[Steps.id].value)

    return Step(
        id = id,
        stepNumber = this[Steps.stepNumber],
        content = this[Steps.content],
        images = images
    )
}