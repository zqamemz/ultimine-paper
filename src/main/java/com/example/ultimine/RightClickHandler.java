package com.example.ultimine;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RightClickHandler implements Listener {

    private final UltimineManager manager;

    public RightClickHandler() {
        this.manager = UltimineManager.getInstance();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        // 检查玩家是否激活了连锁破坏
        if (!manager.isActive(player.getUniqueId())) {
            return;
        }

        // 检查权限
        if (!player.hasPermission("ultimine.use")) {
            return;
        }

        // 处理斧头右键（剥皮）
        if (item.getType().name().toLowerCase().contains("axe") &&
                UltiminePlugin.getInstance().getPluginConfig().getBoolean("features.right-click-axe")) {
            handleAxeStrip(player, block);
        }

        // 处理锄头右键（耕地）
        if (item.getType().name().toLowerCase().contains("hoe") &&
                UltiminePlugin.getInstance().getPluginConfig().getBoolean("features.right-click-hoe")) {
            handleHoeTill(player, block);
        }

        // 处理锹右键（铲平地面）
        if (item.getType().name().toLowerCase().contains("shovel") &&
                UltiminePlugin.getInstance().getPluginConfig().getBoolean("features.right-click-shovel")) {
            handleShovelFlatten(player, block);
        }

        // 处理收获农作物
        if (UltiminePlugin.getInstance().getPluginConfig().getBoolean("features.right-click-harvesting")) {
            handleCropHarvest(player, block);
        }
    }

    /**
     * 处理斧头剥皮
     */
    private void handleAxeStrip(Player player, Block block) {
        Material blockType = block.getType();
        Material strippedType = getStrippedLog(blockType);

        if (strippedType != null) {
            block.setType(strippedType);
            // 播放音效和粒子效果
            player.playSound(block.getLocation(), org.bukkit.Sound.BLOCK_WOOD_BREAK, 1.0f, 1.0f);
        }
    }

    /**
     * 获取剥皮后的木头类型
     */
    private Material getStrippedLog(Material log) {
        switch (log) {
            case OAK_LOG: return Material.STRIPPED_OAK_LOG;
            case SPRUCE_LOG: return Material.STRIPPED_SPRUCE_LOG;
            case BIRCH_LOG: return Material.STRIPPED_BIRCH_LOG;
            case JUNGLE_LOG: return Material.STRIPPED_JUNGLE_LOG;
            case ACACIA_LOG: return Material.STRIPPED_ACACIA_LOG;
            case DARK_OAK_LOG: return Material.STRIPPED_DARK_OAK_LOG;
            case MANGROVE_LOG: return Material.STRIPPED_MANGROVE_LOG;
            case CHERRY_LOG: return Material.STRIPPED_CHERRY_LOG;
            case BAMBOO_BLOCK: return Material.STRIPPED_BAMBOO_BLOCK;
            default: return null;
        }
    }

    /**
     * 处理锄头耕地
     */
    private void handleHoeTill(Player player, Block block) {
        Block aboveBlock = block.getRelative(BlockFace.UP);
        
        // 检查上方是否是空气
        if (aboveBlock.getType() != Material.AIR) {
            return;
        }

        Material blockType = block.getType();
        Material tilledType = getTilledBlock(blockType);

        if (tilledType != null) {
            block.setType(tilledType);
            // 播放音效
            player.playSound(block.getLocation(), org.bukkit.Sound.ITEM_HOE_TILL, 1.0f, 1.0f);
        }
    }

    /**
     * 获取耕地后的方块类型
     */
    private Material getTilledBlock(Material block) {
        switch (block) {
            case GRASS_BLOCK:
            case DIRT:
            case DIRT_PATH:
                return Material.FARMLAND;
            case COARSE_DIRT:
                return Material.DIRT;
            case ROOTED_DIRT:
                return Material.DIRT;
            default:
                return null;
        }
    }

    /**
     * 处理锹铲平地面
     */
    private void handleShovelFlatten(Player player, Block block) {
        Block aboveBlock = block.getRelative(BlockFace.UP);
        
        // 检查上方是否是空气
        if (aboveBlock.getType() != Material.AIR) {
            return;
        }

        Material blockType = block.getType();
        Material flattenedType = getFlattenedBlock(blockType);

        if (flattenedType != null) {
            block.setType(flattenedType);
            // 播放音效
            player.playSound(block.getLocation(), org.bukkit.Sound.ITEM_SHOVEL_FLATTEN, 1.0f, 1.0f);
        }
    }

    /**
     * 获取铲平后的方块类型
     */
    private Material getFlattenedBlock(Material block) {
        switch (block) {
            case GRASS_BLOCK:
            case DIRT:
            case COARSE_DIRT:
            case ROOTED_DIRT:
                return Material.DIRT_PATH;
            default:
                return null;
        }
    }

    /**
     * 处理收获农作物
     */
    private void handleCropHarvest(Player player, Block block) {
        Material blockType = block.getType();
        
        if (isCrop(blockType)) {
            // 检查农作物是否成熟
            if (isCropMature(block)) {
                // 收获农作物
                block.breakNaturally(player.getInventory().getItemInMainHand());
                // 重新种植
                block.setType(blockType);
            }
        }
    }

    /**
     * 检查是否是农作物
     */
    private boolean isCrop(Material material) {
        return material == Material.WHEAT ||
               material == Material.CARROTS ||
               material == Material.POTATOES ||
               material == Material.BEETROOTS ||
               material == Material.NETHER_WART;
    }

    /**
     * 检查农作物是否成熟
     */
    private boolean isCropMature(Block block) {
        org.bukkit.block.data.Ageable ageable = (org.bukkit.block.data.Ageable) block.getBlockData();
        return ageable.getAge() >= ageable.getMaximumAge();
    }
}