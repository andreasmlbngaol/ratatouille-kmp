package com.kotlinonly.moprog.core.database.comments

import org.jetbrains.exposed.v1.jdbc.insertAndGetId
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
            .insertAndGetId {
                it[Comments.recipeId] = recipeId
                it[Comments.userId] = userId
                it[Comments.content] = content
            }
    }
}