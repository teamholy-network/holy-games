package dev.charon.bridge.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum BridgeMapType {

    NORMAL("Normal",56,Material.SANDSTONE),
    SHORT("Short",23,Material.DOUBLE_STONE_SLAB2),
    DIAGONAL("Diagonal",56,Material.SANDSTONE_STAIRS);


    private String name;
    private int length;
    private Material icon;

}
