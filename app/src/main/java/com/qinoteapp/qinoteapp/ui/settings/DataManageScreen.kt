package com.qinoteapp.qinoteapp.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.qinoteapp.qinoteapp.components.DeleteConfirmSheet
import com.qinoteapp.qinoteapp.components.NavBarMode
import com.qinoteapp.qinoteapp.components.PressScale
import com.qinoteapp.qinoteapp.components.QiToastData
import com.qinoteapp.qinoteapp.components.QiToastHost
import com.qinoteapp.qinoteapp.components.QiToggleSwitch
import com.qinoteapp.qinoteapp.components.SectionCard
import com.qinoteapp.qinoteapp.components.SectionHeader
import com.qinoteapp.qinoteapp.components.SheetDragHandle
import com.qinoteapp.qinoteapp.components.TopNavBar
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import kotlinx.coroutines.launch

@Composable
fun DataManageScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = QiTheme.colors
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showClearBillsConfirm by remember { mutableStateOf(false) }
    var showClearChatConfirm by remember { mutableStateOf(false) }
    var showExportSheet by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<QiToastData?>(null) }

    val exportJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val result = viewModel.exportData("json")
                result.onSuccess { data ->
                    context.contentResolver.openOutputStream(it)?.use { os ->
                        os.write(data.toByteArray(Charsets.UTF_8))
                    }
                    toastMessage = QiToastData(message = "数据已导出")
                }.onFailure {
                    toastMessage = QiToastData(message = "导出失败")
                }
            }
        }
    }

    val exportCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val result = viewModel.exportData("csv")
                result.onSuccess { data ->
                    context.contentResolver.openOutputStream(it)?.use { os ->
                        os.write(data.toByteArray(Charsets.UTF_8))
                    }
                    toastMessage = QiToastData(message = "数据已导出")
                }.onFailure {
                    toastMessage = QiToastData(message = "导出失败")
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                try {
                    val json = context.contentResolver.openInputStream(it)?.use { input ->
                        input.bufferedReader().readText()
                    } ?: return@launch
                    val result = viewModel.importData(json)
                    toastMessage = QiToastData(message = if (result.isSuccess) "数据已导入" else "导入失败")
                } catch (_: Exception) {
                    toastMessage = QiToastData(message = "导入失败")
                }
            }
        }
    }

    if (showClearBillsConfirm) {
        DeleteConfirmSheet(
            title = "清空账单",
            message = "确定要清空所有账单数据吗？此操作不可恢复。",
            onDismiss = {
                showClearBillsConfirm = false
            },
            onConfirm = {
                viewModel.clearAllBills()
                showClearBillsConfirm = false
                toastMessage = QiToastData(message = "账单已清空")
            }
        )
    }

    if (showClearChatConfirm) {
        DeleteConfirmSheet(
            title = "清空聊天记录",
            message = "确定要清空所有聊天记录吗？此操作不可恢复。",
            onDismiss = {
                showClearChatConfirm = false
            },
            onConfirm = {
                viewModel.clearChatHistory()
                showClearChatConfirm = false
                toastMessage = QiToastData(message = "聊天记录已清空")
            }
        )
    }

    if (showExportSheet) {
        ExportFormatSheet(
            onDismiss = { showExportSheet = false },
            onExportJson = {
                showExportSheet = false
                exportJsonLauncher.launch("qinote_export_${System.currentTimeMillis()}.json")
            },
            onExportCsv = {
                showExportSheet = false
                exportCsvLauncher.launch("qinote_export_${System.currentTimeMillis()}.csv")
            }
        )
    }

    Scaffold(
        topBar = {
            TopNavBar(mode = NavBarMode.SubPageMode("数据管理") { navController.popBackStack() })
        },
        containerColor = colors.Background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.lg)
                    .padding(bottom = Spacing.xxxl)
            ) {
                Spacer(modifier = Modifier.height(Spacing.lg))

                SectionHeader(
                    title = "账单数据",
                    subtitle = "管理您的收支记录"
                )

                SectionCard {
                    PressScale {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(QiRadius.lg))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { showExportSheet = true }
                                )
                                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CloudDownload,
                                contentDescription = null,
                                tint = colors.Accent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "导出数据",
                                    fontFamily = JakartaFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = colors.OnBackground
                                )
                                Text(
                                    text = "选择导出格式，数据将保存到您选择的位置",
                                    fontFamily = JakartaFontFamily,
                                    fontWeight = FontWeight.W400,
                                    fontSize = 12.sp,
                                    color = colors.TextTertiary
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        color = colors.Border,
                        modifier = Modifier.padding(horizontal = Spacing.lg)
                    )

                    PressScale {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(QiRadius.lg))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        importLauncher.launch(arrayOf("application/json", "*/*"))
                                    }
                                )
                                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CloudUpload,
                                contentDescription = null,
                                tint = colors.Accent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "选择文件导入",
                                    fontFamily = JakartaFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = colors.OnBackground
                                )
                                Text(
                                    text = "支持 JSON 格式导入，将覆盖现有数据",
                                    fontFamily = JakartaFontFamily,
                                    fontWeight = FontWeight.W400,
                                    fontSize = 12.sp,
                                    color = colors.TextTertiary
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        color = colors.Border,
                        modifier = Modifier.padding(horizontal = Spacing.lg)
                    )

                    PressScale {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(QiRadius.lg))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { showClearBillsConfirm = true }
                                )
                                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteForever,
                                contentDescription = null,
                                tint = colors.Expense,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "清空所有账单",
                                    fontFamily = JakartaFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = colors.Expense
                                )
                                Text(
                                    text = "此操作不可恢复，请谨慎操作",
                                    fontFamily = JakartaFontFamily,
                                    fontWeight = FontWeight.W400,
                                    fontSize = 12.sp,
                                    color = colors.TextTertiary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                SectionHeader(
                    title = "聊天记录",
                    subtitle = "管理AI对话数据"
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
                                text = "保存聊天记录",
                                fontFamily = JakartaFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = colors.OnBackground
                            )
                            Text(
                                text = "保留AI对话记录以便回顾",
                                fontFamily = JakartaFontFamily,
                                fontWeight = FontWeight.W400,
                                fontSize = 12.sp,
                                color = colors.TextTertiary
                            )
                        }
                        QiToggleSwitch(
                            checked = uiState.aiConfig.saveChatHistory,
                            onCheckedChange = { enabled ->
                                viewModel.updateSaveChatHistory(enabled)
                            }
                        )
                    }

                    HorizontalDivider(
                        color = colors.Border,
                        modifier = Modifier.padding(horizontal = Spacing.lg)
                    )

                    PressScale {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(QiRadius.lg))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { showClearChatConfirm = true }
                                )
                                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ChatBubbleOutline,
                                contentDescription = null,
                                tint = colors.Expense,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "清空聊天记录",
                                    fontFamily = JakartaFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = colors.Expense
                                )
                                Text(
                                    text = "清空后无法恢复，请谨慎操作",
                                    fontFamily = JakartaFontFamily,
                                    fontWeight = FontWeight.W400,
                                    fontSize = 12.sp,
                                    color = colors.TextTertiary
                                )
                            }
                        }
                    }
                }
            }

            QiToastHost(
                toast = toastMessage,
                onDismiss = { toastMessage = null },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExportFormatSheet(
    onDismiss: () -> Unit,
    onExportJson: () -> Unit,
    onExportCsv: () -> Unit
) {
    val colors = QiTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.Surface,
        dragHandle = { SheetDragHandle() },
        shape = RoundedCornerShape(topStart = QiRadius.xxl, topEnd = QiRadius.xxl)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xxl)
        ) {
            Text(
                text = "选择导出格式",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 18.sp,
                color = colors.OnBackground
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            PressScale {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(QiRadius.lg))
                        .background(colors.Surface1)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onExportJson
                        )
                        .padding(Spacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "JSON",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.W600,
                            fontSize = 15.sp,
                            color = colors.OnBackground
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = "结构化数据格式，适合备份和迁移",
                            fontFamily = JakartaFontFamily,
                            fontWeight = FontWeight.W400,
                            fontSize = 13.sp,
                            color = colors.TextTertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            PressScale {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(QiRadius.lg))
                        .background(colors.Surface1)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onExportCsv
                        )
                        .padding(Spacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CSV",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.W600,
                            fontSize = 15.sp,
                            color = colors.OnBackground
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = "表格格式，可用Excel打开查看",
                            fontFamily = JakartaFontFamily,
                            fontWeight = FontWeight.W400,
                            fontSize = 13.sp,
                            color = colors.TextTertiary
                        )
                    }
                }
            }
        }
    }
}
