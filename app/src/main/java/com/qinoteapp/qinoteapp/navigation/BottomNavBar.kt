package com.qinoteapp.qinoteapp.navigation

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.qinoteapp.qinoteapp.components.FABEnter
import com.qinoteapp.qinoteapp.components.qiPressScale
import com.qinoteapp.qinoteapp.ui.theme.QiNavLabel
import com.qinoteapp.qinoteapp.ui.theme.QiNavLabelSelected
import com.qinoteapp.qinoteapp.ui.theme.QiDuration
import com.qinoteapp.qinoteapp.ui.theme.QiSize
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@Composable
fun BottomNavBar(
    currentRoute: String,
    onRouteSelected: (String) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        val view = LocalView.current
        HorizontalDivider(color = QiTheme.colors.Border)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            QiTheme.colors.Surface.copy(alpha = 0.78f),
                            QiTheme.colors.Surface.copy(alpha = 0.95f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(QiSize.navBarHeight)
                    .padding(horizontal = Spacing.xl),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavTabItem(
                    selected = currentRoute == QiRoute.Home.route,
                    onClick = { onRouteSelected(QiRoute.Home.route) },
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    label = "首页"
                )

                Box(
                    modifier = Modifier.offset(y = (-24).dp),
                    contentAlignment = Alignment.Center
                ) {
                    FABEnter(visible = true) {
                        FloatingActionButton(
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                onFabClick()
                            },
                            containerColor = Color.Transparent,
                            shape = CircleShape,
                            elevation = FloatingActionButtonDefaults.elevation(),
                            modifier = Modifier.size(QiSize.fabSize)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(QiSize.fabSize)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                QiTheme.colors.Primary,
                                                QiTheme.colors.PrimaryLight
                                            )
                                        ),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "添加",
                                    tint = QiTheme.colors.OnPrimary,
                                    modifier = Modifier.size(QiSize.iconLarge)
                                )
                            }
                        }
                    }
                }

                NavTabItem(
                    selected = currentRoute == QiRoute.Stats.route,
                    onClick = { onRouteSelected(QiRoute.Stats.route) },
                    selectedIcon = Icons.AutoMirrored.Filled.ShowChart,
                    unselectedIcon = Icons.AutoMirrored.Outlined.ShowChart,
                    label = "统计"
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    selected: Boolean,
    onClick: () -> Unit,
    selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    val iconColor by animateColorAsState(
        targetValue = if (selected) QiTheme.colors.Primary else QiTheme.colors.TextTertiary,
        animationSpec = tween(QiDuration.Normal),
        label = "navIconColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) QiTheme.colors.Primary else QiTheme.colors.TextTertiary,
        animationSpec = tween(QiDuration.Normal),
        label = "navTextColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .qiPressScale()
            .clip(RoundedCornerShape(Spacing.md))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = Spacing.lg, vertical = Spacing.xs)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
    ) {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(QiSize.iconLarge)
            )
            Spacer(modifier = Modifier.height(Spacing.xxs))
            Text(
                text = label,
                color = textColor,
                style = if (selected) QiNavLabelSelected else QiNavLabel
            )
        }
}
