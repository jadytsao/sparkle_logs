package com.sparklelog.app.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

const val MAX_FEELINGS_PER_SPARKLE = 3

class SparkleRepository(
    private val database: AppDatabase,
    private val feelingDao: FeelingDao,
    private val sparkleDao: SparkleDao
) {
    val feelings: Flow<List<Feeling>> = feelingDao.getAll()
    val sparklesWithFeelings: Flow<List<SparkleWithFeelings>> = sparkleDao.getAllWithFeelings()

    suspend fun addSparkle(text: String, feelingIds: List<Long>) {
        sparkleDao.insertSparkleWithFeelings(
            Sparkle(text = text, timestampMillis = System.currentTimeMillis()),
            feelingIds
        )
    }

    suspend fun updateSparkle(id: Long, text: String, feelingIds: List<Long>) {
        sparkleDao.updateSparkleWithFeelings(id, text, feelingIds)
    }

    suspend fun deleteSparkle(id: Long) {
        sparkleDao.delete(id)
    }

    suspend fun findOrCreateFeeling(name: String, emoji: String? = null): Feeling {
        feelingDao.findByName(name)?.let { return it }
        val nextColor = ColorPalette.colorForIndex(feelingDao.count())
        val newFeeling = Feeling(name = name, colorHex = nextColor, emoji = emoji)
        val newId = feelingDao.insert(newFeeling)
        return newFeeling.copy(id = newId)
    }

    suspend fun updateFeeling(feeling: Feeling, colorHex: String, emoji: String?) {
        feelingDao.update(feeling.copy(colorHex = colorHex, emoji = emoji))
    }

    suspend fun exportAllData(): SparkleLogBackup {
        val feelings = feelingDao.getAll().first()
        val sparkles = sparkleDao.getAllWithFeelings().first()
        return SparkleLogBackup(
            exportedAtMillis = System.currentTimeMillis(),
            feelings = feelings.map { BackupFeeling(it.id, it.name, it.colorHex, it.emoji) },
            sparkles = sparkles.map { s ->
                BackupSparkle(
                    id = s.sparkle.id,
                    text = s.sparkle.text,
                    timestampMillis = s.sparkle.timestampMillis,
                    feelingIds = s.feelings.map { it.id }
                )
            }
        )
    }

    suspend fun replaceAllData(backup: SparkleLogBackup) {
        database.withTransaction {
            sparkleDao.deleteAllSparkles()
            feelingDao.deleteAll()
            feelingDao.insertAll(
                backup.feelings.map { Feeling(id = it.id, name = it.name, colorHex = it.colorHex, emoji = it.emoji) }
            )
            sparkleDao.insertAllSparkles(
                backup.sparkles.map { Sparkle(id = it.id, text = it.text, timestampMillis = it.timestampMillis) }
            )
            sparkleDao.insertCrossRefs(
                backup.sparkles.flatMap { s -> s.feelingIds.map { feelingId -> SparkleFeelingCrossRef(s.id, feelingId) } }
            )
        }
    }
}
