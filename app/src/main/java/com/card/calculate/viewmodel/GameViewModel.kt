package com.card.calculate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.card.calculate.ai.BotAI
import com.card.calculate.ai.BotDifficulty
import com.card.calculate.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * 游戏引擎 ViewModel
 * 采用 MVVM + 单向数据流 (UDF) 架构
 */
class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // AI机器人
    private var botAI = BotAI(BotDifficulty.HARD)

    /**
     * 初始化游戏
     * @param playerCount 玩家人数 (4或5)
     * @param botCount 机器人数量
     * @param difficulty AI难度
     */
    fun initializeGame(playerCount: Int, botCount: Int, difficulty: BotDifficulty = BotDifficulty.HARD) {
        require(playerCount in 4..5) { "玩家人数必须为4或5" }
        require(botCount in 0 until playerCount) { "机器人数量不合法" }

        // 初始化AI
        botAI = BotAI(difficulty)

        // 创建玩家列表
        val players = mutableListOf<Player>()

        // 添加人类玩家
        players.add(Player(id = 0, name = "你", isBot = false))

        // 添加机器人
        for (i in 1 until playerCount) {
            players.add(Player(id = i, name = "机器人$i", isBot = i < botCount + 1))
        }

        // 随机选择第一轮的龙头
        val initialDealer = (0 until playerCount).random()

        _gameState.value = GameState(
            playerCount = playerCount,
            totalRounds = playerCount,
            players = players,
            dealerIndex = initialDealer,
            gamePhase = GamePhase.SETUP
        )

        // 开始第一轮
        startNewRound()
    }

    /**
     * 开始新的一轮
     */
    private fun startNewRound() {
        val state = _gameState.value

        // 洗牌并发牌
        val deck = createAndShuffleDeck()
        val handSize = state.getHandSize()

        val updatedPlayers = state.players.mapIndexed { index, player ->
            val startIndex = index * handSize
            val endIndex = startIndex + handSize
            player.copy(
                hand = deck.subList(startIndex, endIndex),
                tags = emptyList(),
                currentRoundScore = 0,
                isBlackWizard = false,
                blackWizardBonus = 0
            )
        }

        _gameState.value = state.copy(
            players = updatedPlayers,
            gamePhase = GamePhase.BIDDING,
            biddingPlayerIndex = state.dealerIndex,
            blackWizardId = null,
            currentTrick = emptyList(),
            currentPlayerIndex = state.dealerIndex,
            trickCount = 0,
            lastTrickWinnerId = null,
            pendingDiscardPlayerId = null
        )

        // 触发AI竞标（如果第一个玩家是机器人）
        triggerBotBidIfNeeded()
    }

    /**
     * 创建并洗牌
     * 5种颜色，每色1-15号，共75张牌
     */
    private fun createAndShuffleDeck(): List<Card> {
        val deck = mutableListOf<Card>()
        for (color in CardColor.values()) {
            for (number in 1..15) {
                deck.add(Card(color, number))
            }
        }
        return deck.shuffled()
    }

    /**
     * 玩家领取标签
     * @param playerId 玩家ID
     * @param tags 选择的标签列表
     * @param wantsToBeBlackWizard 是否想成为黑巫师
     */
    fun claimRole(playerId: Int, tags: List<TagType>, wantsToBeBlackWizard: Boolean) {
        val state = _gameState.value

        // 验证是否轮到该玩家
        val currentPlayer = state.getBiddingPlayer()
        if (currentPlayer?.id != playerId) {
            return // 不是该玩家的回合
        }

        // 如果想成为黑巫师
        if (wantsToBeBlackWizard) {
            // 检查黑巫师是否已被占用
            if (state.blackWizardId != null) {
                // 黑巫师已被抢占，不能选择
                return
            }

            // 设置为黑巫师
            val updatedPlayers = state.players.map { player ->
                if (player.id == playerId) {
                    player.copy(isBlackWizard = true, tags = emptyList())
                } else {
                    player
                }
            }

            _gameState.value = state.copy(
                players = updatedPlayers,
                blackWizardId = playerId
            )
        } else {
            // 普通玩家领取标签
            // 过滤掉黑色标签（黑色标签只能作为惩罚获得）
            val validTags = tags.filter { it != TagType.BLACK }

            val updatedPlayers = state.players.map { player ->
                if (player.id == playerId) {
                    player.copy(tags = validTags, isBlackWizard = false)
                } else {
                    player
                }
            }

            _gameState.value = state.copy(players = updatedPlayers)
        }

        // 移动到下一个竞标玩家
        moveToNextBiddingPlayer()
    }

    /**
     * 移动到下一个竞标玩家
     */
    private fun moveToNextBiddingPlayer() {
        val state = _gameState.value
        val nextIndex = (state.biddingPlayerIndex + 1) % state.playerCount

        _gameState.value = state.copy(biddingPlayerIndex = nextIndex)

        // 检查是否所有玩家都已竞标
        if (state.hasAllPlayersBid()) {
            // 开始出牌阶段
            _gameState.value = _gameState.value.copy(
                gamePhase = GamePhase.PLAYING,
                currentPlayerIndex = state.dealerIndex
            )
            // 触发AI出牌
            triggerBotPlayIfNeeded()
        } else {
            // 触发AI竞标
            triggerBotBidIfNeeded()
        }
    }

    /**
     * 触发AI竞标（如果当前玩家是机器人）
     */
    private fun triggerBotBidIfNeeded() {
        viewModelScope.launch {
            delay(800) // 延迟800ms模拟思考
            val state = _gameState.value
            val currentPlayer = state.getBiddingPlayer()

            if (currentPlayer?.isBot == true) {
                // AI决策
                val isBlackWizardAvailable = state.blackWizardId == null
                val (tags, wantsBlackWizard) = botAI.decideTagSelection(currentPlayer.hand, isBlackWizardAvailable)
                claimRole(currentPlayer.id, tags, wantsBlackWizard)
            }
        }
    }

    /**
     * 玩家出牌
     * @param playerId 玩家ID
     * @param card 要打出的牌
     */
    fun playCard(playerId: Int, card: Card) {
        val state = _gameState.value

        // 验证游戏阶段
        if (state.gamePhase != GamePhase.PLAYING) {
            return
        }

        // 验证是否轮到该玩家
        val currentPlayer = state.getCurrentPlayer()
        if (currentPlayer?.id != playerId) {
            return
        }

        // 验证出牌合法性
        val leadCard = state.getLeadCard()
        if (!CardValidator.isCardPlayable(card, currentPlayer.hand, leadCard)) {
            return // 出牌不合法
        }

        // 执行出牌
        val updatedPlayers = state.players.map { player ->
            if (player.id == playerId) {
                player.copy(hand = player.hand - card)
            } else {
                player
            }
        }

        val updatedTrick = state.currentTrick + PlayedCard(playerId, card)

        _gameState.value = state.copy(
            players = updatedPlayers,
            currentTrick = updatedTrick
        )

        // 检查Trick是否完成
        if (updatedTrick.size == state.playerCount) {
            // Trick完成，进行结算
            viewModelScope.launch {
                delay(1000) // 延迟1秒展示结果
                evaluateTrick()
            }
        } else {
            // 移动到下一个玩家
            moveToNextPlayer()
        }
    }

    /**
     * 移动到下一个出牌玩家
     */
    private fun moveToNextPlayer() {
        val state = _gameState.value
        val nextIndex = (state.currentPlayerIndex + 1) % state.playerCount

        _gameState.value = state.copy(currentPlayerIndex = nextIndex)

        // 触发AI出牌
        triggerBotPlayIfNeeded()
    }

    /**
     * 触发AI出牌（如果当前玩家是机器人）
     */
    private fun triggerBotPlayIfNeeded() {
        viewModelScope.launch {
            delay(1000) // 延迟1秒模拟思考
            val state = _gameState.value

            if (state.gamePhase != GamePhase.PLAYING) {
                return@launch
            }

            val currentPlayer = state.getCurrentPlayer()
            if (currentPlayer?.isBot == true) {
                // AI决策
                val card = botAI.decideCardToPlay(
                    hand = currentPlayer.hand,
                    leadCard = state.getLeadCard(),
                    trick = state.currentTrick,
                    playerTags = currentPlayer.tags,
                    isBlackWizard = currentPlayer.isBlackWizard
                )
                playCard(currentPlayer.id, card)
            }
        }
    }

    /**
     * 评估Trick结果
     */
    private fun evaluateTrick() {
        val state = _gameState.value
        val trick = state.currentTrick

        // 判定赢家
        val winningPlayedCard = CardValidator.evaluateTrickWinner(trick) ?: return
        val winnerId = winningPlayedCard.playerId

        _gameState.value = state.copy(
            lastTrickWinnerId = winnerId,
            gamePhase = GamePhase.TRICK_RESULT,
            pendingDiscardPlayerId = winnerId
        )

        // 处理Trick结果（丢标签逻辑）
        val leadCard = state.getLeadCard()!!
        val winningCard = winningPlayedCard.card
        processTrickResult(winnerId, leadCard, winningCard)
    }

    /**
     * 处理Trick结果（丢标签或获得黑色标签）
     * @param winnerId 赢家ID
     * @param leadCard 头牌
     * @param winningCard 获胜的牌
     */
    private fun processTrickResult(winnerId: Int, leadCard: Card, winningCard: Card) {
        val state = _gameState.value
        val winner = state.players.find { it.id == winnerId } ?: return

        // 获取可丢弃的标签
        val discardableTags = CardValidator.getDiscardableTags(winner.tags, leadCard, winningCard)

        if (discardableTags.isEmpty()) {
            // 没有可丢弃的标签，获得黑色标签惩罚
            applyBlackTagPenalty(winnerId)
        } else {
            // 自动丢弃第一个可用的标签（如果是玩家，可以让UI选择）
            discardTag(winnerId, discardableTags.first())
        }
    }

    /**
     * 丢弃标签
     * @param playerId 玩家ID
     * @param tagType 要丢弃的标签
     */
    fun discardTag(playerId: Int, tagType: TagType) {
        val state = _gameState.value

        val updatedPlayers = state.players.map { player ->
            if (player.id == playerId) {
                val newTags = player.tags.toMutableList()
                newTags.remove(tagType)
                player.copy(tags = newTags)
            } else {
                player
            }
        }

        _gameState.value = state.copy(
            players = updatedPlayers,
            pendingDiscardPlayerId = null
        )

        // 继续下一个Trick
        continueToNextTrick(playerId)
    }

    /**
     * 应用黑色标签惩罚
     * @param winnerId 赢家ID（获得惩罚的玩家）
     */
    private fun applyBlackTagPenalty(winnerId: Int) {
        val state = _gameState.value

        var updatedPlayers = state.players.map { player ->
            if (player.id == winnerId) {
                // 获得黑色标签
                player.copy(tags = player.tags + TagType.BLACK)
            } else if (player.isBlackWizard) {
                // 黑巫师额外加分 +1
                player.copy(blackWizardBonus = player.blackWizardBonus + 1)
            } else {
                player
            }
        }

        _gameState.value = state.copy(
            players = updatedPlayers,
            pendingDiscardPlayerId = null
        )

        // 继续下一个Trick
        continueToNextTrick(winnerId)
    }

    /**
     * 继续下一个Trick
     * @param nextLeaderId 下一个领头出牌的玩家ID（上一个Trick的赢家）
     */
    private fun continueToNextTrick(nextLeaderId: Int) {
        val state = _gameState.value
        val newTrickCount = state.trickCount + 1

        // 清空当前Trick
        _gameState.value = state.copy(
            currentTrick = emptyList(),
            trickCount = newTrickCount,
            currentPlayerIndex = state.players.indexOfFirst { it.id == nextLeaderId },
            gamePhase = GamePhase.PLAYING
        )

        // 检查轮次是否结束
        if (newTrickCount >= state.getHandSize()) {
            endRound()
        } else {
            // 触发AI出牌
            triggerBotPlayIfNeeded()
        }
    }

    /**
     * 结束当前轮次
     */
    private fun endRound() {
        val state = _gameState.value

        // 计算每个玩家的本轮得分并更新总分
        val updatedPlayers = state.players.map { player ->
            val roundScore = player.calculateFinalRoundScore()
            player.copy(
                currentRoundScore = roundScore,
                totalScore = player.totalScore + roundScore
            )
        }

        _gameState.value = state.copy(
            players = updatedPlayers,
            gamePhase = GamePhase.ROUND_END
        )

        // 检查游戏是否结束
        if (state.currentRound >= state.totalRounds) {
            endGame()
        } else {
            // 准备下一轮
            viewModelScope.launch {
                delay(3000) // 展示结算界面3秒
                prepareNextRound()
            }
        }
    }

    /**
     * 准备下一轮
     */
    private fun prepareNextRound() {
        val state = _gameState.value

        // 龙头顺时针轮换
        val nextDealer = (state.dealerIndex + 1) % state.playerCount

        _gameState.value = state.copy(
            currentRound = state.currentRound + 1,
            dealerIndex = nextDealer
        )

        startNewRound()
    }

    /**
     * 结束游戏
     */
    private fun endGame() {
        _gameState.value = _gameState.value.copy(gamePhase = GamePhase.GAME_END)
    }

    /**
     * 重新开始游戏
     */
    fun restartGame() {
        val state = _gameState.value
        initializeGame(state.playerCount, state.players.count { it.isBot && it.id != 0 })
    }

    /**
     * 获取赢家（总分最高的玩家）
     */
    fun getWinner(): Player? {
        val state = _gameState.value
        return state.players.maxByOrNull { it.totalScore }
    }
}
