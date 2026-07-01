<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-07-01 | Updated: 2026-07-01 -->

# theme

## Purpose
Material Design 3 主题和设计系统配置，包含颜色、字体、间距、圆角、布局常量和组件规格。所有设计决策集中在此，确保全应用视觉一致性。

## Key Files

| File | Description |
|------|-------------|
| `Theme.kt` | Material3 主题包装器，亮色/暗色色彩方案定义 |
| `Color.kt` | 颜色映射：设计令牌 → Material3 颜色属性 + 分类颜色别名 |
| `Type.kt` | Material3 Typography 定义（15 种文字样式） |
| `DesignTokens.kt` | 集中设计系统令牌（颜色、字体大小、间距、圆角、布局、组件规格） |

## Subdirectories

（无子目录）

## For AI Agents

### Working In This Directory
- 设计令牌全部集中在 `DesignTokens.kt`，修改任何视觉元素应从这里开始
- 亮色主题：主色黑色，辅助色灰色，强调色橙色
- 暗色主题：反色方案（白字深底）
- 分类颜色：GENERAL（蓝色）、PROMOTION（绿色）、NOTIFICATION（灰色）、TRANSACTION（紫色）、JUNK（红色）
- 字体使用系统默认无衬线字体（`FontFamily.SansSerif`）
- 字阶：12sp（XS）到 32sp（DisplayLarge），共 6 个等级
- 间距：0dp 到 32dp（7 个等级）
- 圆角：6dp（SM）、12dp（MD）、20dp（LG）、999dp（Full）

### Testing Requirements
- 主题变更需在亮色和暗色模式下验证对比度
- 新增颜色需确保在两种主题下可读
- 字体大小变更需测试文本溢出和布局坍塌

### Common Patterns
- `DesignColors` 定义所有颜色常量
- `DesignTypography` 定义字体大小、字重、行高
- `DesignSpacing`、`DesignBorderRadius`、`DesignLayout`、`DesignComponents`、`DesignIcons` 分层管理
- `JunkboyTheme` 通过 `SideEffect` 设置状态栏颜色

## Dependencies

### Internal
- `../../compose/` — 所有 Composable 使用主题

### External
- Material 3 — 主题和设计系统
- Accompanist System UI — 状态栏控制

<!-- MANUAL: -->
