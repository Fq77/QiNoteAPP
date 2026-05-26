package com.qinoteapp.qinoteapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.qinoteapp.qinoteapp.navigation.BottomNavBar
import com.qinoteapp.qinoteapp.navigation.QiNavGraph
import com.qinoteapp.qinoteapp.navigation.QiRoute
import com.qinoteapp.qinoteapp.ui.entry.EntrySheet
import com.qinoteapp.qinoteapp.ui.settings.SettingsViewModel
import com.qinoteapp.qinoteapp.ui.theme.QiNoteAPPTheme
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.ThemeMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val window = window
            MainApp(window = window)
        }
    }
}

@Composable
fun MainApp(
    window: Window,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val aiConfig by settingsViewModel.uiState.collectAsState()
    val themeMode = when (aiConfig.appConfig.themeMode) {
        "light" -> ThemeMode.LIGHT
        "dark" -> ThemeMode.DARK
        else -> ThemeMode.SYSTEM
    }

    val predictiveBackEnabled = aiConfig.appConfig.predictiveBack

    QiNoteAPPTheme(themeMode = themeMode) {
        val useDarkTheme = when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }

        val colors = QiTheme.colors
        val activity = LocalContext.current as? ComponentActivity

        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) {}

        DisposableEffect(Unit) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (activity?.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            onDispose {}
        }

        window.statusBarColor = colors.Background.toArgb()
        window.navigationBarColor = colors.Background.toArgb()

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !useDarkTheme
            isAppearanceLightNavigationBars = !useDarkTheme
        }

        DisposableEffect(predictiveBackEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && activity != null) {
                if (!predictiveBackEnabled) {
                    val callback = object : android.window.OnBackInvokedCallback {
                        override fun onBackInvoked() {
                            activity.onBackPressedDispatcher.onBackPressed()
                        }
                    }
                    activity.onBackInvokedDispatcher.registerOnBackInvokedCallback(
                        android.window.OnBackInvokedDispatcher.PRIORITY_DEFAULT + 1,
                        callback
                    )
                    onDispose {
                        activity.onBackInvokedDispatcher.unregisterOnBackInvokedCallback(callback)
                    }
                } else {
                    onDispose {}
                }
            } else {
                onDispose {}
            }
        }

        val navController = rememberNavController()
        var showEntrySheet by remember { mutableStateOf(false) }

        val activityForIntent = LocalContext.current as? ComponentActivity
        DisposableEffect(activityForIntent) {
            if (activityForIntent != null) {
                val intent = activityForIntent.intent
                if (intent?.getBooleanExtra("open_entry", false) == true) {
                    showEntrySheet = true
                    intent.removeExtra("open_entry")
                }
                if (intent?.getBooleanExtra("open_home", false) == true) {
                    intent.removeExtra("open_home")
                }
            }
            onDispose {}
        }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val showBottomBar = currentRoute in listOf(
            QiRoute.Home.route,
            QiRoute.Stats.route
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (showBottomBar) {
                    BottomNavBar(
                        currentRoute = currentRoute ?: QiRoute.Home.route,
                        onRouteSelected = { route ->
                            if (currentRoute == route) return@BottomNavBar
                            navController.navigate(route) {
                                popUpTo(QiRoute.Home.route) {
                                    saveState = false
                                }
                                launchSingleTop = true
                                restoreState = false
                            }
                        },
                        onFabClick = { showEntrySheet = true }
                    )
                }
            }
        ) { innerPadding ->
            QiNavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                onFabClick = { showEntrySheet = true }
            )
        }

        if (showEntrySheet) {
            EntrySheet(
                onDismiss = { showEntrySheet = false }
            )
        }
    }
}
