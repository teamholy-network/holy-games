package de.teamholy.bridge.map;

import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public enum BridgeMapType {

    EXTRASHORT("ExtraShort",13,20,
            new ItemBuilder(Material.STONE_SLAB2, 1)
                    .build(),180, new InMapCords(-4, 5, -2), new InMapCords(0, 2, -19), 0.65),
    SHORT("Short",19,20,
            new ItemBuilder(Material.WOOD_STEP, 1)
            .build(),180, new InMapCords(-4, 5, -2), new InMapCords(0, 2, -23),0.5),
    LONG("Long",50, 25,
            new ItemBuilder(Material.WOOD, 1)
                    .build(),180, new InMapCords(-4, 5, -2),new InMapCords(0, 0, -56),0.3),
    DIAGONAL("Diagonal",20, 50,
            new ItemBuilder(Material.WOOD_STAIRS, 1)
            .build(),-135, new InMapCords(-4, 5, -2),new InMapCords(25, 0, -25),0.4);

    private final String name;
    private final int length;
    private final int distanceBetweenMaps;
    private final ItemStack icon;
    private final float spawnYaw;
    private final InMapCords hologramCords;
    private final InMapCords finishLine;
    private final double failReward;

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
