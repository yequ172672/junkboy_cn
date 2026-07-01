<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-07-01 | Updated: 2026-07-01 -->

# assets

## Purpose
应用资源文件目录，存放 TensorFlow Lite 机器学习模型和相关的词汇表、标签文件。这些文件在应用启动时由 `SmsClassifier` 加载到内存。

## Key Files

| File | Description |
|------|-------------|
| `sms_model.tflite` | TensorFlow Lite SMS 分类模型（端侧推理） |
| `labels.txt` | 模型输出类别标签（5 个分类） |
| `vocabulary.txt` | 文本分词词汇表（用于将短信文本转换为模型输入） |
| `model_info.txt` | 模型文档（训练数据、准确率、使用说明） |

## Subdirectories

（无子目录）

## For AI Agents

### Working In This Directory
- 这些文件由 `SmsClassifier.initialize()` 从 assets 读取
- 模型文件较大，不要频繁修改
- 替换模型需同步更新 `labels.txt` 和 `vocabulary.txt`
- 文件路径在代码中硬编码为 assets 文件名

### Testing Requirements
- 更新模型后需在 `TestFilterScreen` 中验证分类准确性
- 确保模型文件正确打包到 APK（在 `build.gradle` 的 `assets` 源集中）

### Common Patterns
- 模型文件通过 `context.assets.open()` 读取
- 词汇表格式：每行一个词，行号即词 ID

## Dependencies

### Internal
- `../../java/com/ovehbe/junkboy/classifier/` — `SmsClassifier` 读取这些文件

### External
- TensorFlow Lite — 模型推理引擎

<!-- MANUAL: -->
