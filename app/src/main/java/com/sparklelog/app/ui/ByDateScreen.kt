package com.sparklelog.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.sparklelog.app.data.SparkleWithFeelings
import com.sparklelog.app.ui.components.EditSparkleDialog
import com.sparklelog.app.ui.components.EmptyState
import com.sparklelog.app.ui.components.FeelingChip
import com.sparklelog.app.ui.theme.OrganicShapes
import com.sparklelog.app.ui.theme.stickerShadow
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ByDateScreen(viewModel: SparkleViewModel, modifier: Modifier = Modifier) {
    val sparkles by viewModel.sparkles.collectAsState()
    val feelings by viewModel.feelings.collectAsState()

    var editingSparkle by remember { mutableStateOf<SparkleWithFeelings?>(null) }

    if (sparkles.isEmpty()) {
        EmptyState(modifier)
        return
    }

    val grouped = sparkles.groupBy { it.localDate() }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        grouped.forEach { (date, entries) ->
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 10.dp)
                ) {
                    Text(date.toDisplayLabel(), style = MaterialTheme.typography.titleSmall)
                    Text(
                        "${entries.size} ${if (entries.size == 1) "sparkle" else "sparkles"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(entries) { sparkle ->
                SparkleCard(sparkle, onClick = { editingSparkle = sparkle })
            }
        }
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
private fun SparkleCard(sparkle: SparkleWithFeelings, onClick: () -> Unit) {
    Surface(
        shape = OrganicShapes.medium,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 9.dp)
            .stickerShadow(OrganicShapes.medium, offsetColor = MaterialTheme.colorScheme.outline, offsetY = 3.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    sparkle.sparkle.text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Text(sparkle.time(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                sparkle.feelings.forEach { feeling ->
                    FeelingChip(name = feeling.name, color = feeling.colorHex.toColor(), emoji = feeling.emoji)
                }
            }
        }
    }
}

private fun SparkleWithFeelings.localDate(): LocalDate =
    Instant.ofEpochMilli(sparkle.timestampMillis).atZone(ZoneId.systemDefault()).toLocalDate()

private fun SparkleWithFeelings.time(): String =
    Instant.ofEpochMilli(sparkle.timestampMillis)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("h:mm a"))
