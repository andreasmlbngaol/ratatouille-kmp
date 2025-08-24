package com.kotlinonly.moprog.database.comments

import com.kotlinonly.moprog.database.insertWithTimestamps
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object CommentsRepository {
    fun findAllByRecipeId(recipeId: Long) = transaction {
        Comments
            .selectAll()
            .where { Comments.recipeId eq recipeId }
            .map { it.toCommentSummary() }
    }

    fun save(
        recipeId: Long,
        userId: String,
        content: String
    ) = transaction {
        Comments
            .insertWithTimestamps {
                it[Comments.recipeId] = recipeId
                it[Comments.userId] = userId
                it[Comments.content] = content
            }
    }.value

    fun delete(commentId: Long) = transaction {
        Comments
            .deleteWhere { Comments.id eq commentId }
    }

    fun isAuthor(commentId: Long, userId: String) = transaction {
        Comments
            .select(Comments.userId)
            .where { (Comments.id eq commentId) and (Comments.userId eq userId) }
            .count() > 0
    }

    fun existById(commentId: Long) = transaction {
        Comments
            .select(Comments.id)
            .where { Comments.id eq commentId }
            .count() > 0
    }
}