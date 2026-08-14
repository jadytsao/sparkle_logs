package com.sparklelog.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sparklelog.app.data.Feeling
import com.sparklelog.app.data.MAX_FEELINGS_PER_SPARKLE
import com.sparklelog.app.data.SparkleWithFeelings
import com.sparklelog.app.ui.toColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditSparkleDialog(
    sparkle: SparkleWithFeelings,
    feelings: List<Feeling>,
    onSave: (text: String, feelingIds: List<Long>) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(sparkle.sparkle.text) }
    var selectedFeelingIds by remember { mutableStateOf(sparkle.feelings.map { it.id }) }

    fun toggle(id: Long) {
        selectedFeelingIds = when {
            id in selectedFeelingIds -> selectedFeelingIds - id
            selectedFeelingIds.size >= MAX_FEELINGS_PER_SPARKLE -> selectedFeelingIds.drop(1) + id
            else -> selectedFeelingIds + id
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit sparkle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth()
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    feelings.forEach { feeling ->
                        FeelingChip(
                            name = feeling.name,
                            color = feeling.colorHex.toColor(),
                            emoji = feeling.emoji,
                            selected = feeling.id in selectedFeelingIds,
                            onClick = { toggle(feeling.id) }
                        )
                    }
                }
                HorizontalDivider()
                TextButton(onClick = onDelete) {
                    Text("Delete this sparkle", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(text, selectedFeelingIds) },
                enabled = text.isNotBlank() && selectedFeelingIds.isNotEmpty()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
