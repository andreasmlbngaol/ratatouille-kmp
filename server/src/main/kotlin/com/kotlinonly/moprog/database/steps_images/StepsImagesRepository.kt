package com.kotlinonly.moprog.database.steps_images

import com.kotlinonly.moprog.database.images.Images
import com.kotlinonly.moprog.database.images.toImage
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object StepsImagesRepository {
    fun findByStepId(stepId: Long) = transaction {
        (StepsImages innerJoin Images)
            .select(Images.url, Images.id)
            .where { StepsImages.stepId eq stepId }
            .map { it.toImage() }
    }
}