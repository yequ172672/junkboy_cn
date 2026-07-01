<!-- Generated: 2026-07-01 | Updated: 2026-07-01 -->

# junkboy_cn

## Purpose
Junkboy 是一个隐私优先的 Android SMS 垃圾信息过滤应用。使用 TensorFlow Lite 机器学习模型和规则引擎在设备端对短信进行智能分类（通用、促销、通知、交易、垃圾），无需网络连接，所有数据本地存储。支持拦截短信、聊天应用通知过滤（Hub）、OTP 自动复制、CSV 导出等功能。

## Key Files

| File | Description |
|------|-------------|
| `build.gradle` | 项目级 Gradle 构建配置 |
| `settings.gradle` | Gradle 项目设置，包含插件版本管理 |
| `gradle.properties` | Gradle 属性配置（Kotlin 版本、JVM 参数等） |
| `PROJECT_SUMMARY.md` | 项目架构与功能详细说明 |
| `README.md` | 项目介绍、安装指南和功能说明 |
| `LICENSE` | 开源许可证 |
| `app-dev-release.apk` | 开发版发布 APK（根目录） |

## Subdirectories

| Directory | Purpose |
|-----------|---------|
| `app/` | Android 应用模块（源码、资源、构建输出） |
| `gradle/` | Gradle Wrapper 配置 |
| `release-v1.0/` | v1.0 发布版本文件 |
| `Other/` | 其他资源文件（如应用图标） |

## For AI Agents

### Working In This Directory
- 这是一个 Android Kotlin 项目，使用 Gradle 构建系统
- 核心业务逻辑全部在 `app/src/main/java/com/ovehbe/junkboy/` 下
- UI 使用 Jetpack Compose + Material Design 3
- 数据库使用 Room，ML 使用 TensorFlow Lite
- 遵循 MVVM 架构模式，使用 Coroutines 处理异步

### Testing Requirements
- 通过 Android Studio 或 `./gradlew assembleDebug` 构建验证
- 测试过滤逻辑可在 `TestFilterScreen` 中交互式验证
- 构建输出位于 `app/build/`（已 .gitignore 排除）

### Common Patterns
- 单例模式：`SmsClassifier`、`CustomFilter`、`PreferencesManager`
- Room 数据库：`Flow` 用于响应式 UI 更新，`suspend` 用于一次性查询
- 前台服务：`SmsFilterService` 和 `NotificationListenerService` 均使用前台服务
- 灵活匹配：发送者匹配支持精确匹配、大小写不敏感、归一化号码、最后10位部分匹配

## Dependencies

### Internal
- `app/src/main/java/com/ovehbe/junkboy/classifier/` — ML 分类模块
- `app/src/main/java/com/ovehbe/junkboy/database/` — 数据持久层
- `app/src/main/java/com/ovehbe/junkboy/filters/` — 规则过滤引擎
- `app/src/main/java/com/ovehbe/junkboy/service/` — 后台服务
- `app/src/main/java/com/ovehbe/junkboy/smsreceiver/` — SMS/MMS 拦截
- `app/src/main/java/com/ovehbe/junkboy/ui/` — 用户界面
- `app/src/main/java/com/ovehbe/junkboy/utils/` — 工具类

### External
- Jetpack Compose — 声明式 UI 框架
- Room — 本地 SQLite 数据库 ORM
- TensorFlow Lite — 端侧 ML 推理
- Gson — JSON 序列化
- AndroidX Core / Lifecycle — 基础组件

<!-- MANUAL: -->
