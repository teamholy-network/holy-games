package de.teamholy.bridge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.dytanic.cloudnet.ext.bridge.bukkit.BukkitCloudNetHelper;
import de.teamholy.bridge.command.BridgeCommand;
import de.teamholy.bridge.command.SpectateCommand;
import de.teamholy.bridge.listener.*;
import de.teamholy.bridge.map.service.BridgeMapService;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.service.BridgeMapSkinPerkService;
import de.teamholy.bridge.player.service.BridgeSoundPerkService;
import de.teamholy.bridge.player.service.BridgePlayerService;
import de.teamholy.bridge.song.SongManager;
import de.teamholy.bridge.tasks.BridgeTimerTask;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

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

    private BridgePlayerService bridgePlayerService;
    private BridgeSoundPerkService bridgeSoundPerkService;
    private BridgeMapService bridgeMapService;
    private BridgeMapSkinPerkService bridgeMapSkinPerkService;


    private SongManager songManager;

    private final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1);

    @Override
    public void onEnable() {
        // Plugin startup logic



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

        // period 20ms
        BridgeTimerTask timer = new BridgeTimerTask();
        //Bukkit.getScheduler().runTaskTimerAsynchronously(this, timer, 0, 20);
        executorService.scheduleAtFixedRate(timer, 5, 50, TimeUnit.MILLISECONDS);

      // mapManagement.getLoader().loadBridgeMapsStartup(20);
    }

    @Override
    public void onDisable() {
        executorService.shutdown();
    }


    private void loadCommand() {
        Bukkit.getPluginCommand("bridge").setExecutor(new BridgeCommand());
        Bukkit.getPluginCommand("spectate").setExecutor(new SpectateCommand());
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
        Bukkit.getPluginManager().registerEvents(new PlayerSpectateListener(), this);

    }

    public void createWorld(String world1) {
        World world = Bukkit.getWorld(world1);
        if (world == null) {
            world = WorldCreator.name(world1)
                    .environment(World.Environment.NORMAL)
                    .type(WorldType.FLAT)
                    .generatorSettings("3;minecraft:air;127;decoration")
                    .generateStructures(false).createWorld();
            world.setTime(6000);
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("randomTickSpeed", "0");
        }
    }

    public static Bridge getInstance() {
        return getPlugin(Bridge.class);
    }

}
