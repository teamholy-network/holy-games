package dev.charon.bridge;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.extent.clipboard.io.SchematicReader;
import dev.charon.bridge.command.BridgeCommand;
import dev.charon.bridge.listener.*;
import dev.charon.bridge.map.BridgeMapType;
import dev.charon.bridge.map.management.BridgeMapManagement;
import dev.charon.bridge.player.management.PlayerManagement;
import dev.charon.bridge.timer.BridgeTimer;
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
