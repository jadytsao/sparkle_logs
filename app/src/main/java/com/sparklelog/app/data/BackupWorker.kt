package com.sparklelog.app.data

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sparklelog.app.SparkleLogApplication

/** Daily safety-net backup, in addition to the change-triggered export in [SparkleLogApplication]. */
class BackupWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val app = applicationContext as SparkleLogApplication
        val folderUri = app.backupPreferences.backupFolderUri ?: return Result.success()
        return try {
            BackupManager.exportToUri(app, app.repository, Uri.parse(folderUri))
            app.backupPreferences.lastBackupMillis = System.currentTimeMillis()
            Result.success()
        } catch (t: Throwable) {
            Result.retry()
        }
    }
}
