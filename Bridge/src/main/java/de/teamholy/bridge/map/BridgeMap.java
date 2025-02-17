package de.teamholy.bridge.map;

import de.teamholy.bridge.map.position.MapPosition;
import de.teamholy.bridge.map.skin.BridgeMapSkin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.Random;

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
    private String user;

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




}
