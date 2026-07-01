<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-07-01 | Updated: 2026-07-01 -->

# app

## Purpose
Android 应用主模块，包含完整的应用源码、资源文件、构建配置和依赖管理。是整个 Junkboy 应用的核心容器。

## Key Files

| File | Description |
|------|-------------|
| `build.gradle` | 应用级 Gradle 构建配置（依赖、编译选项、签名配置） |
| `proguard-rules.pro` | ProGuard/R8 代码混淆规则 |

## Subdirectories

| Directory | Purpose |
|-----------|---------|
| `src/` | 应用源代码和资源（见 `src/AGENTS.md`） |

## For AI Agents

### Working In This Directory
- 所有业务逻辑代码位于 `src/main/java/` 下
- 资源文件位于 `src/main/res/`
- ML 模型文件位于 `src/main/assets/`
- 构建输出不要提交到版本控制
- 修改依赖或构建配置后需同步 Gradle

### Testing Requirements
- 构建命令：`./gradlew :app:assembleDebug`（调试版）或 `:app:assembleRelease`（发布版）
- 安装命令：`./gradlew :app:installDebug`
- 交互式过滤测试：启动应用后进入「测试过滤器」屏幕

### Common Patterns
- 所有 Kotlin 源码在 `com.ovehbe.junkboy` 包下
- 单例类使用 `@Volatile` + `synchronized` 线程安全模式
- 前台服务必须创建通知渠道
- Room DAO 返回 `Flow` 以便 Compose 自动刷新

## Dependencies

### Internal
- `src/main/java/com/ovehbe/junkboy/` — 所有应用源代码

### External
- 依赖声明见 `build.gradle`，包括 Compose、Room、TFLite、Gson 等

<!-- MANUAL: -->
