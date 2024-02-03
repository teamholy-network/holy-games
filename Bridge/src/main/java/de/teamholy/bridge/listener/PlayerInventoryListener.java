package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.management.PerkManagement;
import de.teamholy.bridge.player.settings.sounds.BridgeSoundType;
import de.teamholy.bridge.player.settings.sounds.BridgeSounds;
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
    private final PerkManagement perkManagement = Bridge.getInstance().getPerkManagement();

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
                if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§8» §6Blocks")) {
                    BukkitCore.getInstance().getPerkManager().openSecondPerkInventory(player, PerkType.BLOCK, PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL);
                } else if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§8» §6Maps")) {
                    player.openInventory(mapManagement.getInventory());
                } else if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§8» §6Map Type")) {
                    player.openInventory(mapManagement.getMapLengthInventory());
                } else if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§8» §6Block Break Settings")) {
                    player.openInventory(playerManagement.blockSettingsInventory(bridgePlayer));
                } else if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§8» §6Sounds")) {
                    player.openInventory(perkManagement.openSoundsPerkInventory());
                }
            }
        } else if (view.getTitle().equals("§8» §6Sound Settings")) {
            event.setCancelled(true);
            var clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            var clickedItemMeta = clickedItem.getItemMeta();
            if (clickedItemMeta == null) return;

            var bridgePlayer = playerManagement.getBridgePlayer(player);

            switch (clickedItemMeta.getDisplayName()) {
                case "§7Type§8: §cSad Sounds" ->
                        player.openInventory(perkManagement.openSoundInventory(bridgePlayer, BridgeSoundType.DEATH).getInventory());
                case "§7Type§8: §aHappy Sounds" ->
                        player.openInventory(perkManagement.openSoundInventory(bridgePlayer, BridgeSoundType.WIN).getInventory());
                case "§7Type§8: §fMusic" ->
                        player.openInventory(perkManagement.openSoundInventory(bridgePlayer, BridgeSoundType.SONG).getInventory());
            }


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

            switch (clickedItemMeta.getDisplayName()) {
                case "§c§lClear" -> {
                    bridgePlayer.getSettings().setBlockAnimationType(Settings.BlockAnimationType.NONE);
                    player.sendMessage(Bridge.PREFIX + "You cleared the animation!");
                }
                case "§6Falling" -> {
                    bridgePlayer.getSettings().setBlockAnimationType(Settings.BlockAnimationType.FALLING);
                    player.sendMessage(Bridge.PREFIX + "You set the animation to §6Falling§7!");
                }
                case "§6Dropping" -> {
                    bridgePlayer.getSettings().setBlockAnimationType(Settings.BlockAnimationType.DROPPING);
                    player.sendMessage(Bridge.PREFIX + "You set the animation to §6Dropping§7!");
                }
                case "§6Breaking" -> {
                    bridgePlayer.getSettings().setBlockAnimationType(Settings.BlockAnimationType.BREAK);
                    player.sendMessage(Bridge.PREFIX + "You set the animation to §6Breaking§7!");
                }
            }
            player.closeInventory();
        }
    }

}
