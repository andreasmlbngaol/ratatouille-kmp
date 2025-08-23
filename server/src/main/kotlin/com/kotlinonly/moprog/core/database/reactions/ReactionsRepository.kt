package com.kotlinonly.moprog.core.database.reactions

import com.kotlinonly.moprog.data.reactions.ReactionType
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

object ReactionsRepository {
    fun findAllByRecipeId(recipeId: Long) = transaction {
        Reactions
            .select(Reactions.type)
            .where { Reactions.recipeId eq recipeId }
            .map { it[Reactions.type] }
    }

    fun save(
        recipeId: Long,
        userId: String,
        type: ReactionType
    ) {
        transaction {
            val existing = Reactions
                .selectAll()
                .where { (Reactions.recipeId eq recipeId) and (Reactions.userId eq userId) }
                .singleOrNull()

            if (existing != null) {
                Reactions
                    .update({ (Reactions.recipeId eq recipeId) and (Reactions.userId eq userId) }) {
                        it[Reactions.type] = type
                    }
            } else {
                Reactions
                    .insertAndGetId {
                        it[Reactions.recipeId] = recipeId
                        it[Reactions.userId] = userId
                        it[Reactions.type] = type
                    }
            }
        }
    }

    fun delete(
        recipeId: Long,
        userId: String
    ) = transaction {
        Reactions
            .deleteWhere {
                (Reactions.recipeId eq recipeId) and (Reactions.userId eq userId)
            }
    }
}