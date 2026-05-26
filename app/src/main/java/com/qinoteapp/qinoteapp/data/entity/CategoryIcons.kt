package com.qinoteapp.qinoteapp.data.entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.qinoteapp.qinoteapp.ui.theme.CategoryColors
import com.qinoteapp.qinoteapp.ui.theme.CategoryColorsDark

object CategoryIcons {
    private val iconMap = mapOf<String, ImageVector>(
        "restaurant" to Icons.Filled.Restaurant,
        "directions_car" to Icons.Filled.DirectionsCar,
        "shopping_bag" to Icons.Filled.ShoppingBag,
        "sports_esports" to Icons.Filled.SportsEsports,
        "home" to Icons.Filled.Home,
        "more_horiz" to Icons.Filled.MoreHoriz,
        "local_hospital" to Icons.Filled.LocalHospital,
        "school" to Icons.Filled.School,
        "people" to Icons.Filled.People,
        "pets" to Icons.Filled.Pets,
        "trending_up" to Icons.AutoMirrored.Filled.TrendingUp,
        "devices" to Icons.Filled.Devices,
        "receipt_long" to Icons.AutoMirrored.Filled.ReceiptLong,
        "account_balance_wallet" to Icons.Filled.AccountBalanceWallet,
        "work" to Icons.Filled.Work,
        "show_chart" to Icons.AutoMirrored.Filled.ShowChart,
    )

    private val colorMap = mapOf<String, Color>(
        "food" to CategoryColors.Food,
        "transport" to CategoryColors.Transport,
        "shopping" to CategoryColors.Shopping,
        "entertainment" to CategoryColors.Entertainment,
        "living" to CategoryColors.Living,
        "daily" to CategoryColors.Daily,
        "health" to CategoryColors.Health,
        "education" to CategoryColors.Education,
        "social" to CategoryColors.Social,
        "pet" to CategoryColors.Pet,
        "invest_expense" to CategoryColors.InvestExpense,
        "digital" to CategoryColors.Digital,
        "housing" to CategoryColors.Housing,
        "salary" to CategoryColors.Salary,
        "freelance" to CategoryColors.Freelance,
        "investment" to CategoryColors.Investment,
    )

    private val darkColorMap = mapOf<String, Color>(
        "food" to CategoryColorsDark.Food,
        "transport" to CategoryColorsDark.Transport,
        "shopping" to CategoryColorsDark.Shopping,
        "entertainment" to CategoryColorsDark.Entertainment,
        "living" to CategoryColorsDark.Living,
        "daily" to CategoryColorsDark.Daily,
        "health" to CategoryColorsDark.Health,
        "education" to CategoryColorsDark.Education,
        "social" to CategoryColorsDark.Social,
        "pet" to CategoryColorsDark.Pet,
        "invest_expense" to CategoryColorsDark.InvestExpense,
        "digital" to CategoryColorsDark.Digital,
        "housing" to CategoryColorsDark.Housing,
        "salary" to CategoryColorsDark.Salary,
        "freelance" to CategoryColorsDark.Freelance,
        "investment" to CategoryColorsDark.Investment,
    )

    private val categoryIconMap = mapOf<String, String>(
        "food" to "restaurant",
        "transport" to "directions_car",
        "shopping" to "shopping_bag",
        "entertainment" to "sports_esports",
        "living" to "home",
        "daily" to "more_horiz",
        "health" to "local_hospital",
        "education" to "school",
        "social" to "people",
        "pet" to "pets",
        "invest_expense" to "trending_up",
        "digital" to "devices",
        "housing" to "receipt_long",
        "salary" to "account_balance_wallet",
        "freelance" to "work",
        "investment" to "show_chart",
    )

    fun getIcon(iconName: String): ImageVector {
        return iconMap[iconName] ?: Icons.Filled.Category
    }

    fun getIconForCategory(categoryId: String): ImageVector {
        val iconName = categoryIconMap[categoryId]
        return if (iconName != null) iconMap[iconName] ?: Icons.Filled.Category else Icons.Filled.Category
    }

    fun getCategoryColor(categoryId: String, isDark: Boolean = false): Color {
        val activeColorMap = if (isDark) darkColorMap else colorMap
        activeColorMap[categoryId]?.let { return it }
        val allColors = if (isDark) CategoryColorsDark.AllColors else CategoryColors.AllColors
        val index = Math.abs(categoryId.hashCode()) % allColors.size
        return allColors[index]
    }

    fun getIconNameForCategory(categoryId: String): String {
        return categoryIconMap[categoryId] ?: "category"
    }
}
