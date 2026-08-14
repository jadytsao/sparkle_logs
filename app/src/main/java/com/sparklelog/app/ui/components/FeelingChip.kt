package com.sparklelog.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FeelingChip(
    name: String,
    color: Color,
    emoji: String? = null,
    selected: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val backgroundColor = if (selected) color else color.copy(alpha = 0.18f)
    val textColor = if (selected) Color.White else color
    val clickableModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier
    val label = if (emoji.isNullOrBlank()) name else "$emoji $name"

    Surface(
        color = backgroundColor,
        contentColor = textColor,
        shape = RoundedCornerShape(50),
        border = if (selected) null else BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = clickableModifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
