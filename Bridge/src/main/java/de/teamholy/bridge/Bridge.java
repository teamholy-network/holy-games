package de.teamholy.bridge;

import de.teamholy.bridge.command.BridgeCommand;
import de.teamholy.bridge.listener.*;
import dev.charon.bridge.listener.*;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.map.management.BridgeMapManagement;
import de.teamholy.bridge.player.management.PlayerManagement;
import de.teamholy.bridge.timer.BridgeTimer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;


/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class Bridge extends JavaPlugin {

    private PlayerManagement playerManagement;
    private BridgeMapManagement mapManagement;
    private BukkitTask bridgeTimer;
    public static String PREFIX = "§6Bridge §8* §7";

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.playerManagement = new PlayerManagement();
        this.mapManagement = new BridgeMapManagement();

        loadCommand();
        loadListener();

        bridgeTimer = new BridgeTimer().runTaskTimer(this, 0, 1);

        for (BridgeMapType value : BridgeMapType.values()) {
            playerManagement.getTopPlayer().put(value, new HashMap<>());
        }

      //  mapManagement.getLoader().loadBridgeMapsStartup(20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        bridgeTimer.cancel();
    }

    private void loadCommand() {
        Bukkit.getPluginCommand("bridge").setExecutor(new BridgeCommand());
    }

    private void loadListener() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractAtItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);

        Bukkit.getPluginManager().registerEvents(new FoodLevelChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntitySpawnListener(), this);
        Bukkit.getPluginManager().registerEvents(new WeatherChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDropItemListener(), this);
    }

    public static Bridge getInstance() {
        return getPlugin(Bridge.class);
    }

}
