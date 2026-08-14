package com.sparklelog.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feelings")
data class Feeling(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String,
    val emoji: String? = null
)