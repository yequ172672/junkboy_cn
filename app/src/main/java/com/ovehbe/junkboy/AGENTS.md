<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-07-01 | Updated: 2026-07-01 -->

# junkboy (com.ovehbe.junkboy)

## Purpose
Junkboy 应用的 Kotlin 源码根包，包含应用的完整业务逻辑层。分为 6 个功能模块：ML 分类器、规则过滤器、后台服务、短信接收器、UI 层和工具类。

## Key Files

| File | Description |
|------|-------------|
| （本目录不含文件，所有文件在各子目录中） | — |

## Subdirectories

| Directory | Purpose |
|-----------|---------|
| `classifier/` | TensorFlow Lite ML 短信分类器 |
| `database/` | Room 数据库层（实体、DAO、类型转换器） |
| `filters/` | 规则引擎（关键词、正则、Under Attack 模式） |
| `service/` | 后台服务（SMS 过滤、通知监听、发送服务） |
| `smsreceiver/` | SMS/MMS 广播接收器 |
| `ui/` | Jetpack Compose 用户界面层 |
| `utils/` | 工具类（偏好管理、通知、OTP、应用启动器等） |

## For AI Agents

### Working In This Directory
- 每个子目录对应一个独立的关注面，修改前应先了解其职责
- 大多数核心类是 `object`（Kotlin 单例），注意线程安全
- 数据库操作使用 Coroutines + Flow，不要在主线程执行
- 前台服务需要 `FOREGROUND_SERVICE` 权限
- 短信相关功能要求应用成为默认 SMS 应用

### Testing Requirements
- 在 Android 设备或模拟器上测试（部分功能需要 SMS 权限）
- 使用 `TestFilterScreen` 可交互测试过滤逻辑
- 数据库变更需更新 `AppDatabase` 版本号并处理迁移

### Common Patterns
- 单例：`SmsClassifier`、`CustomFilter`、`PreferencesManager`
- 消息分类枚举：`MessageCategory`（GENERAL / PROMOTION / NOTIFICATION / TRANSACTION / JUNK / ALLOWED）
- 过滤类型枚举：`FilterType`（ML_CLASSIFICATION / KEYWORD_FILTER / REGEX_FILTER / USER_RULE / UNDER_ATTACK_MODE / ALLOWED_SENDER）
- 灵活的发送者匹配：精确匹配、大小写不敏感、归一化号码、最后10位部分匹配

## Dependencies

### Internal
- `database/` — 数据持久化和消息存储
- `classifier/` — ML 推理（被 `service/` 和 `utils/` 调用）
- `filters/` — 规则过滤（被 `service/` 和 `smsreceiver/` 调用）
- `service/` — 核心过滤管道，调用 classifier + filters + database
- `ui/` — 界面层，依赖 database + utils + service
- `utils/` — 工具类，被所有其他模块引用

### External
- TensorFlow Lite (`org.tensorflow.lite`) — ML 推理
- Room — 本地数据库
- Gson — JSON 序列化（偏好设置）
- AndroidX — 生命周期、核心组件

<!-- MANUAL: -->
