package com.kotlinonly.moprog.database.bookmarks

import com.kotlinonly.moprog.database.insertWithTimestamps
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object BookmarksRepository {
    fun save(
        recipeId: Long,
        userId: String
    ) = transaction {
        Bookmarks
            .insertWithTimestamps {
                it[Bookmarks.userId] = userId
                it[Bookmarks.recipeId] = recipeId
            }
    }

    fun delete(
        recipeId: Long,
        userId: String
    ) = transaction {
        Bookmarks
            .deleteWhere {
                (Bookmarks.userId eq userId) and (Bookmarks.recipeId eq recipeId)
            }
    }

    fun countByRecipeId(recipeId: Long) = transaction {
        Bookmarks
            .select(Bookmarks.id)
            .where { Bookmarks.recipeId eq recipeId }
            .count()
    }

    fun isBookmarked(recipeId: Long, userId: String) = transaction {
        Bookmarks
            .select(Bookmarks.id)
            .where { (Bookmarks.recipeId eq recipeId) and (Bookmarks.userId eq userId) }
            .count() > 0
    }
}