<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-07-01 | Updated: 2026-07-01 -->

# database

## Purpose
Room 数据库持久层，管理应用的所有本地数据存储。包含 3 个实体（FilteredMessage、AllowedSender、ChatMessage）、3 个 DAO、类型转换器和 UI 数据模型。

## Key Files

| File | Description |
|------|-------------|
| `AppDatabase.kt` | Room 数据库主类（单例，版本 3，fallbackToDestructiveMigration） |
| `FilteredMessage.kt` | 短信过滤记录实体 + `MessageCategory` 和 `FilterType` 枚举 |
| `FilteredMessageDao.kt` | 过滤消息 DAO（查询、插入、删除、会话分组、标记已读等） |
| `AllowedSender.kt` | 信任发送者白名单实体 |
| `AllowedSenderDao.kt` | 白名单 DAO（CRUD、精确/大小写不敏感匹配查询） |
| `ChatMessage.kt` | 聊天应用通知消息实体 |
| `ChatMessageDao.kt` | 聊天消息 DAO（按应用/分类/发送者查询、未读管理、会话分组） |
| `Converters.kt` | Room 类型转换器（Date、MessageCategory、FilterType） |
| `HubConversation.kt` | Hub 视图数据模型（`HubConversation`、`HubAppSection`） |

## Subdirectories

（无子目录）

## For AI Agents

### Working In This Directory
- `AppDatabase` 是单例，使用 Room 的 `databaseBuilder`
- 数据库版本为 3，迁移策略为 `fallbackToDestructiveMigration`
- DAO 方法使用 `Flow` 实现响应式 UI 更新，`suspend` 用于一次性查询
- `MessageCategory` 枚举：GENERAL / PROMOTION / NOTIFICATION / TRANSACTION / JUNK / ALLOWED
- `FilterType` 枚举：ML_CLASSIFICATION / KEYWORD_FILTER / REGEX_FILTER / USER_RULE / UNDER_ATTACK_MODE / ALLOWED_SENDER
- 会话分组逻辑在 DAO 层实现，返回 `SmsConversationSummary`（SMS）和 `ConversationSummary`（聊天）

### Testing Requirements
- 数据库结构变更需更新版本号并编写 Migration
- DAO 查询应在真实数据量下验证性能和正确性
- `HubConversation` 是纯 UI 数据类，不存储在数据库中

### Common Patterns
- 所有 `@Entity` 使用 `@PrimaryKey(autoGenerate = true)`
- `Date` 通过 `Converters` 与 `Long`（时间戳）互转
- 软删除：`AllowedSender` 使用 `isActive` 字段而非物理删除
- 批量操作：DAO 提供批量插入/删除方法

## Dependencies

### Internal
- `../classifier/` — 使用 `MessageCategory`、`FilterType`
- `../filters/` — 使用 `MessageCategory`、`FilterType`
- `../service/` — 使用 DAO 进行消息存储和查询
- `../ui/` — 使用 DAO 进行数据展示

### External
- Room — SQLite ORM
- Kotlin Coroutines — 异步数据库操作
- Gson — JSON 序列化（与 `PreferencesManager` 配合）

<!-- MANUAL: -->
