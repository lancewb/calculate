package com.card.calculate.model

/**
 * 标签类型枚举（包含分数定义）
 */
enum class TagType(val points: Int) {
    RED(-2),       // 红色标签: -2分/个
    YELLOW(-2),    // 黄色标签: -2分/个
    BLUE(-2),      // 蓝色标签: -2分/个
    GREEN(-2),     // 绿色标签: -2分/个
    PURPLE(-2),    // 紫色标签: -2分/个
    COLORFUL(-3),  // 彩色标签(万能): -3分/个
    BLACK(-4);     // 黑色标签(惩罚): -4分/个

    companion object {
        /**
         * 从卡牌颜色转换为对应的标签类型
         */
        fun fromCardColor(color: CardColor): TagType {
            return when (color) {
                CardColor.RED -> RED
                CardColor.YELLOW -> YELLOW
                CardColor.BLUE -> BLUE
                CardColor.GREEN -> GREEN
                CardColor.PURPLE -> PURPLE
            }
        }
    }
}
