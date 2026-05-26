package com.qinoteapp.qinoteapp.ui.settings

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.qinoteapp.qinoteapp.components.DeleteConfirmSheet
import com.qinoteapp.qinoteapp.components.NavBarMode
import com.qinoteapp.qinoteapp.components.QiSheet
import com.qinoteapp.qinoteapp.components.QiTextField
import com.qinoteapp.qinoteapp.components.SegmentedControl
import com.qinoteapp.qinoteapp.components.QiToastData
import com.qinoteapp.qinoteapp.components.QiToastHost
import com.qinoteapp.qinoteapp.components.SheetDragHandle
import com.qinoteapp.qinoteapp.components.StaggeredItem
import com.qinoteapp.qinoteapp.components.TopNavBar
import com.qinoteapp.qinoteapp.components.qiPressScale
import com.qinoteapp.qinoteapp.data.entity.CategoryEntity
import com.qinoteapp.qinoteapp.data.entity.CategoryIcons
import com.qinoteapp.qinoteapp.ui.theme.CategoryColors
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

private fun getCategoryColor(categoryId: String): Color {
    return when (categoryId) {
        "food" -> CategoryColors.Food
        "transport" -> CategoryColors.Transport
        "shopping" -> CategoryColors.Shopping
        "entertainment" -> CategoryColors.Entertainment
        "living" -> CategoryColors.Living
        "daily" -> CategoryColors.Daily
        "health" -> CategoryColors.Health
        "education" -> CategoryColors.Education
        "social" -> CategoryColors.Social
        "pet" -> CategoryColors.Pet
        "invest_expense" -> CategoryColors.InvestExpense
        "digital" -> CategoryColors.Digital
        "housing" -> CategoryColors.Housing
        "salary" -> CategoryColors.Salary
        "freelance" -> CategoryColors.Freelance
        "investment" -> CategoryColors.Investment
        else -> {
            val hash = categoryId.hashCode()
            val index = Math.abs(hash) % CategoryColors.AllColors.size
            CategoryColors.AllColors[index]
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManageScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = QiTheme.colors

    var selectedTypeIndex by remember { mutableStateOf(0) }
    val selectedType = if (selectedTypeIndex == 0) "expense" else "income"
    val filteredCategories = uiState.categories.filter { it.type == selectedType }
    val builtinCategories = filteredCategories.filter { it.isBuiltin }
    val customCategories = filteredCategories.filter { !it.isBuiltin }

    var showAddSheet by remember { mutableStateOf(false) }
    var showActionSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var deletingCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var toastMessage by remember { mutableStateOf<QiToastData?>(null) }

    if (showDeleteConfirm && deletingCategory != null) {
        DeleteConfirmSheet(
            onDismiss = {
                showDeleteConfirm = false
                deletingCategory = null
            },
            onConfirm = {
                viewModel.deleteCategory(deletingCategory!!)
                showDeleteConfirm = false
                deletingCategory = null
                toastMessage = QiToastData(message = "分类已删除")
            },
            title = "删除分类",
            message = "确定要删除「${deletingCategory!!.name}」吗？"
        )
    }

    if (showAddSheet) {
        AddCategorySheet(
            type = selectedType,
            onConfirm = { name ->
                viewModel.addCategory(
                    CategoryEntity(
                        id = java.util.UUID.randomUUID().toString(),
                        name = name,
                        icon = "custom",
                        type = selectedType,
                        isBuiltin = false,
                        sortOrder = customCategories.size
                    )
                )
                showAddSheet = false
                toastMessage = QiToastData(message = "分类已添加")
            },
            onDismiss = { showAddSheet = false }
        )
    }

    if (showActionSheet && selectedCategory != null) {
        CategoryActionSheet(
            categoryName = selectedCategory!!.name,
            onEdit = {
                showActionSheet = false
                showEditSheet = true
            },
            onDelete = {
                showActionSheet = false
                deletingCategory = selectedCategory
                showDeleteConfirm = true
            },
            onDismiss = {
                showActionSheet = false
                selectedCategory = null
            }
        )
    }

    if (showEditSheet && selectedCategory != null) {
        EditCategorySheet(
            category = selectedCategory!!,
            onConfirm = { updated ->
                viewModel.updateCategory(updated)
                showEditSheet = false
                selectedCategory = null
                toastMessage = QiToastData(message = "分类已更新")
            },
            onDismiss = {
                showEditSheet = false
                selectedCategory = null
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        topBar = {
            TopNavBar(mode = NavBarMode.SubPageMode("分类管理") { navController.popBackStack() })
        },
        bottomBar = {
            Surface(
                color = colors.Background
            ) {
                Row(
                    modifier = Modifier
                        .qiPressScale()
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg)
                        .padding(bottom = Spacing.lg)
                        .height(52.dp)
                        .clip(RoundedCornerShape(QiRadius.xl))
                        .background(colors.Primary)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { showAddSheet = true }
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "添加分类",
                        tint = colors.OnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = "添加分类",
                        fontFamily = JakartaFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = colors.OnPrimary
                    )
                }
            }
        },
        containerColor = colors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(modifier = Modifier.height(Spacing.lg))

            SegmentedControl(
                segments = listOf("支出", "收入"),
                selectedIndex = selectedTypeIndex,
                onSegmentSelected = { selectedTypeIndex = it },
                activeColor = if (selectedTypeIndex == 0) colors.Expense else colors.Income,
                modifier = Modifier.padding(horizontal = Spacing.lg)
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            if (filteredCategories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无分类",
                        fontFamily = JakartaFontFamily,
                        fontSize = 14.sp,
                        color = colors.TextTertiary
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    if (builtinCategories.isNotEmpty()) {
                        item(span = { GridItemSpan(3) }) {
                            SectionHeader(title = "内置分类")
                        }
                        items(builtinCategories, key = { it.id }) { category ->
                            StaggeredItem(index = builtinCategories.indexOf(category)) {
                                CategoryGridItem(
                                    category = category,
                                    isBuiltin = true,
                                    onTap = {}
                                )
                            }
                        }
                    }

                    if (customCategories.isNotEmpty()) {
                        item(span = { GridItemSpan(3) }) {
                            SectionHeader(title = "自定义分类")
                        }
                        items(customCategories, key = { it.id }) { category ->
                            StaggeredItem(index = builtinCategories.size + customCategories.indexOf(category)) {
                                CategoryGridItem(
                                    category = category,
                                    isBuiltin = false,
                                    onTap = {
                                        selectedCategory = category
                                        showActionSheet = true
                                    }
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(Spacing.xl))
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

@Composable
private fun SectionHeader(title: String) {
    val colors = QiTheme.colors
    Text(
        text = title,
        fontFamily = JakartaFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = colors.TextTertiary,
        modifier = Modifier.padding(vertical = Spacing.xs)
    )
}

@Composable
private fun CategoryGridItem(
    category: CategoryEntity,
    isBuiltin: Boolean = false,
    onTap: () -> Unit
) {
    val colors = QiTheme.colors
    val categoryColor = getCategoryColor(category.id)

    Surface(
        modifier = Modifier
            .qiPressScale()
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap
            ),
        shape = RoundedCornerShape(QiRadius.xl),
        color = colors.Surface
    ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(categoryColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (category.icon == "custom") {
                        Text(
                            text = category.name.firstOrNull()?.toString() ?: "",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.W700,
                            fontSize = 18.sp,
                            color = categoryColor
                        )
                    } else {
                        Icon(
                            imageVector = CategoryIcons.getIcon(category.icon),
                            contentDescription = category.name,
                            tint = categoryColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = category.name,
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 13.sp,
                    color = colors.OnBackground
                )
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryActionSheet(
    categoryName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = QiTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    QiSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xxl)
        ) {
            Text(
                text = categoryName,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 18.sp,
                color = colors.OnBackground
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            Row(
                modifier = Modifier
                    .qiPressScale()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(QiRadius.lg))
                    .background(colors.Surface1)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onEdit
                    )
                    .padding(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "编辑",
                    tint = colors.OnBackground,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.md))
                Text(
                    text = "编辑",
                    fontFamily = JakartaFontFamily,
                    fontWeight = FontWeight.W500,
                    fontSize = 15.sp,
                    color = colors.OnBackground
                )
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                modifier = Modifier
                    .qiPressScale()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(QiRadius.lg))
                    .background(colors.ExpenseSoft)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDelete
                    )
                    .padding(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "删除",
                    tint = colors.Expense,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.md))
                Text(
                    text = "删除",
                    fontFamily = JakartaFontFamily,
                    fontWeight = FontWeight.W500,
                    fontSize = 15.sp,
                    color = colors.Expense
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCategorySheet(
    type: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = QiTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf("") }

    QiSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xxl)
        ) {
            Text(
                text = "添加分类",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 18.sp,
                color = colors.OnBackground
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            QiTextField(
                value = name,
                onValueChange = { name = it },
                label = "分类名称",
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )

            if (name.isNotBlank()) {
                Spacer(modifier = Modifier.height(Spacing.lg))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val previewColor = getCategoryColor("preview_${name.hashCode()}")
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(previewColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.firstOrNull()?.toString() ?: "",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.W700,
                            fontSize = 16.sp,
                            color = previewColor
                        )
                    }
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Text(
                        text = name,
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                        color = colors.OnBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            Row(
                modifier = Modifier
                    .qiPressScale()
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(QiRadius.lg))
                    .background(
                        if (name.isNotBlank()) colors.Primary else colors.Surface2
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(name.trim())
                            }
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "确认添加",
                    fontFamily = JakartaFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = if (name.isNotBlank()) colors.OnPrimary else colors.TextTertiary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditCategorySheet(
    category: CategoryEntity,
    onConfirm: (CategoryEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = QiTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf(category.name) }

    QiSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xxl)
        ) {
            Text(
                text = "编辑分类",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 18.sp,
                color = colors.OnBackground
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            QiTextField(
                value = name,
                onValueChange = { name = it },
                label = "分类名称",
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            Row(
                modifier = Modifier
                    .qiPressScale()
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(QiRadius.lg))
                    .background(
                        if (name.isNotBlank()) colors.Primary else colors.Surface2
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(
                                    category.copy(
                                        name = name.trim(),
                                        icon = "custom"
                                    )
                                )
                            }
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "保存修改",
                    fontFamily = JakartaFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = if (name.isNotBlank()) colors.OnPrimary else colors.TextTertiary
                )
            }
        }
    }
}
