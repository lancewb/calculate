package com.card.calculate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.card.calculate.model.TagType
import com.card.calculate.ui.theme.getDisplayName
import com.card.calculate.ui.theme.toComposeColor

/**
 * 标签组件
 */
@Composable
fun TagView(
    tagType: TagType,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val tagColor = tagType.toComposeColor()
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .size(50.dp)
            .background(
                color = if (enabled) tagColor else tagColor.copy(alpha = 0.3f),
                shape = shape
            )
            .then(
                if (isSelected) {
                    Modifier.border(3.dp, Color.White, shape)
                } else {
                    Modifier
                }
            )
            .then(
                if (onClick != null && enabled) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = tagType.getDisplayName(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (tagType == TagType.YELLOW) Color.Black else Color.White
            )
            Text(
                text = "${tagType.points}",
                fontSize = 10.sp,
                color = if (tagType == TagType.YELLOW) Color.Black else Color.White
            )
        }
    }
}

/**
 * 标签墙（显示玩家拥有的所有标签）
 */
@Composable
fun TagWall(
    tags: List<TagType>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tags.forEach { tag ->
            TagView(tagType = tag)
        }

        if (tags.isEmpty()) {
            Text(
                text = "无标签",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * 紧凑标签墙（用小圆点表示，适合空间有限的地方）
 */
@Composable
fun CompactTagWall(
    tags: List<TagType>,
    modifier: Modifier = Modifier
) {
    // 统计每种标签的数量
    val tagCounts = tags.groupingBy { it }.eachCount()

    if (tagCounts.isEmpty()) {
        Text(
            text = "无标签",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tagCounts.forEach { (tagType, count) ->
                CompactTagDot(tagType = tagType, count = count)
            }
        }
    }
}

/**
 * 紧凑标签点（小圆点带数字）
 */
@Composable
fun CompactTagDot(
    tagType: TagType,
    count: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(16.dp)
            .background(
                color = tagType.toComposeColor(),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (count > 1) {
            Text(
                text = count.toString(),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = if (tagType == TagType.YELLOW) Color.Black else Color.White
            )
        }
    }
}
