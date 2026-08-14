package com.sparklelog.app.data

object ColorPalette {
    val presetColors: List<String> = listOf(
        "#F7B32B", "#D97845", "#6F9C93", "#DE7E96",
        "#8E82CC", "#5F8FC6", "#DB6552", "#CE7EBE",
        "#B07E5F", "#7FA85F", "#C97E1F", "#4F8A8B",
        "#A85751", "#6B7FBD", "#B98F4D", "#7E9C6B",
        "#C46D8E", "#7A6BA8", "#5D8A7A", "#B4794A"
    )

    fun colorForIndex(index: Int): String = presetColors[index % presetColors.size]
}
