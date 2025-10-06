package com.kotlinonly.moprog.database.utils

import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

@Suppress("unused")
abstract class Repository<
        ID: Comparable<ID>,
        E: BaseEntity<ID>,
        D: Any
        >(protected val entityClass: BaseEntityClass<ID, E>) {
    protected abstract fun E.toDomain(): D

    fun findAll(): List<D> = transaction {

    fun save(id: ID, block: E.() -> Unit) = transaction {
        entityClass.new(id, block).toDomain()
    }

        entityClass.all().map { it.toDomain() }
    }

    fun findById(id: ID): D? = transaction {
        entityClass.findById(id)?.toDomain()
    }

    fun delete(id: ID) = transaction {
        entityClass.findById(id)?.delete() != null
    }

    fun save(block: E.() -> Unit) = transaction {
        entityClass.new(block).toDomain()
    }

    fun save(id: ID, block: E.() -> Unit) = transaction {
        entityClass.new(id, block).toDomain()
    }

    fun update(id: ID, block: E.() -> Unit) = transaction {
        entityClass.findById(id)
            ?.apply(block)
            ?.toDomain()
    }

    fun count(): Long = transaction {
        entityClass.count()
    }

    fun existsById(id: ID) = transaction {
        entityClass.findById(id) != null
    }

    fun findPaged(limit: Int, offset: Int = 0) = transaction {
        entityClass.all()
            .limit(limit)
            .offset(offset.toLong())
            .map { it.toDomain() }
    }

    fun exists(where: SqlExpressionBuilder.() -> Op<Boolean>) = transaction {
        !entityClass
            .find(where)
            .empty()
    }
}