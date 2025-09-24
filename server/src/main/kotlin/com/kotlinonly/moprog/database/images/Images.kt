package com.kotlinonly.moprog.database.images

import com.kotlinonly.moprog.database.utils.LongBaseTable

object Images: LongBaseTable("images") {
    val url = varchar("url", 255)
    val description = text("description").nullable()
}