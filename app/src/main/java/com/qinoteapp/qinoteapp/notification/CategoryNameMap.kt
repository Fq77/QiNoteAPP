package com.qinoteapp.qinoteapp.notification

object CategoryNameMap {
    private val expenseCategories = mapOf(
        "food" to "餐饮美食",
        "transport" to "交通出行",
        "shopping" to "购物消费",
        "entertainment" to "休闲娱乐",
        "living" to "居住生活",
        "daily" to "日常杂项",
        "health" to "医疗健康",
        "education" to "教育培训",
        "social" to "人情社交",
        "pet" to "宠物萌宠",
        "invest_expense" to "投资理财",
        "digital" to "数码电子",
        "housing" to "住房缴费"
    )

    private val incomeCategories = mapOf(
        "salary" to "工资收入",
        "freelance" to "兼职收入",
        "investment" to "投资收益"
    )

    fun getCategoryName(categoryId: String): String {
        return expenseCategories[categoryId]
            ?: incomeCategories[categoryId]
            ?: categoryId
    }
}
