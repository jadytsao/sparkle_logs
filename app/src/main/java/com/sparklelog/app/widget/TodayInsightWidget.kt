package com.sparklelog.app.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.sparklelog.app.MainActivity
import com.sparklelog.app.SparkleLogApplication
import com.sparklelog.app.data.InsightPeriod
import com.sparklelog.app.data.PeriodInsight
import com.sparklelog.app.data.computeInsights
import kotlinx.coroutines.flow.first

class TodayInsightWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val app = context.applicationContext as SparkleLogApplication
        val sparkles = app.repository.sparklesWithFeelings.first()
        val insight = computeInsights(sparkles, InsightPeriod.TODAY)

        provideContent { Content(insight) }
    }

    @Composable
    private fun Content(insight: PeriodInsight) {
        val context = LocalContext.current
        val bgColor = Color(0xFF2C2620)
        val labelColor = Color(0xFF9E8F79)
        val mainColor = Color(0xFFFBF6EC)
        val themeColor = Color(0xFFCBBFA8)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(androidx.glance.color.ColorProvider(day = bgColor, night = bgColor))
                .cornerRadius(24.dp)
                .padding(16.dp)
                .clickable(actionStartActivity(
                    Intent(context, MainActivity::class.java).apply {
                        putExtra("NAV_DESTINATION", "insights")
                        data = Uri.parse("sparkle://insights")
                    }
                )),
        ) {
            Text(
                "TODAY'S SPARKLES",
                style = TextStyle(
                    fontSize = 11.sp, 
                    color = androidx.glance.color.ColorProvider(day = labelColor, night = labelColor)
                )
            )
            Text(
                "Top feelings:",
                style = TextStyle(
                    fontSize = 15.sp,
                    color = androidx.glance.color.ColorProvider(day = themeColor, night = themeColor)
                ),
                modifier = GlanceModifier.padding(top = 4.dp)
            )
            Text(
                if (insight.topFeelings.isEmpty()) {
                    "No sparkles yet today — go find one ✨"
                } else {
                    insight.topFeelings.joinToString(", ")
                },
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.glance.color.ColorProvider(day = mainColor, night = mainColor)
                ),
                modifier = GlanceModifier.padding(top = 6.dp)
            )
            if (insight.commonThemeWords.isNotEmpty()) {
                Text(
                    "Theme:",
                    style = TextStyle(
                        fontSize = 15.sp,
                        color = androidx.glance.color.ColorProvider(day = themeColor, night = themeColor)
                    ),
                    modifier = GlanceModifier.padding(top = 4.dp)
                )
                Text(
                    insight.commonThemeWords.joinToString(", "),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.glance.color.ColorProvider(day = mainColor, night = mainColor)
                    ),
                    modifier = GlanceModifier.padding(top = 6.dp)
                )
            }
        }
    }
}
