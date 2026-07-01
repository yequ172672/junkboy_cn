<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-07-01 | Updated: 2026-07-01 -->

# ui

## Purpose
Jetpack Compose 用户界面层，提供现代、响应式的 Material Design 3 界面。包含主 Activity、导航路由、主题系统和 10 个功能屏幕。

## Key Files

| File | Description |
|------|-------------|
| `MainActivity.kt` | Android 入口 Activity，处理权限请求 |
| `compose/JunkboyApp.kt` | 根 Composable，管理底部导航和 NavHost |
| `compose/screens/DashboardScreen.kt` | 主面板（权限状态、今日统计、快捷操作） |
| `compose/screens/MenuScreen.kt` | 菜单导航中心（快速统计、应用状态） |
| `compose/screens/MessagesScreen.kt` | 过滤消息列表（按分类筛选、会话视图、允许发送者） |
| `compose/screens/ChatsScreen.kt` | Hub 屏幕（SMS + 聊天应用通知聚合收件箱） |
| `compose/screens/SmsScreen.kt` | 完整短信功能（需默认 SMS 应用权限） |
| `compose/screens/SettingsScreen.kt` | 综合设置（过滤方法、通知、Hub、自定义规则） |
| `compose/screens/StatsScreen.kt` | 统计仪表板（分类统计、性能指标） |
| `compose/screens/TestFilterScreen.kt` | 交互式过滤测试工具 |
| `theme/Theme.kt` | Material3 主题包装器（亮色/暗色方案） |
| `theme/Color.kt` | 颜色映射（设计令牌 → Material3 颜色属性） |
| `theme/Type.kt` | Material3 Typography 定义 |
| `theme/DesignTokens.kt` | 集中设计系统令牌（颜色、字体、间距、圆角、布局常量） |

## Subdirectories

| Directory | Purpose |
|-----------|---------|
| `compose/` | Compose 根 Composable 和导航（见 `compose/AGENTS.md`） |
| `theme/` | 主题和设计系统（颜色、字体、设计令牌） |

## For AI Agents

### Working In This Directory
- 使用 Jetpack Compose + Material Design 3
- 导航使用 `NavHost` + `navigation-compose`
- 所有屏幕 Composable 接收 ViewModel 参数（使用 `collectAsStateWithLifecycle`）
- 底部导航动态显示/隐藏（取决于 Hub 启用状态和默认 SMS 应用状态）
- 状态处理统一使用 `LoadingState`、`EmptyState`、`ErrorState` 辅助组件
- 深色模式跟随系统，通过 `JunkboyTheme` 控制

### Testing Requirements
- UI 变更需在不同屏幕尺寸和暗色模式下验证
- 权限相关界面需测试权限授予/拒绝状态
- 列表滚动性能（LazyColumn）在大量数据下需验证

### Common Patterns
- 分类颜色映射：`getCategoryColor()` 在所有屏幕中一致使用
- 时间格式化：`formatTimestamp()` 统一显示
- 会话卡片：`ConversationCard`、`SmsConversationCard`、`ChatMessageCard`
- 设置项：`ToggleSettingItem`、`ActionSettingItem`、`DropdownSettingItem`
- 弹窗：`AddKeywordDialog`、`AddRegexDialog`、`AddAllowedSenderDialog`

## Dependencies

### Internal
- `../database/` — 通过 DAO 查询消息数据
- `../utils/` — 使用 `AppLauncher`、`PreferencesManager`、`NotificationHelper`
- `../service/` — 间接依赖服务层的数据

### External
- Jetpack Compose — 声明式 UI
- Material 3 — 设计系统
- Navigation Compose — 页面导航
- Accompanist — 系统 UI 控制器（状态栏颜色）
- Gson — JSON 序列化（设置页自定义列表）

<!-- MANUAL: -->
