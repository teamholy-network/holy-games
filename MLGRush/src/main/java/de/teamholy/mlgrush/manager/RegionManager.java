package de.teamholy.mlgrush.manager;

import org.bukkit.Location;

public class RegionManager {

    private double maxX;

    private double maxY;

    private double maxZ;

    private double minX;

    private double minY;

    private double minZ;

    public RegionManager(Location postionOne, Location postionTwo) {
        this.maxX = Math.max(postionOne.getX(), postionTwo.getX());
        this.maxY = Math.max(postionOne.getY(), postionTwo.getY());
        this.maxZ = Math.max(postionOne.getZ(), postionTwo.getZ());
        this.minX = Math.min(postionOne.getX(), postionTwo.getX());
        this.minY = Math.min(postionOne.getY(), postionTwo.getY());
        this.minZ = Math.min(postionOne.getZ(), postionTwo.getZ());
    }

    public boolean isInRegion(Location location, Boolean checkY) {
        if (checkY.booleanValue())
            return (location.getX() > this.minX && location.getX() < this.maxX && location.getY() > this.minY &&
                    location.getY() < this.maxY && location.getZ() > this.minZ && location.getZ() < this.maxZ);
        return (location.getX() > this.minX && location.getX() < this.maxX &&
                location.getZ() > this.minZ && location.getZ() < this.maxZ);
    }

}
