package com.example.ultimine;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UltimineListener implements Listener {

    private final UltimineManager manager;
    private final ShapePreview shapePreview;
    
    // 冷却时间系统
    private final Map<UUID, Long> cooldownMap = new HashMap<>();
    private static final long COOLDOWN_TIME = 1000; // 1秒冷却时间（毫秒）

    public UltimineListener() {
        this.manager = UltimineManager.getInstance();
        this.shapePreview = new ShapePreview();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // 检查冷却时间
        if (isOnCooldown(playerId)) {
            return;
        }

        manager.handleBlockBreak(player, event.getBlock());
        
        // 设置冷却时间
        setCooldown(playerId);
    }

    /**
     * 监听潜行（Shift）切换：按住 Shift 临时激活连锁破坏，松开恢复单块挖掘
     */
    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        // 检查是否启用了 Shift 触发
        if (!UltiminePlugin.getInstance().getPluginConfig().getBoolean("sneak-trigger", true)) {
            return;
        }

        // 检查权限
        if (!player.hasPermission("ultimine.use")) {
            return;
        }

        boolean sneaking = event.isSneaking(); // true = 按下 Shift，false = 松开
        manager.setSneakActive(player.getUniqueId(), sneaking);

        if (sneaking) {
            player.sendActionBar("§a连锁破坏已激活（按住 Shift）");
        } else {
            // 仅当命令也未常开时才提示恢复
            if (!manager.isCommandActive(player.getUniqueId())) {
                player.sendActionBar("§7连锁破坏已关闭");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 清理玩家数据
        Player player = event.getPlayer();
        manager.clearPlayer(player.getUniqueId());
        shapePreview.stopPreview(player);
        cooldownMap.remove(player.getUniqueId());
    }

    /**
     * 检查玩家是否激活了连锁破坏
     */
    public boolean isActive(Player player) {
        return manager.isActive(player.getUniqueId());
    }

    /**
     * 设置玩家命令常开状态
     */
    public void setActive(Player player, boolean active) {
        manager.setActive(player.getUniqueId(), active);
        
        if (active) {
            shapePreview.stopPreview(player);
        } else {
            shapePreview.stopPreview(player);
        }
    }

    /**
     * 切换玩家命令常开状态
     */
    public void toggleActive(Player player) {
        manager.toggleActive(player.getUniqueId());
        boolean active = manager.isCommandActive(player.getUniqueId());
        
        if (active) {
            player.sendMessage("§a连锁破坏已常开（也可按住 Shift 临时触发）");
        } else {
            player.sendMessage("§c连锁破坏已关闭（命令常开）");
            shapePreview.stopPreview(player);
        }
    }

    /**
     * 获取玩家当前形状
     */
    public String getShape(Player player) {
        return manager.getShape(player.getUniqueId());
    }

    /**
     * 设置玩家形状
     */
    public void setShape(Player player, String shape) {
        manager.setShape(player.getUniqueId(), shape);
        player.sendMessage("§a已将形状切换为: §e" + shape);
    }

    /**
     * 检查玩家是否在冷却时间内
     */
    private boolean isOnCooldown(UUID playerId) {
        Long lastUse = cooldownMap.get(playerId);
        if (lastUse == null) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastUse) < COOLDOWN_TIME;
    }

    /**
     * 设置玩家冷却时间
     */
    private void setCooldown(UUID playerId) {
        cooldownMap.put(playerId, System.currentTimeMillis());
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        shapePreview.cleanup();
    }
}