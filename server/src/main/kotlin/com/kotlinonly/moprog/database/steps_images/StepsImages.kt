package com.kotlinonly.moprog.database.steps_images

import com.kotlinonly.moprog.database.images.Images
import com.kotlinonly.moprog.database.steps.Steps
import com.kotlinonly.moprog.database.utils.LongBaseTable

object StepsImages: LongBaseTable("steps_images") {
    val stepId = reference("step_id", Steps)
    val imageId = reference("image_id", Images)
}