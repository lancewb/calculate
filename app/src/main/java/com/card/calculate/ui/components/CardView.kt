package com.card.calculate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.card.calculate.model.Card
import com.card.calculate.ui.theme.toComposeColor

/**
 * 卡牌组件
 */
@Composable
fun CardView(
    card: Card,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val cardColor = card.color.toComposeColor()
    val shape = RoundedCornerShape(8.dp)

    Card(
        modifier = modifier
            .width(60.dp)
            .height(90.dp)
            .then(
                if (onClick != null && enabled) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .then(
                if (isSelected) {
                    Modifier.border(3.dp, Color.Blue, shape)
                } else {
                    Modifier
                }
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color.White else Color.Gray.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (enabled) 4.dp else 1.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 卡牌数字
                Text(
                    text = card.number.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = cardColor
                )

                // 卡牌颜色标记
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(cardColor, RoundedCornerShape(10.dp))
                )
            }
        }
    }
}

/**
 * 小卡牌背面（其他玩家的牌）
 */
@Composable
fun CardBackView(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(40.dp)
            .height(60.dp),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
