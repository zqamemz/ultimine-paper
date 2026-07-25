# Ultimine Plugin

FTB Ultimine 模组的 Paper/Leaves 服务器复刻版本，完全兼容 Minecraft 1.21.11。

## 🎯 版本支持

- ✅ **Minecraft 1.21.11** - 完全兼容（推荐）
- ✅ **Minecraft 1.21.1+** - 兼容
- ✅ **Paper 1.21.11+** - 推荐
- ✅ **Leaves 1.21.11+** - 完全兼容
- ✅ **Spigot 1.21.11+** - 兼容

## 功能特性

### 🎯 核心连锁破坏
- **6种挖掘形状**：无形状、小型方形、小型通道、大型通道、采矿通道、逃生通道
- **智能识别**：自动识别并破坏相同类型的方块
- **灵活配置**：可调整最大方块数、消耗、范围等
- **两种触发方式**：
  - 按住 **Shift（潜行）** 临时触发，松开恢复单块挖掘（原版体验，默认开启）
  - `/ultimine toggle` 命令常开（不按 Shift 也一直连锁）
- **空手连锁**：主手为空时也能连锁，但**仅限手能破坏且会掉落的方块**（草、花、雪、沙、泥土、南瓜等；石头/矿石/木头等手破不掉的不会被连锁）

### 🔨 右键增强功能
- **斧头剥皮**：右键木头自动剥皮
- **锄头耕地**：右键泥土自动耕地
- **锹铲平地面**：右键地面创建路径
- **农作物收获**：右键成熟农作物自动收获并重新种植

### 🎨 视觉反馈
- **形状预览**：粒子效果显示即将破坏的方块
- **可配置粒子**：支持多种粒子类型和数量

### ⚙️ 高级系统
- **冷却时间**：防止滥用，可配置
- **方块控制**：黑名单/白名单系统
- **精细权限**：每个形状独立权限控制
- **工具保护**：防止工具损坏
- **饥饿消耗**：模拟真实挖掘消耗

## 快速开始

### 安装
1. 确保服务器运行 **Paper 或 Leaves 核心 1.21.11+**
2. 将 `UltiminePlugin.jar` 放入 `plugins` 文件夹
3. 重启服务器

### 基本使用
- **按住 Shift + 挖矿** → 临时连锁破坏，松开 Shift 恢复普通挖掘（需要 `ultimine.use`）
- `/ultimine toggle` - 命令常开/关闭连锁破坏
- `/ultimine shape <形状>` - 切换挖掘形状
- `/ultimine help` - 查看帮助

### 空手连锁示例
主手空着，按住 Shift 去打草丛 / 挖沙 / 铲雪 / 拔花 / 拆南瓜 → 成片破坏；
打石头、矿石、原木时由于手破不掉，空手连锁不会触发（符合「仅限手破坏可以掉落的方块」）。
拿对应工具即可正常连锁挖矿。

## 配置示例

```yaml
# 触发方式
sneak-trigger: true     # 允许按住 Shift 临时触发（false 则仅命令常开）
require-tool: true      # 非空手时要求手持工具才连锁

# 基础设置
max-blocks: 64          # 每次最多破坏64个方块
cooldown: 1000          # 1秒冷却时间

# 功能开关
features:
  right-click-axe: true
  right-click-hoe: true
  right-click-shovel: true
  right-click-harvesting: true

# 方块控制
blacklist:
  - "minecraft:bedrock"  # 禁止破坏基岩
```

## 权限节点

### 基础权限
- `ultimine.use` - 使用连锁破坏（默认：true）
- `ultimine.shape` - 切换形状（默认：true）
- `ultimine.reload` - 重载配置（默认：op）

### 形状权限（可选）
- `ultimine.shape.shapeless` - 无形状模式
- `ultimine.shape.small_square` - 小型方形
- `ultimine.shape.small_tunnel` - 小型通道
- `ultimine.shape.large_tunnel` - 大型通道
- `ultimine.shape.mining_tunnel` - 采矿通道
- `ultimine.shape.escape_tunnel` - 逃生通道

## 构建

### 使用 Maven
```bash
mvn clean package
```

### 使用构建脚本（Windows）
```bash
build.bat
```

构建后的 JAR 位于 `target/UltiminePlugin-1.0-SNAPSHOT.jar`

## 文档

- [功能详述](FEATURES.md) - 详细功能说明
- [兼容性说明](COMPATIBILITY.md) - 版本兼容性详情
- [配置说明](src/main/resources/config.yml) - 配置文件示例
- [权限说明](src/main/resources/plugin.yml) - 权限节点列表

## 常见问题

### 插件不工作？
- 检查是否使用 **Paper/Leaves 1.21.11+**
- 检查控制台错误信息
- 确保已授予 `ultimine.use` 权限

### 如何确认版本兼容？
- 查看服务端启动日志，确认 Paper/Leaves 版本
- 检查插件是否成功加载（无错误信息）
- 尝试使用 `/ultimine toggle` 命令

### Shift 触发不生效？
- 确认 `config.yml` 中 `sneak-trigger: true`
- 确认玩家有 `ultimine.use` 权限
- 注意：Tab 键无法在服务端插件中触发（客户端占用），故使用 Shift 替代

### 空手连锁不生效？
- 空手仅对「手能破坏且掉落」的方块生效（草、沙、雪、花等）
- 石头、矿石、原木等需手持对应工具

### 性能问题？
- 降低 `max-blocks` 值
- 关闭形状预览（`preview.enabled: false`）
- 增加冷却时间

## 1.21.11 特定说明

本插件专门为 Minecraft 1.21.11 优化：
- ✅ 使用 Paper API 1.21.11-R0.1-SNAPSHOT
- ✅ 支持 1.21.11 的所有新特性
- ✅ 自动兼容新方块类型
- ✅ 完全向后兼容 1.21.1
- ⚠️ 已规避 1.21 移除的 `Particle.BARRIER`，改用 `Particle.ENCHANT`

## 贡献

欢迎提交 Issue 和 Pull Request！

## 许可证

本插件基于 FTB Ultimine 模组的功能设计，仅供学习和研究使用。

## 更新日志

### v1.1 (2026-07-25)
- ✨ 新增「按住 Shift 临时触发」连锁破坏（替代原版 Tab 键，纯服务端方案）
- ✨ 新增「空手连锁」：仅限手能破坏且会掉落的方块
- ✨ 新增 `sneak-trigger` 配置项
- 🐛 修复 1.21.11 中 `Particle.BARRIER` 已移除导致的编译错误
- ✅ 完全兼容 Minecraft 1.21.11

### v1.0 (2026-07-19)
- ✨ 初始版本
- ✨ 实现6种连锁破坏形状
- ✨ 实现右键增强功能
- ✨ 添加形状预览系统
- ✨ 实现冷却时间和方块控制系统
- ✨ 添加精细权限管理
- ✨ **完全兼容 Minecraft 1.21.11**

---

**注意**：这是 FTB Ultimine 的复刻版本，旨在为 Paper/Leaves 1.21.11 服务器提供类似功能。
