package com.qinoteapp.qinoteapp.ui.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.activity.compose.BackHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.qinoteapp.qinoteapp.components.AuroraGlow
import com.qinoteapp.qinoteapp.components.BillCompareDialog
import com.qinoteapp.qinoteapp.components.ConfirmDialog
import com.qinoteapp.qinoteapp.components.DatePickerSheet
import com.qinoteapp.qinoteapp.components.DeleteConfirmSheet
import com.qinoteapp.qinoteapp.components.MeshBackground
import com.qinoteapp.qinoteapp.components.NavBarMode
import com.qinoteapp.qinoteapp.components.NoiseOverlay
import com.qinoteapp.qinoteapp.components.QiButton
import com.qinoteapp.qinoteapp.components.QiImageViewer
import com.qinoteapp.qinoteapp.components.QiTextField
import com.qinoteapp.qinoteapp.components.TimePickerSheet
import com.qinoteapp.qinoteapp.components.TopNavBar
import com.qinoteapp.qinoteapp.data.entity.BillEntity
import com.qinoteapp.qinoteapp.data.entity.CategoryIcons
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiDetailTitle
import com.qinoteapp.qinoteapp.ui.theme.QiDetailAmount
import com.qinoteapp.qinoteapp.ui.theme.QiDetailLabel
import com.qinoteapp.qinoteapp.ui.theme.QiDetailValue
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import com.qinoteapp.qinoteapp.util.AmountUtils

