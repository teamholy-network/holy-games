package de.teamholy.bridge.map.position;

import com.google.common.collect.Lists;
import de.teamholy.bridge.custom.CustomBlock;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.List;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@Setter
public class MapPosition implements Cloneable {

    private Location high, bottom, start, middle, endHigh, endBottom, endStart;
    private List<CustomBlock> blocksSpawn;
    private List<CustomBlock> blocksEnd;

    private transient List<Location> transientSpawnLocation = Lists.newArrayList(),
            transientEndLocation = Lists.newArrayList();
    private transient Location transientSpawn;

    @Override
    public MapPosition clone() {
        try {
            return (MapPosition) super.clone();
        } catch (CloneNotSupportedException ex) {
            System.out.println("Error while cloning map position but skipping it and using the original one");
        }
        return null;
    }
}
