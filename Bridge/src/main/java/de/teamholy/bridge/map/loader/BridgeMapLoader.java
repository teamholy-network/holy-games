package de.teamholy.bridge.map.loader;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.adapter.GsonBlockAdapter;
import de.teamholy.bridge.custom.CustomBlock;
import de.teamholy.bridge.map.BridgeMap;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.adapter.GsonLocationAdapter;
import de.teamholy.bridge.map.management.BridgeMapManagement;
import de.teamholy.bridge.map.position.MapPosition;
import de.teamholy.bridge.player.management.PlayerManagement;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class BridgeMapLoader {

    private final List<BridgeMap> maps;
    private final List<BridgeMap> loadedMaps;

    private final Gson gson;
    private final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();

    public BridgeMapLoader() {
        this.gson = new GsonBuilder().setPrettyPrinting().serializeNulls()
                .registerTypeAdapter(Location.class, new GsonLocationAdapter())
                .registerTypeAdapter(CustomBlock.class, new GsonBlockAdapter()).create();
        Bridge.getInstance().getLogger().log(Level.INFO, "Loading maps...");
        this.maps = Lists.newArrayList();
        this.loadedMaps = Lists.newArrayList();
    }

    public void loadSchematics() {
        File mapsFolder = new File(Bridge.getInstance().getDataFolder().getAbsolutePath() + "/schematics");
        if (!mapsFolder.exists()) {
            mapsFolder.mkdirs();
        }
        for (File file : Objects.requireNonNull(mapsFolder.listFiles())) {
            if (file.getName().endsWith(".schematic")) {
                System.out.println(file.getName());

                String name = file.getName().replace(".schematic", "");
                BridgeMapType type = BridgeMapType.mapType(file.getName().split("-")[1].replace(".schematic", ""));

                if (type == null) return;

                BridgeMap bridgeMap = new BridgeMap(
                        name,
                        type.getName(), type.getIcon().name());

                maps.add(bridgeMap);

                Bridge.getInstance().getLogger().log(Level.INFO, "Loaded map " + bridgeMap.getName());

            }
        }
    }

    public void loadMapForPlayer(BridgePlayer player, BridgeMap bridgeMap) {
        if (bridgeMap.isLoading()) return;

        BridgeSchematicLoader bridgeMapLoader = new BridgeSchematicLoader(bridgeMap.getName(), bridgeMap.getMapType());
        bridgeMapLoader.loadSchematic(player.getPlayer()).thenAccept(loaded -> {
            if (loaded) {
                Location center = bridgeMapLoader.getLocation().clone();

                if (bridgeMap.getMapPlayer() != player.getPlayer()) {
                    loadMapForPlayer(playerManagement.getBridgePlayer(bridgeMap.getMapPlayer()), bridgeMap);
                    return;
                }

                bridgeMap.setLoading(true);

                if (!loadedMaps.contains(bridgeMap)) {
                    loadedMaps.add(bridgeMap);
                }

                var distanceBetweenMaps = calculateFreeSpaceBetweenMaps(bridgeMap.getMapType());
                bridgeMap.setGivenSpace(distanceBetweenMaps);

                bridgeMap.setLocation(center);

                bridgeMap.setLoading(false);
                player.setMap(bridgeMap);

                var bukkitPlayer = player.getPlayer();
                bridgeMap.setMapPlayer(bukkitPlayer);

                Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> {
                    bukkitPlayer.teleport(center);
                    player.setState(BridgePlayer.PlayerState.INGAME);
                    playerManagement.prepareIngamePlayer(bukkitPlayer);

                    bukkitPlayer.sendMessage(Bridge.PREFIX + "You have joined the map " + bridgeMap.getTitle());
                }, 3);
            }
        });
    }

    public void unloadMap(BridgePlayer bridgePlayer) {
        if (bridgePlayer.getMap() == null) return;
        final BridgeMapManagement mapManagement = Bridge.getInstance().getMapManagement();

        BridgeMap bridgeMap = bridgePlayer.getMap();

        Bukkit.getScheduler().runTask(Bridge.getInstance(), () -> {
            if (!mapManagement.getChangedLocations().isEmpty() && mapManagement.getChangedLocations().containsKey(bridgePlayer.getPlayer().getUniqueId())) {
                for (Location location : mapManagement.getChangedLocations().get(bridgePlayer.getPlayer().getUniqueId())) {
                    location.getBlock().setType(Material.AIR);
                }
            }
        });

        loadedMaps.removeIf(map -> map.getMapPlayer() != null && map.getMapPlayer().equals(bridgePlayer.getPlayer()));

        bridgeMap.setMapPlayer(null);
        bridgePlayer.setMap(null);
        playerManagement.getPlayerTime().remove(bridgePlayer.getPlayer().getUniqueId());
    }

    private int calculateFreeSpaceBetweenMaps(BridgeMapType type) {
        List<BridgeMap> maps = loadedMaps;

        if (maps.isEmpty()) {
            return 0;
        }

        int space = 0;
        int addSpace = type.getDistanceBetweenMaps();

        for (BridgeMap bridgeMap : maps) {
            Location mapPosition = bridgeMap.getLocation();

            if (mapPosition == null) {
                continue;
            }


            if (mapPosition.getWorld().getBlockAt(mapPosition.getBlockX() + space, mapPosition.getBlockY() - 1, mapPosition.getBlockZ()).getType() != Material.AIR) {
                space += addSpace;
            }
        }
        return space;
    }

}
