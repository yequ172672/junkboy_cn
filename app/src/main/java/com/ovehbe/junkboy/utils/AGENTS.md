<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-07-01 | Updated: 2026-07-01 -->

# utils

## Purpose
工具类集合，提供偏好管理、通知管理、OTP 检测、CSV 导出、应用启动、现有消息处理和 SMS 删除等功能。被应用所有其他模块广泛引用。

## Key Files

| File | Description |
|------|-------------|
| `PreferencesManager.kt` | SharedPreferences 封装，管理所有应用设置、功能开关、统计计数器 |
| `NotificationHelper.kt` | 通知管理：分类通知、每日统计通知、6 个通知渠道 |
| `OtpHelper.kt` | OTP/投递码检测与自动复制，4 步级联检测 + 剪贴板集成 |
| `CsvExporter.kt` | 过滤消息 CSV 导出，通过 FileProvider 分享 |
| `AppLauncher.kt` | 应用启动器，打开聊天应用/对话（支持 WhatsApp、Telegram、Signal 等） |
| `SmsAppManager.kt` | 默认 SMS 应用管理，检测/请求/验证默认 SMS 应用状态 |
| `ExistingMessagesProcessor.kt` | 批量处理设备现有短信，使用 ML/规则进行分类和存储 |
| `SmsDeleter.kt` | 垃圾短信删除，从系统内容提供者删除并本地归档 |

## Subdirectories

（无子目录）

## For AI Agents

### Working In This Directory
- `PreferencesManager` 是最核心的工具类，几乎所有模块都依赖它
- 功能开关：Under Attack 模式、ML 过滤、关键词过滤、正则过滤
- 统计管理：按分类增量计数、每日自动重置
- `NotificationHelper` 管理 6 个渠道：blocked、transaction、promotion、notification、general、daily_stats
- `OtpHelper` 4 步检测：高置信度模式 → 字母数字 → 投递码 → 关键词回退
- `CsvExporter` 写入缓存目录并通过 `FileProvider` 分享

### Testing Requirements
- 偏好设置变更后需验证 UI 及时反映
- OTP 检测需在不同格式（纯数字、字母数字、投递码）下测试
- CSV 导出需验证特殊字符转义
- 删除功能仅在默认 SMS 应用时可用

### Common Patterns
- 自定义关键词/正则使用 Gson 序列化存储为 JSON 字符串
- 每日统计通过日期比较自动重置（`checkDailyStatsReset()`）
- 发送者归一化：移除 `+`、`-`、`(`、`)`、空格、前导零
- 聊天应用支持：WhatsApp、Telegram、Signal、Messenger、Gmail、Outlook 等

## Dependencies

### Internal
- `../database/` — DAO 查询、消息存储、允许发送者管理
- `../service/` — 被过滤服务和通知监听服务调用
- `../filters/` — 现有消息处理使用过滤逻辑
- `../ui/` — UI 层通过这里访问应用功能

### External
- Gson — JSON 序列化（自定义过滤规则）
- Android `SharedPreferences` — 持久化偏好设置
- Android `FileProvider` — 文件分享
- Android `SmsManager` — 短信发送

<!-- MANUAL: -->
