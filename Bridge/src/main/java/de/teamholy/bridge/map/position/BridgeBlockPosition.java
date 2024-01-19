package de.teamholy.bridge.map.position;

import de.teamholy.bridge.custom.CustomBlock;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class BridgeBlockPosition {

    public static Block[] bridgeSquaredBlocks(Location pos1, Location pos2) {
        Block[] blocks = new Block[(pos1.getBlockX() - pos2.getBlockX()) * (pos1.getBlockY() - pos2.getBlockY()) * (pos1.getBlockZ() - pos2.getBlockZ())];
        int i = 0;
        for (int x = 0; x < pos1.getBlockX() - pos2.getBlockX(); x++) {
            for (int y = 0; y < pos1.getBlockY() - pos2.getBlockY(); y++) {
                for (int z = 0; z < pos1.getBlockZ() - pos2.getBlockZ(); z++) {
                    blocks[i] = new Location(pos1.getWorld(), x, y, z).getBlock();
                    i++;
                }
            }
        }
        return blocks;
    }

    public static boolean isLocationBetween(Location pos1, Location pos2, Location location) {
        return location.getBlockX() >= pos1.getBlockX() && location.getBlockX() <= pos2.getBlockX() &&
                location.getBlockY() >= pos1.getBlockY() && location.getBlockY() <= pos2.getBlockY() &&
                location.getBlockZ() >= pos1.getBlockZ() && location.getBlockZ() <= pos2.getBlockZ();
    }

    public static List<CustomBlock> select(Location loc1, Location loc2, World w){
        List<CustomBlock> blocks = new ArrayList<>();

        int x1 = loc1.getBlockX();
        int y1 = loc1.getBlockY();
        int z1 = loc1.getBlockZ();

        int x2 = loc2.getBlockX();
        int y2 = loc2.getBlockY();
        int z2 = loc2.getBlockZ();

        int xMin, yMin, zMin;
        int xMax, yMax, zMax;
        int x, y, z;

        if(x1 > x2){ //If x1 is a higher number then x2
            xMin = x2;
            xMax = x1;
        }else{
            xMin = x1;
            xMax = x2;
        }

        if(y1 > y2){
            yMin = y2;
            yMax = y1;
        }else{
            yMin = y1;
            yMax = y2;
        }

        if(z1 > z2){
            zMin = z2;
            zMax = z1;
        }else{
            zMin = z1;
            zMax = z2;
        }

        for(x = xMin; x <= xMax; x ++){
            for(y = yMin; y <= yMax; y ++){
                for(z = zMin; z <= zMax; z ++){
                    Block b = new Location(w, x, y, z).getBlock();
                    blocks.add(new CustomBlock(b.getLocation(), b.getType()));
                }
            }
        }

        return blocks;
    }


}
