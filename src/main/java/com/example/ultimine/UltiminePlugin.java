package com.example.ultimine;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class UltiminePlugin extends JavaPlugin {

    private static UltiminePlugin instance;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        instance = this;
        
        // 保存默认配置
        saveDefaultConfig();
        config = getConfig();
        
        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(new UltimineListener(), this);
        Bukkit.getPluginManager().registerEvents(new RightClickHandler(), this);
        
        // 注册命令
        getCommand("ultimine").setExecutor(new UltimineCommand());
        
        getLogger().info("UltiminePlugin 已启用！");
    }

    @Override
    public void onDisable() {
        getLogger().info("UltiminePlugin 已禁用！");
    }

    public static UltiminePlugin getInstance() {
        return instance;
    }

    public FileConfiguration getPluginConfig() {
        return config;
    }
}