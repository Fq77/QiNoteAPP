package com.qinoteapp.qinoteapp.ui.settings

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.qinoteapp.qinoteapp.components.NavBarMode
import com.qinoteapp.qinoteapp.components.QiToggleSwitch
import com.qinoteapp.qinoteapp.components.StaggeredItem
import com.qinoteapp.qinoteapp.components.TopNavBar
import com.qinoteapp.qinoteapp.notification.DeviceCapabilityDetector
import com.qinoteapp.qinoteapp.notification.NotificationStrategy
import com.qinoteapp.qinoteapp.notification.NotificationStrategyResolver
import com.qinoteapp.qinoteapp.notification.shizuku.ShizukuPermissionHelper
import com.qinoteapp.qinoteapp.notification.shizuku.ShizukuWhitelistBypass
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import kotlinx.coroutines.launch

@Composable
fun NotificationSettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val colors = QiTheme.colors
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val preferencesManager = viewModel.preferencesManager
    val capabilityDetector = remember { DeviceCapabilityDetector(context) }
    val strategyResolver = remember {
        NotificationStrategyResolver(capabilityDetector)
    }

    val superIslandEnabled by preferencesManager.superIslandEnabled.collectAsState(initial = true)
    val currentStrategy = remember { strategyResolver.resolve() }

    val isHyperOS3Plus = remember {
        capabilityDetector.isHyperOS3Plus() && capabilityDetector.isIslandSupported()
    }
    val hasFocusPermission = remember { capabilityDetector.hasFocusPermission() }
    val isShizukuReady = remember { ShizukuWhitelistBypass.isShizukuReady() }
    val isAndroid16Plus = remember { capabilityDetector.isAndroid16Plus() }

    Scaffold(
        topBar = {
            TopNavBar(mode = NavBarMode.SubPageMode("实时通知设置") { navController.popBackStack() })
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
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(QiRadius.xl)),
                    color = colors.Surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "启用实时通知",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.W600,
                                fontSize = 15.sp,
                                color = colors.OnBackground
                            )
                            Text(
                                text = "在通知栏或超级岛显示AI记账的实时状态",
                                fontFamily = JakartaFontFamily,
                                fontWeight = FontWeight.W400,
                                fontSize = 12.sp,
                                color = colors.TextTertiary
                            )
                        }
                        QiToggleSwitch(
                            checked = superIslandEnabled,
                            onCheckedChange = { enabled ->
                                coroutineScope.launch {
                                    preferencesManager.setSuperIslandEnabled(enabled)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            StaggeredItem(index = 1) {
                SectionHeader(title = "设备状态")
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            StaggeredItem(index = 2) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(QiRadius.xl)),
                    color = colors.Surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg)
                    ) {
                        StatusRow(
                            icon = Icons.Filled.PhoneAndroid,
                            label = "设备类型",
                            value = if (capabilityDetector.isXiaomiDevice()) "小米设备" else "非小米设备",
                            valueColor = colors.OnBackground
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = colors.Surface2
                        )
                        StatusRow(
                            icon = Icons.Filled.Settings,
                            label = "HyperOS灵动岛",
                            value = if (isHyperOS3Plus) "支持 (HyperOS 3+)" else "不支持",
                            valueColor = if (isHyperOS3Plus) Color(0xFF22C55E) else colors.TextTertiary
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = colors.Surface2
                        )
                        StatusRow(
                            icon = Icons.Filled.CheckCircle,
                            label = "白名单权限",
                            value = if (hasFocusPermission) "已授权" else "未授权",
                            valueColor = if (hasFocusPermission) Color(0xFF22C55E) else Color(0xFFEF4444)
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = colors.Surface2
                        )
                        StatusRow(
                            icon = Icons.Filled.Shield,
                            label = "Shizuku状态",
                            value = if (isShizukuReady) "已连接" else "未连接",
                            valueColor = if (isShizukuReady) Color(0xFF22C55E) else colors.TextTertiary
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = colors.Surface2
                        )
                        StatusRow(
                            icon = Icons.Filled.Security,
                            label = "当前策略",
                            value = strategyDisplayName(currentStrategy),
                            valueColor = when (currentStrategy) {
                                NotificationStrategy.SUPER_ISLAND_DIRECT -> Color(0xFF22C55E)
                                NotificationStrategy.SUPER_ISLAND_SHIZUKU -> Color(0xFF3B82F6)
                                NotificationStrategy.LIVE_UPDATES -> Color(0xFFF59E0B)
                                NotificationStrategy.ONGOING_NOTIFICATION -> colors.TextTertiary
                            }
                        )
                    }
                }
            }

            if (superIslandEnabled && isHyperOS3Plus && !hasFocusPermission) {
                Spacer(modifier = Modifier.height(Spacing.lg))

                StaggeredItem(index = 3) {
                    SectionHeader(title = "实时通知配置")
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                StaggeredItem(index = 4) {
                    RootSolutionCard()
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                StaggeredItem(index = 5) {
                    ShizukuSolutionCard(
                        isShizukuReady = isShizukuReady,
                        onRequestPermission = {
                            coroutineScope.launch {
                                ShizukuPermissionHelper.requestPermission()
                            }
                        }
                    )
                }
            }

            if (superIslandEnabled && !isHyperOS3Plus && isAndroid16Plus) {
                Spacer(modifier = Modifier.height(Spacing.lg))

                StaggeredItem(index = 3) {
                    SectionHeader(title = "通知方式")
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                StaggeredItem(index = 4) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(QiRadius.xl)),
                        color = colors.Surface
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.lg)
                        ) {
                            Text(
                                text = "Android Live Updates",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.W600,
                                fontSize = 15.sp,
                                color = colors.OnBackground
                            )
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Text(
                                text = "您的设备运行Android 16+，将使用系统原生的Live Updates通知样式在状态栏显示记账状态。此方式无需额外配置。",
                                fontFamily = JakartaFontFamily,
                                fontWeight = FontWeight.W400,
                                fontSize = 12.sp,
                                color = colors.TextTertiary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            if (superIslandEnabled && !isHyperOS3Plus && !isAndroid16Plus) {
                Spacer(modifier = Modifier.height(Spacing.lg))

                StaggeredItem(index = 3) {
                    SectionHeader(title = "通知方式")
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                StaggeredItem(index = 4) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(QiRadius.xl)),
                        color = colors.Surface
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.lg)
                        ) {
                            Text(
                                text = "常驻通知",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.W600,
                                fontSize = 15.sp,
                                color = colors.OnBackground
                            )
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Text(
                                text = "当前设备将使用常驻通知栏方式显示记账状态。升级到Android 16或HyperOS 3+可获得更好的实时通知体验。",
                                fontFamily = JakartaFontFamily,
                                fontWeight = FontWeight.W400,
                                fontSize = 12.sp,
                                color = colors.TextTertiary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    val colors = QiTheme.colors
    Text(
        text = title,
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 13.sp,
        color = colors.OnSurface,
        modifier = Modifier.padding(vertical = Spacing.xs, horizontal = Spacing.xs)
    )
}

@Composable
private fun StatusRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color
) {
    val colors = QiTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.OnSurface,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = label,
                fontFamily = JakartaFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 13.sp,
                color = colors.OnSurface
            )
        }
        Text(
            text = value,
            fontFamily = JakartaFontFamily,
            fontWeight = FontWeight.W500,
            fontSize = 13.sp,
            color = valueColor
        )
    }
}

