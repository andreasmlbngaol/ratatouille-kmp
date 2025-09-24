package com.kotlinonly.moprog.database.steps

import com.kotlinonly.moprog.data.ingredient.StepRequest
import com.kotlinonly.moprog.database.utils.insertWithTimestamps
import com.kotlinonly.moprog.database.utils.updateWithTimestamps
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object StepsRepository {
    fun createEmptyStep(
        recipeId: Long,
        stepNumber: Int
    ) = transaction {
        Steps
            .insertWithTimestamps {
                it[Steps.recipeId] = recipeId
                it[Steps.stepNumber] = stepNumber
                it[Steps.content] = ""
            }
    }
    fun findAllByRecipeId(recipeId: Long) = transaction {
        Steps
            .selectAll()
            .where { Steps.recipeId eq recipeId }
            .orderBy(Steps.stepNumber)
            .map { it.toStep() }
    }

    fun saveAll(
        steps: List<StepRequest>
    ) = transaction {
        steps.forEach { step ->
            Steps.updateWithTimestamps({ Steps.id eq step.id }) {
                it[Steps.content] = step.content
            }
        }
    }

    fun delete(id: Long) = transaction {
        Steps
            .deleteWhere { Steps.id eq id }
    }
}