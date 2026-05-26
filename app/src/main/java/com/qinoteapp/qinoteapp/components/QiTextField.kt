package com.qinoteapp.qinoteapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme

@Composable
fun QiTextField(
    value: String,
    onValueChange: (String) -> Unit = {},
    label: String? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 3,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    prefix: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    textStyle: TextStyle? = null,
    placeholder: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(QiRadius.lg),
    isDatePicker: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val tfColors = QiTheme.textFieldColors

    val effectiveEnabled = if (isDatePicker) false else enabled
    val effectiveReadOnly = if (isDatePicker) true else readOnly

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    } else Modifier

    val tfColorConfig = if (isDatePicker) {
        OutlinedTextFieldDefaults.colors(
            focusedBorderColor = tfColors.focusedBorderColor,
            unfocusedBorderColor = tfColors.unfocusedBorderColor,
            cursorColor = tfColors.cursorColor,
            focusedContainerColor = tfColors.focusedContainerColor,
            unfocusedContainerColor = tfColors.unfocusedContainerColor,
            focusedTextColor = tfColors.focusedTextColor,
            unfocusedTextColor = tfColors.unfocusedTextColor,
            disabledBorderColor = tfColors.unfocusedBorderColor,
            disabledContainerColor = tfColors.unfocusedContainerColor,
            disabledTextColor = tfColors.unfocusedTextColor,
            disabledTrailingIconColor = tfColors.unfocusedTextColor,
            disabledLabelColor = tfColors.unfocusedLabelColor,
            focusedLabelColor = tfColors.focusedLabelColor,
            unfocusedLabelColor = tfColors.unfocusedLabelColor
        )
    } else {
        OutlinedTextFieldDefaults.colors(
            focusedBorderColor = tfColors.focusedBorderColor,
            unfocusedBorderColor = tfColors.unfocusedBorderColor,
            cursorColor = tfColors.cursorColor,
            focusedContainerColor = tfColors.focusedContainerColor,
            unfocusedContainerColor = tfColors.unfocusedContainerColor,
            focusedTextColor = tfColors.focusedTextColor,
            unfocusedTextColor = tfColors.unfocusedTextColor,
            disabledBorderColor = tfColors.disabledBorderColor,
            disabledContainerColor = tfColors.disabledContainerColor,
            disabledTextColor = tfColors.disabledTextColor,
            disabledTrailingIconColor = tfColors.disabledTrailingIconColor,
            disabledLabelColor = tfColors.disabledLabelColor,
            focusedLabelColor = tfColors.focusedLabelColor,
            unfocusedLabelColor = tfColors.unfocusedLabelColor
        )
    }

    Box(modifier = clickModifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = label?.let {
                {
                    Text(
                        text = it,
                        fontFamily = JakartaFontFamily,
                        fontSize = 13.sp
                    )
                }
            },
            modifier = modifier,
            enabled = effectiveEnabled,
            readOnly = effectiveReadOnly,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            prefix = prefix,
            trailingIcon = trailingIcon,
            placeholder = placeholder,
            textStyle = textStyle ?: TextStyle.Default,
            colors = tfColorConfig,
            shape = shape
        )
    }
}
