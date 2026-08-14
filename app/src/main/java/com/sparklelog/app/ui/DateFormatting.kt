package com.sparklelog.app.ui

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun LocalDate.toDisplayLabel(): String {
    val today = LocalDate.now()
    return when (this) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> format(DateTimeFormatter.ofPattern("EEEE, MMM d"))
    }
}
