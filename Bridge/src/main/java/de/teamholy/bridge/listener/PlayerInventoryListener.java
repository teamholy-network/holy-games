package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.util.ItemBuilder;
import de.teamholy.bridge.map.management.BridgeMapManagement;
import de.teamholy.bridge.player.management.PlayerManagement;
import de.teamholy.bridge.player.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PlayerInventoryListener implements Listener {

    private final BridgeMapManagement mapManagement = Bridge.getInstance().getMapManagement();
    private final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        var view = event.getView();

        event.setCancelled(!player.isOp() && player.getGameMode() != GameMode.CREATIVE);

        if (view.getTitle().equals(mapManagement.getTitle())) {
            event.setCancelled(true);

            var clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            var clickedItemMeta = clickedItem.getItemMeta();
            if (clickedItemMeta == null) return;

            var mapName = ChatColor.stripColor(clickedItemMeta.getDisplayName());

            var map = mapManagement.getMap(mapName);
            if (map == null) return;

            var bridgePlayer = playerManagement.getBridgePlayer().get(player.getUniqueId());

            if (bridgePlayer.getMapType() == null) {
                bridgePlayer.setMapType(BridgeMapType.SHORT); // standard map type
            }

            var copiedMap = mapManagement.getClosestMapToNameWithType(map.getName(), bridgePlayer.getMapType()).clone();

            if (copiedMap.isLoading()) {
                player.sendMessage(Bridge.PREFIX + "§cThe Map is currently loading!");
                return;
            }
            if (bridgePlayer.getState() == BridgePlayer.PlayerState.LOBBY) {
                if (bridgePlayer.getMap() != null) {
                    player.sendMessage(Bridge.PREFIX + "§cThe Map is currently loading!");
                    return;
                }
            } else {
                if (bridgePlayer.getMap() != null) {
                    if (bridgePlayer.getMap().getName().equals(map.getName())) return;

                    mapManagement.getLoader().unloadMap(bridgePlayer);

                    if (!bridgePlayer.getBlocks().isEmpty()) {
                        bridgePlayer.getBlocks().forEach((block, time) -> block.setType(Material.AIR));
                    }

                    bridgePlayer.getBlocks().clear();
                }
            }

            copiedMap.setMapPlayer(player);

            copiedMap.setMapType(bridgePlayer.getMapType());
            bridgePlayer.setMap(copiedMap);
            player.closeInventory();

            Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> mapManagement.getLoader().loadMapForPlayer(bridgePlayer, copiedMap), 3L);
        } else if (view.getTitle().equals("§8» §eSettings")) {
            var bridgePlayer = playerManagement.getBridgePlayer(player);
            if (bridgePlayer.getState() == BridgePlayer.PlayerState.INGAME) {
                event.setCancelled(true);

                var clickedItem = event.getCurrentItem();
                if (clickedItem == null) return;

                var clickedItemMeta = clickedItem.getItemMeta();
                if (clickedItemMeta == null) return;

                /*
                if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§cIsland Moving")) {
                    bridgePlayer.getSettings().setIslandMoving(!bridgePlayer.getSettings().isIslandMoving());
                    player.sendMessage(Bridge.PREFIX + "§cIsland Moving: " + bridgePlayer.getSettings().isIslandMoving());
                    player.closeInventory();
                } else*/
                if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§6Blocks")) {
                    player.openInventory(playerManagement.blocksInventory());
                } else if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§bMaps")) {
                    player.openInventory(mapManagement.getInventory());
                } else if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§bMap Settings")) {
                    player.openInventory(mapManagement.getMapSettings(bridgePlayer.getMap().clone()));
                } else if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§6Block Settings")) {
                    player.openInventory(playerManagement.blockSettingsInventory(bridgePlayer));
                }
            }
        } else if (view.getTitle().equalsIgnoreCase("§8» §6Blocks")) {
            event.setCancelled(true);
            var bridgePlayer = playerManagement.getBridgePlayer(player);
            var material = event.getCurrentItem().getType();

            if (material == Material.AIR || material == Material.REDSTONE_COMPARATOR || material == Material.SLIME_BALL) return;

            if (bridgePlayer.getSettings().getBlockMaterial() == material) return;

            bridgePlayer.getSettings().setBlockMaterial(material);
            player.sendMessage(Bridge.PREFIX + "§cBlock: " + material.name());
            playerManagement.refillBlocks(player);
            player.closeInventory();
        } else if (view.getTitle().equalsIgnoreCase("§8» §bMap Settings")) {
            event.setCancelled(true);
            var bridgePlayer = playerManagement.getBridgePlayer(player);

            var clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            var clickedItemMeta = clickedItem.getItemMeta();
            if (clickedItemMeta == null) return;

            var map = bridgePlayer.getMap().clone();
            if (map == null) return;

            var mapType = map.getMapType();

            var selectedType = switch (clickedItemMeta.getDisplayName()) {
                case "§aShort" -> BridgeMapType.SHORT;
                case "§eNormal" -> BridgeMapType.NORMAL;
                case "§cInclined" -> BridgeMapType.DIAGONAL;
                default -> map.getMapType();
            };

            if (mapType == selectedType) return;

            var closestBridgeMap = mapManagement.getClosestMapToNameWithType(map.getName(), selectedType).clone();
            if (closestBridgeMap == null) {
                player.sendMessage(Bridge.PREFIX + "§cNo Map found with this type!");
                return;
            }

            if (closestBridgeMap == map) {
                player.sendMessage(Bridge.PREFIX + "§cYou are already on this map!");
                return;
            }

            mapManagement.getLoader().unloadMap(bridgePlayer);

            if (!bridgePlayer.getBlocks().isEmpty()) {
                bridgePlayer.getBlocks().forEach((block, time) -> block.setType(Material.AIR));
            }

            bridgePlayer.getBlocks().clear();

            closestBridgeMap.setMapPlayer(player);
            closestBridgeMap.setMapType(selectedType);
            bridgePlayer.setMapType(selectedType);
            player.closeInventory();
            player.sendMessage(Bridge.PREFIX + "Changed Map Type to " + clickedItemMeta.getDisplayName());
            Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> mapManagement.getLoader().loadMapForPlayer(bridgePlayer, closestBridgeMap), 3L);
        } else if (view.getTitle().equalsIgnoreCase("§8» §6Block Settings")) {
            event.setCancelled(true);

            var bridgePlayer = playerManagement.getBridgePlayer(player);

            var clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            var clickedItemMeta = clickedItem.getItemMeta();
            if (clickedItemMeta == null) return;

            if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§6Animation")) {
                player.openInventory(playerManagement.blockAnimationInventory(bridgePlayer));
            } else if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§eRemove Timer")) {
                if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                    bridgePlayer.getSettings().setRemoveBlocks(!bridgePlayer.getSettings().isRemoveBlocks());
                    player.sendMessage(Bridge.PREFIX + (bridgePlayer.getSettings().isRemoveBlocks() ? "§aActivated" : "§cDisabled") + " the Block Timer!");
                } else if (event.getClick() == ClickType.LEFT) {
                    bridgePlayer.getSettings().setRemovalTime((bridgePlayer.getSettings().getRemovalTime() + 1) > 8 ? 8 : bridgePlayer.getSettings().getRemovalTime() + 1);
                    player.sendMessage(Bridge.PREFIX + "Changed the removal time to §e" + bridgePlayer.getSettings().getRemovalTime());
                } else if (event.getClick() == ClickType.RIGHT) {
                    bridgePlayer.getSettings().setRemovalTime((bridgePlayer.getSettings().getRemovalTime() - 1) < 1 ? 1 : bridgePlayer.getSettings().getRemovalTime() - 1);
                    player.sendMessage(Bridge.PREFIX + "Changed the removal time to §e" + bridgePlayer.getSettings().getRemovalTime());
                }
                event.getInventory().setItem(5, new ItemBuilder(Material.WATCH).amount(1).name("§eRemove Timer")
                        .lore("§8» §7Current Time: §e" +
                                (bridgePlayer.getSettings().getRemovalTime() == 0 ? "Not set" : bridgePlayer.getSettings().getRemovalTime()))
                        .lore("")
                        .lore("§7Currently " + (bridgePlayer.getSettings().isRemoveBlocks() ? "§aenabled" : "§cdisabled"))
                        .lore("§7§oShift Click to enable / disable").build());
            }

        } else if (view.getTitle().equalsIgnoreCase("§8» §6Block Animation")) {
            event.setCancelled(true);

            var bridgePlayer = playerManagement.getBridgePlayer(player);

            var clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            var clickedItemMeta = clickedItem.getItemMeta();
            if (clickedItemMeta == null) return;

            if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§c§lClear")) {
                bridgePlayer.getSettings().setBlockAnimationType(Settings.BlockAnimationType.NONE);
                player.sendMessage(Bridge.PREFIX + "You cleared the animation!");
            } else if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§6Falling")) {
                bridgePlayer.getSettings().setBlockAnimationType(Settings.BlockAnimationType.FALLING);
                player.sendMessage(Bridge.PREFIX + "You set the animation to §6Falling§7!");
            } else if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§6Dropping")) {
                bridgePlayer.getSettings().setBlockAnimationType(Settings.BlockAnimationType.DROPPING);
                player.sendMessage(Bridge.PREFIX + "You set the animation to §6Dropping§7!");
            } else if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§6Breaking")) {
                bridgePlayer.getSettings().setBlockAnimationType(Settings.BlockAnimationType.BREAK);
                player.sendMessage(Bridge.PREFIX + "You set the animation to §6Breaking§7!");
            }
            player.closeInventory();
        }
    }

}
