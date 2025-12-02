package com.card.calculate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.card.calculate.model.Player

/**
 * 玩家信息卡片
 */
@Composable
fun PlayerInfoCard(
    player: Player,
    isCurrentPlayer: Boolean = false,
    isDealer: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(120.dp)
            .background(
                color = if (isCurrentPlayer) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isCurrentPlayer) 2.dp else 1.dp,
                color = if (isCurrentPlayer) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 头像
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (player.isBlackWizard) {
                        Color.Black
                    } else if (player.isBot) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (player.isBlackWizard) {
                    "巫"
                } else {
                    player.name.take(1)
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // 玩家名称
        Text(
            text = player.name + if (isDealer) " 🎲" else "",
            fontSize = 14.sp,
            fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Normal
        )

        // 剩余牌数
        Text(
            text = "${player.hand.size} 张牌",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        // 标签墙 - 使用紧凑显示
        CompactTagWall(tags = player.tags)

        // 分数
        if (player.totalScore != 0) {
            Text(
                text = "总分: ${player.totalScore}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (player.totalScore >= 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}
