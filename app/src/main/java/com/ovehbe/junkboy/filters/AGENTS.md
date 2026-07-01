<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-07-01 | Updated: 2026-07-01 -->

# filters

## Purpose
规则过滤引擎，使用关键词匹配、正则表达式和 Under Attack 模式对短信进行规则化分类和拦截。支持 25+ 内置垃圾关键词（英语+土耳其语）、10+ 预编译正则模式，以及用户自定义关键词和正则。

## Key Files

| File | Description |
|------|-------------|
| `CustomFilter.kt` | 单例过滤引擎，三级管道：Under Attack → 自定义规则 → 内置规则 |

## Subdirectories

（无子目录）

## For AI Agents

### Working In This Directory
- `CustomFilter` 是 Kotlin 单例 `object`
- 主入口 `filterMessage()` 执行三级管道：
  1. Under Attack 模式（激进的金钱/紧急/号码模式拦截）
  2. 用户自定义关键词/正则
  3. 内置垃圾关键词/正则 + 非垃圾消息分类
- 分类优先级：TRANSACTION > PROMOTION > NOTIFICATION > JUNK > GENERAL
- 置信度基于匹配关键词数量动态缩放
- 所有正则模式在使用前预编译（性能优化）

### Testing Requirements
- 修改关键词列表后需测试正负样本
- 用户自定义正则可能无效，需安全处理异常
- 在 `TestFilterScreen` 中验证过滤结果

### Common Patterns
- `FilterResult` 数据类封装过滤结果（`isBlocked`、`category`、`filterType`、`confidence`、`matchedRule`）
- 内置垃圾关键词同时支持英语和土耳其语
- 正则模式涵盖：中奖/彩票、金钱金额、紧迫感词、4+ 位数字

## Dependencies

### Internal
- `../database/` — 使用 `MessageCategory`、`FilterType`、`FilterResult`
- `../classifier/` — 与 ML 分类结果合并使用

### External
- 无额外外部依赖（纯 Kotlin 逻辑）

<!-- MANUAL: -->
