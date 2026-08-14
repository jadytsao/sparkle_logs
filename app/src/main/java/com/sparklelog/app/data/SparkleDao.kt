package com.sparklelog.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SparkleDao {
    @Insert
    suspend fun insertSparkle(sparkle: Sparkle): Long

    @Insert
    suspend fun insertCrossRefs(refs: List<SparkleFeelingCrossRef>)

    @Query("UPDATE sparkles SET text = :text WHERE id = :id")
    suspend fun updateText(id: Long, text: String)

    @Query("DELETE FROM sparkle_feelings WHERE sparkleId = :id")
    suspend fun clearFeelingsForSparkle(id: Long)

    @Query("DELETE FROM sparkles WHERE id = :id")
    suspend fun delete(id: Long)

    @Insert
    suspend fun insertAllSparkles(sparkles: List<Sparkle>)

    @Query("DELETE FROM sparkles")
    suspend fun deleteAllSparkles()

    @Transaction
    suspend fun insertSparkleWithFeelings(sparkle: Sparkle, feelingIds: List<Long>): Long {
        val id = insertSparkle(sparkle)
        insertCrossRefs(feelingIds.map { SparkleFeelingCrossRef(id, it) })
        return id
    }

    @Transaction
    suspend fun updateSparkleWithFeelings(id: Long, text: String, feelingIds: List<Long>) {
        updateText(id, text)
        clearFeelingsForSparkle(id)
        insertCrossRefs(feelingIds.map { SparkleFeelingCrossRef(id, it) })
    }

    @Transaction
    @Query("SELECT * FROM sparkles ORDER BY timestampMillis DESC")
    fun getAllWithFeelings(): Flow<List<SparkleWithFeelings>>
}
