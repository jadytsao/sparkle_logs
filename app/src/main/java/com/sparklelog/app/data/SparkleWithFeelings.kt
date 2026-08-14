package com.sparklelog.app.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class SparkleWithFeelings(
    @Embedded val sparkle: Sparkle,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            SparkleFeelingCrossRef::class,
            parentColumn = "sparkleId",
            entityColumn = "feelingId"
        )
    )
    val feelings: List<Feeling>
)
