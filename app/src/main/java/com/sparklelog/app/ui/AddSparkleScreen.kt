package com.sparklelog.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sparklelog.app.data.MAX_FEELINGS_PER_SPARKLE
import com.sparklelog.app.ui.components.CelebrationOverlay
import com.sparklelog.app.ui.components.FeelingChip
import com.sparklelog.app.ui.theme.OrganicShapes
import com.sparklelog.app.ui.theme.stickerShadow
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddSparkleScreen(viewModel: SparkleViewModel, modifier: Modifier = Modifier) {
    val feelings by viewModel.feelings.collectAsState()
    val sparkles by viewModel.sparkles.collectAsState()

    var text by remember { mutableStateOf("") }
    var selectedFeelingIds by remember { mutableStateOf(listOf<Long>()) }
    var showNewFeelingField by remember { mutableStateOf(false) }
    var newFeelingText by remember { mutableStateOf("") }
    var newFeelingEmoji by remember { mutableStateOf("") }
    var celebrateTrigger by remember { mutableIntStateOf(0) }
    var celebrationColors by remember { mutableStateOf(emptyList<Color>()) }

    val pendingNewFeeling = showNewFeelingField && newFeelingText.isNotBlank()
    val totalSelectedCount = selectedFeelingIds.size + if (pendingNewFeeling) 1 else 0
    val canSave = text.isNotBlank() && totalSelectedCount > 0
    val saveLabel = if (!canSave && text.isNotBlank()) "Pick a feeling" else "Save this sparkle"

    val todayCount = remember(sparkles) {
        val today = LocalDate.now()
        val zone = ZoneId.systemDefault()
        sparkles.count { Instant.ofEpochMilli(it.sparkle.timestampMillis).atZone(zone).toLocalDate() == today }
    }
    val tallyText = if (todayCount == 0) {
        "Nothing logged yet today — first one is the best one."
    } else {
        "$todayCount ${if (todayCount == 1) "sparkle" else "sparkles"} logged today"
    }

    fun toggleFeeling(id: Long) {
        selectedFeelingIds = when {
            id in selectedFeelingIds -> selectedFeelingIds - id
            selectedFeelingIds.size >= MAX_FEELINGS_PER_SPARKLE -> selectedFeelingIds.drop(1) + id
            else -> selectedFeelingIds + id
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Sparkle Log ✨",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )

            Surface(
                shape = OrganicShapes.large,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .stickerShadow(OrganicShapes.large, offsetColor = MaterialTheme.colorScheme.outline)
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("What sparkled?") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("How did it feel?", style = MaterialTheme.typography.titleMedium)
                Text(
                    if (totalSelectedCount > 0) "$totalSelectedCount of $MAX_FEELINGS_PER_SPARKLE" else "pick up to $MAX_FEELINGS_PER_SPARKLE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                feelings.forEach { feeling ->
                    FeelingChip(
                        name = feeling.name,
                        color = feeling.colorHex.toColor(),
                        emoji = feeling.emoji,
                        selected = feeling.id in selectedFeelingIds,
                        onClick = { toggleFeeling(feeling.id) }
                    )
                }
                FeelingChip(
                    name = "+ new feeling",
                    color = MaterialTheme.colorScheme.secondary,
                    selected = showNewFeelingField,
                    onClick = { showNewFeelingField = !showNewFeelingField }
                )
            }

            if (showNewFeelingField) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newFeelingText,
                        onValueChange = { newFeelingText = it },
                        placeholder = { Text("Name this feeling, e.g. grateful") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = newFeelingEmoji,
                        onValueChange = { newFeelingEmoji = it },
                        placeholder = { Text("🙂") },
                        modifier = Modifier.width(72.dp)
                    )
                }
            }

            Button(
                onClick = {
                    val colorsForCelebration = feelings
                        .filter { it.id in selectedFeelingIds }
                        .map { it.colorHex.toColor() }
                    val newName = if (showNewFeelingField) newFeelingText else null
                    val newEmoji = if (showNewFeelingField) newFeelingEmoji else null
                    viewModel.addSparkle(text, selectedFeelingIds.toSet(), newName, newEmoji) {
                        celebrationColors = colorsForCelebration
                        celebrateTrigger += 1
                    }
                    text = ""
                    selectedFeelingIds = emptyList()
                    newFeelingText = ""
                    newFeelingEmoji = ""
                    showNewFeelingField = false
                },
                enabled = canSave,
                shape = OrganicShapes.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .stickerShadow(OrganicShapes.button, offsetColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
            ) {
                Text(saveLabel, style = MaterialTheme.typography.titleMedium)
            }

            Text(
                tallyText,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }

        CelebrationOverlay(
            trigger = celebrateTrigger,
            feelingColors = celebrationColors,
            modifier = Modifier.fillMaxSize()
        )
    }
}
