package com.kotlinonly.moprog.core.database.ratings

import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

object RatingsRepository {
    fun findAllByRecipeId(recipeId: Long) = transaction {
        Ratings
            .select(Ratings.value)
            .where { Ratings.recipeId eq recipeId }
            .map { it[Ratings.value] }
    }

    fun findByRecipeIdAndAuthorId(
        recipeId: Long,
        authorId: String
    ) = transaction {
        Ratings
            .select(Ratings.value)
            .where { (Ratings.recipeId eq recipeId) and (Ratings.authorId eq authorId) }
            .map { it[Ratings.value] }
            .firstOrNull()
    }

    fun save(
        recipeId: Long,
        authorId: String,
        value: Double
    ) {
        transaction {
            val existing = Ratings
                .selectAll()
                .where { (Ratings.recipeId eq recipeId) and (Ratings.authorId eq authorId) }
                .singleOrNull()

            if(existing != null) {
                Ratings
                    .update({ (Ratings.recipeId eq recipeId) and (Ratings.authorId eq authorId) }) {
                        it[Ratings.value] = value
                    }
            } else {
                Ratings
                    .insertAndGetId {
                        it[Ratings.recipeId] = recipeId
                        it[Ratings.authorId] = authorId
                        it[Ratings.value] = value
                    }
            }
        }
    }
}