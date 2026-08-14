package com.sparklelog.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sparklelog.app.data.DayCount
import com.sparklelog.app.data.FeelingMixSlice
import com.sparklelog.app.data.InsightPeriod
import com.sparklelog.app.data.PeriodInsight
import com.sparklelog.app.data.computeFeelingMix
import com.sparklelog.app.data.computeInsights
import com.sparklelog.app.data.computeLast7Days
import com.sparklelog.app.ui.components.EmptyState
import com.sparklelog.app.ui.theme.OrganicShapes
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InsightsScreen(viewModel: SparkleViewModel, onOpenSettings: () -> Unit, modifier: Modifier = Modifier) {
    val sparkles by viewModel.sparkles.collectAsState()
    var selectedPeriod by remember { mutableStateOf(InsightPeriod.TODAY) }

    Box(modifier = modifier.fillMaxSize()) {
        if (sparkles.isEmpty()) {
            EmptyState(Modifier.fillMaxSize(), message = "No sparkles yet — insights will show up here ✨")
        } else {
            val insight = remember(sparkles, selectedPeriod) { computeInsights(sparkles, selectedPeriod) }
            val feelingMix = remember(sparkles, selectedPeriod) { computeFeelingMix(sparkles, selectedPeriod) }
            val last7Days = remember(sparkles) { computeLast7Days(sparkles) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Insights",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    InsightPeriod.entries.forEach { period ->
                        FilterChip(
                            selected = period == selectedPeriod,
                            onClick = { selectedPeriod = period },
                            label = { Text(period.label) }
                        )
                    }
                }

                InsightBubble(period = selectedPeriod, insight = insight)
                BarChartCard(last7Days)
                if (feelingMix.isNotEmpty()) {
                    FeelingMixCard(feelingMix)
                }
            }
        }

        IconButton(onClick = onOpenSettings, modifier = Modifier.align(Alignment.TopEnd)) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings")
        }
    }
}

@Composable
private fun InsightBubble(period: InsightPeriod, insight: PeriodInsight) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "✨ ${period.label} · ${insight.sparkleCount} ${if (insight.sparkleCount == 1) "sparkle" else "sparkles"}",
                style = MaterialTheme.typography.titleMedium
            )

            if (insight.topFeelings.isEmpty()) {
                Text(
                    "Log a few more sparkles in this period to see an insight here.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "Top feelings",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        insight.topFeelings.joinToString(", "),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "Common theme",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        if (insight.commonThemeWords.isNotEmpty()) {
                            insight.commonThemeWords.joinToString(", ")
                        } else {
                            "No common theme found"
                        },
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun BarChartCard(days: List<DayCount>) {
    val barAreaHeight = 64.dp
    val maxCount = (days.maxOfOrNull { it.count } ?: 1).coerceAtLeast(1)
    val today = LocalDate.now()

    Surface(shape = OrganicShapes.medium, color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "LAST 7 DAYS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp)
            ) {
                days.forEach { day ->
                    val isToday = day.date == today
                    val fraction = (day.count.toFloat() / maxCount).coerceIn(0.06f, 1f)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            if (day.count > 0) "${day.count}" else "·",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .fillMaxWidth()
                                .height(barAreaHeight),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.55f)
                                    .height(barAreaHeight * fraction)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isToday) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    )
                            )
                        }
                        Text(
                            if (isToday) "•" else day.date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isToday) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeelingMixCard(mix: List<FeelingMixSlice>) {
    Surface(shape = OrganicShapes.medium, color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "YOUR FEELING MIX",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(50)),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                mix.forEach { slice ->
                    Box(
                        modifier = Modifier
                            .weight(slice.fraction.coerceAtLeast(0.01f))
                            .fillMaxHeight()
                            .background(slice.colorHex.toColor())
                    )
                }
            }
            Column(
                modifier = Modifier.padding(top = 14.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                mix.forEach { slice ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(slice.colorHex.toColor())
                        )
                        Text(slice.name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text(
                            "${(slice.fraction * 100).roundToInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private val InsightPeriod.label: String
    get() = when (this) {
        InsightPeriod.TODAY -> "Today"
        InsightPeriod.LAST_7_DAYS -> "7 Days"
        InsightPeriod.LAST_30_DAYS -> "30 Days"
        InsightPeriod.ALL_TIME -> "All Time"
    }
