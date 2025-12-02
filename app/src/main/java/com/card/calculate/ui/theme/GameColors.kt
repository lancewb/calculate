package com.card.calculate.ui.theme

import androidx.compose.ui.graphics.Color
import com.card.calculate.model.CardColor as GameCardColor
import com.card.calculate.model.TagType

/**
 * 卡牌颜色映射
 */
fun GameCardColor.toComposeColor(): Color {
    return when (this) {
        GameCardColor.RED -> Color(0xFFE53935)      // 红色
        GameCardColor.YELLOW -> Color(0xFFFDD835)   // 黄色
        GameCardColor.BLUE -> Color(0xFF1E88E5)     // 蓝色
        GameCardColor.GREEN -> Color(0xFF43A047)    // 绿色
        GameCardColor.PURPLE -> Color(0xFF8E24AA)   // 紫色
    }
}

/**
 * 标签颜色映射
 */
fun TagType.toComposeColor(): Color {
    return when (this) {
        TagType.RED -> Color(0xFFE53935)
        TagType.YELLOW -> Color(0xFFFDD835)
        TagType.BLUE -> Color(0xFF1E88E5)
        TagType.GREEN -> Color(0xFF43A047)
        TagType.PURPLE -> Color(0xFF8E24AA)
        TagType.COLORFUL -> Color(0xFF00ACC1)       // 青色（彩色）
        TagType.BLACK -> Color(0xFF424242)          // 深灰（黑色）
    }
}

/**
 * 获取标签显示文本
 */
fun TagType.getDisplayName(): String {
    return when (this) {
        TagType.RED -> "红"
        TagType.YELLOW -> "黄"
        TagType.BLUE -> "蓝"
        TagType.GREEN -> "绿"
        TagType.PURPLE -> "紫"
        TagType.COLORFUL -> "彩"
        TagType.BLACK -> "黑"
    }
}
