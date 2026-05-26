package com.qinoteapp.qinoteapp.ui.settings

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.qinoteapp.qinoteapp.components.NavBarMode
import com.qinoteapp.qinoteapp.components.PressScale
import com.qinoteapp.qinoteapp.components.TopNavBar
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@Composable
fun OptimizationScreen(navController: NavController) {
    val colors = QiTheme.colors
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopNavBar(mode = NavBarMode.SubPageMode("省电与优化") { navController.popBackStack() })
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

            Text(
                text = "为保障AI记账功能的稳定运行，请确保以下权限已正确设置。部分手机厂商会限制后台网络请求，导致AI解析卡住或超时。",
                fontFamily = JakartaFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 13.sp,
                color = colors.TextTertiary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            OptimizationCard(
                icon = Icons.AutoMirrored.Filled.Launch,
                iconTintColor = colors.Primary,
                iconBgColor = colors.PrimaryLighter,
                title = "自启动权限",
                subtitle = "允许应用自启动，确保后台服务正常运行",
                onClick = { openAutoStartSetting(context) }
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            OptimizationCard(
                icon = Icons.Filled.BatteryAlert,
                iconTintColor = colors.Expense,
                iconBgColor = colors.ExpenseSoft,
                title = "省电优化",
                subtitle = "关闭省电限制，避免后台网络请求被中断",
                onClick = { openBatteryOptimizationSetting(context) }
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            Text(
                text = "提示：不同手机品牌的设置界面可能略有差异，请根据实际情况操作。如果找不到对应设置项，可尝试在系统设置中搜索「自启动」或「省电」。",
                fontFamily = JakartaFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                color = colors.TextTertiary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun OptimizationCard(
    icon: ImageVector,
    iconTintColor: Color,
    iconBgColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val colors = QiTheme.colors

    PressScale {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
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
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(iconBgColor, RoundedCornerShape(QiRadius.md)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTintColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(Spacing.md))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 15.sp,
                        color = colors.OnBackground
                    )
                    Text(
                        text = subtitle,
                        fontFamily = JakartaFontFamily,
                        fontWeight = FontWeight.W400,
                        fontSize = 12.sp,
                        color = colors.TextTertiary
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = colors.TextQuaternary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun openAutoStartSetting(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

private fun openBatteryOptimizationSetting(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return
    } catch (_: Exception) {
    }

    val manufacturer = Build.MANUFACTURER.lowercase()
    val intents = when {
        manufacturer.contains("xiaomi") || manufacturer.contains("redmi") -> listOf(
            intentFor("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity")
        )
        manufacturer.contains("huawei") || manufacturer.contains("honor") -> listOf(
            intentFor("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")
        )
        manufacturer.contains("oppo") -> listOf(
            intentFor("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")
        )
        manufacturer.contains("vivo") -> listOf(
            intentFor("com.vivo.abe", "com.vivo.applicationbehaviorengine.ui.ExcessivePowerManagerActivity")
        )
        else -> emptyList()
    }

    for (intent in intents) {
        try {
            intent.putExtra("package", context.packageName)
            context.startActivity(intent)
            return
        } catch (_: Exception) {
        }
    }

    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

private fun intentFor(pkg: String, cls: String): Intent {
    return Intent().apply {
        component = ComponentName(pkg, cls)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}
