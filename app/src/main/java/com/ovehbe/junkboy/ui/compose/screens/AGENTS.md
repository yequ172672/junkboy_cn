<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-07-01 | Updated: 2026-07-01 -->

# screens

## Purpose
所有 Jetpack Compose 功能屏幕的集合，覆盖应用的完整用户界面：仪表板、消息列表、设置、统计、Hub、短信、测试等。

## Key Files

| File | Description |
|------|-------------|
| `DashboardScreen.kt` | 主面板：权限状态、今日统计、Under Attack 开关、快捷操作 |
| `MenuScreen.kt` | 菜单导航中心：快速统计卡片、应用状态指示器 |
| `MessagesScreen.kt` | 消息列表：分类筛选芯片、会话/消息视图、允许发送者 |
| `ChatsScreen.kt` | Hub 屏幕：SMS + 聊天应用通知聚合收件箱、搜索 |
| `SmsScreen.kt` | 完整短信客户端：会话列表、聊天界面、新建消息、发送 |
| `SettingsScreen.kt` | 综合设置：过滤方法、通知、Hub、自定义关键词/正则、白名单 |
| `StatsScreen.kt` | 统计仪表板：总过滤量、今日分类、性能指标、过滤效率 |
| `TestFilterScreen.kt` | 交互式测试工具：输入短信测试完整过滤管道 |

## Subdirectories

（无子目录）

## For AI Agents

### Working In This Directory
- 每个屏幕文件包含多个 Composable（主屏幕 + 辅助组件）
- 使用 `LazyColumn` 实现可滚动列表
- 数据通过 ViewModel + `collectAsStateWithLifecycle` 获取
- 状态异常处理统一使用 `LoadingState`、`EmptyState`、`ErrorState`
- 对话/会话卡片点击使用 `AppLauncher` 打开对应应用

### Testing Requirements
- 每个屏幕需在亮色和暗色模式下验证
- 列表屏幕需测试空状态、加载状态和错误状态
- 聊天界面需验证消息发送、日期分隔、分类徽章显示

### Common Patterns
- 分类颜色：`getCategoryColor()` 映射到各分类主题色
- 分类图标：`getCategoryIcon()` 返回 emoji 图标
- 时间格式化：`formatTimestamp()` 统一时间显示
- 会话卡片支持下拉菜单（打开应用、允许发送者）
- 设置页使用可复用的行组件：`ToggleSettingItem`、`ActionSettingItem`

## Dependencies

### Internal
- `../../database/` — DAO 查询消息数据
- `../../utils/` — `AppLauncher`、`PreferencesManager`

### External
- Jetpack Compose — 所有 UI 组件
- Material 3 — 设计系统组件
- Navigation Compose — 页面参数传递
- Gson — 自定义过滤列表的 JSON 序列化

<!-- MANUAL: -->
