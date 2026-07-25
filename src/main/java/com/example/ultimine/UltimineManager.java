package com.example.ultimine;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class UltimineManager {

    private static UltimineManager instance;
    
    // 存储玩家通过命令常开的激活状态
    private final Map<UUID, Boolean> activePlayers = new HashMap<>();
    // 存储玩家通过按住 Shift（潜行）临时激活的状态
    private final Map<UUID, Boolean> sneakActivePlayers = new HashMap<>();
    // 存储玩家当前选择的形状
    private final Map<UUID, String> playerShapes = new HashMap<>();
    // 防止无限递归
    private final Set<UUID> processingPlayers = new HashSet<>();

    private UltimineManager() {
    }

    public static UltimineManager getInstance() {
        if (instance == null) {
            instance = new UltimineManager();
        }
        return instance;
    }

    /**
     * 检查玩家是否激活了连锁破坏
     * 命令常开 或 按住 Shift 临时激活，任一满足即视为激活
     */
    public boolean isActive(UUID playerId) {
        return activePlayers.getOrDefault(playerId, false)
                || sneakActivePlayers.getOrDefault(playerId, false);
    }

    /**
     * 设置玩家通过命令的常开状态
     */
    public void setActive(UUID playerId, boolean active) {
        activePlayers.put(playerId, active);
    }

    /**
     * 切换玩家命令常开状态
     */
    public void toggleActive(UUID playerId) {
        activePlayers.put(playerId, !activePlayers.getOrDefault(playerId, false));
    }

    /**
     * 设置玩家按住 Shift 的临时激活状态
     */
    public void setSneakActive(UUID playerId, boolean active) {
        sneakActivePlayers.put(playerId, active);
    }

    /**
     * 获取玩家命令常开状态（用于提示信息，不含 Shift 临时状态）
     */
    public boolean isCommandActive(UUID playerId) {
        return activePlayers.getOrDefault(playerId, false);
    }

    /**
     * 清理玩家所有状态（退出服务器时调用）
     */
    public void clearPlayer(UUID playerId) {
        activePlayers.remove(playerId);
        sneakActivePlayers.remove(playerId);
        playerShapes.remove(playerId);
    }

    /**
     * 获取玩家当前形状
     */
    public String getShape(UUID playerId) {
        return playerShapes.getOrDefault(playerId,
                UltiminePlugin.getInstance().getPluginConfig().getString("default-shape", "shapeless"));
    }

    /**
     * 设置玩家形状
     */
    public void setShape(UUID playerId, String shape) {
        playerShapes.put(playerId, shape);
    }

    /**
     * 检查玩家是否有权限使用某个形状
     */
    public boolean hasShapePermission(Player player, String shape) {
        // 检查是否启用了精细权限控制
        boolean finePermissions = UltiminePlugin.getInstance().getPluginConfig()
                .getBoolean("permissions.enable-fine-permissions", false);
        
        if (!finePermissions) {
            return true; // 如果没有启用精细权限，默认允许
        }

        // 获取形状对应的权限
        String permission = UltiminePlugin.getInstance().getPluginConfig()
                .getString("permissions.shape-permissions." + shape);
        
        if (permission == null || permission.isEmpty()) {
            return true; // 如果没有配置权限，默认允许
        }

        return player.hasPermission(permission);
    }

    /**
     * 处理方块破坏事件
     */
    public void handleBlockBreak(Player player, Block block) {
        UUID playerId = player.getUniqueId();

        // 防止无限递归
        if (processingPlayers.contains(playerId)) {
            return;
        }

        // 检查玩家是否激活了连锁破坏
        if (!isActive(playerId)) {
            return;
        }

        // 检查游戏模式
        if (player.getGameMode() != org.bukkit.GameMode.SURVIVAL && player.getGameMode() != org.bukkit.GameMode.ADVENTURE) {
            return;
        }

        // 检查权限
        if (!player.hasPermission("ultimine.use")) {
            return;
        }

        // 检查形状权限
        String shape = getShape(playerId);
        if (!hasShapePermission(player, shape)) {
            player.sendMessage("§c你没有权限使用这个形状！");
            return;
        }

        Material blockType = block.getType();

        // 检查方块是否在黑名单中
        if (isBlockBlacklisted(blockType)) {
            return;
        }

        // 检查方块是否在白名单中（如果白名单不为空）
        if (!isBlockWhitelisted(blockType)) {
            return;
        }

        // 判断是否空手
        ItemStack tool = player.getInventory().getItemInMainHand();
        boolean emptyHand = tool.getType() == Material.AIR;

        // 非空手时，若配置了需要工具则必须是工具
        if (!emptyHand && UltiminePlugin.getInstance().getPluginConfig().getBoolean("require-tool")) {
            if (!isTool(tool.getType())) {
                return;
            }
        }

        // 开始处理
        processingPlayers.add(playerId);

        try {
            // 根据形状执行连锁破坏
            List<Block> blocksToBreak = getBlocksToBreak(block, shape, player);

            // 限制最大方块数
            int maxBlocks = UltiminePlugin.getInstance().getPluginConfig().getInt("max-blocks", 64);
            if (blocksToBreak.size() > maxBlocks) {
                blocksToBreak = blocksToBreak.subList(0, maxBlocks);
            }

            // 破坏方块
            for (Block targetBlock : blocksToBreak) {
                if (targetBlock.equals(block)) {
                    continue; // 跳过原始方块（已经被破坏了）
                }

                // 检查方块是否可以破坏
                if (!canBreakBlock(player, targetBlock)) {
                    continue;
                }

                // 破坏方块
                breakBlock(player, targetBlock);
            }

            // 消耗饥饿值
            float exhaustion = (float) UltiminePlugin.getInstance().getPluginConfig()
                    .getDouble("exhaustion-per-block", 0.005);
            player.setExhaustion(player.getExhaustion() + exhaustion * blocksToBreak.size());

        } finally {
            processingPlayers.remove(playerId);
        }
    }

    /**
     * 检查方块是否在黑名单中
     */
    private boolean isBlockBlacklisted(Material material) {
        List<String> blacklist = UltiminePlugin.getInstance().getPluginConfig()
                .getStringList("blacklist");
        
        if (blacklist == null || blacklist.isEmpty()) {
            return false;
        }

        String materialName = material.name().toLowerCase();
        for (String blocked : blacklist) {
            if (blocked.toLowerCase().contains(materialName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查方块是否在白名单中
     */
    private boolean isBlockWhitelisted(Material material) {
        List<String> whitelist = UltiminePlugin.getInstance().getPluginConfig()
                .getStringList("whitelist");
        
        // 如果白名单为空，表示允许所有方块
        if (whitelist == null || whitelist.isEmpty()) {
            return true;
        }

        String materialName = material.name().toLowerCase();
        for (String allowed : whitelist) {
            if (allowed.toLowerCase().contains(materialName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查物品是否是工具
     */
    private boolean isTool(Material material) {
        if (material == Material.AIR) {
            return false;
        }
        String name = material.name().toLowerCase();
        return name.contains("pickaxe") || name.contains("axe") ||
                name.contains("shovel") || name.contains("hoe") ||
                name.contains("sword");
    }

    /**
     * 判断方块是否「手能破坏且会掉落」
     * 用空手模拟采集，若掉落列表为空说明手不能破坏或破坏后无掉落（如石头）
     */
    private boolean canHandBreak(Block block) {
        try {
            Collection<ItemStack> drops = block.getDrops(new ItemStack(Material.AIR));
            return drops != null && !drops.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据形状获取需要破坏的方块列表
     */
    private List<Block> getBlocksToBreak(Block startBlock, String shape, Player player) {
        List<Block> blocks = new ArrayList<>();
        blocks.add(startBlock);

        switch (shape) {
            case "shapeless":
                blocks.addAll(getShapelessBlocks(startBlock, player));
                break;
            case "small_square":
                blocks.addAll(getSquareBlocks(startBlock, 1, player));
                break;
            case "small_tunnel":
                blocks.addAll(getTunnelBlocks(startBlock, 5, false, player));
                break;
            case "large_tunnel":
                blocks.addAll(getTunnelBlocks(startBlock, 5, true, player));
                break;
            case "mining_tunnel":
                blocks.addAll(getVerticalTunnelBlocks(startBlock, 10, false, player));
                break;
            case "escape_tunnel":
                blocks.addAll(getVerticalTunnelBlocks(startBlock, 10, true, player));
                break;
            default:
                blocks.addAll(getShapelessBlocks(startBlock, player));
        }

        return blocks;
    }

    /**
     * 无形状模式：破坏周围相同类型的方块
     */
    private List<Block> getShapelessBlocks(Block startBlock, Player player) {
        List<Block> blocks = new ArrayList<>();
        Material targetType = startBlock.getType();

        // 空手时额外要求：目标方块类型必须是手能破坏且掉落的
        boolean emptyHand = player.getInventory().getItemInMainHand().getType() == Material.AIR;
        if (emptyHand && !canHandBreak(startBlock)) {
            return blocks; // 起点都不符合条件，直接返回（仅起点本身，不会连锁）
        }

        // 使用 BFS 搜索相同类型的方块
        Queue<Block> queue = new LinkedList<>();
        Set<Block> visited = new HashSet<>();
        queue.add(startBlock);
        visited.add(startBlock);

        int radius = UltiminePlugin.getInstance().getPluginConfig().getInt("shapes.shapeless.radius", 3);

        while (!queue.isEmpty()) {
            Block current = queue.poll();

            // 检查范围
            if (current.getLocation().distance(startBlock.getLocation()) > radius) {
                continue;
            }

            // 检查周围的方块
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) {
                            continue;
                        }

                        Block neighbor = current.getRelative(x, y, z);

                        if (!visited.contains(neighbor) && neighbor.getType() == targetType) {
                            visited.add(neighbor);
                            queue.add(neighbor);
                            blocks.add(neighbor);
                        }
                    }
                }
            }
        }

        return blocks;
    }

    /**
     * 方形模式
     */
    private List<Block> getSquareBlocks(Block center, int radius, Player player) {
        List<Block> blocks = new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = center.getRelative(x, y, z);
                    if (block.getType() == center.getType()) {
                        blocks.add(block);
                    }
                }
            }
        }

        return blocks;
    }

    /**
     * 通道模式
     */
    private List<Block> getTunnelBlocks(Block start, int length, boolean large, Player player) {
        List<Block> blocks = new ArrayList<>();

        // 简化实现：向玩家看的方向延伸
        org.bukkit.util.Vector direction = player.getLocation().getDirection().normalize();
        int dx = (int) Math.round(direction.getX());
        int dz = (int) Math.round(direction.getZ());

        int radius = large ? 1 : 0;

        for (int i = 1; i <= length; i++) {
            for (int rx = -radius; rx <= radius; rx++) {
                for (int ry = -radius; ry <= radius; ry++) {
                    Block block = start.getRelative(dx * i, ry, dz * i);
                    if (block.getType() == start.getType()) {
                        blocks.add(block);
                    }
                }
            }
        }

        return blocks;
    }

    /**
     * 垂直通道模式
     */
    private List<Block> getVerticalTunnelBlocks(Block start, int length, boolean up, Player player) {
        List<Block> blocks = new ArrayList<>();

        int direction = up ? 1 : -1;
        Material targetType = start.getType();

        for (int i = 1; i <= length; i++) {
            Block block = start.getRelative(0, i * direction, 0);
            if (block.getType() == targetType) {
                blocks.add(block);
            } else {
                break;
            }
        }

        return blocks;
    }

    /**
     * 检查方块是否可以破坏
     */
    private boolean canBreakBlock(Player player, Block block) {
        // 检查方块是否可破坏
        if (block.getType() == Material.AIR || block.getType() == Material.BEDROCK) {
            return false;
        }

        // 空手时：仅允许「手能破坏且会掉落」的方块
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getType() == Material.AIR) {
            return canHandBreak(block);
        }

        // 非空手：检查工具耐久度
        if (UltiminePlugin.getInstance().getPluginConfig().getBoolean("prevent-tool-break")) {
            int minDurability = UltiminePlugin.getInstance().getPluginConfig().getInt("prevent-tool-break", 5);
            if (tool.getType().getMaxDurability() > 0 &&
                    tool.getType().getMaxDurability() - tool.getDurability() <= minDurability) {
                return false;
            }
        }

        return true;
    }

    /**
     * 破坏方块（自动判断空手/持工具）
     */
    private void breakBlock(Player player, Block block) {
        ItemStack tool = player.getInventory().getItemInMainHand();

        if (tool.getType() == Material.AIR) {
            // 空手：徒手破坏（按原版徒手规则掉落）
            block.breakNaturally();
        } else {
            // 持工具：使用工具破坏
            block.breakNaturally(tool);

            // 减少工具耐久度
            if (tool.getType().getMaxDurability() > 0) {
                tool.setDurability((short) (tool.getDurability() + 1));
            }
        }
    }
}