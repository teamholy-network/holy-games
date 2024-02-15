package de.teamholy.bridge.map;

import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@AllArgsConstructor
@Getter
public enum BridgeMapType {

    SHORT("Short",25,25,
            new ItemBuilder(Material.WOOD_STEP, 1)
            .build(),180, new InMapCords(-4, 5, -2), new InMapCords(0, 0, -30)),
    LONG("Long",50, 25,
            new ItemBuilder(Material.WOOD, 1)
                    .build(),180, new InMapCords(-4, 5, -2),new InMapCords(0, 0, -56)),
    DIAGONAL("Diagonal",20, 50,
            new ItemBuilder(Material.WOOD_STAIRS, 1)
            .build(),-135, new InMapCords(-4, 5, -2),new InMapCords(25, 0, -25));

    private final String name;
    private final int length;
    private final int distanceBetweenMaps;
    private final ItemStack icon;
    private final float spawnYaw;
    private final InMapCords hologramCords;
    private final InMapCords finishLine;

    public record InMapCords(int xADD, int yADD, int zADD) {
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
