package com.sparklelog.app.ui.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

private val CELEBRATE_LINES = listOf(
    "That one is yours to keep.",
    "Your week just got brighter.",
    "Small things, stacking up.",
    "Noted, and kept."
)

private data class ConfettiPiece(
    val xFraction: Float,
    val sizeSp: Float,
    val delayMs: Int,
    val color: Color
)

/**
 * Brief full-screen celebration shown after saving a sparkle: a pulsing accent glyph,
 * floating confetti, and a random encouraging line. Auto-dismisses after 0.7s.
 * Retriggers whenever [trigger] changes to a non-zero value.
 */
@Composable
fun CelebrationOverlay(trigger: Int, feelingColors: List<Color>, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    var confetti by remember { mutableStateOf(emptyList<ConfettiPiece>()) }
    var line by remember { mutableStateOf(CELEBRATE_LINES.first()) }
    val fallbackColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(trigger) {
        if (trigger == 0) return@LaunchedEffect
        val palette = feelingColors.ifEmpty { listOf(fallbackColor) }
        confetti = List(14) {
            ConfettiPiece(
                xFraction = Random.nextFloat(),
                sizeSp = 12f + Random.nextFloat() * 16f,
                delayMs = (Random.nextFloat() * 300).toInt(),
                color = palette[Random.nextInt(palette.size)]
            )
        }
        line = CELEBRATE_LINES.random()
        visible = true
        delay(700)
        visible = false
    }

    if (!visible) return

    Surface(
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.94f),
        modifier = modifier.fillMaxSize()
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            confetti.forEach { piece -> ConfettiGlyph(piece) }

            val scale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "celebrationPop"
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "✦",
                    fontSize = 52.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.scale(scale)
                )
                Text("Logged it.", style = MaterialTheme.typography.titleMedium)
                Text(line, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun ConfettiGlyph(piece: ConfettiPiece) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(piece) { started = true }
    val progress by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = piece.delayMs, easing = LinearOutSlowInEasing),
        label = "confetti"
    )
    Text(
        "✦",
        fontSize = piece.sizeSp.sp,
        color = piece.color.copy(alpha = (1f - progress).coerceIn(0f, 1f)),
        modifier = Modifier.offset(
            x = 130.dp * (piece.xFraction - 0.5f),
            y = 90.dp - 150.dp * progress
        )
    )
}
