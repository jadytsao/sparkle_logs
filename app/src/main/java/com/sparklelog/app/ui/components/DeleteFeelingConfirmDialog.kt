package com.sparklelog.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sparklelog.app.data.SparkleWithFeelings

/**
 * Two-step confirmation for deleting a feeling that still has sparkles tagged with it.
 * Step 1 warns with a count; step 2 lists the actual sparkles before the final delete.
 */
@Composable
fun DeleteFeelingConfirmDialog(
    feelingName: String,
    attachedSparkles: List<SparkleWithFeelings>,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var confirmedOnce by remember { mutableStateOf(false) }
    val count = attachedSparkles.size
    val sparkleWord = if (count == 1) "sparkle" else "sparkles"

    if (!confirmedOnce) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Delete \"$feelingName\"?") },
            text = {
                Text(
                    "You have $count $sparkleWord attached to this feeling. Deleting the feeling will " +
                        "detach it from ${if (count == 1) "that sparkle" else "these sparkles"}. " +
                        "Are you sure you want to proceed?"
                )
            },
            confirmButton = {
                TextButton(onClick = { confirmedOnce = true }) {
                    Text("Continue", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Confirm deletion") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("These $sparkleWord will lose the \"$feelingName\" tag:")
                    Column(
                        modifier = Modifier
                            .heightIn(max = 240.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        attachedSparkles.forEach { sparkle ->
                            Text("• ${sparkle.sparkle.text}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirmDelete) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        )
    }
}
