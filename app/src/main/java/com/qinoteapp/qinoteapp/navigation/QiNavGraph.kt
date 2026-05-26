package com.qinoteapp.qinoteapp.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.qinoteapp.qinoteapp.ui.home.BillDetailScreen
import com.qinoteapp.qinoteapp.ui.home.HomeScreen
import com.qinoteapp.qinoteapp.ui.settings.AboutScreen
import com.qinoteapp.qinoteapp.ui.settings.AiConfigScreen
import com.qinoteapp.qinoteapp.ui.settings.CategoryManageScreen
import com.qinoteapp.qinoteapp.ui.settings.DataManageScreen
import com.qinoteapp.qinoteapp.ui.settings.SettingsScreen
import com.qinoteapp.qinoteapp.ui.settings.NotificationSettingsScreen
import com.qinoteapp.qinoteapp.ui.settings.OptimizationScreen
import com.qinoteapp.qinoteapp.ui.settings.ThemeSettingsScreen
import com.qinoteapp.qinoteapp.ui.stats.StatsScreen
import com.qinoteapp.qinoteapp.ui.theme.QiEasing

private val SubPageEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(300, easing = QiEasing.EaseOut)) + fadeIn(tween(300))
}

private val SubPageExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(300)) + fadeOut(tween(300))
}

private val SubPagePopEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(300, easing = QiEasing.EaseOut)) + fadeIn(tween(300))
}

private val SubPagePopExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    scaleOut(targetScale = 0.9f, transformOrigin = TransformOrigin(0.5f, 0.5f), animationSpec = tween(300)) + fadeOut(tween(300))
}

@Composable
fun QiNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onFabClick: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = QiRoute.Home.route,
        modifier = modifier
    ) {
        composable(
            QiRoute.Home.route,
            enterTransition = { fadeIn(tween(250)) },
            exitTransition = { fadeOut(tween(250)) },
            popEnterTransition = { fadeIn(tween(250)) },
            popExitTransition = { fadeOut(tween(250)) }
        ) {
            HomeScreen(
                navController = navController,
                onFabClick = onFabClick
            )
        }
        composable(
            QiRoute.Stats.route,
            enterTransition = { fadeIn(tween(250)) },
            exitTransition = { fadeOut(tween(250)) },
            popEnterTransition = { fadeIn(tween(250)) },
            popExitTransition = { fadeOut(tween(250)) }
        ) {
            StatsScreen(navController = navController)
        }
        composable(
            QiRoute.Settings.route,
            enterTransition = SubPageEnterTransition,
            exitTransition = SubPageExitTransition,
            popEnterTransition = SubPagePopEnterTransition,
            popExitTransition = SubPagePopExitTransition
        ) {
            SettingsScreen(navController = navController)
        }
        composable(
            QiRoute.AiConfig.route,
            enterTransition = SubPageEnterTransition,
            exitTransition = SubPageExitTransition,
            popEnterTransition = SubPagePopEnterTransition,
            popExitTransition = SubPagePopExitTransition
        ) {
            AiConfigScreen(navController = navController)
        }
        composable(
            QiRoute.CategoryManage.route,
            enterTransition = SubPageEnterTransition,
            exitTransition = SubPageExitTransition,
            popEnterTransition = SubPagePopEnterTransition,
            popExitTransition = SubPagePopExitTransition
        ) {
            CategoryManageScreen(navController = navController)
        }
        composable(
            QiRoute.DataManage.route,
            enterTransition = SubPageEnterTransition,
            exitTransition = SubPageExitTransition,
            popEnterTransition = SubPagePopEnterTransition,
            popExitTransition = SubPagePopExitTransition
        ) {
            DataManageScreen(navController = navController)
        }
        composable(
            QiRoute.About.route,
            enterTransition = SubPageEnterTransition,
            exitTransition = SubPageExitTransition,
            popEnterTransition = SubPagePopEnterTransition,
            popExitTransition = SubPagePopExitTransition
        ) {
            AboutScreen(navController = navController)
        }
        composable(
            QiRoute.ThemeSettings.route,
            enterTransition = SubPageEnterTransition,
            exitTransition = SubPageExitTransition,
            popEnterTransition = SubPagePopEnterTransition,
            popExitTransition = SubPagePopExitTransition
        ) {
            ThemeSettingsScreen(navController = navController)
        }
        composable(
            QiRoute.Optimization.route,
            enterTransition = SubPageEnterTransition,
            exitTransition = SubPageExitTransition,
            popEnterTransition = SubPagePopEnterTransition,
            popExitTransition = SubPagePopExitTransition
        ) {
            OptimizationScreen(navController = navController)
        }
        composable(
            QiRoute.NotificationSettings.route,
            enterTransition = SubPageEnterTransition,
            exitTransition = SubPageExitTransition,
            popEnterTransition = SubPagePopEnterTransition,
            popExitTransition = SubPagePopExitTransition
        ) {
            NotificationSettingsScreen(navController = navController)
        }
        composable(
            QiRoute.BillDetail.route,
            enterTransition = SubPageEnterTransition,
            exitTransition = SubPageExitTransition,
            popEnterTransition = SubPagePopEnterTransition,
            popExitTransition = SubPagePopExitTransition
        ) { backStackEntry ->
            val billId = backStackEntry.arguments?.getString("billId") ?: ""
            BillDetailScreen(
                navController = navController,
                billId = billId
            )
        }
    }
}
