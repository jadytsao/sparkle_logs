package com.sparklelog.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sparklelog.app.ui.components.AddNavIcon
import com.sparklelog.app.ui.components.DateNavIcon
import com.sparklelog.app.ui.components.FeelingNavIcon
import com.sparklelog.app.ui.components.InsightsNavIcon

private sealed class Destination(val route: String, val label: String) {
    data object Add : Destination("add", "Add")
    data object ByDate : Destination("byDate", "By Date")
    data object ByFeeling : Destination("byFeeling", "By Feeling")
    data object Insights : Destination("insights", "Insights")
}

private val destinations = listOf(Destination.Add, Destination.ByDate, Destination.ByFeeling, Destination.Insights)

@Composable
fun AppNavigation(viewModel: SparkleViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                destinations.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            val tint = if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
                            when (destination) {
                                Destination.Add -> AddNavIcon(tint)
                                Destination.ByDate -> DateNavIcon(tint)
                                Destination.ByFeeling -> FeelingNavIcon(tint)
                                Destination.Insights -> InsightsNavIcon(tint)
                            }
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Add.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.Add.route) { AddSparkleScreen(viewModel) }
            composable(Destination.ByDate.route) { ByDateScreen(viewModel) }
            composable(Destination.ByFeeling.route) { ByFeelingScreen(viewModel) }
            composable(Destination.Insights.route) { InsightsScreen(viewModel) }
        }
    }
}
