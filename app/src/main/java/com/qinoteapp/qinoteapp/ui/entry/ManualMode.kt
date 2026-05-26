package com.qinoteapp.qinoteapp.ui.entry

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.qinoteapp.qinoteapp.components.QiTextField
import com.qinoteapp.qinoteapp.components.QiButton
import com.qinoteapp.qinoteapp.components.qiPressScale
import com.qinoteapp.qinoteapp.components.SegmentedControl
import com.qinoteapp.qinoteapp.components.SheetDragHandle
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiFieldLabel
import com.qinoteapp.qinoteapp.ui.theme.QiUploadLabel
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualMode(
    viewModel: EntryViewModel,
    imageUri: Uri?
) {
    val uiState by viewModel.uiState.collectAsState()
    val entry = uiState.manualEntry
    val colors = QiTheme.colors
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setImageUri(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = Spacing.lg)
    ) {
        SegmentedControl(
            segments = listOf("支出", "收入"),
            selectedIndex = if (entry.type == "expense") 0 else 1,
            onSegmentSelected = { index ->
                viewModel.updateManualEntry {
                    it.copy(
                        type = if (index == 0) "expense" else "income",
                        selectedCategory = ""
                    )
                }
            },
            activeColor = if (entry.type == "expense") colors.Expense else colors.Income
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            text = "选择分类",
            style = QiFieldLabel,
            color = colors.OnBackground
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        CategoryGrid(
            categories = uiState.categories,
            selectedId = entry.selectedCategory,
            onSelect = { id ->
                viewModel.updateManualEntry { it.copy(selectedCategory = id) }
            },
            filterType = entry.type
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        QiTextField(
            value = entry.title,
            onValueChange = { text -> viewModel.updateManualEntry { it.copy(title = text) } },
            label = "标题",
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontFamily = JakartaFontFamily,
                fontSize = 14.sp,
                color = colors.OnBackground
            )
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        val amountError = if (entry.amount.isNotBlank() && entry.amount.toDoubleOrNull() == null) {
            "请输入有效金额"
        } else if (entry.amount.toDoubleOrNull()?.let { it <= 0 } == true) {
            "金额必须大于0"
        } else null

        QiTextField(
            value = entry.amount,
            onValueChange = { text -> viewModel.updateManualEntry { it.copy(amount = text) } },
            label = "金额",
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 20.sp,
                color = if (amountError != null) colors.Expense else (if (entry.type == "expense") colors.Expense else colors.Income)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            prefix = {
                Text(
                    text = "¥",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 20.sp,
                    color = colors.TextTertiary
                )
            }
        )

        if (amountError != null) {
            Spacer(modifier = Modifier.height(Spacing.xxs))
            Text(
                text = amountError,
                style = MaterialTheme.typography.bodySmall,
                color = colors.Expense,
                modifier = Modifier.padding(start = Spacing.lg)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        QiTextField(
            value = entry.date,
            onValueChange = {},
            label = "日期",
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontFamily = JakartaFontFamily,
                fontSize = 14.sp,
                color = colors.OnBackground
            ),
            isDatePicker = true,
            onClick = { showDatePicker = true },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "选择日期",
                        tint = colors.OnSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        QiTextField(
            value = entry.time,
            onValueChange = {},
            label = "时间",
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontFamily = JakartaFontFamily,
                fontSize = 14.sp,
                color = colors.OnBackground
            ),
            isDatePicker = true,
            onClick = { showTimePicker = true },
            trailingIcon = {
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "选择时间",
                        tint = colors.OnSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        QiTextField(
            value = entry.note,
            onValueChange = { text -> viewModel.updateManualEntry { it.copy(note = text) } },
            label = "备注",
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontFamily = JakartaFontFamily,
                fontSize = 14.sp,
                color = colors.OnBackground
            ),
            singleLine = false,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = "附件",
            style = QiFieldLabel,
            color = colors.OnBackground
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        val currentImageUri = imageUri?.toString() ?: entry.image
        if (currentImageUri != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp, max = 200.dp)
                    .clip(RoundedCornerShape(QiRadius.md))
            ) {
                AsyncImage(
                    model = currentImageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = {
                        viewModel.updateManualEntry { it.copy(image = null) }
                        viewModel.clearImageUri()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Spacing.xs)
                        .size(24.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colors.Surface.copy(alpha = 0.8f),
                        contentColor = colors.Expense
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "移除图片",
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(QiRadius.lg))
                    .background(colors.Surface1)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { galleryLauncher.launch("image/*") }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "上传图片",
                        tint = colors.TextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = "上传图片",
                        style = QiUploadLabel,
                        color = colors.TextTertiary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        QiButton.Primary(
            text = "确认记账",
            onClick = {
                keyboardController?.hide()
                viewModel.submitManualEntry()
            },
            modifier = Modifier
                .fillMaxWidth()
                .qiPressScale(),
            enabled = entry.title.isNotBlank() &&
                entry.amount.toDoubleOrNull()?.let { it > 0 } == true &&
                entry.selectedCategory.isNotBlank() &&
                amountError == null
        )

        Spacer(modifier = Modifier.height(Spacing.xl))
    }

    com.qinoteapp.qinoteapp.components.DatePickerSheet(
        visible = showDatePicker,
        currentDate = entry.date,
        onConfirm = { date ->
            viewModel.updateManualEntry { it.copy(date = date) }
            showDatePicker = false
        },
        onDismiss = { showDatePicker = false }
    )

    com.qinoteapp.qinoteapp.components.TimePickerSheet(
        visible = showTimePicker,
        currentTime = entry.time,
        onConfirm = { time ->
            viewModel.updateManualEntry { it.copy(time = time) }
            showTimePicker = false
        },
        onDismiss = { showTimePicker = false }
    )
}
