package de.teamholy.bridge.map;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.position.MapPosition;
import de.teamholy.bridge.map.skin.BridgeMapSkin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@Setter
public class BridgeMap implements Cloneable {

    private final int id = new Random().nextInt(1000000);

    private String name;
    private MapPosition mapPosition;

    private Location spawnLocation;
    private Location pasteLocation;

    private int givenSpace;
    private BridgeMapType mapType;
    private boolean isUsed = false;

    private BridgeMapSkin bridgeMapSkin;

    public BridgeMap(String name, BridgeMapType bridgeMapType) {
        this.name = name;
        this.mapType = bridgeMapType;
    }

    @Override
    public BridgeMap clone() {
        try {
            return (BridgeMap) super.clone();
        } catch (CloneNotSupportedException ex) {
            System.out.println("Error while cloning map " + name + " but skipping it and using the original one");
        }
        return new BridgeMap(name, mapType);
    }

    public void loadMap(boolean firstPaste, Location location, BridgeMapSkin bridgeMapSkin, boolean pasteAir) {


        if (firstPaste) {
            this.pasteLocation = location.clone();

            this.spawnLocation = location.clone().add(0.5,0,0.5);
            int distance = (mapType.getLength() + 10);
            if (mapType == BridgeMapType.DIAGONAL) {
                mapPosition = new MapPosition(location.clone().add(distance, 35, 10), location.clone().subtract(10, 2, distance));
            } else {
                mapPosition = new MapPosition(location.clone().add(10, 35, 10), location.clone().subtract(10, 2, distance));
            }

        }


        loadMapAsync(bridgeMapSkin, pasteAir).whenComplete((complete, throwabke) -> {
            if (throwabke != null) {
                throwabke.printStackTrace();
                return;
            }
            this.bridgeMapSkin = complete;
        });
    }

    private CompletableFuture<BridgeMapSkin> loadMapAsync(BridgeMapSkin bridgeMap, boolean air) {
        CompletableFuture<BridgeMapSkin> completableFuture = new CompletableFuture<>();

        completableFuture.completeAsync(() -> {
            File file = new File(Bridge.getInstance().getDataFolder().getAbsolutePath() + "/schematics/" + bridgeMap.getSchematic().getName().replace("%type%", mapType.getName()));
            ClipboardFormat format = ClipboardFormat.findByFile(file);

            if (format == null) {
                System.out.println("Format not found");
                return null;
            }

            try {
                var bukkitWorld = FaweAPI.getWorld(mapType.getName());
                BlockVector vector = new BlockVector(pasteLocation.getBlockX(), pasteLocation.getBlockY(), pasteLocation.getBlockZ());

                Schematic schematic = format.load(file);

                schematic.paste(bukkitWorld, vector, true, air, null);

            } catch (IOException exception) {
                exception.printStackTrace();
            }

            return bridgeMap;
        });


        return completableFuture;
    }

}
