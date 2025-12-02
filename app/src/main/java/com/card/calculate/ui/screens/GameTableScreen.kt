package com.card.calculate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.card.calculate.ai.BotDifficulty
import com.card.calculate.model.*
import com.card.calculate.ui.components.*
import com.card.calculate.viewmodel.GameViewModel

/**
 * 游戏主界面
 */
@Composable
fun GameTableScreen(
    viewModel: GameViewModel = viewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    var showSetup by remember { mutableStateOf(true) }

    if (showSetup) {
        // 显示设置界面
        GameSetupScreen(
            onStartGame = { difficulty ->
                showSetup = false
                // 初始化游戏：4人局，3个机器人
                viewModel.initializeGame(playerCount = 4, botCount = 3, difficulty = difficulty)
            }
        )
    } else {
        // 显示游戏界面
        GameContent(gameState = gameState, viewModel = viewModel)
    }
}

/**
 * 游戏内容
 */
@Composable
fun GameContent(
    gameState: GameState,
    viewModel: GameViewModel
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B5E20)) // 深绿色桌面
    ) {
        when (gameState.gamePhase) {
            GamePhase.SETUP -> {
                LoadingScreen()
            }
            GamePhase.BIDDING -> {
                GameTable(gameState = gameState, viewModel = viewModel)

                // 显示竞标对话框（仅对人类玩家）
                val currentPlayer = gameState.getBiddingPlayer()
                if (currentPlayer?.isBot == false && currentPlayer.tags.isEmpty() && !currentPlayer.isBlackWizard) {
                    BiddingDialog(
                        hand = currentPlayer.hand,
                        isBlackWizardAvailable = gameState.blackWizardId == null,
                        onConfirm = { tags, wantsBlackWizard ->
                            viewModel.claimRole(currentPlayer.id, tags, wantsBlackWizard)
                        }
                    )
                }
            }
            GamePhase.PLAYING -> {
                GameTable(gameState = gameState, viewModel = viewModel)
            }
            GamePhase.TRICK_RESULT -> {
                GameTable(gameState = gameState, viewModel = viewModel)
                // Trick结算动画会自动处理
            }
            GamePhase.ROUND_END -> {
                GameTable(gameState = gameState, viewModel = viewModel)
                RoundEndDialog(gameState = gameState)
            }
            GamePhase.GAME_END -> {
                GameTable(gameState = gameState, viewModel = viewModel)
                GameEndDialog(
                    gameState = gameState,
                    onRestart = { viewModel.restartGame() }
                )
            }
        }
    }
}

/**
 * 游戏桌面
 */
@Composable
fun GameTable(
    gameState: GameState,
    viewModel: GameViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶部：轮次信息和其他玩家
        TopSection(gameState = gameState)

        Spacer(modifier = Modifier.weight(1f))

        // 中央：出牌区
        CenterPlayArea(gameState = gameState)

        Spacer(modifier = Modifier.weight(1f))

        // 底部：玩家手牌
        val humanPlayer = gameState.players.firstOrNull { !it.isBot }
        if (humanPlayer != null) {
            BottomPlayerHand(
                player = humanPlayer,
                gameState = gameState,
                onCardClick = { card ->
                    viewModel.playCard(humanPlayer.id, card)
                }
            )
        }
    }
}

/**
 * 顶部区域
 */
@Composable
fun TopSection(gameState: GameState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 轮次信息
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "第 ${gameState.currentRound}/${gameState.totalRounds} 轮",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Trick: ${gameState.trickCount}/${gameState.getHandSize()}",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 其他玩家信息
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            gameState.players.filter { it.isBot }.forEach { player ->
                PlayerInfoCard(
                    player = player,
                    isCurrentPlayer = gameState.getCurrentPlayer()?.id == player.id,
                    isDealer = gameState.getDealer()?.id == player.id
                )
            }
        }
    }
}

/**
 * 中央出牌区
 */
@Composable
fun CenterPlayArea(gameState: GameState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
            .background(
                color = Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (gameState.currentTrick.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                gameState.currentTrick.forEach { playedCard ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CardView(card = playedCard.card, enabled = true)

                        val player = gameState.players.find { it.id == playedCard.playerId }
                        Text(
                            text = player?.name ?: "",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            Text(
                text = "等待出牌...",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * 底部玩家手牌
 */
@Composable
fun BottomPlayerHand(
    player: Player,
    gameState: GameState,
    onCardClick: (Card) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 玩家信息
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = player.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))
                TagWall(tags = player.tags)
            }

            Text(
                text = "总分: ${player.totalScore}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 手牌
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(player.hand) { card ->
                val leadCard = gameState.getLeadCard()
                val isPlayable = CardValidator.isCardPlayable(card, player.hand, leadCard)
                val isMyTurn = gameState.gamePhase == GamePhase.PLAYING &&
                        gameState.getCurrentPlayer()?.id == player.id

                CardView(
                    card = card,
                    enabled = isMyTurn && isPlayable,
                    onClick = if (isMyTurn && isPlayable) {
                        { onCardClick(card) }
                    } else null
                )
            }
        }
    }
}

/**
 * 加载界面
 */
@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

/**
 * 轮次结束对话框
 */
@Composable
fun RoundEndDialog(gameState: GameState) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text("第 ${gameState.currentRound} 轮结束")
        },
        text = {
            Column {
                gameState.players.forEach { player ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(player.name)
                        Text(
                            text = "${player.currentRoundScore} 分",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}

/**
 * 游戏结束对话框
 */
@Composable
fun GameEndDialog(
    gameState: GameState,
    onRestart: () -> Unit
) {
    val winner = gameState.players.maxByOrNull { it.totalScore }

    AlertDialog(
        onDismissRequest = {},
        title = {
            Text("游戏结束！")
        },
        text = {
            Column {
                Text(
                    text = "🎉 ${winner?.name} 获胜！",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "最终排名：",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                gameState.players
                    .sortedByDescending { it.totalScore }
                    .forEachIndexed { index, player ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${index + 1}. ${player.name}")
                            Text(
                                text = "${player.totalScore} 分",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
            }
        },
        confirmButton = {
            Button(onClick = onRestart) {
                Text("再来一局")
            }
        }
    )
}
