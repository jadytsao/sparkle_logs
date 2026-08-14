package com.sparklelog.app.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Two-layer "paper sticker" shadow: a hard-edged solid offset copy of the shape
 * (the design's `0 4px 0 color` layer) plus a soft ambient ombre shadow.
 */
fun Modifier.stickerShadow(
    shape: Shape,
    offsetColor: Color,
    offsetY: Dp = 4.dp,
    ambientElevation: Dp = 10.dp
): Modifier = this
    .shadow(
        elevation = ambientElevation,
        shape = shape,
        clip = false,
        ambientColor = Color.Black.copy(alpha = 0.12f),
        spotColor = Color.Black.copy(alpha = 0.16f)
    )
    .drawBehind {
        val outline = shape.createOutline(size, layoutDirection, this)
        translate(top = offsetY.toPx()) {
            drawOutline(outline, color = offsetColor)
        }
    }
