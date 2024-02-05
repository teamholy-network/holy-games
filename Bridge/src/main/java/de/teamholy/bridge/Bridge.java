package de.teamholy.bridge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.teamholy.bridge.command.BridgeCommand;
import de.teamholy.bridge.listener.*;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.map.management.BridgeMapManagement;
import de.teamholy.bridge.player.management.SoundPerkManagement;
import de.teamholy.bridge.player.management.PlayerManagement;
import de.teamholy.bridge.song.SongManager;
import de.teamholy.bridge.timer.BridgeTimer;
import de.teamholy.bridge.util.VoidGenerator;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
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

    public static String PREFIX = "§6Bridge §8* §7";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();

    private PlayerManagement playerManagement;
    private SoundPerkManagement soundPerkManagement;
    private BridgeMapManagement mapManagement;
    private BukkitTask bridgeTimer;

    private SongManager songManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        if (Bukkit.getWorld("world") != null) {
            Bukkit.unloadWorld("world", false);
            getLogger().info("Unloaded world");

            Bukkit.createWorld(new WorldCreator("world").generator(new VoidGenerator()).generateStructures(false));
            Bukkit.getWorld("world").getBlockAt(0, 70, 0).setType(Material.AIR);
            getLogger().info("Created world");
        }

        this.songManager = new SongManager();

        this.playerManagement = new PlayerManagement();
        this.soundPerkManagement = new SoundPerkManagement();
        this.mapManagement = new BridgeMapManagement();

        loadCommand();
        loadListener();

        bridgeTimer = new BridgeTimer().runTaskTimer(this, 0, 1);

        for (BridgeMapType value : BridgeMapType.values()) {
            playerManagement.getTopPlayer().put(value, new HashMap<>());
        }

      // mapManagement.getLoader().loadBridgeMapsStartup(20);
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
        Bukkit.getPluginManager().registerEvents(new PlayerCloseListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerPickupItemListener(), this);

        Bukkit.getPluginManager().registerEvents(new FoodLevelChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new WeatherChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDropItemListener(), this);

    }

    public static Bridge getInstance() {
        return getPlugin(Bridge.class);
    }

}
