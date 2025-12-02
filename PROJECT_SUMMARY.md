# 巫师的标签 (Wizard's Tags)

一款基于 Kotlin 和 Jetpack Compose 开发的 Android 纸牌游戏。

## 项目概述

这是一个完整实现的单机纸牌游戏，支持 4-5 人游戏，包含完整的游戏逻辑、AI 机器人和现代化的 Material Design 3 UI。

## 技术栈

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose (Material Design 3)
- **架构**: MVVM + 单向数据流 (UDF)
- **状态管理**: StateFlow / ViewModel
- **最低 SDK**: Android 7.0 (API 24)
- **目标 SDK**: Android 14 (API 35)

## 项目结构

```
app/src/main/java/com/card/calculate/
├── model/                          # 数据模型层
│   ├── Card.kt                     # 卡牌数据类
│   ├── CardColor.kt                # 卡牌颜色枚举
│   ├── CardValidator.kt            # 卡牌验证器（跟牌规则）
│   ├── GameState.kt                # 游戏状态
│   ├── Player.kt                   # 玩家数据类
│   └── TagType.kt                  # 标签类型枚举
│
├── viewmodel/                      # ViewModel 层
│   └── GameViewModel.kt            # 游戏引擎逻辑
│
├── ai/                             # AI 机器人
│   └── BotAI.kt                    # 三种难度的 AI 策略
│
├── ui/
│   ├── components/                 # 可复用 UI 组件
│   │   ├── BiddingDialog.kt        # 竞标对话框
│   │   ├── CardView.kt             # 卡牌显示组件
│   │   ├── PlayerInfoCard.kt       # 玩家信息卡片
│   │   └── TagView.kt              # 标签显示组件
│   │
│   ├── screens/                    # 游戏屏幕
│   │   └── GameTableScreen.kt      # 游戏主界面
│   │
│   └── theme/                      # 主题配置
│       ├── GameColors.kt           # 游戏颜色映射
│       └── ...
│
└── MainActivity.kt                 # 主入口
```

## 游戏规则

### 基础配置
- **人数**: 4人或5人
- **牌库**: 红、黄、橙、绿、紫 5种颜色，每色1-15号，共75张牌
- **手牌数**: 4人局每人12张，5人局每人15张

### 标签系统
- 彩色标签（红/黄/橙/绿/紫）: -2分/个
- 彩色标签（万能）: -3分/个
- 黑色标签（惩罚）: -4分/个

### 游戏流程
1. **竞标阶段**: 玩家选择标签或成为"黑巫师"（每轮限一人）
2. **出牌阶段**:
   - 必须跟牌（有同色必出同色）
   - 红牌为常驻王牌
3. **结算阶段**: 赢家丢弃标签或获得黑色标签惩罚

## 核心功能

### ✅ 已实现功能

1. **完整的游戏逻辑**
   - 严格的跟牌机制
   - 红牌王牌系统
   - 复杂的标签丢弃规则
   - 黑巫师特殊机制

2. **AI 机器人**
   - 简单难度：随机策略
   - 中等难度：基本策略
   - 困难难度：高级策略（会根据手牌分布和标签情况做出最优决策）

3. **现代化 UI**
   - Material Design 3 风格
   - 流畅的动画过渡
   - 清晰的视觉反馈
   - 响应式布局

4. **游戏阶段**
   - 竞标对话框
   - 实时游戏状态显示
   - 轮次结算界面
   - 游戏结束排名

## 编译和运行

### 前置条件
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 8 或更高版本

### 编译命令
```bash
# 编译 Debug 版本
./gradlew assembleDebug

# 运行测试
./gradlew test

# 安装到设备
./gradlew installDebug
```

### APK 位置
编译成功后，APK 文件位于：
```
app/build/outputs/apk/debug/app-debug.apk
```

## 游戏截图说明

主界面包含：
- 顶部：轮次信息和其他玩家状态
- 中央：出牌区域
- 底部：玩家手牌（可点击出牌）

## 开发亮点

1. **严格遵守规则**: CardValidator 确保所有出牌都符合规则
2. **智能 AI**: 困难模式 AI 会根据标签情况策略性地出牌
3. **响应式设计**: 使用 StateFlow 实现单向数据流，状态管理清晰
4. **模块化架构**: 数据、逻辑、UI 分离，易于维护和扩展

## 未来扩展

可能的扩展方向：
- [ ] 添加在线多人游戏
- [ ] 添加游戏历史记录
- [ ] 添加自定义规则选项
- [ ] 添加更多 AI 难度级别
- [ ] 添加成就系统

## 许可证

本项目仅用于学习和演示目的。

## 致谢

感谢提供游戏规则设计和需求文档！
