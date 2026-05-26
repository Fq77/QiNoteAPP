package com.qinoteapp.qinoteapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiTheme

sealed class NavBarMode {
    data class HomeMode(
        val title: String,
        val onSettingsClick: () -> Unit
    ) : NavBarMode()

    data class StatsMode(
        val title: String
    ) : NavBarMode()

    data class SubPageMode(
        val title: String,
        val onBackClick: () -> Unit
    ) : NavBarMode()

    data class DetailMode(
        val title: String,
        val onBackClick: () -> Unit,
        val actions: @Composable () -> Unit = {}
    ) : NavBarMode()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(
    mode: NavBarMode,
    modifier: Modifier = Modifier
) {
    val colors = QiTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colors.Surface.copy(alpha = 0.92f),
                        colors.Surface.copy(alpha = 0.78f)
                    )
                )
            )
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = when (mode) {
                        is NavBarMode.HomeMode -> mode.title
                        is NavBarMode.StatsMode -> mode.title
                        is NavBarMode.SubPageMode -> mode.title
                        is NavBarMode.DetailMode -> mode.title
                    },
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = colors.OnBackground
                )
            },
            navigationIcon = {
                when (mode) {
                    is NavBarMode.SubPageMode -> {
                        IconButton(onClick = mode.onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回",
                                tint = colors.OnBackground
                            )
                        }
                    }
                    is NavBarMode.DetailMode -> {
                        IconButton(onClick = mode.onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回",
                                tint = colors.OnBackground
                            )
                        }
                    }
                    else -> {}
                }
            },
            actions = {
                when (mode) {
                    is NavBarMode.HomeMode -> {
                        IconButton(onClick = mode.onSettingsClick) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "设置",
                                tint = colors.OnBackground
                            )
                        }
                    }
                    is NavBarMode.DetailMode -> {
                        mode.actions()
                    }
                    else -> {}
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
        HorizontalDivider(
            color = colors.Border,
            thickness = androidx.compose.ui.unit.Dp.Hairline
        )
    }
}
