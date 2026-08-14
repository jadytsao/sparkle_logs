package com.sparklelog.app.data

import kotlinx.serialization.Serializable

@Serializable
data class BackupFeeling(
    val id: Long,
    val name: String,
    val colorHex: String,
    val emoji: String? = null
)

@Serializable
data class BackupSparkle(
    val id: Long,
    val text: String,
    val timestampMillis: Long,
    val feelingIds: List<Long>
)

@Serializable
data class SparkleLogBackup(
    val version: Int = 1,
    val exportedAtMillis: Long,
    val feelings: List<BackupFeeling>,
    val sparkles: List<BackupSparkle>
)
