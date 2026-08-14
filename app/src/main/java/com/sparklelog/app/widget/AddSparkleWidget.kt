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
import androidx.glance.LocalSize
import androidx.glance.appwidget.SizeMode
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.sparklelog.app.MainActivity

class AddSparkleWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { Content() }
    }

    @Composable
    private fun Content() {
        val context = LocalContext.current
        val size = LocalSize.current
        val bgColor = Color(0xFFF7B32B)
        val textColor = Color.White
        
        val isSmall = size.width < 100.dp || size.height < 100.dp

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(androidx.glance.color.ColorProvider(day = bgColor, night = bgColor))
                .cornerRadius(if (isSmall) 50.dp else 24.dp)
                .padding(if (isSmall) 4.dp else 12.dp)
                .clickable(actionStartActivity(
                    Intent(context, MainActivity::class.java).apply {
                        putExtra("NAV_DESTINATION", "add")
                        data = Uri.parse("sparkle://add")
                    }
                )),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "✦",
                style = TextStyle(
                    fontSize = if (isSmall) 22.sp else 26.sp, 
                    color = androidx.glance.color.ColorProvider(day = textColor, night = textColor), 
                    textAlign = TextAlign.Center
                )
            )
            if (!isSmall) {
                Text(
                    "Add a sparkle",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.glance.color.ColorProvider(day = textColor, night = textColor),
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}
