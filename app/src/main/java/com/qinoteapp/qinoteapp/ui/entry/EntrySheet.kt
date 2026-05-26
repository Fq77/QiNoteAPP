package com.qinoteapp.qinoteapp.ui.entry

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qinoteapp.qinoteapp.components.QiSheet
import com.qinoteapp.qinoteapp.components.QiToastData
import com.qinoteapp.qinoteapp.components.QiToastHost
import com.qinoteapp.qinoteapp.components.SegmentedControl
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntrySheet(
    onDismiss: () -> Unit,
    initialImageUri: Uri? = null
) {
    val viewModel: EntryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val colors = QiTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentImageUri by remember { mutableStateOf<Uri?>(initialImageUri) }
    var toastMessage by remember { mutableStateOf<QiToastData?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            currentImageUri = it
            viewModel.setImageUri(it)
        }
    }

    LaunchedEffect(uiState.billJustAdded) {
        if (uiState.billJustAdded) {
            viewModel.resetBillAdded()
            toastMessage = QiToastData(message = "记账成功")
        }
    }

    LaunchedEffect(initialImageUri) {
        initialImageUri?.let {
            currentImageUri = it
            viewModel.setImageUri(it)
        }
    }

    QiSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(colors.Surface1),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "关闭",
                                tint = colors.OnSurface,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    SegmentedControl(
                        segments = listOf("AI记账", "手动记账"),
                        selectedIndex = uiState.mode,
                        onSegmentSelected = { viewModel.switchMode(it) },
                        icons = listOf(Icons.AutoMirrored.Filled.Chat, Icons.Default.Edit),
                        activeColor = if (uiState.mode == 0) colors.Accent else colors.Primary,
                        modifier = Modifier.width(220.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                Crossfade(
                    targetState = uiState.mode,
                    animationSpec = tween(300),
                    label = "modeSwitch"
                ) { mode ->
                    when (mode) {
                        0 -> ChatMode(
                            viewModel = viewModel,
                            onImagePick = { galleryLauncher.launch("image/*") }
                        )
                        1 -> ManualMode(
                            viewModel = viewModel,
                            imageUri = currentImageUri
                        )
                    }
                }
            }

            QiToastHost(
                toast = toastMessage,
                onDismiss = { toastMessage = null },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = Spacing.lg)
            )
        }
    }
}
