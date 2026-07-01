<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-07-01 | Updated: 2026-07-01 -->

# smsreceiver

## Purpose
短信和彩信广播接收器，拦截系统传入的 SMS 和 MMS 消息。SMS 接收器会重组多段 PDU 短信、执行快速垃圾检测，并将完整消息传递给前台服务进行深度处理。

## Key Files

| File | Description |
|------|-------------|
| `SmsReceiver.kt` | SMS 广播接收器，拦截传入短信并启动过滤服务 |
| `MmsReceiver.kt` | MMS 接收器桩，仅用于满足默认 SMS 应用要求 |

## Subdirectories

（无子目录）

## For AI Agents

### Working In This Directory
- `SmsReceiver` 需在 `AndroidManifest.xml` 中注册为高优先级广播接收器
- 应用必须成为默认 SMS 应用才能接收 SMS_DELIVER 广播
- 多段 SMS 重组：从 `intent` 中提取所有 PDU 并合并为完整消息体
- 快速垃圾检测：对明显垃圾消息直接拦截并中止广播，减少资源消耗
- 快速检测遵循用户偏好（`isKeywordFilteringEnabled`、`isMlFilteringEnabled`）
- `MmsReceiver` 当前仅记录日志，不处理实际 MMS 内容

### Testing Requirements
- 必须设置为默认 SMS 应用才能接收短信
- 多段短信测试：发送长短信验证 PDU 重组
- 快速拦截测试：发送含明显垃圾关键词的短信验证广播中止

### Common Patterns
- PDU 解析支持格式化（`createFromPdu`）和过时（`getMessageFromPdu`）两种路径
- 快速检测逻辑：2+ 个模式匹配或 1 个高置信度短语 → 视为垃圾
- 可疑 URL 和赌博术语纳入快速检测关键词

## Dependencies

### Internal
- `../utils/` — 使用 `PreferencesManager` 读取过滤偏好
- `../service/` — 启动 `SmsFilterService` 进行深度过滤
- `../filters/` — 快速检测使用关键词模式

### External
- Android SMS 广播 API (`Telephony.Sms.Intents.SMS_DELIVER_ACTION`)
- Android `SmsMessage` API（PDU 解析）

<!-- MANUAL: -->
