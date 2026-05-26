package com.qinoteapp.qinoteapp.network

fun localParseBill(text: String): ParsedBill? {
    val amountRegex = Regex(
        """[¥￥]\s*(\d+\.?\d*)""" +
        """|(\d+\.?\d*)\s*[元块]""" +
        """|(\d+\.?\d*)\s*块钱""" +
        """|花了\s*(\d+\.?\d*)""" +
        """|消费\s*(\d+\.?\d*)""" +
        """|支付\s*(\d+\.?\d*)""" +
        """|付了\s*(\d+\.?\d*)""" +
        """|充了\s*(\d+\.?\d*)"""
    )
    val amountMatch = amountRegex.find(text) ?: return null
    val amountStr = amountMatch.groupValues.drop(1).firstOrNull { it.isNotBlank() } ?: return null
    val amount = amountStr.toDoubleOrNull() ?: return null
    val amountCents = Math.round(amount * 100)

    val expenseKeywords = mapOf(
        "餐饮" to "food", "吃饭" to "food", "外卖" to "food", "午餐" to "food",
        "晚餐" to "food", "早餐" to "food", "夜宵" to "food", "奶茶" to "food",
        "咖啡" to "food", "零食" to "food", "水果" to "food", "烧烤" to "food",
        "火锅" to "food", "饮料" to "food",
        "打车" to "transport", "地铁" to "transport", "公交" to "transport",
        "加油" to "transport", "停车" to "transport", "高铁" to "transport",
        "机票" to "transport", "火车" to "transport", "滴滴" to "transport",
        "出行" to "transport", "骑车" to "transport",
        "购物" to "shopping", "买" to "shopping", "超市" to "shopping",
        "淘宝" to "shopping", "京东" to "shopping", "网购" to "shopping",
        "电影" to "entertainment", "游戏" to "entertainment", "唱歌" to "entertainment",
        "KTV" to "entertainment", "旅游" to "entertainment", "门票" to "entertainment",
        "房租" to "housing", "水电" to "housing", "物业" to "housing",
        "燃气" to "housing", "宽带" to "housing", "话费" to "digital",
        "医疗" to "health", "看病" to "health", "药" to "health",
        "体检" to "health", "牙" to "health", "挂号" to "health",
        "培训" to "education", "课程" to "education", "书" to "education",
        "学费" to "education", "网课" to "education",
        "红包" to "social", "随礼" to "social", "请客" to "social",
        "生日" to "social", "结婚" to "social",
        "宠物" to "pet", "猫粮" to "pet", "狗粮" to "pet",
        "猫砂" to "pet", "疫苗" to "pet",
        "理财" to "invest_expense", "基金" to "invest_expense", "股票" to "invest_expense",
        "手机" to "digital", "电脑" to "digital", "数码" to "digital",
        "会员" to "digital", "充值" to "digital", "订阅" to "digital",
        "理发" to "living", "洗衣" to "living", "维修" to "living",
        "保洁" to "living", "快递" to "living"
    )

    val incomeKeywords = mapOf(
        "工资" to "salary", "薪资" to "salary", "薪水" to "salary",
        "兼职" to "freelance", "外包" to "freelance", "稿费" to "freelance",
        "收益" to "investment", "利息" to "investment", "分红" to "investment",
        "退款" to "food", "退货" to "shopping"
    )

    var type = "expense"
    var category = "daily"
    var title = ""

    val sortedIncome = incomeKeywords.entries.sortedByDescending { it.key.length }
    for ((keyword, catId) in sortedIncome) {
        if (text.contains(keyword)) {
            type = if (keyword == "退款" || keyword == "退货") "income" else "income"
            category = catId
            title = keyword
            break
        }
    }

    if (title.isEmpty() || type == "expense") {
        val sortedExpense = expenseKeywords.entries.sortedByDescending { it.key.length }
        for ((keyword, catId) in sortedExpense) {
            if (text.contains(keyword)) {
                type = "expense"
                category = catId
                title = keyword
                break
            }
        }
    }

    if (title.isEmpty()) {
        title = "日常支出"
    }

    val date = resolveRelativeDate(text)
    val time = resolveTime(text)

    return ParsedBill(
        type = type,
        title = title,
        category = category,
        amount = amountCents,
        note = "",
        date = date,
        time = time
    )
}

private fun resolveRelativeDate(text: String): String {
    val today = java.time.LocalDate.now()
    return when {
        text.contains("大前天") -> today.minusDays(3).toString()
        text.contains("前天") -> today.minusDays(2).toString()
        text.contains("昨天") -> today.minusDays(1).toString()
        text.contains("今天") -> today.toString()
        else -> ""
    }
}

private fun resolveTime(text: String): String {
    val specificTimePatterns = listOf(
        Regex("""(?:下午|傍晚)(\d{1,2})[点时:：](\d{1,2})?[分]?""") to { m: MatchResult ->
            val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
            val minute = m.groupValues[2].toIntOrNull() ?: 0
            if (hour in 1..12) String.format("%02d:%02d", hour + 12, minute) else ""
        },
        Regex("""(?:下午|傍晚)(\d{1,2})[点时点半]""") to { m: MatchResult ->
            val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
            if (hour in 1..12) String.format("%02d:00", hour + 12) else ""
        },
        Regex("""(?:上午|早上|早晨|凌晨)(\d{1,2})[点时:：](\d{1,2})?[分]?""") to { m: MatchResult ->
            val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
            val minute = m.groupValues[2].toIntOrNull() ?: 0
            if (hour in 0..12) String.format("%02d:%02d", hour, minute) else ""
        },
        Regex("""(?:上午|早上|早晨|凌晨)(\d{1,2})[点时点半]""") to { m: MatchResult ->
            val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
            if (hour in 0..12) String.format("%02d:00", hour) else ""
        },
        Regex("""中午(\d{1,2})[点时:：](\d{1,2})?[分]?""") to { m: MatchResult ->
            val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
            val minute = m.groupValues[2].toIntOrNull() ?: 0
            if (hour in 10..12) String.format("%02d:%02d", hour, minute) else ""
        },
        Regex("""晚上(\d{1,2})[点时:：](\d{1,2})?[分]?""") to { m: MatchResult ->
            val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
            val minute = m.groupValues[2].toIntOrNull() ?: 0
            if (hour in 1..12) String.format("%02d:%02d", hour + 12, minute.coerceAtMost(59)) else ""
        },
        Regex("""晚上(\d{1,2})[点时点半]""") to { m: MatchResult ->
            val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
            if (hour in 1..12) String.format("%02d:00", hour + 12) else ""
        },
        Regex("""夜里(\d{1,2})[点时:：](\d{1,2})?[分]?""") to { m: MatchResult ->
            val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
            val minute = m.groupValues[2].toIntOrNull() ?: 0
            if (hour in 1..5) String.format("%02d:%02d", hour + 12, minute.coerceAtMost(59)) else ""
        },
        Regex("""(\d{1,2})[点时:：](\d{1,2})[分]?""") to { m: MatchResult ->
            val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
            val minute = m.groupValues[2].toIntOrNull() ?: 0
            if (hour in 0..23 && minute in 0..59) String.format("%02d:%02d", hour, minute) else ""
        },
        Regex("""(\d{1,2})[点时点半]""") to { m: MatchResult ->
            val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
            if (hour in 0..23) String.format("%02d:00", hour) else ""
        }
    )

    for ((pattern, converter) in specificTimePatterns) {
        val match = pattern.find(text)
        if (match != null) {
            val result = converter(match)
            if (result.isNotBlank()) return result
        }
    }

    return ""
}
