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
    private final List<BridgeMap> mapTypes;
    private final List<BridgeMap> loadedMaps;

    private final Gson gson;
    private final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();

    public BridgeMapLoader() {
        this.gson = new GsonBuilder().setPrettyPrinting().serializeNulls()
                .registerTypeAdapter(Location.class, new GsonLocationAdapter())
                .registerTypeAdapter(CustomBlock.class, new GsonBlockAdapter()).create();
        Bridge.getInstance().getLogger().log(Level.INFO, "Loading maps...");
        this.maps = Lists.newArrayList();
        this.mapTypes = Lists.newArrayList();
        this.loadedMaps = Lists.newArrayList();
    }

    public void loadMaps() {
        File mapsFolder = new File(Bridge.getInstance().getDataFolder().getAbsolutePath() + "/maps");
        if (!mapsFolder.exists()) {
            mapsFolder.mkdirs();
        }
        for (File file : Objects.requireNonNull(mapsFolder.listFiles())) {
            if (file.getName().endsWith(".json")) {
                try {
                    BridgeMap bridgeMap = gson.fromJson(new FileReader(file), BridgeMap.class);
                    if (bridgeMap.getName().endsWith("-Normal") || bridgeMap.getName().endsWith("-Inclined")) {
                        mapTypes.add(bridgeMap);
                    } else {
                        maps.add(bridgeMap);
                    }

                    Bridge.getInstance().getLogger().log(Level.INFO, "Loaded map " + bridgeMap.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void save(BridgeMap bridgeMap) {
        File mapsFolder = new File(Bridge.getInstance().getDataFolder().getAbsolutePath() + "/maps");
        if (!mapsFolder.exists()) {
            mapsFolder.mkdirs();
        }
        try {
            Writer writer = new FileWriter(Bridge.getInstance().getDataFolder().getAbsolutePath() + "/maps/" + bridgeMap.getName() + ".json");
            gson.toJson(bridgeMap, writer);
            writer.flush();
            writer.close();
            Bridge.getInstance().getLogger().log(Level.INFO, "Saved map " + bridgeMap.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(BridgeMap bridgeMap) {
        maps.remove(bridgeMap);
        File file = new File(Bridge.getInstance().getDataFolder().getAbsolutePath() + "/maps/" + bridgeMap.getName() + ".json");
        if (file.exists()) {
            file.delete();
        }
    }

    public void loadMapForPlayer(BridgePlayer player, BridgeMap bridgeMap) {
        if (bridgeMap.isLoading()) return;

        MapPosition mapPosition = bridgeMap.getMapPosition().clone();
        var world = mapPosition.getStart().getWorld();

        if (world == null) {
            Bridge.getInstance().getLogger().log(Level.WARNING, "World " + mapPosition.getStart().getWorld().getName() + " is null! Skipping map loading...");
            return;
        }

        if (bridgeMap.getMapPlayer() != player.getPlayer()) {
            loadMapForPlayer(playerManagement.getBridgePlayer(bridgeMap.getMapPlayer()), bridgeMap);
            return;
        }

        bridgeMap.setLoading(true);

        if (!loadedMaps.contains(bridgeMap)) {
            loadedMaps.add(bridgeMap);
        }

        List<CustomBlock> blocksSpawn = mapPosition.getBlocksSpawn();
        List<CustomBlock> blocksEnd = mapPosition.getBlocksEnd();

        var distanceBetweenMaps = calculateFreeSpaceBetweenMaps(bridgeMap.getMapType());
        bridgeMap.setGivenSpace(distanceBetweenMaps);

        List<Location> spawnLocations = Lists.newArrayList();
        List<Location> endLocations = Lists.newArrayList();

        Bukkit.getScheduler().runTask(Bridge.getInstance(), () -> {
            var spawnLocation = mapPosition.getStart().clone().add(distanceBetweenMaps, 0, 0);
            mapPosition.setTransientSpawn(spawnLocation);
            bridgeMap.setMapPosition(mapPosition);

            for (CustomBlock block : blocksSpawn) {
                var distancedLocation = block.getLocation().clone().add(distanceBetweenMaps, 0, 0);

                if (world.getBlockAt(distancedLocation).getType() == Material.AIR) {
                    if (block.getMaterial() != Material.AIR) {
                        distancedLocation.getBlock().setType(block.getMaterial());
                        spawnLocations.add(distancedLocation);
                    }
                }
            }

            for (CustomBlock block : blocksEnd) {
                var distancedLocation = block.getLocation().clone().add(distanceBetweenMaps, 0, 0);

                if (world.getBlockAt(distancedLocation).getType() == Material.AIR) {
                    if (block.getMaterial() != Material.AIR) {
                        distancedLocation.getBlock().setType(block.getMaterial());
                        endLocations.add(distancedLocation);
                    }
                }
            }
        });

        mapPosition.setTransientSpawnLocation(spawnLocations);
        mapPosition.setTransientEndLocation(endLocations);
        bridgeMap.setMapPosition(mapPosition);
        spawnLocations.clear();
        endLocations.clear();

        bridgeMap.setLoading(false);
        player.setMap(bridgeMap);

        var bukkitPlayer = player.getPlayer();
        bridgeMap.setMapPlayer(bukkitPlayer);

        Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> {
            bukkitPlayer.teleport(mapPosition.getTransientSpawn());
            player.setState(BridgePlayer.PlayerState.INGAME);
            playerManagement.prepareIngamePlayer(bukkitPlayer);

            bukkitPlayer.sendMessage(Bridge.PREFIX + "You have joined the map " + bridgeMap.getTitle());
        }, 3);
    }

    public void unloadMap(BridgePlayer bridgePlayer) {
        if (bridgePlayer.getMap() == null) return;
        final BridgeMapManagement mapManagement = Bridge.getInstance().getMapManagement();

        BridgeMap bridgeMap = bridgePlayer.getMap();

        MapPosition mapPosition = bridgeMap.getMapPosition().clone();

        List<Location> blocksSpawn = mapPosition.getTransientSpawnLocation();
        List<Location> blocksEnd = mapPosition.getTransientEndLocation();

        Bukkit.getScheduler().runTask(Bridge.getInstance(), () -> {
            for (var blockLocation : blocksSpawn) {
                blockLocation.getBlock().setType(Material.AIR);
            }

            for (var blockLocation : blocksEnd) {
                blockLocation.getBlock().setType(Material.AIR);
            }

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
        int addSpace = switch (type) {
            case SHORT, NORMAL -> 15;
            case DIAGONAL -> 30;
        };

        for (BridgeMap bridgeMap : maps) {
            MapPosition mapPosition = bridgeMap.getMapPosition();

            if (mapPosition == null) {
                continue;
            }

            Location start = mapPosition.getStart().clone();

            if (start.getWorld().getBlockAt(start.getBlockX() + space, start.getBlockY() - 1, start.getBlockZ()).getType() != Material.AIR) {
                space += addSpace;
            }
        }
        return space;
    }

}
