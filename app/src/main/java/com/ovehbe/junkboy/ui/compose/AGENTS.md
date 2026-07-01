<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-07-01 | Updated: 2026-07-01 -->

# compose

## Purpose
Compose 导航和根 Composable 所在目录，管理底部导航栏和页面路由。根据应用状态动态显示导航项。

## Key Files

| File | Description |
|------|-------------|
| `JunkboyApp.kt` | 根 Composable + NavHost，底部导航路由管理 |

## Subdirectories

| Directory | Purpose |
|-----------|---------|
| `screens/` | 所有功能屏幕 Composable（见 `screens/AGENTS.md`） |

## For AI Agents

### Working In This Directory
- `JunkboyApp` 是整个 UI 树的根，使用 `Scaffold` + `NavigationBar`
- 导航路由：`hub`、`sms`、`messages`、`menu`、`dashboard`、`test`、`settings`、`stats`
- 底部导航项根据 Hub 启用状态和默认 SMS 应用状态动态调整
- 物理键盘偏移：底部添加 `Spacer` 补偿系统导航栏高度

### Testing Requirements
- 导航路由变更需验证每个页面可正常跳转
- 动态导航项：Hub 关闭时 hub/sms 不应出现在导航栏

### Common Patterns
- `BottomNavItem` 数据类封装路由和图标
- 使用 `remember` + `snapshotFlow` 监听 Hub 状态变化

## Dependencies

### Internal
- `../` — 所有屏幕 Composable
- `../theme/` — `JunkboyTheme` 和设计令牌

### External
- Jetpack Compose Navigation — 页面导航
- Material 3 — 导航栏组件
- Accompanist — 系统 UI 控制

<!-- MANUAL: -->