@Composable
private fun RootSolutionCard() {
    val colors = QiTheme.colors
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(QiRadius.xl)),
        color = colors.Surface,
        shape = RoundedCornerShape(QiRadius.xl)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(colors.PrimaryLighter, RoundedCornerShape(QiRadius.md)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        tint = colors.Primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = "Root方案（推荐）",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 15.sp,
                    color = colors.OnBackground
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(QiRadius.sm),
                    color = colors.PrimaryLighter
                ) {
                    Text(
                        text = "需Root",
                        fontFamily = JakartaFontFamily,
                        fontWeight = FontWeight.W500,
                        fontSize = 10.sp,
                        color = colors.Primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "已root的用户可以刷入无视白名单的Xposed/LSPosed模块来启用实时通知功能。模块安装后，应用将自动获得超级岛的白名单权限，无需额外配置Shizuku。",
                fontFamily = JakartaFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                color = colors.TextTertiary,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Xposed模块需自行搜索安装，常用的有「焦点通知解锁」「CustoMIUIzer」等模块。安装后请在LSPosed中激活并勾选本应用。",
                fontFamily = JakartaFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 11.sp,
                color = colors.TextQuaternary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun ShizukuSolutionCard(
    isShizukuReady: Boolean,
    onRequestPermission: () -> Unit
) {
    val colors = QiTheme.colors
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(QiRadius.xl)),
        color = colors.Surface,
        shape = RoundedCornerShape(QiRadius.xl)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(colors.AccentSoft, RoundedCornerShape(QiRadius.md)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = colors.Accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = "Shizuku方案（免Root）",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 15.sp,
                    color = colors.OnBackground
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(QiRadius.sm),
                    color = if (isShizukuReady) Color(0xFF22C55E).copy(alpha = 0.15f) else colors.Surface2
                ) {
                    Text(
                        text = if (isShizukuReady) "已连接" else "需配置",
                        fontFamily = JakartaFontFamily,
                        fontWeight = FontWeight.W500,
                        fontSize = 10.sp,
                        color = if (isShizukuReady) Color(0xFF22C55E) else colors.TextTertiary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "通过Shizuku获取系统级权限，绕过超级岛的白名单限制。此方案无需Root，但需要安装Shizuku应用并通过无线调试或ADB激活。",
                fontFamily = JakartaFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                color = colors.TextTertiary,
                lineHeight = 18.sp
            )
            if (!isShizukuReady) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Shizuku未连接，请先安装并激活Shizuku",
                        fontFamily = JakartaFontFamily,
                        fontWeight = FontWeight.W400,
                        fontSize = 11.sp,
                        color = Color(0xFFEF4444)
                    )
                    Button(
                        onClick = onRequestPermission,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.Accent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(QiRadius.md)
                    ) {
                        Text(
                            text = "授权",
                            fontFamily = JakartaFontFamily,
                            fontWeight = FontWeight.W600,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "配置步骤：1. 下载安装Shizuku > 2. 通过无线调试启动 > 3. 返回本页面点击「授权」",
                fontFamily = JakartaFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 11.sp,
                color = colors.TextQuaternary,
                lineHeight = 16.sp
            )
        }
    }
}

private fun strategyDisplayName(strategy: NotificationStrategy): String {
    return when (strategy) {
        NotificationStrategy.SUPER_ISLAND_DIRECT -> "超级岛直通"
        NotificationStrategy.SUPER_ISLAND_SHIZUKU -> "Shizuku代理"
        NotificationStrategy.LIVE_UPDATES -> "系统Live Updates"
        NotificationStrategy.ONGOING_NOTIFICATION -> "常驻通知"
    }
}