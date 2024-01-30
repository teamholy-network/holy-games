package de.teamholy.bridge.map.loader;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMap;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.map.management.BridgeMapManagement;
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
    private final List<BridgeMap> loadedMaps;

    private final Gson gson;
    private final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();

    private final BridgeMapManagement mapManagement;

    public BridgeMapLoader(BridgeMapManagement mapManagement) {
        this.gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        Bridge.getInstance().getLogger().log(Level.INFO, "Loading maps...");
        this.maps = Lists.newArrayList();
        this.loadedMaps = Lists.newArrayList();
        this.mapManagement = mapManagement;
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
                        name.split("-")[0],
                        type.getIcon().name(), type);

                maps.add(bridgeMap);

                Bridge.getInstance().getLogger().log(Level.INFO, "Loaded map " + bridgeMap.getName());
            }
        }
    }

    public void loadMapForPlayer(BridgePlayer player, BridgeMap bridgeMap) {
        if (bridgeMap.isLoading()) return;

        if (bridgeMap.getMapType() == null) bridgeMap.setMapType(player.getMap().getMapType());

        int x = calculateFreeSpaceBetweenMaps(bridgeMap.getMapType());
        bridgeMap.setGivenSpace(x);

        Location mapLocation = new Location(Bukkit.getWorld("world"), x, 100, 0, 180.0f, 0.5f).add(0.5, 0, 0.5);
        while (locationIsNotFree(mapLocation)) {
            int newX = calculateFreeSpaceBetweenMaps(bridgeMap.getMapType());
            mapLocation.add(newX, 0, 0);
            bridgeMap.setGivenSpace(newX);
        }

        playerManagement.setScoreboard(player);

        bridgeMap.loadMap(mapLocation).thenAccept(loaded -> {
            if (loaded) {

                player.setMap(bridgeMap);

                if (!loadedMaps.contains(bridgeMap)) {
                    loadedMaps.add(bridgeMap);
                }

                var bukkitPlayer = player.getPlayer();

                Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> {
                    if (player.getState() != BridgePlayer.PlayerState.INGAME)
                        player.setState(BridgePlayer.PlayerState.INGAME);

                    bukkitPlayer.teleport(mapLocation);
                    playerManagement.prepareIngamePlayer(bukkitPlayer);

                    playerManagement.updateScoreboard(player);

                    bukkitPlayer.sendMessage(Bridge.PREFIX + "You have joined the map §e" + bridgeMap.getTitle() + " §7with the type §6" + bridgeMap.getMapType().getName() + "§8!");
                }, 3);
            } else {
                player.getPlayer().sendMessage(Bridge.PREFIX + "§cThe map §e" + bridgeMap.getName() + " §ccould not be loaded!");
            }
        });
    }



    public void unloadMap(BridgePlayer bridgePlayer) {
        if (bridgePlayer.getMap() == null) return;

        BridgeMap bridgeMap = bridgePlayer.getMap();

        loadedMaps.remove(bridgeMap);

        bridgeMap.unloadMap();

        Bukkit.getScheduler().runTask(Bridge.getInstance(), () -> {
            if (!mapManagement.getChangedLocations().isEmpty() && mapManagement.getChangedLocations().containsKey(bridgePlayer.getPlayer().getUniqueId())) {
                for (Location location : mapManagement.getChangedLocations().get(bridgePlayer.getPlayer().getUniqueId())) {
                    location.getBlock().setType(Material.AIR);
                }
            }
        });

        bridgePlayer.setMap(null);
        playerManagement.getPlayerTime().remove(bridgePlayer.getPlayer().getUniqueId());
    }

    public boolean locationIsNotFree(Location location) {
        for (BridgeMap bridgeMap : loadedMaps) {
            if (bridgeMap.getLocation().equals(location)) {
                return true;
            }
        }
        return false;
    }

    private int calculateFreeSpaceBetweenMaps(BridgeMapType type) {
        List<BridgeMap> maps = loadedMaps;

        if (maps.isEmpty()) {
            return 0;
        }

        int space = 0;
        int addSpace = type.getDistanceBetweenMaps();

        for (BridgeMap bridgeMap : maps) {
            Location spawnLoc = bridgeMap.getLocation();

            if (spawnLoc == null) {
                continue;
            }

            if (spawnLoc.getWorld().getBlockAt(spawnLoc.getBlockX() + space, spawnLoc.getBlockY() - 1, spawnLoc.getBlockZ()).getType() != Material.AIR) {
                space += addSpace;
            }
            /**/
        }
        return space;
    }

}
