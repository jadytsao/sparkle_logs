package com.sparklelog.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sparkles")
data class Sparkle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val timestampMillis: Long
)
