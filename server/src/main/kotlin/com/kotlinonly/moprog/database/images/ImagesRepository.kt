package com.kotlinonly.moprog.database.images

import com.kotlinonly.moprog.database.utils.insertWithTimestamps
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
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

    fun findUrlById(id: Long) = transaction {
        Images
            .select(Images.url)
            .where { Images.id eq id }
            .map { it[Images.url] }
            .firstOrNull()
    }

    fun deleteById(id: Long) = transaction {
        Images
            .deleteWhere { Images.id eq id }
    }
}