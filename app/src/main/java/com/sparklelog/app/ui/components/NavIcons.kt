package com.sparklelog.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Small custom-drawn nav bar glyphs matching the imported design's geometric icon style. */

@Composable
fun AddNavIcon(tint: Color, modifier: Modifier = Modifier, size: Dp = 22.dp) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = 2.4.dp.toPx()
        drawLine(
            tint,
            Offset(this.size.width / 2, this.size.height * 0.18f),
            Offset(this.size.width / 2, this.size.height * 0.82f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            tint,
            Offset(this.size.width * 0.18f, this.size.height / 2),
            Offset(this.size.width * 0.82f, this.size.height / 2),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun DateNavIcon(tint: Color, modifier: Modifier = Modifier, size: Dp = 22.dp) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = 2.4.dp.toPx()
        val rows = listOf(0.28f to 1f, 0.5f to 1f, 0.72f to 0.64f)
        rows.forEach { (yFraction, widthFraction) ->
            drawLine(
                tint,
                Offset(0f, this.size.height * yFraction),
                Offset(this.size.width * widthFraction, this.size.height * yFraction),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun FeelingNavIcon(tint: Color, modifier: Modifier = Modifier, size: Dp = 22.dp) {
    Canvas(modifier = modifier.size(size)) {
        val cy = this.size.height / 2
        val dots = listOf(0.18f to 3.dp.toPx(), 0.5f to 4.5.dp.toPx(), 0.82f to 3.dp.toPx())
        dots.forEach { (xFraction, radius) ->
            drawCircle(tint, radius = radius, center = Offset(this.size.width * xFraction, cy))
        }
    }
}

@Composable
fun InsightsNavIcon(tint: Color, modifier: Modifier = Modifier, size: Dp = 22.dp) {
    Canvas(modifier = modifier.size(size)) {
        val barWidth = 4.5.dp.toPx()
        val gap = 3.dp.toPx()
        val heights = listOf(9.dp.toPx(), 16.dp.toPx(), 12.dp.toPx())
        val totalWidth = barWidth * heights.size + gap * (heights.size - 1)
        var x = (this.size.width - totalWidth) / 2
        heights.forEach { h ->
            drawRoundRect(
                tint,
                topLeft = Offset(x, this.size.height - h),
                size = Size(barWidth, h),
                cornerRadius = CornerRadius(1.5.dp.toPx())
            )
            x += barWidth + gap
        }
    }
}
