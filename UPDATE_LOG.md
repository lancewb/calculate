# 游戏更新日志

## 版本 1.1 - 2025-12-02

### 🎨 视觉改进

#### 1. 颜色调整
- ✅ 将所有**橙色**卡牌和标签改为**蓝色**
  - 修改文件：`CardColor.kt`, `TagType.kt`, `GameColors.kt`, `BotAI.kt`, `BiddingDialog.kt`
  - 新的5种颜色：红、黄、**蓝**、绿、紫
  - 蓝色色值：`#1E88E5` (Material Blue 600)

#### 2. 标签显示优化
- ✅ 改进机器人区域的标签展示
  - 新增 `CompactTagWall` 组件：用小圆点代替完整标签
  - 新增 `CompactTagDot` 组件：16dp 圆形带数量标识
  - 优点：
    - 节省空间，适合窄卡片
    - 自动统计并显示每种标签数量
    - 多个相同标签合并显示（如红色圆点上显示"3"）
- ✅ 玩家手牌区继续使用完整标签显示（便于清晰识别）

### 🎮 游戏功能增强

#### 3. 难度选择系统
- ✅ 新增游戏设置界面 (`GameSetupScreen.kt`)
- ✅ 三种AI难度可选：
  - **简单**：AI 随机出牌，适合新手
  - **普通**：AI 具有基本策略
  - **困难**（默认）：AI 具有高级策略，极具挑战性
- ✅ 精美的难度选择卡片界面
  - 选中状态带高亮效果
  - 包含每种难度的详细说明
  - 带确认指示器（✓）

### 🔧 技术改进

#### 架构优化
- `GameViewModel` 支持动态设置 AI 难度
- `initializeGame()` 方法新增 `difficulty` 参数
- AI 实例可根据难度动态创建

#### UI 组件
- 新增 `GameSetupScreen` - 游戏设置界面
- 新增 `DifficultyOption` - 难度选项组件
- 新增 `CompactTagWall` - 紧凑标签墙
- 新增 `CompactTagDot` - 紧凑标签点

### 📦 文件变更清单

#### 新增文件
- `ui/screens/GameSetupScreen.kt` - 设置界面

#### 修改文件
- `model/CardColor.kt` - ORANGE → BLUE
- `model/TagType.kt` - ORANGE → BLUE
- `ui/theme/GameColors.kt` - 更新颜色映射
- `ui/components/TagView.kt` - 新增紧凑显示组件
- `ui/components/PlayerInfoCard.kt` - 使用紧凑标签墙
- `ui/components/BiddingDialog.kt` - 更新标签列表
- `ui/screens/GameTableScreen.kt` - 集成设置界面
- `viewmodel/GameViewModel.kt` - 支持难度参数
- `ai/BotAI.kt` - 更新颜色引用

### 🎯 游戏流程

```
启动游戏
    ↓
[设置界面]
  选择难度（简单/普通/困难）
  点击"开始游戏"
    ↓
[游戏界面]
  竞标 → 出牌 → 结算 → ...
```

### 📸 UI 预览

#### 设置界面
- 居中显示的精美卡片
- 三个难度选项带描述
- Material Design 3 风格
- 深绿色背景

#### 标签显示对比
**之前（机器人区域）**：
```
[红-2] [黄-2] （空间不足，显示不全）
```

**现在（机器人区域）**：
```
🔴3 🟡2 🔵1 （圆点+数量，清晰紧凑）
```

### ✅ 测试状态
- [x] 编译通过
- [x] 颜色正确显示
- [x] 标签紧凑显示正常
- [x] 难度选择界面正常
- [x] 三种难度AI正常工作

---

## 使用说明

### 编译和运行
```bash
# 编译
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug
```

### APK 位置
```
app/build/outputs/apk/debug/app-debug.apk
```
