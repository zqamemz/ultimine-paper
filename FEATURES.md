# Ultimine 插件功能详述

## 核心功能

### 1. 连锁破坏系统
- **激活方式**：
  - **按住 Shift（潜行）临时触发**：按住时连锁生效，松开恢复单块挖掘（默认开启，原版体验）
  - `/ultimine toggle` 命令常开：不按 Shift 也一直连锁
- **智能识别**：自动识别相同类型的方块
- **形状支持**：6种不同挖掘形状
- **空手连锁**：主手为空时也能连锁，但仅限「手能破坏且会掉落」的方块（草、花、雪、沙、泥土、南瓜等）；石头/矿石/原木等手破不掉的不会被连锁
- **配置灵活**：可调整最大方块数、消耗等参数

### 2. 挖掘形状详解

#### 无形状（Shapeless）
- 破坏周围所有相同类型的方块
- 基于 BFS 算法搜索
- 可配置搜索半径（默认 3）

#### 小型方形（Small Square）
- 3x3 方形区域
- 适合快速清理区域

#### 小型通道（Small Tunnel）
- 直线通道模式
- 根据玩家朝向自动调整方向
- 长度可配置（默认 5）

#### 大型通道（Large Tunnel）
- 3x3 通道模式
- 适合快速挖掘隧道
- 长度可配置（默认 5）

#### 采矿通道（Mining Tunnel）
- 垂直向下挖掘
- 适合快速挖矿
- 长度可配置（默认 10）

#### 逃生通道（Escape Tunnel）
- 垂直向上挖掘
- 适合快速逃生
- 长度可配置（默认 10）

### 3. 右键功能系统

#### 斧头剥皮
- 右键点击木头自动剥皮
- 支持所有原木类型
- 可配置开关

#### 锄头耕地
- 右键点击泥土自动耕地
- 支持草方块、泥土等
- 可配置开关

#### 锹铲平地面
- 右键点击地面自动铲平
- 创建路径方块
- 可配置开关

#### 农作物收获
- 右键点击成熟农作物自动收获
- 自动重新种植
- 支持小麦、胡萝卜、马铃薯等
- 可配置开关

### 4. 高级功能

#### Shift（潜行）触发系统
- 监听 `PlayerToggleSneakEvent` 实现「按住即触发，松开即恢复」
- 通过 `sneak-trigger` 配置项可关闭
- 顶部 ActionBar 提示当前激活状态
- 与命令常开（`/ultimine toggle`）状态相互独立、可叠加
- 说明：纯服务端插件无法捕获 Tab 键（客户端占用），故采用 Shift 作为替代触发键

#### 空手连锁系统
- 主手为空（AIR）时跳过工具类型检查
- 使用 `block.getDrops(空手)` 判断方块「手能破坏且会掉落」
  - 掉落为空（石头、矿石、原木等）→ 排除
  - 掉落非空（草、沙、雪、花、南瓜等）→ 允许
- 空手破坏使用 `block.breakNaturally()`（无参，纯徒手掉落），不消耗耐久

#### 形状预览系统
- 显示即将破坏的方块轮廓
- 粒子效果可视化（默认 `Particle.ENCHANT`，因 `BARRIER` 在 1.21 已移除）
- 可配置粒子类型和数量
- 实时更新预览

#### 冷却时间系统
- 防止玩家滥用
- 可配置冷却时间（默认 1秒）
- 权限绕过支持

#### 方块黑名单/白名单
- 黑名单：禁止破坏特定方块（如基岩）
- 白名单：只允许破坏特定方块
- 支持子串匹配配置

#### 精细权限控制
- 每个形状独立权限
- 绕过权限（冷却、限制、黑名单）
- 支持权限组管理

#### 工具耐久度保护
- 防止工具损坏
- 可配置最小耐久度阈值
- 自动停止连锁破坏

#### 饥饿值消耗
- 模拟真实挖掘消耗
- 可配置消耗乘数
- 防止无限使用

### 5. 配置选项详解

#### 基础配置
```yaml
sneak-trigger: true     # 允许按住 Shift 临时触发
require-tool: true      # 非空手时要求手持工具
max-blocks: 64          # 每次最大方块数
exhaustion-per-block: 0.005  # 饥饿值消耗
prevent-tool-break: 5  # 工具保护（剩余耐久阈值）
cooldown: 1000         # 冷却时间（毫秒）
```

