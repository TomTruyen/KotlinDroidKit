package com.tomtruyen.kotlindroidkit.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BaseDao<T: BaseEntity>(private val tableName: String) {
    open val idColumnName = "id"

    @Upsert
    abstract fun upsert(entity: T): Long

    @Upsert
    abstract fun upsertMany(entities: List<T>): List<Long>

    @Delete
    abstract fun delete(entity: T)

    @Delete
    abstract fun deleteMany(entities: List<T>)

    @RawQuery
    abstract fun <R> rawQuery(query: SimpleSQLiteQuery): R

    fun findAll(): List<T> {
        val query = SimpleSQLiteQuery("SELECT * FROM $tableName")

        return rawQuery(query)
    }

    fun findAllAsync(): Flow<List<T>> {
        val query = SimpleSQLiteQuery("SELECT * FROM $tableName")

        return rawQuery(query)
    }

    fun findById(id: Any): T? {
        val query = SimpleSQLiteQuery("SELECT * FROM $tableName WHERE $idColumnName = \'$id\'")

        return rawQuery(query)
    }

    fun findByIdAsync(id: Any): Flow<T?> {
        val query = SimpleSQLiteQuery("SELECT * FROM $tableName WHERE $idColumnName = \'$id\'")

        return rawQuery(query)
    }

    fun upsertManyDeleteOthers(entities: List<T>, excludedIds: List<Any> = emptyList()): List<Long> {
        val entityIds = entities.map { it.id } + excludedIds
        val ids = entityIds.joinToString { "\'$it\'" }

        val query = SimpleSQLiteQuery("DELETE FROM $tableName WHERE $idColumnName NOT IN ($ids)")

        rawQuery<Unit>(query)

        return upsertMany(entities)
    }

    fun deleteAll() {
        val query = SimpleSQLiteQuery("DELETE FROM $tableName")

        return rawQuery(query)
    }
}