@Composable
fun BillDetailScreen(
    navController: NavHostController,
    billId: String,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val reAnalyzeState by viewModel.reAnalyzeState.collectAsState()
    val colors = QiTheme.colors
    val keyboardController = LocalSoftwareKeyboardController.current

    val bill = uiState.bills.find { it.id == billId }
    val categoryInfo = bill?.let { uiState.categories[it.category] }

    if (bill == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.Background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "账单不存在",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.TextTertiary
            )
        }
        return
    }

    var isEditing by remember { mutableStateOf(false) }
    var hasUnsavedChanges by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showDeleteSheet by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var viewingImageUri by remember { mutableStateOf<String?>(null) }

    var editTitle by remember(bill.id) { mutableStateOf(bill.title) }
    var editAmount by remember(bill.id, bill.amount) { mutableStateOf(AmountUtils.centsToYuan(bill.amount).toString()) }
    var editNote by remember(bill.id) { mutableStateOf(bill.note) }
    var editDate by remember(bill.id) { mutableStateOf(bill.date) }
    var editTime by remember(bill.id) { mutableStateOf(bill.time) }

    BackHandler(enabled = isEditing) {
        if (hasUnsavedChanges) {
            showDiscardDialog = true
        } else {
            isEditing = false
            editTitle = bill.title
            editAmount = AmountUtils.centsToYuan(bill.amount).toString()
            editNote = bill.note
            editDate = bill.date
            editTime = bill.time
        }
    }

    val isExpense = bill.type == "expense"
    val categoryName = categoryInfo?.first ?: bill.category
    val categoryIcon = categoryInfo?.second
    val categoryColor = CategoryIcons.getCategoryColor(bill.category, isSystemInDarkTheme())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.Background)
    ) {
        MeshBackground()
        NoiseOverlay()

        Column(modifier = Modifier.fillMaxSize()) {
            TopNavBar(
                mode = NavBarMode.DetailMode(
                    title = if (isEditing) "编辑账单" else "账单详情",
                    onBackClick = {
                        if (isEditing) {
                            if (hasUnsavedChanges) {
                                showDiscardDialog = true
                            } else {
                                isEditing = false
                                editTitle = bill.title
                                editAmount = AmountUtils.centsToYuan(bill.amount).toString()
                                editNote = bill.note
                                editDate = bill.date
                                editTime = bill.time
                            }
                        } else {
                            viewModel.resetReAnalyze()
                            navController.popBackStack()
                        }
                    },
                    actions = {
                        if (!isEditing) {
                            IconButton(onClick = {
                                isEditing = true
                                hasUnsavedChanges = false
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "编辑",
                                    tint = colors.OnSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        IconButton(onClick = { showDeleteSheet = true }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "删除",
                                tint = colors.Expense,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )
            )

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Spacing.xxl)
                        .padding(top = Spacing.xl, bottom = Spacing.xxxl),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                ) {
                    if (isEditing) {
                        QiTextField(
                            value = editTitle,
                            onValueChange = {
                                editTitle = it
                                hasUnsavedChanges = true
                            },
                            label = "标题",
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )

                        QiTextField(
                            value = editAmount,
                            onValueChange = {
                                editAmount = it
                                hasUnsavedChanges = true
                            },
                            label = "金额",
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.W600,
                                fontSize = 14.sp
                            )
                        )

                        QiTextField(
                            value = editDate,
                            onValueChange = {},
                            label = "日期",
                            modifier = Modifier.fillMaxWidth(),
                            isDatePicker = true,
                            onClick = { showDatePicker = true },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = colors.TextTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyMedium
                        )

                        QiTextField(
                            value = editTime,
                            onValueChange = {},
                            label = "时间",
                            modifier = Modifier.fillMaxWidth(),
                            isDatePicker = true,
                            onClick = { showTimePicker = true },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = colors.TextTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyMedium
                        )

                        QiTextField(
                            value = editNote,
                            onValueChange = {
                                editNote = it
                                hasUnsavedChanges = true
                            },
                            label = "备注",
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            minLines = 2,
                            maxLines = 4,
                            textStyle = MaterialTheme.typography.bodyMedium
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                        ) {
                            QiButton.Secondary(
                                text = "取消",
                                onClick = {
                                    if (hasUnsavedChanges) {
                                        showDiscardDialog = true
                                    } else {
                                        isEditing = false
                                        editTitle = bill.title
                                        editAmount = AmountUtils.centsToYuan(bill.amount).toString()
                                        editNote = bill.note
                                        editDate = bill.date
                                        editTime = bill.time
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )

                            QiButton.Primary(
                                text = "保存",
                                onClick = {
                                    keyboardController?.hide()
                                    val amountValue = AmountUtils.yuanToCents(editAmount) ?: bill.amount
                                    viewModel.updateBill(
                                        bill.copy(
                                            title = editTitle,
                                            amount = amountValue,
                                            note = editNote,
                                            date = editDate,
                                            time = editTime
                                        )
                                    )
                                    viewModel.resetReAnalyze()
                                    isEditing = false
                                    hasUnsavedChanges = false
                                    viewModel.showToast("账单已更新")
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(QiRadius.xl),
                            color = colors.Surface,
                            tonalElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.lg)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                                    ) {
                                        Text(
                                            text = bill.title,
                                            style = QiDetailTitle,
                                            color = colors.OnBackground
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(categoryColor.copy(alpha = 0.1f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = CategoryIcons.getIcon(categoryIcon ?: ""),
                                                    contentDescription = categoryName,
                                                    tint = categoryColor,
                                                    modifier = Modifier.size(10.dp)
                                                )
                                            }
                                            Text(
                                                text = categoryName,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = colors.OnSurface
                                            )
                                            Text(
                                                text = "·",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = colors.TextTertiary
                                            )
                                            Text(
                                                text = if (isExpense) "支出" else "收入",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = colors.TextTertiary
                                            )
                                        }
                                    }
                                    Text(
                                        text = AmountUtils.formatCentsWithSign(bill.amount, isExpense),
                                        style = QiDetailAmount,
                                        color = if (isExpense) colors.Expense else colors.Income
                                    )
                                }

                                Spacer(modifier = Modifier.height(Spacing.lg))

                                HorizontalDivider(color = colors.Border)

                                Spacer(modifier = Modifier.height(Spacing.md))

                                DetailRow(label = "日期", value = bill.date, colors = colors)
                                Spacer(modifier = Modifier.height(Spacing.sm))
                                DetailRow(label = "时间", value = bill.time, colors = colors)
                                if (bill.note.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(Spacing.sm))
                                    DetailRow(label = "备注", value = bill.note, colors = colors)
                                }
                            }
                        }

                        if (!bill.image.isNullOrBlank()) {
                            val imageModel = remember(bill.image) {
                                val imageFile = java.io.File(bill.image)
                                when {
                                    imageFile.exists() -> Uri.fromFile(imageFile)
                                    bill.image.startsWith("content://") -> Uri.parse(bill.image)
                                    bill.image.startsWith("file://") -> Uri.parse(bill.image)
                                    else -> Uri.fromFile(imageFile)
                                }
                            }
                            AsyncImage(
                                model = imageModel,
                                contentDescription = "账单图片",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 360.dp)
                                    .clip(RoundedCornerShape(QiRadius.xl))
                                    .clickable { viewingImageUri = bill.image },
                                contentScale = ContentScale.Fit
                            )

                            QiButton.Secondary(
                                text = "重新分析",
                                icon = Icons.Filled.Refresh,
                                onClick = { viewModel.reAnalyzeBill(bill) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                if (reAnalyzeState.error != null && !isEditing) {
                    val errorMsg = reAnalyzeState.error!!
                    Surface(
                        shape = RoundedCornerShape(QiRadius.lg),
                        color = colors.Expense.copy(alpha = 0.1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.xxl, vertical = Spacing.md)
                            .align(Alignment.BottomCenter)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.md),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Error,
                                contentDescription = null,
                                tint = colors.Expense,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = errorMsg,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.Expense
                            )
                        }
                    }
                }
            }
        }
    }

    AuroraGlow(
        visible = reAnalyzeState.isLoading,
        modifier = Modifier.fillMaxSize()
    )

    if (reAnalyzeState.newParsedBill != null && !isEditing) {
        val newParsed = reAnalyzeState.newParsedBill!!
        BillCompareDialog(
            originalBill = bill,
            newParsedBill = newParsed,
            categoryNames = uiState.categories,
            onConfirm = {
                val updatedBill = bill.copy(
                    title = newParsed.title,
                    amount = newParsed.amount,
                    category = newParsed.category,
                    type = newParsed.type,
                    note = newParsed.note,
                    date = newParsed.date.ifBlank { bill.date },
                    time = newParsed.time.ifBlank { bill.time },
                    updatedAt = System.currentTimeMillis()
                )
                viewModel.updateBill(updatedBill)
                viewModel.resetReAnalyze()
                viewModel.showToast("账单已更新")
            },
            onDismiss = {
                viewModel.resetReAnalyze()
            }
        )
    }

    DatePickerSheet(
        visible = showDatePicker,
        currentDate = editDate,
        onConfirm = { date ->
            editDate = date
            showDatePicker = false
        },
        onDismiss = { showDatePicker = false }
    )

    TimePickerSheet(
        visible = showTimePicker,
        currentTime = editTime,
        onConfirm = { time ->
            editTime = time
            showTimePicker = false
        },
        onDismiss = { showTimePicker = false }
    )

    if (showDeleteSheet) {
        DeleteConfirmSheet(
            onDismiss = { showDeleteSheet = false },
            onConfirm = {
                showDeleteSheet = false
                viewModel.deleteBill(bill)
                viewModel.resetReAnalyze()
                navController.popBackStack()
            },
            title = "删除账单",
            message = "确定要删除这条账单吗？此操作无法撤销。"
        )
    }

    if (showDiscardDialog) {
        ConfirmDialog(
            title = "放弃修改？",
            message = "你有未保存的修改，确定要离开吗？",
            confirmText = "放弃",
            cancelText = "继续编辑",
            isDangerous = true,
            onConfirm = {
                showDiscardDialog = false
                isEditing = false
                editTitle = bill.title
                editAmount = AmountUtils.centsToYuan(bill.amount).toString()
                editNote = bill.note
                editDate = bill.date
                editTime = bill.time
            },
            onDismiss = {
                showDiscardDialog = false
            }
        )
    }

    QiImageViewer(
        imageUri = viewingImageUri,
        onDismiss = { viewingImageUri = null }
    )
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = QiTheme.colors.OnBackground,
    colors: com.qinoteapp.qinoteapp.ui.theme.QiColorScheme = QiTheme.colors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = QiDetailLabel,
            color = colors.TextTertiary
        )
        Text(
            text = value,
            style = QiDetailValue,
            color = valueColor
        )
    }
}
