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
            new ItemBuilder(Material.SKULL_ITEM, 1, 3)
            .setSkullMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3Rl" +
                    "eHR1cmUvNWEyZjY3NTAwYTY1ZjNjZTc5ZDM0ZWMxNTBkZTkzZGY4ZjYwZWJlNTJlMjQ4ZjVlMWNkYjY5YjA3MjYyNTZmNyJ9fX0=", "")
            .build(),180, new HologramCords(-4, 5, -2)),
    LONG("Long",50, 25,
            new ItemBuilder(Material.SKULL_ITEM, 1, 3)
                    .setSkullMeta(
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3R" +
                                    "leHR1cmUvNjY1NWVmZTA0MTExOTA4NmYxYmJmNmJiMmQ2NWQzYTQ0ZmE0ODkwOGQyODQ4NjA5ZjI4YmZjMDBkMmVlZjg1In19fQ==", "")
                    .build(),180, new HologramCords(-4, 5, -2)),
    DIAGONAL("Diagonal",20, 50,
            new ItemBuilder(Material.SKULL_ITEM, 1, 3)
            .setSkullMeta(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" +
                            "NmE0MDY3NWQzMzNhZGNjYTc1YjM1YjZiYTI5YjRmZDdlNWVhZWI5OTgzOTUyOTUzODViNDk0MTI4NzE1ZGFiMyJ9fX0=", "")
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
