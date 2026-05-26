package com.qinoteapp.qinoteapp.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.qinoteapp.qinoteapp.R
import com.qinoteapp.qinoteapp.components.HeroEntrance
import com.qinoteapp.qinoteapp.components.NavBarMode
import com.qinoteapp.qinoteapp.components.TopNavBar
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@Composable
fun AboutScreen(
    navController: NavController
) {
    val colors = QiTheme.colors
    val context = LocalContext.current
    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
        } catch (_: Exception) {
            "1.0"
        }
    }

    Scaffold(
        topBar = {
            TopNavBar(mode = NavBarMode.SubPageMode("关于QiNote 奇记") { navController.popBackStack() })
        },
        containerColor = colors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacing.xxxl))

            HeroEntrance(visible = true) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.rounded_icon),
                        contentDescription = "奇记图标",
                        modifier = Modifier
                            .size(120.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(Spacing.lg))

                    Text(
                        text = "QiNote 奇记",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.W800,
                        fontSize = 32.sp,
                        color = colors.OnBackground
                    )

                    Spacer(modifier = Modifier.height(Spacing.xs))

                    Text(
                        text = "版本 $versionName",
                        fontFamily = JakartaFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = colors.TextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxxl))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg),
                shape = RoundedCornerShape(QiRadius.xl),
                color = colors.Surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg)
                ) {
                    Text(
                        text = "功能亮点",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 15.sp,
                        color = colors.OnBackground
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    FeatureItem(icon = Icons.Filled.SmartToy, title = "AI 智能记账", description = "自然语言对话，AI 自动识别金额、分类与备注")
                    FeatureItem(icon = Icons.Filled.AutoAwesome, title = "超级岛实时状态", description = "适配小米 HyperOS 超级岛，记账状态一目了然")
                    FeatureItem(icon = Icons.Filled.Insights, title = "数据统计洞察", description = "可视化收支趋势，按日/月/年多维度分析")
                    FeatureItem(icon = Icons.Filled.Category, title = "自定义分类", description = "灵活管理收支分类，个性化记账体验")
                    FeatureItem(icon = Icons.Filled.NotificationsActive, title = "智能提醒", description = "记账成功与失败实时通知，不遗漏每笔记录")
                    FeatureItem(icon = Icons.Filled.DarkMode, title = "深色模式", description = "护眼暗色主题，随系统自动切换")
                    FeatureItem(icon = Icons.Filled.CloudSync, title = "数据导入导出", description = "JSON 格式备份与恢复，数据安全有保障")
                    FeatureItem(icon = Icons.Filled.Security, title = "本地优先", description = "数据存储在设备本地，隐私安全不泄露")
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxxl))

            Text(
                text = "\u00A9 2026 风起",
                fontFamily = JakartaFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = colors.TextTertiary
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            Text(
                text = "Made with love by 风起",
                fontFamily = JakartaFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = colors.TextQuaternary,
                modifier = Modifier.padding(bottom = Spacing.xxxl)
            )
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    val colors = QiTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.Primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 14.sp,
                color = colors.OnBackground
            )
            Text(
                text = description,
                fontFamily = JakartaFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                color = colors.TextTertiary
            )
        }
    }
}
