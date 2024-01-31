package de.teamholy.bridge.map.management;

import com.google.common.collect.Maps;
import de.teamholy.bridge.map.BridgeMap;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.map.loader.BridgeMapLoader;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class BridgeMapManagement {

    private Inventory inventory;
    private final BridgeMapLoader loader;
    private final String title = "§8» §6Maps";

    private final Map<UUID, List<Location>> changedLocations = Maps.newHashMap();
    private final HashMap<BridgeMapType, Inventory> mapSettings = Maps.newHashMap();

    public BridgeMapManagement() {
        this.loader = new BridgeMapLoader(this);
        loadMaps();
    }

    public void loadMaps() {
        loader.loadSchematics();
        if (loader.getMaps().isEmpty())
            return;
        if (this.inventory == null) {
            registerInventory();
        }

        loader.getMaps().forEach(this::addMap);
        System.out.println("Loaded " + loader.getMaps().size() + " maps");
    }

    private void registerInventory() {
        this.inventory = Bukkit.createInventory(null, 9, title);
        this.inventory.setItem(3, new ItemBuilder(BridgeMapType.SHORT.getIcon())
                .name("§a§lShort").amount(loader.getMaps().stream().filter(type -> type.getMapType() == BridgeMapType.SHORT).toList().size()).build());
        this.inventory.setItem(4, new ItemBuilder(BridgeMapType.LONG.getIcon()
        ).name("§e§lLong").amount(loader.getMaps().stream().filter(type -> type.getMapType() == BridgeMapType.LONG).toList().size()).build());
        this.inventory.setItem(5, new ItemBuilder(BridgeMapType.DIAGONAL.getIcon())
                .name("§c§lDiagonal").amount(loader.getMaps().stream().filter(type -> type.getMapType() == BridgeMapType.DIAGONAL).toList().size()).build());

        this.mapSettings.put(BridgeMapType.SHORT,
                Bukkit.createInventory(null, getInventorySizeByMap(BridgeMapType.SHORT), "§8» §a§lShort Maps"));
        this.mapSettings.put(BridgeMapType.LONG,
                Bukkit.createInventory(null, getInventorySizeByMap(BridgeMapType.LONG), "§8» §e§lLong Maps"));
        this.mapSettings.put(BridgeMapType.DIAGONAL,
                Bukkit.createInventory(null, getInventorySizeByMap(BridgeMapType.DIAGONAL), "§8» §c§lDiagonal Maps"));
    }

    private int getInventorySizeByMap(BridgeMapType bridgeMapType) {
        int size = 9;
        if (loader.getMaps().stream().filter(map -> map.getMapType() == bridgeMapType).toList().size() > 9)
            size = 18;
        if (loader.getMaps().stream().filter(map -> map.getMapType() == bridgeMapType).toList().size() > 18)
            size = 27;
        if (loader.getMaps().stream().filter(map -> map.getMapType() == bridgeMapType).toList().size() > 27)
            size = 36;
        if (loader.getMaps().stream().filter(map -> map.getMapType() == bridgeMapType).toList().size() > 36)
            size = 45;
        if (loader.getMaps().stream().filter(map -> map.getMapType() == bridgeMapType).toList().size() > 45)
            size = 54;
        return size;
    }

    public void addMap(BridgeMap map) {
        if (map == null) return;

        Inventory mapInventory = mapSettings.get(map.getMapType());
        if (mapInventory == null) return;

        ItemStack toAdd = new ItemBuilder(Material.valueOf(map.getMaterialName())).name(colorCodeByType(map.getMapType()) + map.getName()).build();
        if (mapInventory.firstEmpty() != -1) {
            if (!mapInventory.contains(toAdd))
                mapInventory.addItem(toAdd);
        }
    }

    public String colorCodeByType(BridgeMapType bridgeMapType) {
        if (bridgeMapType == BridgeMapType.SHORT) return "§a";
        if (bridgeMapType == BridgeMapType.LONG) return "§e";
        if (bridgeMapType == BridgeMapType.DIAGONAL) return "§c";
        return "§7";
    }

    public BridgeMap getMap(String name) {
        return loader.getMaps().stream().filter(map -> map.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void moveIslandForPlayer(BridgePlayer bridgePlayer, BridgeMap bridgeMap, PlayerMoveEvent event) {
        /*var player = bridgePlayer.getPlayer();

        var map = bridgeMap.clone();

        var mapPosition = map.getMapPosition().clone();
        var givenSpace = map.getGivenSpace();

        if (player.getLocation().getY() > mapPosition.getStart().getY()) {
            if (event.getTo().getY() > event.getFrom().getY()) {
                for (Location location : mapPosition.getTransientEndLocation()) {
                    var copiedloc = location.clone();
                    var block = location.clone().getBlock();
                    var type = block.getType();
                    var newLocation = new Location(copiedloc.getWorld(), copiedloc.getX(), player.getLocation().clone().subtract(0, 1, 0).getY(), copiedloc.getZ());

                    if (newLocation.getY() < mapPosition.getEndBottom().clone().getY()) {
                        newLocation = location.clone();
                    }

                    var newBlock = newLocation.getBlock();
                    var oldBlock = newLocation.clone().subtract(0, 1, 0).getBlock();

                    if (newLocation != location) {
                        if (type == Material.SANDSTONE) {
                            newBlock.setType(type);
                            if (!changedLocations.containsKey(player.getUniqueId()))
                                changedLocations.put(player.getUniqueId(), Lists.newArrayList());

                            if (!changedLocations.get(player.getUniqueId()).contains(newLocation))
                                changedLocations.get(player.getUniqueId()).add(newLocation);
                        }
                    }

                }
            } else if (event.getTo().getY() <= mapPosition.getStart().getY()) {

                if (!changedLocations.get(player.getUniqueId()).isEmpty()) {
                    for (Location changedLocation : changedLocations.get(player.getUniqueId())) {
                        if (changedLocation.getBlock().getType() != Material.AIR) {
                            changedLocation.getBlock().setType(Material.AIR);
                        }
                    }
                }
                changedLocations.remove(player.getUniqueId());
                for (Location location : mapPosition.getTransientEndLocation()) {
                    if (location.getBlock().getType() == Material.AIR) {
                        for (CustomBlock block : mapPosition.getBlocksEnd()) {
                            var distancedLocation = block.getLocation().clone().add(givenSpace, 0, 0);

                            if (location.getWorld().getBlockAt(distancedLocation).getType() == Material.AIR) {
                                if (block.getMaterial() != Material.AIR) {
                                    distancedLocation.getBlock().setType(block.getMaterial());
                                }
                            }
                        }
                    }
                }
            }
        }*/
    }

    public Inventory getMapLengthInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, "§8» §bMap Length");
        inventory.setItem(3, new ItemBuilder(BridgeMapType.SHORT.getIcon()).name("§a§lShort").lore("§8» §7Distance§8: §e" + BridgeMapType.SHORT.getLength()).build());
        inventory.setItem(4, new ItemBuilder(BridgeMapType.LONG.getIcon()).name("§e§lLong").lore("§8» §7Distance§8: §e" + BridgeMapType.LONG.getLength()).build());
        inventory.setItem(5, new ItemBuilder(BridgeMapType.DIAGONAL.getIcon()).name("§c§lDiagonal").lore("§8» §7Distance§8: §e" + BridgeMapType.DIAGONAL.getLength()).build());
        return inventory;
    }

    public BridgeMap getClosestMapToNameWithType(String name, BridgeMapType type) {
        return loader.getMaps().stream()
                .filter(map -> map.getName().split("-")[0].equalsIgnoreCase(name.split("-")[0]) && map.getMapType() == type)
                .findFirst()
                .orElse(null);
    }
}
