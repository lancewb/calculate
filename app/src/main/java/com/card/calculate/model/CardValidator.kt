package com.card.calculate.model

/**
 * 卡牌验证器 - 单例对象
 * 用于严格执行跟牌逻辑
 */
object CardValidator {

    /**
     * 获取可以打出的牌列表
     *
     * 规则：
     * 1. 如果是第一个出牌（leadCard为null），可以出任意牌
     * 2. 如果手中有与头牌同色的牌，必须出同色牌
     * 3. 如果手中没有与头牌同色的牌，可以出任意牌
     *
     * @param hand 玩家手牌
     * @param leadCard 头牌（第一张出的牌），如果为null表示当前玩家是第一个出牌
     * @return 可以打出的牌列表
     */
    fun getPlayableCards(hand: List<Card>, leadCard: Card?): List<Card> {
        // 如果是第一个出牌，所有牌都可以打
        if (leadCard == null) {
            return hand
        }

        // 查找手牌中与头牌同色的牌
        val sameColorCards = hand.filter { it.color == leadCard.color }

        // 如果有同色牌，必须打同色牌；否则可以打任意牌
        return if (sameColorCards.isNotEmpty()) {
            sameColorCards
        } else {
            hand
        }
    }

    /**
     * 验证出牌是否合法
     *
     * @param card 要打出的牌
     * @param hand 玩家手牌
     * @param leadCard 头牌
     * @return true表示合法，false表示不合法
     */
    fun isCardPlayable(card: Card, hand: List<Card>, leadCard: Card?): Boolean {
        // 检查牌是否在手牌中
        if (!hand.contains(card)) {
            return false
        }

        // 获取可以打的牌列表
        val playableCards = getPlayableCards(hand, leadCard)

        // 检查要打的牌是否在可打列表中
        return playableCards.contains(card)
    }

    /**
     * 判定Trick的赢家
     *
     * 规则：
     * 1. 如果场上有红牌，红牌中数字最大者获胜
     * 2. 如果场上无红牌，与头牌同色的牌中数字最大者获胜
     *
     * @param trick 当前出牌堆
     * @return 赢家的PlayedCard
     */
    fun evaluateTrickWinner(trick: List<PlayedCard>): PlayedCard? {
        if (trick.isEmpty()) return null

        val leadCard = trick.first().card

        // 分离红牌和非红牌
        val redCards = trick.filter { it.card.isRed() }

        // 情况A：场上有红牌，红牌中数字最大者获胜
        if (redCards.isNotEmpty()) {
            return redCards.maxByOrNull { it.card.number }
        }

        // 情况B：场上无红牌，与头牌同色的牌中数字最大者获胜
        val sameColorCards = trick.filter { it.card.color == leadCard.color }
        return sameColorCards.maxByOrNull { it.card.number }
    }

    /**
     * 获取可以丢弃的标签列表
     *
     * 规则：
     * 1. 若红牌获胜且头牌不是红色: 可丢弃 [头牌颜色标签] 或 [红标签] 或 [彩标签]
     * 2. 若红牌获胜且头牌是红色: 只能丢弃 [红标签] 或 [彩标签]
     * 3. 若非红牌获胜: 只能丢弃 [获胜牌颜色标签] 或 [彩标签]
     *
     * @param playerTags 玩家当前拥有的标签
     * @param leadCard 头牌
     * @param winningCard 获胜的牌
     * @return 可以丢弃的标签列表
     */
    fun getDiscardableTags(
        playerTags: List<TagType>,
        leadCard: Card,
        winningCard: Card
    ): List<TagType> {
        val discardable = mutableListOf<TagType>()

        // 彩色标签总是可以丢弃
        if (playerTags.contains(TagType.COLORFUL)) {
            discardable.add(TagType.COLORFUL)
        }

        when {
            // 情况1: 红牌获胜且头牌不是红色
            winningCard.isRed() && !leadCard.isRed() -> {
                // 可丢弃头牌颜色标签
                val leadColorTag = TagType.fromCardColor(leadCard.color)
                if (playerTags.contains(leadColorTag)) {
                    discardable.add(leadColorTag)
                }
                // 可丢弃红标签
                if (playerTags.contains(TagType.RED)) {
                    discardable.add(TagType.RED)
                }
            }
            // 情况2: 红牌获胜且头牌是红色
            winningCard.isRed() && leadCard.isRed() -> {
                // 只能丢弃红标签
                if (playerTags.contains(TagType.RED)) {
                    discardable.add(TagType.RED)
                }
            }
            // 情况3: 非红牌获胜
            else -> {
                // 只能丢弃获胜牌颜色标签
                val winningColorTag = TagType.fromCardColor(winningCard.color)
                if (playerTags.contains(winningColorTag)) {
                    discardable.add(winningColorTag)
                }
            }
        }

        return discardable
    }

    /**
     * 判断是否需要获得黑色标签惩罚
     * （即没有可丢弃的标签）
     */
    fun needsBlackTagPenalty(
        playerTags: List<TagType>,
        leadCard: Card,
        winningCard: Card
    ): Boolean {
        return getDiscardableTags(playerTags, leadCard, winningCard).isEmpty()
    }
}
