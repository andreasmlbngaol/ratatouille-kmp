package com.kotlinonly.moprog.database.users

import com.kotlinonly.moprog.database.utils.insertWithTimestamps
import com.kotlinonly.moprog.data.auth.User
import com.kotlinonly.moprog.database.utils.updateWithTimestamps
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object UsersRepository {
    fun findById(id: String) = transaction {
        Users
            .selectAll()
            .where { Users.id eq id }
            .map { it.toUser() }
            .firstOrNull()
    }

    fun existByEmail(email: String) = transaction {
        Users
            .selectAll()
            .where { Users.email eq email }
            .count() > 0
    }

    fun updateName(id: String, name: String) = transaction {
        Users.updateWithTimestamps({ Users.id eq id }) {
            it[Users.name] = name
        }
    }

    fun save(user: User) = transaction {
        Users.insertWithTimestamps {
            it[id] = user.id
            it[name] = user.name
            it[email] = user.email
            it[profilePictureUrl] = user.profilePictureUrl
            it[method] = user.method
            it[isEmailVerified] = user.isEmailVerified
            it[createdAt] = user.createdAt
        }
    }

}