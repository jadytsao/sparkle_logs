package com.sparklelog.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sparklelog.app.ui.AppNavigation
import com.sparklelog.app.ui.SparkleViewModel
import com.sparklelog.app.ui.SparkleViewModelFactory
import com.sparklelog.app.ui.theme.SparkleLogTheme

class MainActivity : ComponentActivity() {
    private var destination by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)

        val app = application as SparkleLogApplication
        setContent {
            SparkleLogTheme {
                val viewModel: SparkleViewModel = viewModel(factory = SparkleViewModelFactory(app.repository))
                AppNavigation(viewModel = viewModel, initialDestination = destination)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        destination = intent?.getStringExtra("NAV_DESTINATION")
    }
}
