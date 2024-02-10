package de.teamholy.bridge.map;

import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public enum BridgeMapType {

    SHORT("Short",25,25,
            new ItemBuilder(Material.WOOD_STEP, 1)
            .build(),180, new HologramCords(-4, 5, -2)),
    LONG("Long",50, 25,
            new ItemBuilder(Material.WOOD, 1)
                    .build(),180, new HologramCords(-4, 5, -2)),
    DIAGONAL("Diagonal",20, 50,
            new ItemBuilder(Material.WOOD_STAIRS, 1)
            .build(),-135, new HologramCords(-4, 5, -2));

    private String name;
    private int length;
    private int distanceBetweenMaps;
    private ItemStack icon;
    private float spawnYaw;
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
