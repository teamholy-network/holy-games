package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.management.SoundPerkManagement;
import de.teamholy.bridge.player.settings.sounds.BridgeSoundType;
import de.teamholy.bridge.util.ItemBuilder;
import de.teamholy.bridge.map.management.BridgeMapManagement;
import de.teamholy.bridge.player.management.PlayerManagement;
import de.teamholy.bridge.player.settings.Settings;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.PerkManager;
import de.teamholy.core.bukkit.perks.PerkType;
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
    private final SoundPerkManagement soundPerkManagement = Bridge.getInstance().getSoundPerkManagement();

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

            var selectedInv = switch (clickedItemMeta.getDisplayName()) {
                case "§a§lShort" -> mapManagement.getMapSettings().get(BridgeMapType.SHORT);
                case "§e§lLong" -> mapManagement.getMapSettings().get(BridgeMapType.LONG);
                case "§c§lDiagonal" -> mapManagement.getMapSettings().get(BridgeMapType.DIAGONAL);
                default -> null;
            };

            if (selectedInv == null) return;

            player.openInventory(selectedInv);
        } else if (view.getTitle().endsWith(" Maps")) {
            event.setCancelled(true);

            var clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            var clickedItemMeta = clickedItem.getItemMeta();
            if (clickedItemMeta == null) return;

            var mapName = ChatColor.stripColor(clickedItemMeta.getDisplayName());

            var map = mapManagement.getMap(mapName).clone();
            if (map == null) {
                player.sendMessage(Bridge.PREFIX + "§cThis map does not exist!");
                return;
            }

            var bridgePlayer = playerManagement.getBridgePlayers().get(player.getUniqueId());

            var currentMap = bridgePlayer.getMap();
            if (currentMap == null) return;

            if (currentMap.getName().equals(map.getName())) {
                player.sendMessage(Bridge.PREFIX + "§cYou are already on this map!");
                return;
            }

            mapManagement.getLoader().unloadMap(bridgePlayer, false);

            if (!bridgePlayer.getBlocks().isEmpty()) {
                bridgePlayer.getBlocks().forEach((block, time) -> block.setType(Material.AIR));
            }

            bridgePlayer.getBlocks().clear();

            if (map.isLoading()) {
                player.sendMessage(Bridge.PREFIX + "§cThe Map is currently loading!");
                return;
            }

            bridgePlayer.setMap(map);
            player.closeInventory();

            Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> mapManagement.getLoader().loadMapForPlayer(bridgePlayer, map, false), 3L);
        } else if (view.getTitle().equals("§8» §6Sound Settings")) {
            event.setCancelled(true);
            var bridgePlayer = playerManagement.getBridgePlayer(player);
            var soundPerkInventory = soundPerkManagement.openSoundInventory(bridgePlayer, BridgeSoundType.ALL, PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL,1).getInventory();


            var clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            var clickedItemMeta = clickedItem.getItemMeta();
            if (clickedItemMeta == null) return;

            player.openInventory(soundPerkInventory);


        } else if (view.getTitle().equalsIgnoreCase("§8» §bMap Length")) {
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
                case "§a§lShort" -> BridgeMapType.SHORT;
                case "§e§lLong" -> BridgeMapType.LONG;
                case "§c§lDiagonal" -> BridgeMapType.DIAGONAL;
                default -> map.getMapType();
            };

            if (mapType == selectedType) {
                player.sendMessage(Bridge.PREFIX + "§cYou are already on this map!");
                return;
            }

            mapManagement.getLoader().unloadMap(bridgePlayer, true);

            if (!bridgePlayer.getBlocks().isEmpty()) {
                bridgePlayer.getBlocks().forEach((block, time) -> block.setType(Material.AIR));
            }

            bridgePlayer.getBlocks().clear();
            player.closeInventory();
            player.sendMessage(Bridge.PREFIX + "Changed type to " + clickedItemMeta.getDisplayName());

            Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () ->
                    mapManagement.getLoader().loadMapForPlayer(bridgePlayer,
                            mapManagement.getClosestMapToNameWithType(map.getName(), selectedType), true), 3L);
        } else if (view.getTitle().equalsIgnoreCase("§8» §6Block Settings")) {
            event.setCancelled(true);

            var bridgePlayer = playerManagement.getBridgePlayer(player);

            var clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            var clickedItemMeta = clickedItem.getItemMeta();
            if (clickedItemMeta == null) return;

            if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§6Animation")) {
                player.openInventory(playerManagement.blockSettingsInventory(bridgePlayer));
            }

        }
    }

}
