package com.card.calculate.ai

import com.card.calculate.model.*

/**
 * AI难度等级
 */
enum class BotDifficulty {
    EASY,       // 简单：随机策略
    MEDIUM,     // 中等：基本策略
    HARD        // 困难：高级策略
}

/**
 * AI机器人策略类
 */
class BotAI(private val difficulty: BotDifficulty = BotDifficulty.HARD) {

    /**
     * 决定标签选择
     * @param hand 机器人手牌
     * @param isBlackWizardAvailable 黑巫师位置是否可用
     * @return Pair<标签列表, 是否选择黑巫师>
     */
    fun decideTagSelection(hand: List<Card>, isBlackWizardAvailable: Boolean): Pair<List<TagType>, Boolean> {
        return when (difficulty) {
            BotDifficulty.EASY -> decideTagSelectionEasy(hand, isBlackWizardAvailable)
            BotDifficulty.MEDIUM -> decideTagSelectionMedium(hand, isBlackWizardAvailable)
            BotDifficulty.HARD -> decideTagSelectionHard(hand, isBlackWizardAvailable)
        }
    }

    /**
     * 简单策略：随机选择1-3个标签
     */
    @Suppress("UNUSED_PARAMETER")
    private fun decideTagSelectionEasy(hand: List<Card>, isBlackWizardAvailable: Boolean): Pair<List<TagType>, Boolean> {
        // 10%概率选择黑巫师（如果可用）
        if (isBlackWizardAvailable && Math.random() < 0.1) {
            return Pair(emptyList(), true)
        }

        // 随机选择1-3个标签
        val tagCount = (1..3).random()
        val availableTags = listOf(TagType.RED, TagType.YELLOW, TagType.BLUE, TagType.GREEN, TagType.PURPLE)
        val selectedTags = availableTags.shuffled().take(tagCount)

        return Pair(selectedTags, false)
    }

    /**
     * 中等策略：根据手牌有无某种颜色决定
     */
    private fun decideTagSelectionMedium(hand: List<Card>, isBlackWizardAvailable: Boolean): Pair<List<TagType>, Boolean> {
        val colorCounts = countCardsByColor(hand)

        // 如果手牌分布很差（有2种或以上颜色的牌数<=1），考虑选择黑巫师
        val poorColors = colorCounts.values.count { it <= 1 }
        if (isBlackWizardAvailable && poorColors >= 2 && Math.random() < 0.3) {
            return Pair(emptyList(), true)
        }

        // 选择手牌中有的颜色标签
        val selectedTags = mutableListOf<TagType>()
        colorCounts.forEach { (color, count) ->
            if (count > 0) {
                selectedTags.add(TagType.fromCardColor(color))
            }
        }

        // 可能添加一个彩色标签
        if (Math.random() < 0.3) {
            selectedTags.add(TagType.COLORFUL)
        }

        return Pair(selectedTags, false)
    }

    /**
     * 困难策略：根据手牌颜色分布按比例选取
     */
    private fun decideTagSelectionHard(hand: List<Card>, isBlackWizardAvailable: Boolean): Pair<List<TagType>, Boolean> {
        val colorCounts = countCardsByColor(hand)
        val totalCards = hand.size

        // 评估手牌质量
        val strongColors = colorCounts.filter { it.value >= 3 }
        val weakColors = colorCounts.filter { it.value <= 1 }

        // 如果强色少且弱色多，考虑选择黑巫师
        if (isBlackWizardAvailable && strongColors.size <= 1 && weakColors.size >= 3) {
            if (Math.random() < 0.5) {
                return Pair(emptyList(), true)
            }
        }

        // 根据手牌分布选择标签
        val selectedTags = mutableListOf<TagType>()

        // 优先选择强色标签（有3张以上的颜色）
        strongColors.forEach { (color, count) ->
            // 根据牌数决定是否选择该颜色标签
            val probability = count.toDouble() / totalCards * 2.0
            if (Math.random() < probability) {
                selectedTags.add(TagType.fromCardColor(color))
            }
        }

        // 对于中等强度的颜色（2-3张），也有机会选择
        colorCounts.filter { it.value in 2..3 }.forEach { (color, _) ->
            if (Math.random() < 0.4 && !selectedTags.contains(TagType.fromCardColor(color))) {
                selectedTags.add(TagType.fromCardColor(color))
            }
        }

        // 根据红牌数量决定是否选择红标签（红牌是王牌）
        val redCount = colorCounts[CardColor.RED] ?: 0
        if (redCount >= 4 && !selectedTags.contains(TagType.RED)) {
            selectedTags.add(TagType.RED)
        }

        // 可能添加彩色标签（万能）
        if (selectedTags.size >= 2 && Math.random() < 0.4) {
            selectedTags.add(TagType.COLORFUL)
        }

        // 如果一个标签都没选到，至少选一个
        if (selectedTags.isEmpty()) {
            val bestColor = colorCounts.maxByOrNull { it.value }?.key
            if (bestColor != null) {
                selectedTags.add(TagType.fromCardColor(bestColor))
            }
        }

        return Pair(selectedTags, false)
    }

