package com.kotlinonly.moprog.database.ratings

import com.kotlinonly.moprog.database.utils.insertWithTimestamps
import com.kotlinonly.moprog.database.utils.updateWithTimestamps
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

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
                    .updateWithTimestamps({ (Ratings.recipeId eq recipeId) and (Ratings.authorId eq authorId) }) {
                        it[Ratings.value] = value
                    }
            } else {
                Ratings
                    .insertWithTimestamps {
                        it[Ratings.recipeId] = recipeId
                        it[Ratings.authorId] = authorId
                        it[Ratings.value] = value
                    }
            }
        }
    }
}