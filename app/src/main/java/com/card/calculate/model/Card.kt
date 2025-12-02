package com.card.calculate.model

/**
 * 卡牌数据类
 * @param color 卡牌颜色
 * @param number 卡牌数字 (1-15)
 */
data class Card(
    val color: CardColor,
    val number: Int
) {
    init {
        require(number in 1..15) { "卡牌数字必须在1-15之间" }
    }

    /**
     * 判断是否为红牌（王牌）
     */
    fun isRed(): Boolean = color == CardColor.RED

    /**
     * 判断是否与另一张牌同色
     */
    fun isSameColor(other: Card): Boolean = this.color == other.color

    override fun toString(): String = "${color.name}-$number"
}
