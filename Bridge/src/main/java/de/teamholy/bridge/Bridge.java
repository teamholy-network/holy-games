package de.teamholy.bridge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import de.teamholy.bridge.command.BridgeCommand;
import de.teamholy.bridge.listener.*;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.map.loader.BridgeSchematicIndex;
import de.teamholy.bridge.map.management.BridgeMapManagement;
import de.teamholy.bridge.player.management.PlayerManagement;
import de.teamholy.bridge.timer.BridgeTimer;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;


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
    private BridgeMapManagement mapManagement;
    private BukkitTask bridgeTimer;

    private BridgeSchematicIndex bridgeSchematicIndex;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.bridgeSchematicIndex = loadBridgeSchematicIndex();

        this.playerManagement = new PlayerManagement();
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

    @SneakyThrows
    private BridgeSchematicIndex loadBridgeSchematicIndex() {
        return new BridgeSchematicIndex(0);
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
