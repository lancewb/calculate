package com.card.calculate.model

/**
 * 游戏状态数据类
 */
data class GameState(
    // 游戏配置
    val playerCount: Int = 4,                    // 玩家人数 (4或5)
    val totalRounds: Int = playerCount,          // 总轮数

    // 玩家相关
    val players: List<Player> = emptyList(),     // 所有玩家

    // 轮次相关
    val currentRound: Int = 1,                   // 当前轮数 (1-based)
    val dealerIndex: Int = 0,                    // 当前龙头索引

    // 阶段相关
    val gamePhase: GamePhase = GamePhase.SETUP,  // 游戏阶段

    // 竞标阶段
    val biddingPlayerIndex: Int = 0,             // 当前竞标的玩家索引
    val blackWizardId: Int? = null,              // 黑巫师玩家ID（null表示未选择）

    // 出牌阶段
    val currentTrick: List<PlayedCard> = emptyList(),  // 当前出牌堆
    val currentPlayerIndex: Int = 0,             // 当前行动玩家索引
    val trickCount: Int = 0,                     // 当前轮已完成的trick数

    // 结算阶段
    val lastTrickWinnerId: Int? = null,          // 上一个trick的赢家ID
    val pendingDiscardPlayerId: Int? = null      // 等待丢弃标签的玩家ID
) {
    init {
        require(playerCount in 4..5) { "玩家人数必须为4或5" }
        require(currentRound in 1..totalRounds) { "当前轮数超出范围" }
    }

    /**
     * 获取当前手牌数（4人局12张，5人局15张）
     */
    fun getHandSize(): Int = if (playerCount == 4) 12 else 15

    /**
     * 获取当前行动的玩家
     */
    fun getCurrentPlayer(): Player? = players.getOrNull(currentPlayerIndex)

    /**
     * 获取当前竞标的玩家
     */
    fun getBiddingPlayer(): Player? = players.getOrNull(biddingPlayerIndex)

    /**
     * 获取龙头玩家
     */
    fun getDealer(): Player? = players.getOrNull(dealerIndex)

    /**
     * 获取当前Trick的头牌（第一张出的牌）
     */
    fun getLeadCard(): Card? = currentTrick.firstOrNull()?.card

    /**
     * 判断是否所有玩家都已竞标
     */
    fun hasAllPlayersBid(): Boolean {
        return players.all { it.isBlackWizard || it.tags.isNotEmpty() }
    }

    /**
     * 判断当前Trick是否完成（所有玩家都已出牌）
     */
    fun isTrickComplete(): Boolean {
        return currentTrick.size == playerCount
    }

    /**
     * 判断当前轮是否结束（所有牌都打完）
     */
    fun isRoundComplete(): Boolean {
        return trickCount >= getHandSize()
    }

    /**
     * 判断游戏是否结束
     */
    fun isGameOver(): Boolean {
        return currentRound > totalRounds
    }
}

/**
 * 游戏阶段枚举
 */
enum class GamePhase {
    SETUP,          // 初始化阶段
    BIDDING,        // 竞标阶段（领标签）
    PLAYING,        // 出牌阶段
    TRICK_RESULT,   // Trick结算阶段（丢标签）
    ROUND_END,      // 轮次结束
    GAME_END        // 游戏结束
}

/**
 * 已出的牌（包含玩家信息）
 */
data class PlayedCard(
    val playerId: Int,
    val card: Card
)
