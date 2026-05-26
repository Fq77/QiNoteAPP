package com.qinoteapp.qinoteapp.navigation

sealed class QiRoute(val route: String) {
    data object Home : QiRoute("home")
    data object Stats : QiRoute("stats")
    data object Settings : QiRoute("settings")
    data object AiConfig : QiRoute("settings/ai")
    data object CategoryManage : QiRoute("settings/category")
    data object DataManage : QiRoute("settings/data")
    data object About : QiRoute("settings/about")
    data object ThemeSettings : QiRoute("settings/theme")
    data object Optimization : QiRoute("settings/optimization")
    data object NotificationSettings : QiRoute("settings/notification")
    data object BillDetail : QiRoute("bill_detail/{billId}") {
        fun createRoute(billId: String) = "bill_detail/$billId"
    }
}
