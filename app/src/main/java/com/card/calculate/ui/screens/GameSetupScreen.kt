package com.card.calculate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.card.calculate.ai.BotDifficulty

/**
 * 游戏设置界面（难度选择）
 */
@Composable
fun GameSetupScreen(
    onStartGame: (difficulty: BotDifficulty) -> Unit
) {
    var selectedDifficulty by remember { mutableStateOf(BotDifficulty.HARD) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B5E20)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 游戏标题
                Text(
                    text = "巫师的标签",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Wizard's Tags",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 难度选择标题
                Text(
                    text = "选择难度",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                // 难度选项
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DifficultyOption(
                        title = "简单",
                        description = "AI 随机出牌，适合新手",
                        isSelected = selectedDifficulty == BotDifficulty.EASY,
                        onClick = { selectedDifficulty = BotDifficulty.EASY }
                    )

                    DifficultyOption(
                        title = "普通",
                        description = "AI 具有基本策略",
                        isSelected = selectedDifficulty == BotDifficulty.MEDIUM,
                        onClick = { selectedDifficulty = BotDifficulty.MEDIUM }
                    )

                    DifficultyOption(
                        title = "困难",
                        description = "AI 具有高级策略，极具挑战性",
                        isSelected = selectedDifficulty == BotDifficulty.HARD,
                        onClick = { selectedDifficulty = BotDifficulty.HARD }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 开始游戏按钮
                Button(
                    onClick = { onStartGame(selectedDifficulty) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "开始游戏",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * 难度选项组件
 */
@Composable
fun DifficultyOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    }
                )
            }

            // 选中指示器
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
