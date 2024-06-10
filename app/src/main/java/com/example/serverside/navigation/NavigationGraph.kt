package com.example.serverside.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.serverside.ui.theme.screen.LogsScreen
import com.example.serverside.ui.theme.screen.MainScreen

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavigationItem.Main.route) {
        composable(NavigationItem.Main.route) { NavigationItem.Main.screen(navController) }
        composable(NavigationItem.Logs.route) { NavigationItem.Logs.screen(navController) }
    }
}

sealed class NavigationItem(
    val route: String,
    val image: Int,
    val screen: @Composable (navController: NavHostController) -> Unit,
) {
    object Main : NavigationItem(
        route = "MAIN",
        image = 0,
        screen = { navController -> MainScreen(navController) })

    object Logs : NavigationItem(
        route = "LOGS",
        image = 0,
        screen = { navController -> LogsScreen(navController) })

}