package com.qinoteapp.qinoteapp.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.qinoteapp.qinoteapp.components.AiIcon
import com.qinoteapp.qinoteapp.components.NavBarMode
import com.qinoteapp.qinoteapp.components.QiSheet
import com.qinoteapp.qinoteapp.components.qiPressScale
import com.qinoteapp.qinoteapp.components.StaggeredItem
import com.qinoteapp.qinoteapp.components.TopNavBar
import com.qinoteapp.qinoteapp.navigation.QiRoute
import com.qinoteapp.qinoteapp.ui.theme.QiSettingsCardTitle
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val colors = QiTheme.colors
    var showFeedbackSheet by remember { mutableStateOf(false) }

    if (showFeedbackSheet) {
        FeedbackBottomSheet(onDismiss = { showFeedbackSheet = false })
    }

    Scaffold(
        topBar = {
            TopNavBar(mode = NavBarMode.SubPageMode("设置") { navController.popBackStack() })
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

            StaggeredItem(index = 0) {
                SettingsCard(
                    icon = Icons.Filled.SmartToy,
                    iconTintColor = colors.Primary,
                    iconBgColor = colors.PrimaryLighter,
                    title = "AI功能设置",
                    subtitle = "配置AI智能识别和API参数",
                    onClick = { navController.navigate(QiRoute.AiConfig.route) },
                    customIcon = {
                        AiIcon(
                            iconSize = 22.dp,
                            containerSize = 44.dp,
                            shape = RoundedCornerShape(QiRadius.md)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            StaggeredItem(index = 1) {
                SettingsCard(
                    icon = Icons.Filled.BatteryChargingFull,
                    iconTintColor = colors.Expense,
                    iconBgColor = colors.ExpenseSoft,
                    title = "省电与优化",
                    subtitle = "自启动权限和省电设置，保障AI解析稳定",
                    onClick = { navController.navigate(QiRoute.Optimization.route) }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            StaggeredItem(index = 2) {
                SettingsCard(
                    icon = Icons.Filled.Notifications,
                    iconTintColor = colors.Primary,
                    iconBgColor = colors.PrimaryLighter,
                    title = "实时通知设置",
                    subtitle = "管理记账状态实时通知的显示方式",
                    onClick = { navController.navigate(QiRoute.NotificationSettings.route) }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            StaggeredItem(index = 3) {
                SettingsCard(
                    icon = Icons.Filled.Storage,
                    iconTintColor = colors.Accent,
                    iconBgColor = colors.AccentSoft,
                    title = "数据管理",
                    subtitle = "导入导出和清除数据",
                    onClick = { navController.navigate(QiRoute.DataManage.route) }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            StaggeredItem(index = 4) {
                SettingsCard(
                    icon = Icons.Filled.Category,
                    iconTintColor = colors.Income,
                    iconBgColor = colors.IncomeSoft,
                    title = "分类管理",
                    subtitle = "管理收支分类",
                    onClick = { navController.navigate(QiRoute.CategoryManage.route) }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            StaggeredItem(index = 5) {
                SettingsCard(
                    icon = Icons.Filled.Palette,
                    iconTintColor = colors.Primary,
                    iconBgColor = colors.PrimaryLighter,
                    title = "显示与个性化",
                    subtitle = "选择应用显示模式和个性化设置",
                    onClick = { navController.navigate(QiRoute.ThemeSettings.route) }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            StaggeredItem(index = 6) {
                SettingsCard(
                    icon = Icons.Filled.Email,
                    iconTintColor = colors.Accent,
                    iconBgColor = colors.AccentSoft,
                    title = "意见反馈",
                    subtitle = "帮助我们改进产品",
                    onClick = { showFeedbackSheet = true }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            StaggeredItem(index = 7) {
                SettingsCard(
                    icon = Icons.Filled.Info,
                    iconTintColor = colors.Primary,
                    iconBgColor = colors.PrimaryLighter,
                    title = "关于QiNote 奇记",
                    subtitle = "版本信息和许可说明",
                    onClick = { navController.navigate(QiRoute.About.route) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedbackBottomSheet(onDismiss: () -> Unit) {
    val colors = QiTheme.colors
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    QiSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "意见反馈",
                style = MaterialTheme.typography.titleLarge,
                color = colors.OnBackground
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                text = "如果您有任何建议或问题，欢迎通过邮件联系我们：",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.OnSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            Text(
                text = "458998070@qq.com",
                style = MaterialTheme.typography.titleMedium,
                color = colors.Primary
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            TextButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("email", "458998070@qq.com")
                    clipboard.setPrimaryClip(clip)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = colors.PrimaryLighter,
                    contentColor = colors.Primary
                ),
                shape = RoundedCornerShape(QiRadius.lg)
            ) {
                Text(
                    text = "复制邮箱",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(vertical = Spacing.xs)
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(
    icon: ImageVector,
    iconTintColor: Color,
    iconBgColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    customIcon: (@Composable () -> Unit)? = null
) {
    val colors = QiTheme.colors

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .qiPressScale()
            .clip(RoundedCornerShape(QiRadius.xl))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        color = colors.Surface
    ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (customIcon != null) {
                    customIcon()
                } else {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(iconBgColor, RoundedCornerShape(QiRadius.md)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconTintColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(Spacing.md))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        style = QiSettingsCardTitle,
                        color = colors.OnBackground
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.TextTertiary
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "查看$title",
                    tint = colors.TextQuaternary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }