package com.sparklelog.app.data

import android.content.Context

class BackupPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("sparkle_backup", Context.MODE_PRIVATE)

    var backupFolderUri: String?
        get() = prefs.getString(KEY_URI, null)
        set(value) = prefs.edit().putString(KEY_URI, value).apply()

    var lastBackupMillis: Long
        get() = prefs.getLong(KEY_LAST_BACKUP, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_BACKUP, value).apply()

    private companion object {
        const val KEY_URI = "backup_folder_uri"
        const val KEY_LAST_BACKUP = "last_backup_millis"
    }
}
