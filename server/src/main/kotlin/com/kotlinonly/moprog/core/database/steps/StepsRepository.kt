package com.kotlinonly.moprog.core.database.steps

import org.jetbrains.exposed.v1.jdbc.batchInsert
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
            .batchInsert(steps.withIndex()) { (index, step) ->
                this[Steps.recipeId] = recipeId
                this[Steps.stepNumber] = index + 1
                this[Steps.content] = step
            }
    }
}