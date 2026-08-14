package com.sparklelog.app.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.sparklelog.app.SparkleLogApplication
import com.sparklelog.app.data.BackupManager
import com.sparklelog.app.ui.theme.OrganicShapes
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as SparkleLogApplication
    val scope = rememberCoroutineScope()

    var folderUri by remember { mutableStateOf(app.backupPreferences.backupFolderUri) }
    var lastBackupMillis by remember { mutableStateOf(app.backupPreferences.lastBackupMillis) }
    var showImportConfirm by remember { mutableStateOf<Uri?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        app.backupPreferences.backupFolderUri = uri.toString()
        folderUri = uri.toString()
        scope.launch {
            BackupManager.exportToUri(context, app.repository, uri)
            app.backupPreferences.lastBackupMillis = System.currentTimeMillis()
            lastBackupMillis = app.backupPreferences.lastBackupMillis
            statusMessage = "Backup folder set — first backup saved."
        }
    }

    val importPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) showImportConfirm = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Back") }

        Text("Settings", style = MaterialTheme.typography.titleLarge)
        Text("Backup & Export", style = MaterialTheme.typography.titleMedium)

        Surface(shape = OrganicShapes.medium, color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Backup folder",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    folderUri?.let { folderDisplayName(context, it) }
                        ?: "Not set — pick a folder (can be inside Google Drive) for automatic backups",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (lastBackupMillis > 0) {
                    Text(
                        "Last backup: ${DateFormat.getDateTimeInstance().format(Date(lastBackupMillis))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(onClick = { folderPicker.launch(null) }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (folderUri == null) "Choose backup folder" else "Change backup folder")
                }
            }
        }

        Surface(shape = OrganicShapes.medium, color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            val uri = BackupManager.exportToShareableFile(context, app.repository)
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/json"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, "Export Sparkle Log"))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export now")
                }
                OutlinedButton(
                    onClick = { importPicker.launch(arrayOf("application/json")) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Import from file")
                }
            }
        }

        statusMessage?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    showImportConfirm?.let { uri ->
        AlertDialog(
            onDismissRequest = { showImportConfirm = null },
            title = { Text("Replace all data?") },
            text = {
                Text("Importing will replace all current sparkles and feelings with the contents of this file. This can't be undone.")
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        val result = BackupManager.importFromUri(context, app.repository, uri)
                        statusMessage = if (result.isSuccess) {
                            "Import complete."
                        } else {
                            "Import failed: ${result.exceptionOrNull()?.message}"
                        }
                        showImportConfirm = null
                    }
                }) {
                    Text("Replace", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportConfirm = null }) { Text("Cancel") }
            }
        )
    }
}

private fun folderDisplayName(context: Context, uriString: String): String =
    try {
        val uri = Uri.parse(uriString)
        DocumentFile.fromTreeUri(context, uri)?.name ?: uriString
    } catch (t: Throwable) {
        uriString
    }
