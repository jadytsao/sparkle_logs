package com.sparklelog.app.data

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

enum class InsightPeriod { TODAY, LAST_7_DAYS, LAST_30_DAYS, ALL_TIME }

data class PeriodInsight(
    val sparkleCount: Int,
    val topFeelings: List<String>,
    val commonThemeWords: List<String>
)

data class FeelingMixSlice(
    val name: String,
    val colorHex: String,
    val fraction: Float
)

data class DayCount(
    val date: LocalDate,
    val count: Int
)

private const val MIN_SPARKLES_FOR_INSIGHT = 3

private val STOPWORDS = setOf(
    "the", "a", "an", "and", "or", "but", "with", "for", "of", "to", "in", "on", "at", "is", "was", "were",
    "are", "am", "be", "been", "being", "i", "me", "my", "mine", "it", "its", "that", "this", "these", "those",
    "he", "she", "they", "them", "we", "us", "you", "your", "his", "her", "their", "our",
    "today", "felt", "feel", "feeling", "feelings", "really", "very", "so", "just", "got", "get", "had",
    "have", "has", "did", "do", "does", "went", "go", "going", "up", "out", "about", "some", "all", "not",
    "no", "when", "because", "after", "before", "during", "while", "then", "than", "too", "also"
)

private fun SparkleWithFeelings.localDate(zone: ZoneId): LocalDate =
    Instant.ofEpochMilli(sparkle.timestampMillis).atZone(zone).toLocalDate()

private fun extractKeywords(text: String): Set<String> =
    Regex("[A-Za-z']+").findAll(text.lowercase())
        .map { it.value }
        .filter { it.length > 2 && it !in STOPWORDS }
        .toSet()

private fun topFeelingNames(entries: List<SparkleWithFeelings>, limit: Int = 3): List<String> =
    entries.flatMap { it.feelings }
        .groupBy { it.name }
        .entries.sortedByDescending { it.value.size }
        .take(limit)
        .map { it.key }

private fun commonThemeWords(entries: List<SparkleWithFeelings>): List<String> {
    val wordToEntryCount = mutableMapOf<String, Int>()
    entries.forEach { entry ->
        extractKeywords(entry.sparkle.text).forEach { word ->
            wordToEntryCount[word] = (wordToEntryCount[word] ?: 0) + 1
        }
    }
    return wordToEntryCount.entries
        .filter { it.value >= 2 }
        .sortedByDescending { it.value }
        .take(3)
        .map { it.key }
}

fun computeInsights(
    sparkles: List<SparkleWithFeelings>,
    period: InsightPeriod,
    today: LocalDate = LocalDate.now()
): PeriodInsight {
    val zone = ZoneId.systemDefault()

    val periodStart = when (period) {
        InsightPeriod.TODAY -> today
        InsightPeriod.LAST_7_DAYS -> today.minusDays(6)
        InsightPeriod.LAST_30_DAYS -> today.minusDays(29)
        InsightPeriod.ALL_TIME -> LocalDate.MIN
    }
    val periodSparkles = sparkles.filter {
        val date = it.localDate(zone)
        date >= periodStart && date <= today
    }

    val hasEnoughForInsight = periodSparkles.size >= MIN_SPARKLES_FOR_INSIGHT

    return PeriodInsight(
        sparkleCount = periodSparkles.size,
        topFeelings = if (hasEnoughForInsight) topFeelingNames(periodSparkles) else emptyList(),
        commonThemeWords = if (hasEnoughForInsight) commonThemeWords(periodSparkles) else emptyList()
    )
}

/** Proportional breakdown of feelings within the selected period, for a mix bar. Top 5, by share. */
fun computeFeelingMix(
    sparkles: List<SparkleWithFeelings>,
    period: InsightPeriod,
    today: LocalDate = LocalDate.now()
): List<FeelingMixSlice> {
    val zone = ZoneId.systemDefault()
    val periodStart = when (period) {
        InsightPeriod.TODAY -> today
        InsightPeriod.LAST_7_DAYS -> today.minusDays(6)
        InsightPeriod.LAST_30_DAYS -> today.minusDays(29)
        InsightPeriod.ALL_TIME -> LocalDate.MIN
    }
    val periodFeelings = sparkles
        .filter { val date = it.localDate(zone); date >= periodStart && date <= today }
        .flatMap { it.feelings }

    val total = periodFeelings.size
    if (total == 0) return emptyList()

    return periodFeelings.groupBy { it.name }
        .entries.sortedByDescending { it.value.size }
        .take(5)
        .map { (name, entries) ->
            FeelingMixSlice(
                name = name,
                colorHex = entries.first().colorHex,
                fraction = entries.size.toFloat() / total
            )
        }
}

/** Sparkle counts for the trailing 7 days (including today), oldest first — for a fixed bar chart. */
fun computeLast7Days(
    sparkles: List<SparkleWithFeelings>,
    today: LocalDate = LocalDate.now()
): List<DayCount> {
    val zone = ZoneId.systemDefault()
    val countsByDate = sparkles.groupingBy { it.localDate(zone) }.eachCount()
    return (6 downTo 0).map { offset ->
        val date = today.minusDays(offset.toLong())
        DayCount(date = date, count = countsByDate[date] ?: 0)
    }
}
