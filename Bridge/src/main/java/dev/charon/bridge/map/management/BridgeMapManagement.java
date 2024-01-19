package dev.charon.bridge.map.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.charon.bridge.custom.CustomBlock;
import dev.charon.bridge.map.BridgeMap;
import dev.charon.bridge.map.BridgeMapType;
import dev.charon.bridge.map.loader.BridgeMapLoader;
import dev.charon.bridge.map.position.BridgeBlockPosition;
import dev.charon.bridge.map.position.MapPosition;
import dev.charon.bridge.player.BridgePlayer;
import dev.charon.bridge.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private final String title = "§8» §6Bridge Maps";

    private final Map<UUID, List<Location>> changedLocations = Maps.newHashMap();

    public BridgeMapManagement() {
        this.loader = new BridgeMapLoader();
        loadMaps();
    }

    public void loadMaps() {
        loader.loadMaps();
        if (loader.getMaps().isEmpty())
            return;
        if (this.inventory == null) {
            registerInventory();
        }
        loader.getMaps().forEach(this::addMap);
        System.out.println("Loaded " + loader.getMaps().size() + " maps");
    }

    private void registerInventory() {
        this.inventory = Bukkit.createInventory(null, getInventorySizeByMap(), title);
    }

    private int getInventorySizeByMap() {
        int size = 9;
        if (loader.getMaps().size() > 9)
            size = 18;
        if (loader.getMaps().size() > 18)
            size = 27;
        if (loader.getMaps().size() > 27)
            size = 36;
        if (loader.getMaps().size() > 36)
            size = 45;
        if (loader.getMaps().size() > 45)
            size = 54;
        return size;
    }

    public void addMap(BridgeMap map) {
        if (map == null) return;
        if (map.getName().endsWith("-Normal") || map.getName().endsWith("-Inclined")) return;

        this.inventory.addItem(new ItemBuilder(Material.valueOf(map.getMaterialName())).name(map.getTitle()).build());
    }

    public BridgeMap getMap(String name) {
        return loader.getMaps().stream().filter(map -> map.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void create(Location highLeft, Location bottomRight, Location endHighLeft, Location endBottomRight, Location location, String name, String title, ItemStack itemStack) {
        BridgeMap map = new BridgeMap(name, title);

        map.setMaterialName(itemStack.getType().name());

        MapPosition mapPosition = new MapPosition();
        mapPosition.setHigh(highLeft);
        mapPosition.setBottom(bottomRight);

        List<CustomBlock> blocksSpawn = BridgeBlockPosition.select(highLeft, bottomRight, location.getWorld());
        mapPosition.setBlocksSpawn(blocksSpawn);
        List<CustomBlock> blocksEnd = BridgeBlockPosition.select(endHighLeft, endBottomRight, location.getWorld());
        mapPosition.setBlocksEnd(blocksEnd);
        mapPosition.setStart(location);
        mapPosition.setMiddle(location);
        mapPosition.setEndHigh(endHighLeft);
        mapPosition.setEndBottom(endBottomRight);
        mapPosition.setEndStart(location);
        map.setMapPosition(mapPosition);

        if (loader.getMaps().contains(map))
            return;

        loader.getMaps().add(map);
        loader.save(map);

        System.out.println("Created map " + map.getName());

        if (inventory.contains(new ItemBuilder(Material.valueOf(map.getMaterialName())).name(map.getTitle()).build()))
            return;
        addMap(map);
    }

    public void moveIslandForPlayer(BridgePlayer bridgePlayer, BridgeMap bridgeMap, PlayerMoveEvent event) {
        var player = bridgePlayer.getPlayer();

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
        }
    }

    public Inventory getMapSettings(BridgeMap bridgeMap) {
        Inventory inventory = Bukkit.createInventory(null, 9, "§8» §bMap Settings");
        inventory.setItem(3, new ItemBuilder(Material.valueOf(bridgeMap.getMaterialName())).name("§aShort").lore("§8» §7Distance§8: §e21").build());
        inventory.setItem(4, new ItemBuilder(Material.valueOf(bridgeMap.getMaterialName())).name("§eNormal").lore("§8» §7Distance§8: §e41").build());
        inventory.setItem(5, new ItemBuilder(Material.valueOf(bridgeMap.getMaterialName())).name("§cInclined").lore("§c§lSOON!").build());
        return inventory;
    }

    public BridgeMap getClosestMapToNameWithType(String name, BridgeMapType type) {
        return loader.getMapTypes().stream().filter(map -> map.getName().equalsIgnoreCase(name + "-" + type.name())).findFirst().orElse(getMap(name.replace("-Normal", "").replace("-Inclined", "")));
    }
}
