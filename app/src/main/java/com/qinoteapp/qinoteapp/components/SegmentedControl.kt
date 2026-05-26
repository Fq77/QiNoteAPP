package com.qinoteapp.qinoteapp.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme

@Composable
fun SegmentedControl(
    segments: List<String>,
    selectedIndex: Int,
    onSegmentSelected: (Int) -> Unit,
    icons: List<ImageVector>? = null,
    activeColor: Color? = null,
    modifier: Modifier = Modifier
) {
    val colors = QiTheme.colors
    val view = LocalView.current
    val density = LocalDensity.current
    val containerPadding = 4.dp
    var containerWidthPx by remember { mutableIntStateOf(0) }

    val segmentWidthDp = if (containerWidthPx > 0 && segments.isNotEmpty()) {
        with(density) { (containerWidthPx.toDp() - containerPadding * 2) / segments.size }
    } else {
        0.dp
    }

    val indicatorOffset by animateDpAsState(
        targetValue = segmentWidthDp * selectedIndex,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "indicatorOffset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(
                colors.Surface1,
                RoundedCornerShape(QiRadius.xxxl)
            )
            .padding(containerPadding)
            .onSizeChanged { containerWidthPx = it.width }
    ) {
        if (containerWidthPx > 0) {
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(segmentWidthDp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(QiRadius.lg))
                    .background(activeColor ?: colors.Primary)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            segments.forEachIndexed { index, segment ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .height(32.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                                onSegmentSelected(index)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (icons != null && index < icons.size) {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = null,
                                tint = if (isSelected) (activeColor?.let { Color.White } ?: colors.OnPrimary) else colors.OnSurface,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = segment,
                            color = if (isSelected) (activeColor?.let { Color.White } ?: colors.OnPrimary) else colors.OnSurface,
                            fontFamily = JakartaFontFamily,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
