package com.qinoteapp.qinoteapp.ui.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.qinoteapp.qinoteapp.components.NavBarMode
import com.qinoteapp.qinoteapp.components.QiToastData
import com.qinoteapp.qinoteapp.components.QiToastHost
import com.qinoteapp.qinoteapp.components.QiToggleSwitch
import com.qinoteapp.qinoteapp.components.SectionCard
import com.qinoteapp.qinoteapp.components.SectionHeader
import com.qinoteapp.qinoteapp.components.TopNavBar
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import kotlinx.coroutines.launch

@Composable
fun AiConfigScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = QiTheme.colors
    val scope = rememberCoroutineScope()

    var apiAddress by remember(uiState.aiConfig.apiAddress) { mutableStateOf(uiState.aiConfig.apiAddress) }
    var apiKey by remember(uiState.aiConfig.apiKey) { mutableStateOf(uiState.aiConfig.apiKey) }
    var textModel by remember(uiState.aiConfig.textModel) { mutableStateOf(uiState.aiConfig.textModel) }
    var visionModel by remember(uiState.aiConfig.visionModel) { mutableStateOf(uiState.aiConfig.visionModel) }
    var isTesting by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }
    var isTestingVision by remember { mutableStateOf(false) }
    var visionTestResult by remember { mutableStateOf<String?>(null) }
    var toastMessage by remember { mutableStateOf<QiToastData?>(null) }
    var apiKeyVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopNavBar(mode = NavBarMode.SubPageMode("AI功能设置") { navController.popBackStack() })
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
                    title = "功能开关",
                    subtitle = "控制AI功能的启用和行为"
                )
                SectionCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AI功能",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.W600,
                                fontSize = 14.sp,
                                color = colors.OnBackground
                            )
                            Text(
                                text = "启用AI智能识别功能",
                                fontFamily = JakartaFontFamily,
                                fontWeight = FontWeight.W400,
                                fontSize = 12.sp,
                                color = colors.TextTertiary
                            )
                        }
                        QiToggleSwitch(
                            checked = uiState.aiConfig.aiEnabled,
                            onCheckedChange = { enabled ->
                                viewModel.updateAiConfig(uiState.aiConfig.copy(aiEnabled = enabled))
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.lg))

                    HorizontalDivider(color = colors.Border)

                    Spacer(modifier = Modifier.height(Spacing.lg))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "自动重试",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.W600,
                                fontSize = 14.sp,
                                color = colors.OnBackground
                            )
                            Text(
                                text = "请求失败时自动重试",
                                fontFamily = JakartaFontFamily,
                                fontWeight = FontWeight.W400,
                                fontSize = 12.sp,
                                color = colors.TextTertiary
                            )
                        }
                        QiToggleSwitch(
                            checked = uiState.aiConfig.autoRetry,
                            onCheckedChange = { enabled ->
                                viewModel.updateAiConfig(uiState.aiConfig.copy(autoRetry = enabled))
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                SectionHeader(
                    title = "模型配置",
                    subtitle = "设置API连接和模型参数"
                )
                SectionCard {
                    ConfigField(
                        label = "API 地址",
                        value = apiAddress,
                        onValueChange = { apiAddress = it },
                        placeholder = "https://api.openai.com/v1"
                    )

                    Spacer(modifier = Modifier.height(Spacing.lg))

                    ApiKeyField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        visible = apiKeyVisible,
                        onToggleVisible = { apiKeyVisible = !apiKeyVisible }
                    )

                    Spacer(modifier = Modifier.height(Spacing.lg))

                    ConfigField(
                        label = "文本模型",
                        value = textModel,
                        onValueChange = { textModel = it },
                        placeholder = "gpt-4o-mini"
                    )

                    Spacer(modifier = Modifier.height(Spacing.lg))

                    ConfigField(
                        label = "多模态模型",
                        value = visionModel,
                        onValueChange = { visionModel = it },
                        placeholder = "gpt-4o"
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                SectionHeader(
                    title = "连接测试",
                    subtitle = "验证API配置是否正确"
                )
                SectionCard {
                    val textTestSuccess = testResult == "连接成功"
                    TextButton(
                        onClick = {
                            scope.launch {
                                isTesting = true
                                testResult = null
                                val result = viewModel.testConnection(apiAddress, apiKey, textModel)
                                testResult = result.getOrElse { it.message }
                                isTesting = false
                            }
                        },
                        enabled = !isTesting && !textTestSuccess,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = if (textTestSuccess) colors.IncomeSoft else colors.Surface1,
                            contentColor = if (textTestSuccess) colors.Income else colors.OnBackground,
                            disabledContainerColor = if (textTestSuccess) colors.IncomeSoft else colors.Surface1,
                            disabledContentColor = if (textTestSuccess) colors.Income else colors.TextTertiary
                        ),
                        shape = RoundedCornerShape(QiRadius.lg)
                    ) {
                        Text(
                            text = when {
                                isTesting -> "测试中..."
                                textTestSuccess -> "连接成功 ✓"
                                else -> "测试文本模型"
                            },
                            fontFamily = JakartaFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = Spacing.xs)
                        )
                    }

                    if (testResult != null && !textTestSuccess) {
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = testResult!!,
                            fontFamily = JakartaFontFamily,
                            fontSize = 13.sp,
                            color = colors.Expense,
                            modifier = Modifier.padding(horizontal = Spacing.xs)
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.md))

                    val visionTestSuccess = visionTestResult == "连接成功"
                    TextButton(
                        onClick = {
                            scope.launch {
                                isTestingVision = true
                                visionTestResult = null
                                val result = viewModel.testConnection(apiAddress, apiKey, visionModel)
                                visionTestResult = result.getOrElse { it.message }
                                isTestingVision = false
                            }
                        },
                        enabled = !isTestingVision && !visionTestSuccess,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = if (visionTestSuccess) colors.IncomeSoft else colors.Surface1,
                            contentColor = if (visionTestSuccess) colors.Income else colors.OnBackground,
                            disabledContainerColor = if (visionTestSuccess) colors.IncomeSoft else colors.Surface1,
                            disabledContentColor = if (visionTestSuccess) colors.Income else colors.TextTertiary
                        ),
                        shape = RoundedCornerShape(QiRadius.lg)
                    ) {
                        Text(
                            text = when {
                                isTestingVision -> "测试中..."
                                visionTestSuccess -> "连接成功 ✓"
                                else -> "测试多模态模型"
                            },
                            fontFamily = JakartaFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = Spacing.xs)
                        )
                    }

                    if (visionTestResult != null && !visionTestSuccess) {
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = visionTestResult!!,
                            fontFamily = JakartaFontFamily,
                            fontSize = 13.sp,
                            color = colors.Expense,
                            modifier = Modifier.padding(horizontal = Spacing.xs)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(QiRadius.lg))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(colors.Primary, colors.PrimaryLight)
                            )
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                viewModel.updateAiConfig(
                                    uiState.aiConfig.copy(
                                        apiAddress = apiAddress,
                                        apiKey = apiKey,
                                        textModel = textModel,
                                        visionModel = visionModel
                                    )
                                )
                                toastMessage = QiToastData(message = "配置已保存")
                            }
                        )
                        .padding(vertical = Spacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "保存配置",
                        fontFamily = JakartaFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = colors.OnPrimary
                    )
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

