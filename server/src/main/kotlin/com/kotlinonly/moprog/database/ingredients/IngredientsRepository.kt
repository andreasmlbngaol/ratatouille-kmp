package com.kotlinonly.moprog.database.ingredients

import com.kotlinonly.moprog.database.ingredient_tags.IngredientTags
import com.kotlinonly.moprog.database.utils.insertWithTimestamps
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object IngredientsRepository {
    fun findAllByRecipeId(recipeId: Long) = transaction {
        (Ingredients innerJoin IngredientTags)
            .selectAll()
            .where { Ingredients.recipeId eq recipeId }
            .orderBy(Ingredients.id)
            .map { it.toIngredient() }
    }

//    fun saveAll(
//        recipeId: Long,
//        ingredients: List<IngredientRequest>
//    ) = transaction {
//        Ingredients
//            .batchInsertWithTimestamps(ingredients) { ingredient ->
//                this[Ingredients.recipeId] = recipeId
//                this[Ingredients.tagId] = ingredient.tagId
//                this[Ingredients.alternative] = ingredient.alternative
//                this[Ingredients.amount] = ingredient.amount
//                this[Ingredients.unit] = ingredient.unit
//            }
//    }

    fun save(
        recipeId: Long,
        tagId: Long,
        alternative: String?,
        amount: Double?,
        unit: String?
    ) = transaction {
        Ingredients
            .insertWithTimestamps {
                it[Ingredients.recipeId] = recipeId
                it[Ingredients.tagId] = tagId
                it[Ingredients.alternative] = alternative
                it[Ingredients.amount] = amount
                it[Ingredients.unit] = unit
            }
    }
}