package com.kotlinonly.moprog.database.steps

import com.kotlinonly.moprog.database.utils.batchInsertWithTimestamps
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object StepsRepository {
    fun findAllByRecipeId(recipeId: Long) = transaction {
        Steps
            .select(Steps.content)
            .orderBy(Steps.stepNumber)
            .where { Steps.recipeId eq recipeId }
            .map { it[Steps.content] }
    }

    fun saveAll(
        recipeId: Long,
        steps: List<String>
    ) = transaction {
        Steps
            .batchInsertWithTimestamps(steps.withIndex()) { (index, step) ->
                this[Steps.recipeId] = recipeId
                this[Steps.stepNumber] = index + 1
                this[Steps.content] = step
            }
    }
}