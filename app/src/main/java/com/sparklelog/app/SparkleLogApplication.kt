package com.sparklelog.app

import android.app.Application
import android.net.Uri
import androidx.glance.appwidget.updateAll
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.sparklelog.app.data.AppDatabase
import com.sparklelog.app.data.BackupManager
import com.sparklelog.app.data.BackupPreferences
import com.sparklelog.app.data.BackupWorker
import com.sparklelog.app.data.SparkleRepository
import com.sparklelog.app.widget.TodayInsightWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SparkleLogApplication : Application() {
    val repository: SparkleRepository by lazy {
        val db = AppDatabase.getInstance(this)
        SparkleRepository(db, db.feelingDao(), db.sparkleDao())
    }

    val backupPreferences: BackupPreferences by lazy { BackupPreferences(this) }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @OptIn(FlowPreview::class)
    override fun onCreate() {
        super.onCreate()

        scheduleDailyBackup()

        applicationScope.launch {
            repository.sparklesWithFeelings.debounce(3000).collect {
                val uriString = backupPreferences.backupFolderUri ?: return@collect
                BackupManager.exportToUri(this@SparkleLogApplication, repository, Uri.parse(uriString))
                backupPreferences.lastBackupMillis = System.currentTimeMillis()
            }
        }

        applicationScope.launch {
            repository.sparklesWithFeelings.collect {
                TodayInsightWidget().updateAll(this@SparkleLogApplication)
            }
        }
    }

    private fun scheduleDailyBackup() {
        val now = Calendar.getInstance()
        val next2am = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 2)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_MONTH, 1)
        }
        val initialDelay = next2am.timeInMillis - now.timeInMillis

        val request = PeriodicWorkRequestBuilder<BackupWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_backup",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
