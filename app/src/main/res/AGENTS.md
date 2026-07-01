<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-07-01 | Updated: 2026-07-01 -->

# res

## Purpose
Android 资源目录，包含应用运行所需的静态资源：矢量图标、多密度应用图标、字符串/主题/样式定义、配置文件。

## Key Files

| File/Directory | Description |
|------|-------------|
| `drawable/` | 矢量图标和可绘制资源 |
| `values/` | 字符串、颜色、主题、样式 XML 定义 |
| `xml/` | Android 配置文件（如可访问性服务配置） |
| `mipmap-*/` | 多密度应用图标资源（mdpi 到 xxxhdpi + anydpi-v26） |

## Subdirectories

| Directory | Purpose |
|-----------|---------|
| `drawable/` | 矢量图形和可绘制资源 |
| `values/` | 字符串、颜色、主题、样式、尺寸定义 |
| `xml/` | 系统配置 XML |
| `mipmap-hdpi/` | HDPI 密度应用图标 |
| `mipmap-mdpi/` | MDPI 密度应用图标 |
| `mipmap-xhdpi/` | XHDPI 密度应用图标 |
| `mipmap-xxhdpi/` | XXHDPI 密度应用图标 |
| `mipmap-xxxhdpi/` | XXXHDPI 密度应用图标 |
| `mipmap-anydpi-v26/` | Android 8.0+ 自适应图标 |

## For AI Agents

### Working In This Directory
- 颜色资源优先使用 `values/colors.xml` 中定义的颜色
- 字符串资源通过 `string` 资源引用，不要在代码中硬编码
- 矢量图标使用 XML 格式（`drawable/`），确保兼容性
- 应用图标需在各 mipmap 目录中保持一致性

### Testing Requirements
- 资源变更后需清理并重建项目
- 在不同屏幕密度和语言环境下验证

### Common Patterns
- 主题定义在 `values/themes.xml` 中
- 可访问性服务配置在 `xml/` 目录下

## Dependencies

### Internal
- `../../java/com/ovehbe/junkboy/ui/` — UI Composable 引用这些资源

### External
- Android SDK 资源系统

<!-- MANUAL: -->
