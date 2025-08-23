package com.kotlinonly.moprog.core.database.images

import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object ImagesRepository {
    fun save(
        url: String,
        description: String? = null
    ) = transaction {
        Images.insertAndGetId {
            it[Images.url] = url
            it[Images.description] = description
        }.value
    }
}