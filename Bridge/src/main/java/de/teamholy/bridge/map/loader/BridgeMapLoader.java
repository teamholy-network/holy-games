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
import java.util.UUID;
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
    private final List<UUID> loadedMaps;

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
                String name = file.getName().replace(".schematic", "");
                BridgeMapType type = BridgeMapType.mapType(file.getName().split("-")[1].replace(".schematic", "").toLowerCase());

                if (type == null) return;

                BridgeMap bridgeMap = new BridgeMap(
                        name,
                        name.split("-")[0],
                        type.getIcon().name(), type);

                for (int i= 0; i <= 100; i++) {
                    maps.add(bridgeMap);
                }

                Bridge.getInstance().getLogger().log(Level.INFO, "Loaded map " + bridgeMap.getName() + " with type: " + bridgeMap.getMapType().getName());
            }
        }
    }

    public BridgeMap getMap(String name, BridgeMapType type) {
        for (BridgeMap bridgeMap : maps) {
            if (bridgeMap.getName().equalsIgnoreCase(name) && bridgeMap.getMapType() == type) {
                return bridgeMap;
            }
        }
        return null;
    }

    public void loadMapForPlayer(BridgePlayer player, BridgeMap bridgeMap, boolean changedType) {
        if (bridgeMap.isLoading()) return;
        if (bridgeMap.getMapType() == null) return;

        int x = calculateFreeSpaceBetweenMaps();
        if (bridgeMap.getGivenSpace() != x)
            bridgeMap.setGivenSpace(x);

        Location mapLocation = new Location(Bukkit.getWorld("world"), x, 100, 0, bridgeMap.getMapType().getSpawnYaw(), 0.5f).add(0.5, 0, 0.5);
        while (locationIsNotFree(player, mapLocation)) {
            int newX = calculateFreeSpaceBetweenMaps();
            mapLocation.add(newX, 0, 0);
            if (bridgeMap.getGivenSpace() != newX)
                bridgeMap.setGivenSpace(newX);
        }

        playerManagement.setScoreboard(player);

        /*
        if (bridgeMap.isInUse()) {
            Bukkit.broadcastMessage("§cThe map §e" + bridgeMap.getTitle() + " §cis already in use!");
            return;
        }*/

        bridgeMap.loadMap(mapLocation).thenAccept(loaded -> {
            if (loaded) {

                player.setMap(bridgeMap);

                if (mapLocation != player.getMapLocation())
                    player.setMapLocation(mapLocation);

                loadedMaps.add(player.getUuid());

                var bukkitPlayer = player.getPlayer();

                Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> {
                    if (player.getState() != BridgePlayer.PlayerState.INGAME)
                        player.setState(BridgePlayer.PlayerState.INGAME);

                    bukkitPlayer.teleport(mapLocation);
                    playerManagement.prepareIngamePlayer(bukkitPlayer);
                    playerManagement.updateHologram(player,true);

                    playerManagement.updateScoreboard(player);

                    if (!changedType)
                        bukkitPlayer.sendMessage(Bridge.PREFIX + "You have joined the map §e" + bridgeMap.getTitle() + " §7with the type §6" + bridgeMap.getMapType().getName() + "§8!");
                }, 3);
            } else {
                player.getPlayer().sendMessage(Bridge.PREFIX + "§cThe map §e" + bridgeMap.getName() + " §ccould not be loaded!");
            }
        });
    }


    public void unloadMap(BridgePlayer bridgePlayer, boolean changedType) {
        if (bridgePlayer.getMap() == null) return;

        playerManagement.getPlayerTime().remove(bridgePlayer.getPlayer().getUniqueId());

        BridgeMap bridgeMap = bridgePlayer.getMap();

        loadedMaps.remove(bridgePlayer.getUuid());

        bridgeMap.unloadMap();

        Bukkit.getScheduler().runTask(Bridge.getInstance(), () -> {
            if (!mapManagement.getChangedLocations().isEmpty() && mapManagement.getChangedLocations().containsKey(bridgePlayer.getPlayer().getUniqueId())) {
                for (Location location : mapManagement.getChangedLocations().get(bridgePlayer.getPlayer().getUniqueId())) {
                    location.getBlock().setType(Material.AIR);
                }
            }
        });

        bridgePlayer.setMap(null);
    }


    public boolean locationIsNotFree(BridgePlayer check, Location location) {
        for (BridgePlayer bridgePlayer : playerManagement.getBridgePlayers().values()) {
            if (bridgePlayer.getMapLocation() == null) continue;
            if (!bridgePlayer.getUuid().equals(check.getUuid()) && bridgePlayer.getMapLocation().equals(location)) {
                return true;
            }
        }
        return false;
    }

    public boolean mapIsNotFree(BridgeMap bridgeMap) {
        for (BridgePlayer bridgePlayer : playerManagement.getBridgePlayers().values()) {
            if (bridgePlayer.getMap() == null || bridgePlayer.getMapLocation() == null) continue;

            return bridgePlayer.getMap() == bridgeMap && bridgePlayer.getMap().isInUse();
        }
        return false;
    }

    private int calculateFreeSpaceBetweenMaps() {
        if (loadedMaps.isEmpty()) {
            return 0;
        }

        int space = 0;
        int addSpace = 50;

        for (UUID ignored : loadedMaps) {
            while (Bukkit.getWorld("world").getBlockAt(space, 100 - 2, 0).getType() != Material.AIR) {
                space += addSpace;
            }
        }
        return space;
    }

}
