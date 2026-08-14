package com.sparklelog.app.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import kotlinx.serialization.json.Json
import java.io.File

private const val BACKUP_FILE_NAME = "sparkle_log_backup.json"
private const val FILE_PROVIDER_AUTHORITY = "com.sparklelog.app.fileprovider"

object BackupManager {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    /** Writes/overwrites a single rolling backup file inside a previously-granted SAF tree. */
    suspend fun exportToUri(context: Context, repository: SparkleRepository, treeUri: Uri) {
        val backup = repository.exportAllData()
        val text = json.encodeToString(SparkleLogBackup.serializer(), backup)
        val dir = DocumentFile.fromTreeUri(context, treeUri) ?: return
        dir.findFile(BACKUP_FILE_NAME)?.delete()
        val file = dir.createFile("application/json", BACKUP_FILE_NAME) ?: return
        context.contentResolver.openOutputStream(file.uri)?.use { it.write(text.toByteArray()) }
    }

    /** Writes a backup to the app's cache dir and returns a shareable content:// URI. */
    suspend fun exportToShareableFile(context: Context, repository: SparkleRepository): Uri {
        val backup = repository.exportAllData()
        val text = json.encodeToString(SparkleLogBackup.serializer(), backup)
        val file = File(context.cacheDir, BACKUP_FILE_NAME)
        file.writeText(text)
        return FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file)
    }

    suspend fun importFromUri(context: Context, repository: SparkleRepository, uri: Uri): Result<Unit> = runCatching {
        val text = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            ?: error("Could not read the selected file")
        val backup = json.decodeFromString(SparkleLogBackup.serializer(), text)
        repository.replaceAllData(backup)
    }
}
