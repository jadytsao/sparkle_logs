package com.sparklelog.app

import android.app.Application
import com.sparklelog.app.data.AppDatabase
import com.sparklelog.app.data.SparkleRepository

class SparkleLogApplication : Application() {
    val repository: SparkleRepository by lazy {
        val db = AppDatabase.getInstance(this)
        SparkleRepository(db.feelingDao(), db.sparkleDao())
    }
}
