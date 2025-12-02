package com.card.calculate.model

/**
 * 玩家数据类
 * @param id 玩家唯一标识
 * @param name 玩家名称
 * @param isBot 是否为机器人
 * @param hand 当前手牌
 * @param tags 当前拥有的标签列表
 * @param currentRoundScore 当前轮得分
 * @param totalScore 总得分
 * @param isBlackWizard 是否为黑巫师
 * @param blackWizardBonus 黑巫师额外加分
 */
data class Player(
    val id: Int,
    val name: String,
    val isBot: Boolean = false,
    val hand: List<Card> = emptyList(),
    val tags: List<TagType> = emptyList(),
    val currentRoundScore: Int = 0,
    val totalScore: Int = 0,
    val isBlackWizard: Boolean = false,
    val blackWizardBonus: Int = 0
) {
    /**
     * 计算当前标签总分（不含黑巫师加分）
     */
    fun calculateTagsScore(): Int {
        return tags.sumOf { it.points }
    }

    /**
     * 计算本轮最终得分（标签分 + 黑巫师加分）
     */
    fun calculateFinalRoundScore(): Int {
        return calculateTagsScore() + blackWizardBonus
    }

    /**
     * 判断手牌中是否有某种颜色的牌
     */
    fun hasColor(color: CardColor): Boolean {
        return hand.any { it.color == color }
    }

    /**
     * 判断是否拥有某种标签
     */
    fun hasTag(tagType: TagType): Boolean {
        return tags.contains(tagType)
    }

    /**
     * 获取手牌中指定颜色的所有牌
     */
    fun getCardsByColor(color: CardColor): List<Card> {
        return hand.filter { it.color == color }
    }
}
