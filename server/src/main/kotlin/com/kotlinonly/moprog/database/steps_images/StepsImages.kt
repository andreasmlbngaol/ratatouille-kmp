package com.kotlinonly.moprog.database.steps_images

import com.kotlinonly.moprog.database.images.Images
import com.kotlinonly.moprog.database.steps.Steps
import com.kotlinonly.moprog.database.utils.LongBaseTable
import org.jetbrains.exposed.v1.core.ReferenceOption

object StepsImages: LongBaseTable("steps_images") {
    val stepId = reference("step_id", Steps, onDelete = ReferenceOption.CASCADE)
    val imageId = reference("image_id", Images, onDelete = ReferenceOption.CASCADE)
}