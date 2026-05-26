package com.qinoteapp.qinoteapp.notification

data class BookkeepingNotificationData(
    val state: BookkeepingState,
    val title: String = "",
    val amount: String = "",
    val category: String = "",
    val categoryName: String = "",
    val categoryIconName: String = "",
    val type: String = "expense",
    val note: String = "",
    val errorMessage: String? = null
)
