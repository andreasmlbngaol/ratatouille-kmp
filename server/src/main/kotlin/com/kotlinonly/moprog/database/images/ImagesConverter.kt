package com.kotlinonly.moprog.database.images

import com.kotlinonly.moprog.MY_DOMAIN
import com.kotlinonly.moprog.data.image.Image
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toImage() = Image(
    id = this[Images.id].value,
    url = "$MY_DOMAIN/${this[Images.url]}"
)