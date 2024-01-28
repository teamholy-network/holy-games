package de.teamholy.bridge.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum BridgeMapType {

    LONG("Long",50, 50,Material.SANDSTONE),
    SHORT("Short",25,50, Material.DOUBLE_STONE_SLAB2),
    DIAGONAL("Diagonal",20, 25, Material.SANDSTONE_STAIRS);

    private String name;
    private int length;
    private int distanceBetweenMaps;
    private Material icon;

    public static BridgeMapType mapType(String name) {
        for (var value : values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
}