    /**
     * 决定要打出的牌
     * @param hand 机器人手牌
     * @param leadCard 头牌（第一张牌），null表示是第一个出牌
     * @param trick 当前已出的牌
     * @param playerTags 机器人当前拥有的标签
     * @param isBlackWizard 是否为黑巫师
     * @return 要打出的牌
     */
    fun decideCardToPlay(
        hand: List<Card>,
        leadCard: Card?,
        trick: List<PlayedCard>,
        playerTags: List<TagType>,
        isBlackWizard: Boolean
    ): Card {
        // 获取可以打的牌
        val playableCards = CardValidator.getPlayableCards(hand, leadCard)

        if (playableCards.isEmpty()) {
            return hand.first() // 理论上不会发生
        }

        return when (difficulty) {
            BotDifficulty.EASY -> decideCardToPlayEasy(playableCards)
            BotDifficulty.MEDIUM -> decideCardToPlayMedium(playableCards, leadCard, trick, playerTags, isBlackWizard)
            BotDifficulty.HARD -> decideCardToPlayHard(playableCards, leadCard, trick, playerTags, isBlackWizard)
        }
    }

    /**
     * 简单策略：随机出牌
     */
    @Suppress("UNUSED_PARAMETER")
    private fun decideCardToPlayEasy(playableCards: List<Card>): Card {
        return playableCards.random()
    }

    /**
     * 中等策略：尝试赢或尝试输
     */
    @Suppress("UNUSED_PARAMETER")
    private fun decideCardToPlayMedium(
        playableCards: List<Card>,
        leadCard: Card?,
        trick: List<PlayedCard>,
        playerTags: List<TagType>,
        isBlackWizard: Boolean
    ): Card {
        // 如果是第一个出牌，出中等大小的牌
        if (leadCard == null) {
            return playableCards.sortedBy { it.number }.getOrNull(playableCards.size / 2) ?: playableCards.first()
        }

        // 如果是黑巫师，尝试输掉（出小牌）
        if (isBlackWizard) {
            return playableCards.minByOrNull { it.number } ?: playableCards.first()
        }

        // 普通玩家，尝试赢得Trick
        return playableCards.maxByOrNull { it.number } ?: playableCards.first()
    }

    /**
     * 困难策略：高级策略
     */
    private fun decideCardToPlayHard(
        playableCards: List<Card>,
        leadCard: Card?,
        trick: List<PlayedCard>,
        playerTags: List<TagType>,
        isBlackWizard: Boolean
    ): Card {
        // 如果是第一个出牌
        if (leadCard == null) {
            return decideFirstCardHard(playableCards, playerTags, isBlackWizard)
        }

        // 评估当前局势
        val currentWinner = CardValidator.evaluateTrickWinner(trick)
        val currentWinningCard = currentWinner?.card

        // 如果是黑巫师
        if (isBlackWizard) {
            return decideCardAsBlackWizardHard(playableCards, currentWinningCard, leadCard)
        }

        // 普通玩家策略
        return decideCardAsNormalPlayerHard(playableCards, currentWinningCard, leadCard, playerTags)
    }

    /**
     * 困难策略：第一个出牌
     */
    private fun decideFirstCardHard(playableCards: List<Card>, playerTags: List<TagType>, isBlackWizard: Boolean): Card {
        if (isBlackWizard) {
            // 黑巫师：出中小牌，避免太容易赢
            return playableCards.sortedBy { it.number }.getOrNull(playableCards.size / 3) ?: playableCards.first()
        }

        // 普通玩家：出自己有标签的颜色，且牌面较大的
        val cardsWithTags = playableCards.filter { card ->
            playerTags.contains(TagType.fromCardColor(card.color)) || playerTags.contains(TagType.COLORFUL)
        }

        if (cardsWithTags.isNotEmpty()) {
            // 出有标签颜色的大牌
            return cardsWithTags.maxByOrNull { it.number } ?: cardsWithTags.first()
        }

        // 没有对应标签的牌，出中等大小的
        return playableCards.sortedBy { it.number }.getOrNull(playableCards.size / 2) ?: playableCards.first()
    }

    /**
     * 困难策略：黑巫师出牌
     * 目标：故意输掉，或迫使别人赢下他们无法丢标签的局
     */
    private fun decideCardAsBlackWizardHard(
        playableCards: List<Card>,
        currentWinningCard: Card?,
        leadCard: Card
    ): Card {
        if (currentWinningCard == null) {
            // 只有一张牌，出小牌
            return playableCards.minByOrNull { it.number } ?: playableCards.first()
        }

        // 分析是否有红牌
        val hasRedInTrick = currentWinningCard.isRed()
        val redCards = playableCards.filter { it.isRed() }

        if (hasRedInTrick) {
            // 场上已有红牌
            if (redCards.isNotEmpty()) {
                // 有红牌，出比当前赢家小的红牌，或最小的红牌
                val smallerReds = redCards.filter { it.number < currentWinningCard.number }
                if (smallerReds.isNotEmpty()) {
                    return smallerReds.first()
                }
                // 被迫出大红牌，出最小的
                return redCards.minByOrNull { it.number } ?: redCards.first()
            } else {
                // 没有红牌，出最小的牌
                return playableCards.minByOrNull { it.number } ?: playableCards.first()
            }
        } else {
            // 场上没有红牌
            if (redCards.isNotEmpty()) {
                // 有红牌但不想赢，出最小的红牌（可能会赢）
                // 或者出非红牌
                val nonRedCards = playableCards.filter { !it.isRed() }
                if (nonRedCards.isNotEmpty()) {
                    return nonRedCards.minByOrNull { it.number } ?: nonRedCards.first()
                }
                return redCards.minByOrNull { it.number } ?: redCards.first()
            } else {
                // 没有红牌，出比当前赢家小的牌
                val smallerCards = playableCards.filter {
                    it.color == leadCard.color && it.number < currentWinningCard.number
                }
                if (smallerCards.isNotEmpty()) {
                    return smallerCards.first()
                }
                // 出最小的牌
                return playableCards.minByOrNull { it.number } ?: playableCards.first()
            }
        }
    }

