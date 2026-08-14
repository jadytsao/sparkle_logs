package com.sparklelog.app.ui.theme

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Reproduces CSS's independent per-corner horizontal/vertical border-radius
 * (e.g. `30px 24px 28px 22px/24px 30px 22px 28px`), which Compose's
 * RoundedCornerShape can't express — it only supports one radius per corner,
 * not separate x/y ellipse radii. Each corner is approximated with a cubic
 * bezier using the standard circular-arc constant, scaled independently per axis.
 */
class OrganicShape(
    private val topLeftH: Dp, private val topLeftV: Dp,
    private val topRightH: Dp, private val topRightV: Dp,
    private val bottomRightH: Dp, private val bottomRightV: Dp,
    private val bottomLeftH: Dp, private val bottomLeftV: Dp
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val k = 0.5522847f
        val w = size.width
        val h = size.height

        with(density) {
            val tlH = topLeftH.toPx(); val tlV = topLeftV.toPx()
            val trH = topRightH.toPx(); val trV = topRightV.toPx()
            val brH = bottomRightH.toPx(); val brV = bottomRightV.toPx()
            val blH = bottomLeftH.toPx(); val blV = bottomLeftV.toPx()

            val path = Path().apply {
                moveTo(tlH, 0f)
                lineTo(w - trH, 0f)
                cubicTo(
                    w - trH + trH * k, 0f,
                    w, trV - trV * k,
                    w, trV
                )
                lineTo(w, h - brV)
                cubicTo(
                    w, h - brV + brV * k,
                    w - brH + brH * k, h,
                    w - brH, h
                )
                lineTo(blH, h)
                cubicTo(
                    blH - blH * k, h,
                    0f, h - blV + blV * k,
                    0f, h - blV
                )
                lineTo(0f, tlV)
                cubicTo(
                    0f, tlV - tlV * k,
                    tlH - tlH * k, 0f,
                    tlH, 0f
                )
                close()
            }
            return Outline.Generic(path)
        }
    }
}

/** Named presets matching the recurring corner patterns used throughout the design. */
object OrganicShapes {
    val large = OrganicShape(30.dp, 24.dp, 24.dp, 30.dp, 28.dp, 22.dp, 22.dp, 28.dp)
    val medium = OrganicShape(24.dp, 19.dp, 19.dp, 24.dp, 22.dp, 18.dp, 18.dp, 22.dp)
    val small = OrganicShape(19.dp, 14.dp, 14.dp, 19.dp, 17.dp, 13.dp, 13.dp, 17.dp)
    val button = OrganicShape(26.dp, 20.dp, 20.dp, 26.dp, 24.dp, 19.dp, 19.dp, 24.dp)
    val field = OrganicShape(20.dp, 15.dp, 15.dp, 20.dp, 18.dp, 14.dp, 14.dp, 18.dp)
}
