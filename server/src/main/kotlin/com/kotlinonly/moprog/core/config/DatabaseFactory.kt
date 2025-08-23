package com.kotlinonly.moprog.core.config

import com.kotlinonly.moprog.core.database.SchemaVersion
import com.kotlinonly.moprog.core.database.migrations
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.exists
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

object DatabaseFactory {
    fun init(config: ApplicationConfig) {
        val dbConfig = config.config("ktor.database")
        println("URL: " + dbConfig.property("url").getString())
        println("User: " + dbConfig.property("user").getString())


        val hikariConfig = HikariConfig().apply {
            jdbcUrl = dbConfig.property("url").getString()
            driverClassName = dbConfig.property("driver").getString()
            username = dbConfig.property("user").getString()
            password = dbConfig.property("password").getString()
            maximumPoolSize = dbConfig.property("maxPoolSize").getString().toInt()
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
        val datasource = HikariDataSource(hikariConfig)
        Database.connect(datasource)
        runMigrations()
    }

    private fun runMigrations() {
        transaction {
            if (!SchemaVersion.exists()) {
                SchemaUtils.create(SchemaVersion)
                SchemaVersion.insert { it[version] = 0 }
            } else if(SchemaVersion.selectAll().empty()) {
                SchemaVersion.insert { it[version] = 0 }
            }

            var currentVersion = SchemaVersion
                .selectAll()
                .single()[SchemaVersion.version]

            // Jalankan migrasi yang versinya lebih tinggi
            migrations
                .filter { it.version > currentVersion }
                .sortedBy { it.version }
                .forEachIndexed { index, migration ->
                    println("Migrating to version ${migration.version} (${index + 1}/${migrations.size})")
                    migration.run(this)
                    SchemaVersion.update { it[version] = migration.version }
                    currentVersion = migration.version
                }

            println("Database is up to date: version $currentVersion")
        }
    }
}