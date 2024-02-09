package de.teamholy.bridge.map;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.player.management.PlayerManagement;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;
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
    private int pasteCount = 30;

    private final Gson gson;

    private final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();

    public BridgeMapLoader() {
        this.gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        for (BridgeMapType value : BridgeMapType.values()) {
            Bridge.getInstance().createWorld(value.getName());
            Bridge.getInstance().getLogger().log(Level.INFO, "created world");
        }


        this.maps = Lists.newArrayList();
        loadSchematics();
    }

    public void loadSchematics() {

        for (BridgeMapType bridgeMapType : BridgeMapType.values()) {
            int xCord = 0;
            for (int i = 0; i < pasteCount; i++) {

                BridgeMap bridgeMap = new BridgeMap(bridgeMapType.toString() + "-" + i, bridgeMapType);
                bridgeMap.loadMap(new Location(Bukkit.getWorld(bridgeMapType.getName()), xCord, 102, 0),
                        BridgeMapSkins.getDefaultSkin(bridgeMapType).getSchematic());
                maps.add(bridgeMap);

                xCord += bridgeMapType.getDistanceBetweenMaps();
            }
        }

    }


}
