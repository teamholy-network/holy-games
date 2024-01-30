package de.teamholy.bridge.map.position;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@Setter
public class MapPosition {

    private double minX, minY, minZ, maxX, maxY, maxZ;

    public MapPosition(final Location first, final Location second) {
        this.minX = Math.min(first.getX(), second.getX());
        this.minY = Math.min(first.getY(), second.getY());
        this.minZ = Math.min(first.getZ(), second.getZ());
        this.maxX = Math.max(first.getX(), second.getX());
        this.maxY = Math.max(first.getY(), second.getY());
        this.maxZ = Math.max(first.getZ(), second.getZ());
    }

    public boolean isInMapPosition(Location location, boolean useY) {
        double x = location.getX(), y = location.getY(), z = location.getZ();
        if (useY) {
            return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
        } else
            return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }

}
