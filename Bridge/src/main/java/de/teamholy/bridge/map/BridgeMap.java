package de.teamholy.bridge.map;

import de.teamholy.bridge.map.position.MapPosition;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@Setter
public class BridgeMap implements Cloneable {

    private String name;
    private String title;
    private String materialName;

    private Location location;

    private transient boolean isLoading;
    private transient Player mapPlayer;
    private transient int givenSpace;
    private transient BridgeMapType mapType;

    public BridgeMap(String name, String title, String materialName) {
        this.name = name;
        this.title = title;
        this.materialName = materialName;
    }

    @Override
    public BridgeMap clone() {
        try {
            return (BridgeMap) super.clone();
        } catch (CloneNotSupportedException ex) {
            System.out.println("Error while cloning map " + name + " but skipping it and using the original one");
        }
        return new BridgeMap(name, title, materialName);
    }
}