@Composable
private fun ConfigField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isPassword: Boolean = false
) {
    val colors = QiTheme.colors
    Column {
        Text(
            text = label,
            fontFamily = JakartaFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = colors.OnSurface
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = JakartaFontFamily,
                    fontSize = 14.sp,
                    color = colors.TextQuaternary
                )
            },
            textStyle = TextStyle(
                fontFamily = JakartaFontFamily,
                fontSize = 14.sp,
                color = colors.OnBackground
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.Primary,
                unfocusedBorderColor = colors.BorderStrong,
                cursorColor = colors.Primary,
                focusedContainerColor = colors.Surface,
                unfocusedContainerColor = colors.Surface
            ),
            shape = RoundedCornerShape(QiRadius.lg)
        )
    }
}

@Composable
private fun ApiKeyField(
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onToggleVisible: () -> Unit
) {
    val colors = QiTheme.colors
    Column {
        Text(
            text = "API 密钥",
            fontFamily = JakartaFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = colors.OnSurface
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "请输入API Key",
                    fontFamily = JakartaFontFamily,
                    fontSize = 14.sp,
                    color = colors.TextQuaternary
                )
            },
            textStyle = TextStyle(
                fontFamily = JakartaFontFamily,
                fontSize = 14.sp,
                color = colors.OnBackground
            ),
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = if (visible) KeyboardOptions.Default else KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = onToggleVisible,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = colors.TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.Primary,
                unfocusedBorderColor = colors.BorderStrong,
                cursorColor = colors.Primary,
                focusedContainerColor = colors.Surface,
                unfocusedContainerColor = colors.Surface
            ),
            shape = RoundedCornerShape(QiRadius.lg)
        )
    }
}