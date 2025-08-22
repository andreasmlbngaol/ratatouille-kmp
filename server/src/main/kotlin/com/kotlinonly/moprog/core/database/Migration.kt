package com.kotlinonly.moprog.core.database

import org.jetbrains.exposed.v1.jdbc.JdbcTransaction

data class Migration(
    val version: Int,
    val run: JdbcTransaction.() -> Unit
)

val migrations = listOf(
    Migration(1) {
//        SchemaUtils.create(
//        )
    },
)
