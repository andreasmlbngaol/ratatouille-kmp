package com.kotlinonly.moprog.database.images

import com.kotlinonly.moprog.database.utils.insertWithTimestamps
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object ImagesRepository {
    fun save(
        url: String,
        description: String? = null
    ) = transaction {
        Images.insertWithTimestamps {
            it[Images.url] = url
            it[Images.description] = description
        }.value
    }
}