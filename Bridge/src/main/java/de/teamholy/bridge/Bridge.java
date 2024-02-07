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
import de.teamholy.bridge.tasks.BridgeTimer;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.concurrent.*;


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


    private SongManager songManager;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

    @Override
    public void onEnable() {
        // Plugin startup logic

        if (Bukkit.getWorld("world") != null) {
            Bukkit.unloadWorld("world", false);
            getLogger().info("Unloaded world");

            createWorld("world");
            Bukkit.getWorld("world").getBlockAt(0, 70, 0).setType(Material.AIR);
            getLogger().info("Created world");
        }

        this.songManager = new SongManager();

        this.playerManagement = new PlayerManagement();
        this.soundPerkManagement = new SoundPerkManagement();
        this.mapManagement = new BridgeMapManagement();
        this.playerManagement.setBridgeMapManagement(mapManagement);

        loadCommand();
        loadListener();

        // period 20ms
        executorService.scheduleAtFixedRate(new BridgeTimer(), 0, 20, TimeUnit.MILLISECONDS);

        for (BridgeMapType value : BridgeMapType.values()) {
            playerManagement.getTopPlayer().put(value, new HashMap<>());
        }

      // mapManagement.getLoader().loadBridgeMapsStartup(20);
    }

    @Override
    public void onDisable() {
        executorService.shutdown();
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

    private void createWorld(String world1) {
        World world = Bukkit.getWorld(world1);
        if (world == null) {
            world = WorldCreator.name(world1)
                    .environment(World.Environment.NORMAL)
                    .type(WorldType.FLAT)
                    .generatorSettings("3;minecraft:air;127;decoration")
                    .generateStructures(false).createWorld();
            world.setTime(6000);
            world.setGameRuleValue("doDaylightCycle", "false");
        }
    }

    public static Bridge getInstance() {
        return getPlugin(Bridge.class);
    }

}
