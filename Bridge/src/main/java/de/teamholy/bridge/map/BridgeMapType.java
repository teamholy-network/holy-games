package de.teamholy.bridge.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum BridgeMapType {

    NORMAL("Normal",56, 50,Material.SANDSTONE),
    SHORT("Short",23,50, Material.DOUBLE_STONE_SLAB2),
    DIAGONAL("Diagonal",56, 100, Material.SANDSTONE_STAIRS);


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
