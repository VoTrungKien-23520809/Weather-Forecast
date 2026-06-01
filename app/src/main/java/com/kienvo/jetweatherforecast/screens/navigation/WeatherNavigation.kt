package com.kienvo.jetweatherforecast.screens.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kienvo.jetweatherforecast.screens.about.AboutScreen
import com.kienvo.jetweatherforecast.screens.favorites.FavoriteScreen
import com.kienvo.jetweatherforecast.screens.main.MainScreen
import com.kienvo.jetweatherforecast.screens.main.MainViewModel
import com.kienvo.jetweatherforecast.screens.search.SearchScreen
import com.kienvo.jetweatherforecast.screens.settings.SettingsScreen
import com.kienvo.jetweatherforecast.screens.splash.WeatherSplashScreen
import com.kienvo.jetweatherforecast.screens.insights.WeatherInsightsScreen

// Transition specs chung cho toàn bộ Navigation
private const val TRANSITION_DURATION = 400

@Composable
fun WeatherNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = WeatherScreens.SplashScreen.name,
        // Transition mặc định: slide + fade
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeIn(animationSpec = tween(TRANSITION_DURATION))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeOut(animationSpec = tween(TRANSITION_DURATION / 2))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeIn(animationSpec = tween(TRANSITION_DURATION))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeOut(animationSpec = tween(TRANSITION_DURATION / 2))
        }
    ) {
        // Splash — no transition (it self-navigates)
        composable(
            route = WeatherScreens.SplashScreen.name,
            enterTransition = { EnterTransition.None },
            exitTransition = { fadeOut(animationSpec = tween(500)) }
        ) {
            WeatherSplashScreen(navController = navController)
        }

        composable(
            route = "${WeatherScreens.MainScreen.name}/{city}",
            arguments = listOf(navArgument("city") { type = NavType.StringType })
        ) {
            val mainViewModel = hiltViewModel<MainViewModel>()
            MainScreen(navController = navController, viewModel = mainViewModel)
        }

        composable(WeatherScreens.SearchScreen.name) {
            SearchScreen(navController = navController)
        }

        composable(WeatherScreens.AboutScreen.name) {
            AboutScreen(navController = navController)
        }

        composable(WeatherScreens.FavoriteScreen.name) {
            FavoriteScreen(navController = navController)
        }

        composable(WeatherScreens.SettingsScreen.name) {
            SettingsScreen(navController = navController)
        }

        composable(
            route = "${WeatherScreens.WeatherInsightsScreen.name}/{city}",
            arguments = listOf(navArgument("city") { type = NavType.StringType })
        ) { backStackEntry ->
            val city = backStackEntry.arguments?.getString("city") ?: "Ho Chi Minh"
            WeatherInsightsScreen(navController = navController, city = city)
        }
    }
}