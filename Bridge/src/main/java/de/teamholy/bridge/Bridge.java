package de.teamholy.bridge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.dytanic.cloudnet.ext.bridge.bukkit.BukkitCloudNetHelper;
import de.teamholy.bridge.command.BridgeCommand;
import de.teamholy.bridge.command.SpectateCommand;
import de.teamholy.bridge.listener.*;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.map.service.BridgeMapService;
import de.teamholy.bridge.player.service.BridgeMapSkinPerkService;
import de.teamholy.bridge.player.service.BridgePlayerService;
import de.teamholy.bridge.player.service.BridgeSoundPerkService;
import de.teamholy.bridge.song.SongManager;
import de.teamholy.bridge.tasks.BridgeTimerTask;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main plugin class for Bridge game mode.
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 */
@Getter
public class Bridge extends JavaPlugin {

    public static final String PREFIX = "§6Bridge §8* §7";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();

    private BridgePlayerService bridgePlayerService;
    private BridgeSoundPerkService bridgeSoundPerkService;
    private BridgeMapService bridgeMapService;
    private BridgeMapSkinPerkService bridgeMapSkinPerkService;

    private SongManager songManager;

    private final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1);

    @Override
    public void onEnable() {
        this.songManager = new SongManager();
        this.bridgeSoundPerkService = new BridgeSoundPerkService();

        this.bridgePlayerService = new BridgePlayerService();
        this.bridgeMapService = new BridgeMapService();
        this.bridgeMapSkinPerkService = new BridgeMapSkinPerkService();

        loadCommand();
        loadListener();

        for (BridgeMapType value : BridgeMapType.values()) {
            bridgePlayerService.getTopPlayer().put(value, new HashMap<>());
        }

        BukkitCloudNetHelper.setMaxPlayers(BridgeMapService.MAP_COUNT);
        BukkitCloudNetHelper.updateServiceInfo();

        BridgeTimerTask timer = new BridgeTimerTask();
        executorService.scheduleAtFixedRate(timer, 0, 10, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDisable() {
        executorService.shutdown();
    }

    /**
     * Registers plugin commands.
     */
    private void loadCommand() {
        Bukkit.getPluginCommand("bridge").setExecutor(new BridgeCommand());
        Bukkit.getPluginCommand("spectate").setExecutor(new SpectateCommand());
    }

    /**
     * Registers event listeners.
     */
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
        Bukkit.getPluginManager().registerEvents(new PlayerSpectateListener(), this);
    }

    /**
     * Creates a flat world for the game.
     *
     * @param worldName the name of the world to create
     */
    public void createWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = WorldCreator.name(worldName)
                    .environment(World.Environment.NORMAL)
                    .type(WorldType.FLAT)
                    .generatorSettings("3;minecraft:air;127;decoration")
                    .generateStructures(false).createWorld();
            world.setTime(6000);
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("randomTickSpeed", "0");
        }
    }

    /**
     * Gets the plugin instance.
     *
     * @return the Bridge plugin instance
     */
    public static Bridge getInstance() {
        return getPlugin(Bridge.class);
    }
}
