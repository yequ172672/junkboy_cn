<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-07-01 | Updated: 2026-07-01 -->

# service

## Purpose
后台服务层，负责短信过滤的核心管道处理和通知监听。包含 SMS 过滤前台服务、聊天应用通知监听服务和发送服务桩。

## Key Files

| File | Description |
|------|-------------|
| `SmsFilterService.kt` | 核心 SMS 过滤引擎（前台 Service），执行完整过滤管道 |
| `NotificationListenerService.kt` | 通知监听服务，过滤聊天应用通知（WhatsApp、Telegram 等） |
| `SmsSendService.kt` | 发送服务桩，满足 Android 默认 SMS 应用资格要求 |

## Subdirectories

（无子目录）

## For AI Agents

### Working In This Directory
- `SmsFilterService` 运行为前台服务，必须创建通知渠道
- 过滤管道顺序：允许发送者检查 → 用户偏好读取 → ML/规则过滤 → 数据库存储 → 通知 → OTP 检测 → 自动删除
- `NotificationListenerService` 监听 9 个聊天应用的通知，支持 20+ 已知 SMS 应用包名
- 发送者匹配使用灵活策略：精确、大小写不敏感、归一化号码、最后10位部分匹配
- 自动删除垃圾短信要求应用成为默认 SMS 应用

### Testing Requirements
- SMS 过滤需在真实设备上测试（需要 SIM 卡或模拟短信）
- 通知监听需在 Android 设置中授予「通知访问权限」
- 前台服务通知必须在 5 秒内创建

### Common Patterns
- 过滤结果增强：ML 结果可通过规则引擎提高/降低置信度
- OTP 检测：4 步级联（高置信度模式 → 字母数字 → 投递码 → 回退）
- 允许发送者消息立即跳过所有过滤，仅存储不拦截
- 统计计数在 `PreferencesManager` 中按分类增量更新

## Dependencies

### Internal
- `../classifier/` — 调用 `SmsClassifier` 进行 ML 分类
- `../filters/` — 调用 `CustomFilter.filterMessage()` 进行规则过滤
- `../database/` — 使用 DAO 存储和查询消息
- `../utils/` — 使用 `PreferencesManager`、`NotificationHelper`、`OtpHelper`、`SmsDeleter`

### External
- Android 前台服务 API (`startForeground`)
- Android 通知监听服务 API (`NotificationListenerService`)

<!-- MANUAL: -->