#### 功能开关
```yaml
features:
  right-click-axe: true      # 斧头剥皮
  right-click-hoe: true      # 锄头耕地
  right-click-shovel: true   # 锹铲平
  right-click-harvesting: true  # 收获农作物
```

#### 预览设置
```yaml
preview:
  enabled: true
  particles:
    enabled: true
    type: "ENCHANT"   # 注意：BARRIER / ENCHANTMENT_TABLE 在 1.21 已移除
    count: 1
```

#### 方块控制
```yaml
blacklist:  # 黑名单
  - "minecraft:bedrock"
whitelist: []  # 白名单（空=全部允许）
```

#### 权限设置
```yaml
permissions:
  enable-fine-permissions: false  # 启用精细权限
  shape-permissions:  # 每个形状的权限
    shapeless: "ultimine.shape.shapeless"
```

### 6. 命令系统

#### 玩家命令
- `/ultimine toggle` - 切换命令常开
- `/ultimine shape <形状>` - 切换形状
- `/ultimine help` - 查看帮助

#### 管理员命令
- `/ultimine reload` - 重载配置

### 7. 权限节点

#### 基础权限
- `ultimine.use` - 使用连锁破坏
- `ultimine.shape` - 切换形状
- `ultimine.reload` - 重载配置

#### 形状权限
- `ultimine.shape.shapeless` - 无形状
- `ultimine.shape.small_square` - 小型方形
- `ultimine.shape.small_tunnel` - 小型通道
- `ultimine.shape.large_tunnel` - 大型通道
- `ultimine.shape.mining_tunnel` - 采矿通道
- `ultimine.shape.escape_tunnel` - 逃生通道

#### 绕过权限
- `ultimine.bypass.cooldown` - 绕过冷却
- `ultimine.bypass.limit` - 绕过限制
- `ultimine.bypass.blacklist` - 绕过黑名单

### 8. 兼容性

#### 服务端核心
- ✅ Paper 1.21.11+
- ✅ Leaves 核心 1.21.11+
- ✅ Spigot 1.21.11+
- ✅ 所有 Paper 分支

#### 客户端
- 无需客户端模组
- 完全服务端侧实现
- 兼容所有客户端版本

### 9. 性能优化

#### 已实现
- 防止无限递归
- 限制最大方块数
- 冷却时间防止滥用

#### 待优化
- 缓存方块数据
- 减少粒子效果计算
- 优化 BFS 算法

### 10. 已知限制

1. 通道方向检测基于玩家朝向，可能不够精确
2. 形状预览可能影响性能（大量粒子）
3. Tab 键无法在服务端触发，已用 Shift 替代
4. 空手连锁的「可掉落」判断依赖 `getDrops`，草方块等会掉泥土/草籽的方块也会被允许

### 11. 未来计划

#### 短期
- [ ] 添加 GUI 配置界面
- [ ] 优化形状方向检测
- [ ] 添加更多粒子效果选项
- [ ] 支持自定义形状

#### 中期
- [ ] 添加经济系统支持（收费功能）
- [ ] 支持 WorldGuard 区域保护
- [ ] 添加统计系统
- [ ] 支持 PlaceholderAPI

#### 长期
- [ ] 添加数据库支持
- [ ] 支持 BungeeCord/Velocity
- [ ] 添加 API 供其他插件调用
- [ ] 开发配套客户端模组（可选，用于实现真正的 Tab 键触发）

## 安装与使用

### 安装步骤
1. 下载 JAR 文件
2. 放入 `plugins` 文件夹
3. 重启服务器
4. 修改 `config.yml`（可选）
5. 执行 `/ultimine reload`

### 使用建议
1. 根据服务器类型调整 `max-blocks`
2. 启用精细权限控制管理玩家能力
3. 配置黑名单防止破坏重要方块
4. 调整冷却时间平衡游戏性

## 技术支持

### 常见问题
1. **插件不工作**：检查是否使用 Paper/Leaves 核心 1.21.11+
2. **权限问题**：检查权限配置
3. **Shift 不触发**：检查 `sneak-trigger` 配置与 `ultimine.use` 权限
4. **性能问题**：降低 `max-blocks` 或关闭预览

### 调试方法
- 查看控制台错误信息
- 使用 `/ultimine reload` 重载配置
- 检查 `plugins/UltiminePlugin/config.yml` 配置
