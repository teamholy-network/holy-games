package de.teamholy.bridge.map.service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMap;
import de.teamholy.bridge.map.loader.BridgeMapLoader;
import de.teamholy.bridge.map.loader.BridgeSchematicMapLoader;
import de.teamholy.bridge.map.skin.BridgeMapSkin;
import de.teamholy.bridge.map.skin.BridgeMapSkins;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.service.BridgePlayerService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class BridgeMapService {

    private final List<BridgeMap> maps;
    public static int MAP_COUNT = 6*9;

    public static boolean MAPS_PASTED = false;

    private final Gson gson;

    private final BridgePlayerService bridgePlayerService = Bridge.getInstance().getBridgePlayerService();

    private final BridgeMapLoader bridgeMapLoader;

    public BridgeMapService() {
        this.gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        for (BridgeMapType value : BridgeMapType.values()) {
            Bridge.getInstance().createWorld(value.getName());
            Bridge.getInstance().getLogger().log(Level.INFO, "created world " + value.getName());
        }

        this.bridgeMapLoader = new BridgeSchematicMapLoader();

        this.maps = Lists.newArrayList();
        Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), this::loadSchematics, 40L);
    }

    public void loadSchematics() {
        new BukkitRunnable() {

            int mapTypeIndex = 0;
            int schematicIndex = 0;
            int xCord = 0;

            @Override
            public void run() {
                if (mapTypeIndex < BridgeMapType.values().length) {
                    BridgeMapType bridgeMapType = BridgeMapType.values()[mapTypeIndex];

                    if (schematicIndex < MAP_COUNT) {
                        BridgeMap bridgeMap = new BridgeMap(bridgeMapType.toString() + "-" + schematicIndex, bridgeMapType);
                        bridgeMapLoader.loadMap(bridgeMap,true, new Location(Bukkit.getWorld(bridgeMapType.getName()), xCord, 102, 0),
                                BridgeMapSkins.getDefaultSkin(bridgeMapType),false);
                        maps.add(bridgeMap);

                        xCord += bridgeMapType.getDistanceBetweenMaps();
                        schematicIndex++;
                    } else {
                        xCord = 0;
                        schematicIndex = 0;
                        mapTypeIndex++;
                    }
                } else {
                    MAPS_PASTED = true;
                    this.cancel();
                }
            }
        }.runTaskTimer(Bridge.getInstance(), 0L, 2L);


    }

    public BridgeMap getFreeMap(BridgeMapType bridgeMapType) {
        Optional<BridgeMap> bridgeMapOptional = maps.stream().filter(bridgeMap -> !bridgeMap.isUsed() && bridgeMap.getMapType() == bridgeMapType).findFirst();
        return bridgeMapOptional.orElse(null);
    }

    public void findSelectedMapForPlayer(String name, BridgePlayer bridgePlayer) {
        BridgeMap bridgeMap = maps.stream().filter(bridgeMap1 -> bridgeMap1.getName().equals(name)).findFirst().orElse(null);

        if (bridgeMap == null || bridgeMap.isUsed()) {
            bridgePlayer.getPlayer().kickPlayer("§4§ERROR §cplease rejoin the bridge server.");
        }

        System.out.println("Found map " + bridgeMap.getName() + " for player " + bridgePlayer.getPlayer().getName());
        bridgeMap.setUser(bridgePlayer.getPlayer().getName());
        bridgeMap.setUsed(true);
        bridgePlayer.setMap(bridgeMap);
        bridgePlayer.setLastPlayedMap(bridgeMap.getMapType());
        Location location = new Location(Bukkit.getWorld(bridgeMap.getMapType().getName()),
                bridgeMap.getSpawnLocation().getX(), bridgeMap.getSpawnLocation().getY(),
                bridgeMap.getSpawnLocation().getZ(), bridgeMap.getMapType().getSpawnYaw(), 0);

        bridgePlayer.setUneditedLocation(location);

        bridgePlayer.setMapLocation(
                location.clone().add(0, 0, bridgePlayer.getBridgeSettings().getOffsetZ()));
        Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> bridgePlayer.getPlayer().teleport(bridgePlayer.getMapLocation()), 2);

        if (!bridgePlayer.getSelectedSkins().get(bridgeMap.getMapType()).isDefault()) {
            bridgeMapLoader.loadMap(bridgeMap,false, bridgeMap.getSpawnLocation(), bridgePlayer.getSelectedSkins().get(bridgeMap.getMapType()), true);
        }
        bridgePlayerService.prepareIngamePlayer(bridgePlayer.getPlayer());
        bridgePlayerService.updateHologram(bridgePlayer, true);

    }


    public void findMapForPlayer(BridgeMapType bridgeMapType, BridgePlayer bridgePlayer) {
        BridgeMap bridgeMap = getFreeMap(bridgeMapType);

        if (bridgeMap == null) {
            bridgePlayer.getPlayer().kickPlayer("§c§lNo map found for you, please try again later.");
        }

        System.out.println("Found map " + bridgeMap.getName() + " for player " + bridgePlayer.getPlayer().getName());
        bridgeMap.setUser(bridgePlayer.getPlayer().getName());
        bridgeMap.setUsed(true);
        bridgePlayer.setState(BridgePlayer.PlayerState.INGAME);
        bridgePlayer.setMap(bridgeMap);
        bridgePlayer.setLastPlayedMap(bridgeMap.getMapType());
        Location location = new Location(Bukkit.getWorld(bridgeMap.getMapType().getName()),
                bridgeMap.getSpawnLocation().getX(), bridgeMap.getSpawnLocation().getY(),
                bridgeMap.getSpawnLocation().getZ(), bridgeMap.getMapType().getSpawnYaw(), 0);

        bridgePlayer.setUneditedLocation(location);

        bridgePlayer.setMapLocation(
                location.clone().add(0, 0, bridgePlayer.getBridgeSettings().getOffsetZ()));
        Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> bridgePlayer.getPlayer().teleport(bridgePlayer.getMapLocation()), 2);

        if (!bridgePlayer.getSelectedSkins().get(bridgeMap.getMapType()).isDefault()) {
            bridgeMapLoader.loadMap(bridgeMap,false, bridgeMap.getSpawnLocation(), bridgePlayer.getSelectedSkins().get(bridgeMap.getMapType()), true);
        }
        bridgePlayerService.prepareIngamePlayer(bridgePlayer.getPlayer());
        bridgePlayerService.updateHologram(bridgePlayer, true);
        bridgePlayerService.setScoreboard(bridgePlayer);
        bridgePlayerService.updateScoreboard(bridgePlayer);
    }

    public void resetMap(BridgeMap bridgeMap) {
        bridgeMap.setUsed(false);
        bridgeMap.setUser(null);
        if (!bridgeMap.getBridgeMapSkin().isDefault()) {
            bridgeMapLoader.loadMap(bridgeMap,false, bridgeMap.getSpawnLocation(), BridgeMapSkins.getDefaultSkin(bridgeMap.getMapType()), true);
        }
    }

    public void loadMap(BridgeMap bridgeMap, boolean firstPaste, Location location, BridgeMapSkin bridgeMapSkin, boolean pasteAir)
    {
        bridgeMapLoader.loadMap(bridgeMap, firstPaste, location, bridgeMapSkin, pasteAir);
    }

    public void loadMapAsync(BridgeMap bridgeMap, BridgeMapSkin bridgeMapSkin, boolean air) {
        bridgeMapLoader.loadMapAsync(bridgeMap, bridgeMapSkin, air);
    }

}
