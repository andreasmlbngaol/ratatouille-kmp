package com.kotlinonly.moprog.core.database.images

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object Images: LongIdTable("images") {
    val url = varchar("url", 255)
    val description = text("description").nullable()
}