package com.qinoteapp.qinoteapp.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.qinoteapp.qinoteapp.components.NavBarMode
import com.qinoteapp.qinoteapp.components.SectionCard
import com.qinoteapp.qinoteapp.components.SectionHeader
import com.qinoteapp.qinoteapp.components.TopNavBar
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@Composable
fun ThemeSettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = QiTheme.colors

    val currentMode = uiState.appConfig.themeMode

    val themeOptions = listOf(
        "system" to ("跟随系统" to "自动适配系统深浅色设置"),
        "light" to ("浅色模式" to "始终使用浅色主题"),
        "dark" to ("深色模式" to "始终使用深色主题")
    )

    Scaffold(
        topBar = {
            TopNavBar(mode = NavBarMode.SubPageMode("主题与个性化") { navController.popBackStack() })
        },
        containerColor = colors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg)
                .padding(bottom = Spacing.xxxl)
        ) {
            Spacer(modifier = Modifier.height(Spacing.lg))

            SectionHeader(
                title = "显示模式",
                subtitle = "选择应用的外观主题"
            )

            SectionCard {
                themeOptions.forEachIndexed { index, (mode, labelDesc) ->
                    val (label, desc) = labelDesc
                    val isSelected = currentMode == mode

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(QiRadius.lg))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { viewModel.updateThemeMode(mode) }
                            )
                            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = label,
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.W600,
                                fontSize = 15.sp,
                                color = colors.OnBackground
                            )
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Text(
                                text = desc,
                                fontFamily = JakartaFontFamily,
                                fontWeight = FontWeight.W400,
                                fontSize = 13.sp,
                                color = colors.TextTertiary
                            )
                        }

                        Spacer(modifier = Modifier.width(Spacing.md))

                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .then(
                                    if (isSelected) {
                                        Modifier.background(colors.Primary)
                                    } else {
                                        Modifier.background(colors.Surface1, CircleShape)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(colors.OnPrimary)
                                )
                            }
                        }
                    }

                    if (index < themeOptions.size - 1) {
                        HorizontalDivider(
                            color = colors.Border,
                            modifier = Modifier.padding(horizontal = Spacing.lg)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            SectionHeader(
                title = "交互体验",
                subtitle = "自定义应用交互行为"
            )

            SectionCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "预测式返回",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.W600,
                            fontSize = 15.sp,
                            color = colors.OnBackground
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = "返回时显示上一个页面的预览动画",
                            fontFamily = JakartaFontFamily,
                            fontWeight = FontWeight.W400,
                            fontSize = 13.sp,
                            color = colors.TextTertiary
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.md))

                    Switch(
                        checked = uiState.appConfig.predictiveBack,
                        onCheckedChange = { viewModel.updatePredictiveBack(it) },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = colors.Primary
                        )
                    )
                }
            }
        }
    }
}


