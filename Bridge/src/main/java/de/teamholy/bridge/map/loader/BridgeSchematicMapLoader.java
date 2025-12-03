package de.teamholy.bridge.map.loader;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMap;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.map.position.MapPosition;
import de.teamholy.bridge.map.skin.BridgeMapSkin;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class BridgeSchematicMapLoader implements BridgeMapLoader {

    @Override
    public void loadMap(BridgeMap bridgeMap, boolean firstPaste, Location location, BridgeMapSkin bridgeMapSkin, boolean pasteAir) {
        var mapType = bridgeMap.getMapType();

        if (firstPaste) {
            bridgeMap.setPasteLocation(location.clone());

            bridgeMap.setSpawnLocation(location.clone().add(0.5,0,0.5));
            int distance = (mapType.getLength() + 10);
            if (mapType == BridgeMapType.DIAGONAL) {
                bridgeMap.setMapPosition(new MapPosition(location.clone().add(distance, 35, 10), location.clone().subtract(10, 2, distance)));
            } else {
                bridgeMap.setMapPosition(new MapPosition(location.clone().add(7, 35, 10), location.clone().subtract(7, 2, distance)));
            }
        }

        loadMapAsync(bridgeMap, bridgeMapSkin, pasteAir).whenComplete((complete, throwabke) -> {
            if (throwabke != null) {
                throwabke.printStackTrace();
                return;
            }
            bridgeMap.setBridgeMapSkin(complete);
        });
    }

    @Override
    public CompletableFuture<BridgeMapSkin> loadMapAsync(BridgeMap bridgeMap, BridgeMapSkin bridgeMapSkin, boolean air) {
        CompletableFuture<BridgeMapSkin> completableFuture = new CompletableFuture<>();

        completableFuture.completeAsync(() -> {
            File file = new File(Bridge.getInstance().getDataFolder().getAbsolutePath() + "/schematics/" + bridgeMapSkin.schematic().getName().replace("%type%", bridgeMap.getMapType().getName()));
            ClipboardFormat format = ClipboardFormat.findByFile(file);

            if (format == null) {
                System.out.println("Format not found");
                return null;
            }            try {
                var bukkitWorld = FaweAPI.getWorld(bridgeMap.getMapType().getName());
                BlockVector vector = new BlockVector(bridgeMap.getPasteLocation().getBlockX(), bridgeMap.getPasteLocation().getBlockY(), bridgeMap.getPasteLocation().getBlockZ());

                Schematic schematic = FaweAPI.load(file);
                schematic.paste(bukkitWorld, vector, true, air, null);

            } catch (IOException exception) {
                exception.printStackTrace();
            }

            return bridgeMapSkin;
        });


        return completableFuture;
    }

}
