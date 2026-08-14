package com.sparklelog.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "sparkle_feelings",
    primaryKeys = ["sparkleId", "feelingId"],
    foreignKeys = [
        ForeignKey(
            entity = Sparkle::class,
            parentColumns = ["id"],
            childColumns = ["sparkleId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Feeling::class,
            parentColumns = ["id"],
            childColumns = ["feelingId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("feelingId")]
)
data class SparkleFeelingCrossRef(
    val sparkleId: Long,
    val feelingId: Long
)
