package com.card.calculate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.card.calculate.model.Card
import com.card.calculate.model.TagType

/**
 * 竞标对话框
 */
@Composable
fun BiddingDialog(
    hand: List<Card>,
    isBlackWizardAvailable: Boolean,
    onConfirm: (tags: List<TagType>, wantsBlackWizard: Boolean) -> Unit,
    onDismiss: () -> Unit = {}
) {
    var selectedTags by remember { mutableStateOf(listOf<TagType>()) }
    var wantsBlackWizard by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "选择标签",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                // 显示手牌
                Text(
                    text = "你的手牌：",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(hand) { card ->
                        CardView(card = card, enabled = false)
                    }
                }

                HorizontalDivider()

                // 黑巫师选项
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBlackWizardAvailable) "成为黑巫师" else "黑巫师已被抢占",
                        fontSize = 16.sp
                    )
                    Switch(
                        checked = wantsBlackWizard,
                        onCheckedChange = {
                            if (isBlackWizardAvailable) {
                                wantsBlackWizard = it
                                if (it) {
                                    selectedTags = emptyList()
                                }
                            }
                        },
                        enabled = isBlackWizardAvailable
                    )
                }

                HorizontalDivider()

                // 标签选择
                Text(
                    text = "选择标签（点击添加，可重复选择）：",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // 彩色标签
                val availableTags = listOf(
                    TagType.RED,
                    TagType.YELLOW,
                    TagType.BLUE,
                    TagType.GREEN,
                    TagType.PURPLE,
                    TagType.COLORFUL
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableTags.take(3).forEach { tag ->
                            TagSelector(
                                tagType = tag,
                                count = selectedTags.count { it == tag },
                                enabled = !wantsBlackWizard,
                                onAdd = {
                                    if (!wantsBlackWizard) {
                                        selectedTags = selectedTags + tag
                                    }
                                },
                                onRemove = {
                                    if (!wantsBlackWizard && selectedTags.contains(tag)) {
                                        val index = selectedTags.indexOf(tag)
                                        selectedTags = selectedTags.toMutableList().apply {
                                            removeAt(index)
                                        }
                                    }
                                }
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableTags.drop(3).forEach { tag ->
                            TagSelector(
                                tagType = tag,
                                count = selectedTags.count { it == tag },
                                enabled = !wantsBlackWizard,
                                onAdd = {
                                    if (!wantsBlackWizard) {
                                        selectedTags = selectedTags + tag
                                    }
                                },
                                onRemove = {
                                    if (!wantsBlackWizard && selectedTags.contains(tag)) {
                                        val index = selectedTags.indexOf(tag)
                                        selectedTags = selectedTags.toMutableList().apply {
                                            removeAt(index)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                // 当前分数预览
                val currentScore = if (wantsBlackWizard) {
                    0
                } else {
                    selectedTags.sumOf { it.points }
                }

                Text(
                    text = "当前分数：$currentScore",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (currentScore >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )

                // 确认按钮
                Button(
                    onClick = {
                        onConfirm(selectedTags, wantsBlackWizard)
                    },
                    enabled = wantsBlackWizard || selectedTags.isNotEmpty()
                ) {
                    Text("确认")
                }
            }
        }
    }
}

/**
 * 标签选择器（带数量显示和加减按钮）
 */
@Composable
fun TagSelector(
    tagType: TagType,
    count: Int,
    enabled: Boolean,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 标签显示
        TagView(
            tagType = tagType,
            enabled = enabled,
            onClick = onAdd
        )

        // 数量和减少按钮
        if (count > 0) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 减少按钮
                Button(
                    onClick = onRemove,
                    enabled = enabled,
                    modifier = Modifier.size(30.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("-", fontSize = 16.sp)
                }

                // 数量显示
                Text(
                    text = "×$count",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }
    }
}
