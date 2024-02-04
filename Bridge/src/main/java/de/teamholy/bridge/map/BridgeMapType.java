package de.teamholy.bridge.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum BridgeMapType {

    LONG("Long",50, 50,Material.SANDSTONE, new HologramCords(-4, 4, -2)),
    SHORT("Short",25,50, Material.STONE_SLAB2, new HologramCords(-4, 4, -2)),
    DIAGONAL("Diagonal",20, 50, Material.SANDSTONE_STAIRS, new HologramCords(-4, 4, -2));

    private String name;
    private int length;
    private int distanceBetweenMaps;
    private Material icon;
    private HologramCords hologramCords;

    public record HologramCords(int xADD, int yADD, int zADD) {
    }

    public static BridgeMapType mapType(String name) {
        for (var value : values()) {
            if (value.getName().toLowerCase().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
