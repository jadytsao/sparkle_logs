package com.sparklelog.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FeelingDao {
    @Query("SELECT * FROM feelings ORDER BY name ASC")
    fun getAll(): Flow<List<Feeling>>

    @Insert
    suspend fun insert(feeling: Feeling): Long

    @Update
    suspend fun update(feeling: Feeling)

    @Query("SELECT * FROM feelings WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): Feeling?

    @Query("SELECT COUNT(*) FROM feelings")
    suspend fun count(): Int

    @Insert
    suspend fun insertAll(feelings: List<Feeling>)

    @Query("DELETE FROM feelings")
    suspend fun deleteAll()
}
