package com.kotlinonly.moprog.database.follows

import com.kotlinonly.moprog.database.utils.insertWithTimestamps
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object FollowsRepository {
    fun saveWithTimestamps(
        followerId: String,
        followedId: String
    ) = transaction {
        Follows.insertWithTimestamps {
            it[Follows.followerId] = followerId
            it[Follows.followedId] = followedId
        }.value
    }

    fun delete(followerId: String, followedId: String) = transaction {
        Follows
            .deleteWhere {
                (Follows.followerId eq followerId) and
                        (Follows.followedId eq followedId)
            }
    }

    fun countFollowingById(userId: String) = transaction {
        Follows
            .select(Follows.id)
            .where { Follows.followerId eq userId }
            .count()
    }

    fun countFollowerById(userId: String) = transaction {
        Follows
            .select(Follows.id)
            .where { Follows.followedId eq userId }
            .count()
    }

    fun isFollowed(
        followerId: String,
        followedId: String
    ) = transaction {
        Follows
            .select(Follows.id)
            .where { Follows.followedId eq followedId }
            .andWhere { Follows.followerId eq followerId }
            .count() > 0
    }
}