    /**
     * 困难策略：普通玩家出牌
     * 目标：尽量赢下自己能丢标签的局
     */
    private fun decideCardAsNormalPlayerHard(
        playableCards: List<Card>,
        currentWinningCard: Card?,
        leadCard: Card,
        playerTags: List<TagType>
    ): Card {
        if (currentWinningCard == null) {
            // 只有一张牌，出中等大小的
            return playableCards.sortedBy { it.number }.getOrNull(playableCards.size / 2) ?: playableCards.first()
        }

        // 判断如果自己赢了，能否丢掉标签
        val canDiscardIfWinWithRed = canDiscardTag(playerTags, leadCard, CardColor.RED)
        val canDiscardIfWinWithLead = canDiscardTag(playerTags, leadCard, leadCard.color)

        // 分析可打的牌
        val redCards = playableCards.filter { it.isRed() }
        val sameColorCards = playableCards.filter { it.color == leadCard.color }

        val hasRedInTrick = currentWinningCard.isRed()

        if (hasRedInTrick) {
            // 场上已有红牌
            if (canDiscardIfWinWithRed && redCards.isNotEmpty()) {
                // 能丢标签，尝试用红牌赢
                val winningReds = redCards.filter { it.number > currentWinningCard.number }
                if (winningReds.isNotEmpty()) {
                    return winningReds.minByOrNull { it.number } ?: winningReds.first() // 用最小能赢的红牌
                }
            }
            // 不能丢标签或无法赢，出小牌
            return playableCards.minByOrNull { it.number } ?: playableCards.first()
        } else {
            // 场上没有红牌
            if (redCards.isNotEmpty()) {
                // 有红牌
                if (canDiscardIfWinWithRed) {
                    // 能丢标签，用红牌赢
                    return redCards.minByOrNull { it.number } ?: redCards.first() // 用最小的红牌
                } else {
                    // 不能丢标签，不要用红牌
                    val nonRedCards = playableCards.filter { !it.isRed() }
                    if (nonRedCards.isNotEmpty()) {
                        // 用非红牌
                        if (canDiscardIfWinWithLead && sameColorCards.isNotEmpty()) {
                            // 用同色大牌尝试赢
                            val winningSameColor = sameColorCards.filter { it.number > currentWinningCard.number }
                            if (winningSameColor.isNotEmpty()) {
                                return winningSameColor.minByOrNull { it.number } ?: winningSameColor.first()
                            }
                        }
                        return nonRedCards.minByOrNull { it.number } ?: nonRedCards.first()
                    }
                    // 只有红牌，出最小的
                    return redCards.minByOrNull { it.number } ?: redCards.first()
                }
            } else {
                // 没有红牌
                if (canDiscardIfWinWithLead && sameColorCards.isNotEmpty()) {
                    // 能丢标签，用同色牌尝试赢
                    val winningSameColor = sameColorCards.filter { it.number > currentWinningCard.number }
                    if (winningSameColor.isNotEmpty()) {
                        return winningSameColor.minByOrNull { it.number } ?: winningSameColor.first()
                    }
                }
                // 不能赢或不想赢，出小牌
                return playableCards.minByOrNull { it.number } ?: playableCards.first()
            }
        }
    }

    /**
     * 判断如果用某种颜色赢了，是否能丢掉标签
     */
    private fun canDiscardTag(playerTags: List<TagType>, leadCard: Card, winningColor: CardColor): Boolean {
        // 简化判断：检查是否有对应颜色的标签或彩色标签
        val winningTag = TagType.fromCardColor(winningColor)
        val leadTag = TagType.fromCardColor(leadCard.color)

        return playerTags.contains(TagType.COLORFUL) ||
                playerTags.contains(winningTag) ||
                (winningColor == CardColor.RED && !leadCard.isRed() && playerTags.contains(leadTag))
    }

    /**
     * 统计手牌中各颜色的数量
     */
    private fun countCardsByColor(hand: List<Card>): Map<CardColor, Int> {
        val counts = mutableMapOf<CardColor, Int>()
        for (color in CardColor.values()) {
            counts[color] = hand.count { it.color == color }
        }
        return counts
    }
}
