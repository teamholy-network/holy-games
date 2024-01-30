package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.map.management.BridgeMapManagement;
import de.teamholy.bridge.player.management.PlayerManagement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PlayerInteractAtItemListener implements Listener {


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        ItemStack item = event.getItem();

        final BridgeMapManagement mapManagement = Bridge.getInstance().getMapManagement();
        final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();

        event.setCancelled(!event.getPlayer().isOp());

        BridgePlayer bridgePlayer = playerManagement.getBridgePlayers().get(player.getUniqueId());

        if (bridgePlayer.getState() == BridgePlayer.PlayerState.LOBBY) {
            if (item == null) return;

            if (item.getItemMeta() != null
                    && item.getItemMeta().getDisplayName() != null) {

                if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§bMaps")) {
                    player.openInventory(getMapInventory());
                } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§cLeave")) {
                    player.kickPlayer("");
                }
            }
        } else if (bridgePlayer.getState() == BridgePlayer.PlayerState.INGAME) {

            event.setCancelled(false);

            if (item == null) return;

            if (item.getItemMeta() != null
                    && item.getItemMeta().getDisplayName() != null) {
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§cQuit")) {
           /*         bridgePlayer.setState(BridgePlayer.PlayerState.LOBBY);
                    playerManagement.loadLobbyInventory(player);
                    mapManagement.getLoader().unloadMap(bridgePlayer);

                    if (!bridgePlayer.getBlocks().isEmpty()) {
                        bridgePlayer.getBlocks().forEach((block, time) -> block.setType(Material.AIR));
                    }

                    bridgePlayer.getBlocks().clear();
*/
                    player.kickPlayer("");

                } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§eSettings")) {
                    event.setCancelled(true);
                    playerManagement.ingameSettingsInventory(player);
                }
            }
        }
    }

    private Inventory getMapInventory() {
        return Bridge.getInstance().getMapManagement().getInventory();
    }

}
