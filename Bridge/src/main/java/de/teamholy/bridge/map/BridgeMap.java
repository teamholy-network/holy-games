package de.teamholy.bridge.map;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.position.MapPosition;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

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
    private String title;
    private String materialName;

    private MapPosition mapPosition;
    private EditSession editSession;

    private boolean isLoading, inUse;
    private int givenSpace;
    private BridgeMapType mapType;

    public BridgeMap(String name, String title, String materialName, BridgeMapType bridgeMapType) {
        this.name = name;
        this.title = title;
        this.materialName = materialName;
        this.mapType = bridgeMapType;
    }

    @Override
    public BridgeMap clone() {
        try {
            return (BridgeMap) super.clone();
        } catch (CloneNotSupportedException ex) {
            System.out.println("Error while cloning map " + name + " but skipping it and using the original one");
        }
        return new BridgeMap(name, title, materialName, mapType);
    }

    public CompletableFuture<Boolean> loadMap(Location location) {
        if (editSession != null) {
            editSession.undo(editSession);
        }

        int distance = (mapType.getLength() + 10);
        if (mapType == BridgeMapType.DIAGONAL) {
            mapPosition = new MapPosition(location.clone().add(distance, 35, 10), location.clone().subtract(10, 6, distance));
        } else {
            mapPosition = new MapPosition(location.clone().add(10, 35, 10), location.clone().subtract(10, 6, distance));
        }

        isLoading = true;
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        File schematicFile = new File(Bridge.getInstance().getDataFolder() + "/schematics", getName() + ".schematic");

        ClipboardFormat format = ClipboardFormat.findByFile(schematicFile);
        if (format == null) {
            System.out.println("Format not found");
            completableFuture.complete(false);
            return completableFuture;
        }

        try {
            var bukkitWorld = FaweAPI.getWorld("world");
            BlockVector vector = new BlockVector(location.getBlockX(), location.getBlockY(), location.getBlockZ());

            Schematic schematic = format.load(schematicFile);

            EditSession editSession = schematic.paste(bukkitWorld, vector, true, false, null);
            setEditSession(editSession);

            completableFuture.complete(true);
            isLoading = false;
            inUse = true;
        } catch (IOException exception) {
            exception.printStackTrace();
            completableFuture.complete(false);
        }
        return completableFuture;
    }

    public void unloadMap() {
        givenSpace = 0;

        if (editSession != null) {
            editSession.undo(editSession);
        }

        isLoading = false;
        inUse = false;

        editSession = null;
    }

}
