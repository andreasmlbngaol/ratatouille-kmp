package com.kotlinonly.moprog.core.database

import com.kotlinonly.moprog.core.database.users.Users
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.SchemaUtils

data class Migration(
    val version: Int,
    val run: JdbcTransaction.() -> Unit
)

val migrations = listOf(
    Migration(1) {
        SchemaUtils.create(
            Users
        )
    },
)
