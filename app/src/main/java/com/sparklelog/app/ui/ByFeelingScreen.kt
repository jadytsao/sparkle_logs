package com.sparklelog.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sparklelog.app.data.Feeling
import com.sparklelog.app.data.SparkleWithFeelings
import com.sparklelog.app.ui.components.EditFeelingDialog
import com.sparklelog.app.ui.components.EditSparkleDialog
import com.sparklelog.app.ui.components.EmptyState
import com.sparklelog.app.ui.theme.OrganicShapes
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ByFeelingScreen(viewModel: SparkleViewModel, modifier: Modifier = Modifier) {
    val feelings by viewModel.feelings.collectAsState()
    val sparkles by viewModel.sparkles.collectAsState()

    var editingFeeling by remember { mutableStateOf<Feeling?>(null) }
    var editingSparkle by remember { mutableStateOf<SparkleWithFeelings?>(null) }
    var expandedFeelingId by remember { mutableStateOf<Long?>(null) }

    if (feelings.isEmpty()) {
        EmptyState(modifier)
        return
    }

    val sparklesByFeelingId = remember(sparkles) {
        val map = mutableMapOf<Long, MutableList<SparkleWithFeelings>>()
        sparkles.forEach { s -> s.feelings.forEach { f -> map.getOrPut(f.id) { mutableListOf() }.add(s) } }
        map
    }
    val countsById = sparklesByFeelingId.mapValues { it.value.size }
    val maxCount = (countsById.values.maxOrNull() ?: 1).coerceAtLeast(1)
    val activeFeelings = feelings
        .filter { (countsById[it.id] ?: 0) > 0 }
        .sortedByDescending { countsById[it.id] ?: 0 }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        activeFeelings.forEach { feeling ->
            val entries = sparklesByFeelingId[feeling.id].orEmpty()
            val count = entries.size
            val expanded = expandedFeelingId == feeling.id

            item {
                FeelingGroupHeader(
                    feeling = feeling,
                    count = count,
                    barFraction = count.toFloat() / maxCount,
                    onToggle = { expandedFeelingId = if (expanded) null else feeling.id },
                    onEditColor = { editingFeeling = feeling }
                )
            }
            if (expanded) {
                items(entries) { sparkle ->
                    NestedSparkleRow(sparkle, onClick = { editingSparkle = sparkle })
                }
            }
        }
    }

    editingFeeling?.let { feeling ->
        EditFeelingDialog(
            currentColorHex = feeling.colorHex,
            currentEmoji = feeling.emoji,
            onSave = { colorHex, emoji ->
                viewModel.updateFeeling(feeling, colorHex, emoji)
                editingFeeling = null
            },
            onDismiss = { editingFeeling = null }
        )
    }

    editingSparkle?.let { sparkle ->
        EditSparkleDialog(
            sparkle = sparkle,
            feelings = feelings,
            onSave = { text, feelingIds ->
                viewModel.updateSparkle(sparkle.sparkle.id, text, feelingIds)
                editingSparkle = null
            },
            onDelete = {
                viewModel.deleteSparkle(sparkle.sparkle.id)
                editingSparkle = null
            },
            onDismiss = { editingSparkle = null }
        )
    }
}

@Composable
private fun FeelingGroupHeader(
    feeling: Feeling,
    count: Int,
    barFraction: Float,
    onToggle: () -> Unit,
    onEditColor: () -> Unit
) {
    val color = feeling.colorHex.toColor()
    Surface(
        shape = OrganicShapes.medium,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable(onClick = onToggle)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable(onClick = onEditColor),
                contentAlignment = Alignment.Center
            ) {
                Text(feeling.emoji ?: "✦", fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(feeling.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    if (count == 1) "once" else "$count times",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color.copy(alpha = 0.15f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(barFraction.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(50))
                        .background(color)
                )
            }
            Text(
                "$count",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NestedSparkleRow(sparkle: SparkleWithFeelings, onClick: () -> Unit) {
    Surface(
        shape = OrganicShapes.small,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 46.dp, bottom = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(10.dp)) {
            Text(sparkle.sparkle.text, style = MaterialTheme.typography.bodyMedium)
            Text(
                sparkle.displayDate(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun SparkleWithFeelings.displayDate(): String =
    Instant.ofEpochMilli(sparkle.timestampMillis)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
