<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-07-01 | Updated: 2026-07-01 -->

# classifier

## Purpose
ML 分类模块，使用 TensorFlow Lite 对短信文本进行智能分类。将消息分为 5 个类别（通用、促销、通知、交易、垃圾），并决定是否拦截。当 ML 模型不可用或出错时，自动降级到基于关键词的规则分类。

## Key Files

| File | Description |
|------|-------------|
| `SmsClassifier.kt` | 单例 TFLite 分类器，包含文本预处理、推理和规则回退 |

## Subdirectories

（无子目录）

## For AI Agents

### Working In This Directory
- `SmsClassifier` 是线程安全的单例（`@Volatile` + `synchronized`）
- 分类结果通过 `FilterResult` 数据类返回（包含 `category`、`confidence`、`isBlocked`、`filterType`）
- 仅当分类为 `JUNK` 且置信度 > 0.7 时才拦截
- 模型加载从 `assets/` 读取：`sms_model.tflite`、`labels.txt`、`vocabulary.txt`
- 文本预处理：分词 → 词表映射 → 填充/截断到 `maxSequenceLength`（100）

### Testing Requirements
- 修改模型或预处理逻辑后需在真实设备上测试分类准确性
- 可在 `TestFilterScreen` 中交互式验证分类结果
- 确保 `initialize()` 在 `SmsFilterService.onCreate()` 中被调用

### Common Patterns
- 回退分类 `fallbackClassification()` 支持英语和土耳其语关键词
- 预处理将文本转为长度为 100 的 float 数组
- `cleanup()` 关闭 TFLite Interpreter 释放资源

## Dependencies

### Internal
- `../database/` — 使用 `MessageCategory`、`FilterType`、`FilterResult`
- `../filters/` — 回退逻辑依赖关键词列表

### External
- TensorFlow Lite (`org.tensorflow.lite.Interpreter`) — 端侧 ML 推理

<!-- MANUAL: -->
