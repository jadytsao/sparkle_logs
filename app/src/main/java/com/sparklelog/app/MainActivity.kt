package com.sparklelog.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sparklelog.app.ui.AppNavigation
import com.sparklelog.app.ui.SparkleViewModel
import com.sparklelog.app.ui.SparkleViewModelFactory
import com.sparklelog.app.ui.theme.SparkleLogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as SparkleLogApplication
        setContent {
            SparkleLogTheme {
                val viewModel: SparkleViewModel = viewModel(factory = SparkleViewModelFactory(app.repository))
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}