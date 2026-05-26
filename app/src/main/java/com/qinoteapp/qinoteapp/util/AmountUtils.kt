package com.qinoteapp.qinoteapp.util

import java.math.BigDecimal
import java.text.DecimalFormat

object AmountUtils {

    fun yuanToCents(yuan: Double): Long {
        return BigDecimal.valueOf(yuan).multiply(BigDecimal(100)).setScale(0, java.math.RoundingMode.HALF_UP).longValueExact()
    }

    fun yuanToCents(yuanStr: String): Long? {
        val yuan = yuanStr.toDoubleOrNull() ?: return null
        return yuanToCents(yuan)
    }

    fun centsToYuan(cents: Long): Double {
        return cents / 100.0
    }

    fun formatCents(cents: Long): String {
        val df = DecimalFormat("#,##0.00")
        return df.format(centsToYuan(cents))
    }

    fun formatCentsWithSign(cents: Long, isExpense: Boolean): String {
        val sign = if (isExpense) "-" else "+"
        return "$sign¥${formatCents(cents)}"
    }
}
