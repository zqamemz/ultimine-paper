package com.example.ultimine;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ShapePreview {

    private final UltimineManager manager;
    private final UltiminePlugin plugin;
    
    // 存储每个玩家的预览任务
    private final java.util.Map<Player, BukkitRunnable> previewTasks = new java.util.HashMap<>();

    public ShapePreview() {
        this.manager = UltimineManager.getInstance();
        this.plugin = UltiminePlugin.getInstance();
    }

    /**
     * 开始显示形状预览
     */
    public void startPreview(Player player, Block targetBlock) {
        // 停止之前的预览任务
        stopPreview(player);

        // 创建新的预览任务
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !manager.isActive(player.getUniqueId())) {
                    stopPreview(player);
                    return;
                }

                // 获取形状并预览
                String shape = manager.getShape(player.getUniqueId());
                List<Block> blocks = getPreviewBlocks(targetBlock, shape, player);

                // 显示粒子效果
                for (Block block : blocks) {
                    if (!block.equals(targetBlock)) {
                        spawnPreviewParticle(block.getLocation());
                    }
                }
            }
        };

        task.runTaskTimer(plugin, 0L, 5L); // 每5 tick更新一次
        previewTasks.put(player, task);
    }

    /**
     * 停止形状预览
     */
    public void stopPreview(Player player) {
        BukkitRunnable task = previewTasks.remove(player);
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * 获取预览方块列表
     */
    private List<Block> getPreviewBlocks(Block startBlock, String shape, Player player) {
        List<Block> blocks = new ArrayList<>();
        
        // 简化实现：只显示少量方块作为预览
        int previewRadius = 2;
        for (int x = -previewRadius; x <= previewRadius; x++) {
            for (int y = -previewRadius; y <= previewRadius; y++) {
                for (int z = -previewRadius; z <= previewRadius; z++) {
                    Block block = startBlock.getRelative(x, y, z);
                    if (block.getType() == startBlock.getType()) {
                        blocks.add(block);
                    }
                }
            }
        }

        return blocks;
    }

    /**
     * 生成预览粒子
     */
    private void spawnPreviewParticle(Location location) {
        // 在方块周围生成粒子
        Location particleLoc = location.clone().add(0.5, 0.5, 0.5);
        
        // 使用屏障粒子效果
        if (plugin.getPluginConfig().getBoolean("preview.particles.enabled", true)) {
            String particleType = plugin.getPluginConfig().getString("preview.particles.type", "BARRIER");
            try {
                Particle particle = Particle.valueOf(particleType);
                location.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
            } catch (IllegalArgumentException e) {
                // 使用默认粒子（BARRIER/ENCHANTMENT_TABLE 在 1.21 已移除，改用 ENCHANT）
                location.getWorld().spawnParticle(Particle.ENCHANT, particleLoc, 1, 0, 0, 0, 0);
            }
        }
    }

    /**
     * 清理所有预览任务
     */
    public void cleanup() {
        for (BukkitRunnable task : previewTasks.values()) {
            task.cancel();
        }
        previewTasks.clear();
    }
}