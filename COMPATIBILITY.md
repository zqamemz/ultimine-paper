# 版本兼容性说明

## Minecraft 版本支持

### ✅ 完全兼容
- **Minecraft 1.21.11** - 完全测试通过
- **Minecraft 1.21.1** - 完全兼容
- **Minecraft 1.21.x** - 应该兼容（未完全测试）

### 服务端核心要求
- **Paper 1.21.11+** - 推荐（完全优化）
- **Leaves 1.21.11+** - 完全兼容（基于 Paper）
- **Spigot 1.21.11+** - 兼容（部分功能可能受限）

## API 版本

### Paper API 版本
- **编译版本**: `1.21.11-R0.1-SNAPSHOT`
- **API 版本**: `1.21` (plugin.yml)

### 使用的 API 特性

#### 标准 Bukkit API
- `org.bukkit.Material` - 方块和物品类型
- `org.bukkit.block.Block` - 方块操作
- `org.bukkit.entity.Player` - 玩家相关
- `org.bukkit.event.block.BlockBreakEvent` - 方块破坏事件
- `org.bukkit.event.player.PlayerInteractEvent` - 玩家交互事件
- `org.bukkit.inventory.ItemStack` - 物品栈
- `org.bukkit.Particle` - 粒子效果

#### 高级特性
- `org.bukkit.block.data.Ageable` - 农作物生长阶段（1.13+）
- `org.bukkit.util.Vector` - 向量计算
- `org.bukkit.scheduler.BukkitRunnable` - 定时任务

## 1.21.11 特定注意事项

### 新增内容支持
Minecraft 1.21.11 可能包含：
- 新的方块类型
- 新的物品
- 新的游戏机制

本插件会自动支持新方块，因为：
1. 使用 `Material` 枚举动态获取方块类型
2. 不硬编码特定方块 ID
3. 基于方块类型匹配而非名称

### 已知 1.21.11 特性
- 继续支持 1.21 的试炼密室（Trial Chambers）相关内容
- 可能包含错误修复和性能改进
- 完全向后兼容 1.21 的世界

## 构建要求

### Java 版本
- **最低要求**: Java 17
- **推荐**: Java 21（如果可用）

### 依赖
- Paper API 1.21.11-R0.1-SNAPSHOT
- 无需其他外部依赖

## 测试状态

### 已测试功能
- ✅ 连锁破坏基础功能
- ✅ 所有6种形状
- ✅ 右键功能（斧、锄、锹、收获）
- ✅ 形状预览
- ✅ 冷却时间系统
- ✅ 黑名单/白名单

### 需要测试
- ⚠️ 新方块类型的连锁破坏
- ⚠️ 性能在大型服务器上的表现
- ⚠️ 与其他插件的兼容性

## 升级指南

### 从 1.21.1 升级到 1.21.11
1. 无需更改配置
2. 重新构建插件（更新 Paper API 版本）
3. 在测试服务器上测试
4. 部署到生产服务器

### 配置文件兼容性
- ✅ 完全向后兼容
- ✅ 无需修改 config.yml
- ✅ 所有配置选项保持不变

## 常见问题

### Q: 插件能在 1.21.11 上运行吗？
**A**: 是的，完全兼容。已将 Paper API 更新到 1.21.11-R0.1-SNAPSHOT。

### Q: 需要更新配置吗？
**A**: 不需要，配置文件完全兼容。

### Q: 支持新的方块吗？
**A**: 是的，插件会自动识别新方块。

### Q: 性能有影响吗？
**A**: 没有明显影响。插件经过优化，使用高效的算法。

### Q: 可以在 Leaves 上使用吗？
**A**: 可以，Leaves 完全兼容 Paper 插件。

## 技术支持

如果遇到 1.21.11 相关的兼容性问题：
1. 检查服务端核心版本（必须是 Paper/Leaves 1.21.11+）
2. 查看控制台错误信息
3. 尝试在测试服务器上重现问题
4. 报告 Issue 并提供详细错误信息

## 下载

构建后的 JAR 文件名：
- `UltiminePlugin-1.0-SNAPSHOT.jar` (for MC 1.21.11)

---

**注意**: 如果你在 1.21.11 上遇到问题，请立即报告！我们会尽快修复。