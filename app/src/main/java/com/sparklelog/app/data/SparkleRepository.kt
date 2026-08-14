package com.sparklelog.app.data

import kotlinx.coroutines.flow.Flow

const val MAX_FEELINGS_PER_SPARKLE = 3

class SparkleRepository(
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
}
