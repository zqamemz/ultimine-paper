package com.example.ultimine;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class UltimineCommand implements CommandExecutor {

    private final UltiminePlugin plugin;
    private final UltimineListener listener;

    public UltimineCommand() {
        this.plugin = UltiminePlugin.getInstance();
        this.listener = new UltimineListener();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("ultimine.reload")) {
                    sender.sendMessage("§c你没有权限执行此命令！");
                    return true;
                }
                plugin.reloadConfig();
                sender.sendMessage("§a配置已重新加载！");
                break;

            case "toggle":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§c只有玩家可以执行此命令！");
                    return true;
                }
                if (!sender.hasPermission("ultimine.use")) {
                    sender.sendMessage("§c你没有权限使用连锁破坏！");
                    return true;
                }
                Player player = (Player) sender;
                listener.toggleActive(player);
                boolean active = listener.isActive(player);
                sender.sendMessage("§a连锁破坏已" + (active ? "§2激活" : "§c关闭"));
                break;

            case "shape":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§c只有玩家可以执行此命令！");
                    return true;
                }
                if (!sender.hasPermission("ultimine.shape")) {
                    sender.sendMessage("§c你没有权限切换形状！");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§c用法: /ultimine shape <形状名称>");
                    listShapes(sender);
                    return true;
                }
                Player shapePlayer = (Player) sender;
                String shape = args[1].toLowerCase();
                java.util.List<String> availableShapes = Arrays.asList("shapeless", "small_square",
                        "small_tunnel", "large_tunnel", "mining_tunnel", "escape_tunnel");

                if (!availableShapes.contains(shape)) {
                    sender.sendMessage("§c未知的形状: " + shape);
                    listShapes(sender);
                    return true;
                }

                listener.setShape(shapePlayer, shape);
                sender.sendMessage("§a已将形状切换为: §e" + shape);
                break;

            case "help":
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== Ultimine 帮助 ===");
        sender.sendMessage("§e/ultimine toggle §7- 切换连锁破坏开关");
        sender.sendMessage("§e/ultimine shape <形状> §7- 切换挖掘形状");
        sender.sendMessage("§e/ultimine reload §7- 重载配置 (需要权限)");
        sender.sendMessage("§e/ultimine help §7- 显示此帮助");

        if (sender.hasPermission("ultimine.shape")) {
            sender.sendMessage("§6可用形状:");
            listShapes(sender);
        }
    }

    private void listShapes(CommandSender sender) {
        sender.sendMessage("§7- shapeless (无形状)");
        sender.sendMessage("§7- small_square (小型方形)");
        sender.sendMessage("§7- small_tunnel (小型通道)");
        sender.sendMessage("§7- large_tunnel (大型通道)");
        sender.sendMessage("§7- mining_tunnel (采矿通道)");
        sender.sendMessage("§7- escape_tunnel (逃生通道)");
    }
}