package com.sparklelog.app.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sparklelog.app.SparkleLogApplication
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneId

/** Daily ~8pm nudge to log a sparkle — skipped if one's already been logged since 6pm today. */
class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val app = applicationContext as SparkleLogApplication
        val sparkles = app.repository.sparklesWithFeelings.first()
        val zone = ZoneId.systemDefault()
        val sixPmTodayMillis = LocalDate.now(zone).atTime(18, 0).atZone(zone).toInstant().toEpochMilli()

        val loggedSince6pm = sparkles.any { it.sparkle.timestampMillis >= sixPmTodayMillis }
        if (!loggedSince6pm) {
            ReminderNotifier.showReminder(app)
        }
        return Result.success()
    }
}
