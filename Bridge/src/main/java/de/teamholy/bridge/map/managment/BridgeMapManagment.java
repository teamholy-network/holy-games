package de.teamholy.bridge.map.managment;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMap;
import de.teamholy.bridge.map.skin.BridgeMapSkins;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.management.PlayerManagement;
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
public class BridgeMapManagment {

    private final List<BridgeMap> maps;
    public static int MAP_COUNT = 50;

    public static boolean MAPS_PASTED = false;

    private final Gson gson;

    private final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();

    public BridgeMapManagment() {
        this.gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        for (BridgeMapType value : BridgeMapType.values()) {
            Bridge.getInstance().createWorld(value.getName());
            Bridge.getInstance().getLogger().log(Level.INFO, "created world " + value.getName());
        }


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
                        bridgeMap.loadMap(true, new Location(Bukkit.getWorld(bridgeMapType.getName()), xCord, 102, 0),
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


    public void findMapForPlayer(BridgeMapType bridgeMapType, BridgePlayer bridgePlayer) {
        BridgeMap bridgeMap = getFreeMap(bridgeMapType);

        if (bridgeMap == null) {
            bridgePlayer.getPlayer().kickPlayer("§c§lNo map found for you, please try again later.");
        }

        bridgeMap.setUsed(true);
        bridgePlayer.setState(BridgePlayer.PlayerState.INGAME);
        bridgePlayer.setMap(bridgeMap);
        bridgePlayer.setMapLocation(new Location(Bukkit.getWorld(bridgeMap.getMapType().getName()), bridgeMap.getSpawnLocation().getX(), bridgeMap.getSpawnLocation().getY(), bridgeMap.getSpawnLocation().getZ(), bridgeMap.getMapType().getSpawnYaw(), 0));
        Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> bridgePlayer.getPlayer().teleport(bridgePlayer.getMapLocation()), 2);

        if (!bridgePlayer.getSelectedSkins().get(bridgeMap.getMapType()).isDefault()) {
            bridgeMap.loadMap(false, bridgeMap.getSpawnLocation(), bridgePlayer.getSelectedSkins().get(bridgeMap.getMapType()), true);
        }
        playerManagement.prepareIngamePlayer(bridgePlayer.getPlayer());
        playerManagement.updateHologram(bridgePlayer, true);
        playerManagement.setScoreboard(bridgePlayer);
        playerManagement.updateScoreboard(bridgePlayer);
    }

    public void resetMap(BridgeMap bridgeMap) {
        bridgeMap.setUsed(false);
        if (!bridgeMap.getBridgeMapSkin().isDefault()) {
            bridgeMap.loadMap(false, bridgeMap.getSpawnLocation(), BridgeMapSkins.getDefaultSkin(bridgeMap.getMapType()), true);
        }
    }


}
