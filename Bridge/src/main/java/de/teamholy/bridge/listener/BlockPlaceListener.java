package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.management.PlayerManagement;
import de.teamholy.core.bukkit.perks.Perk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class BlockPlaceListener implements Listener {

    private final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        BridgePlayer bridgePlayer = playerManagement.getBridgePlayers().get(event.getPlayer().getUniqueId());

        if (bridgePlayer.getState() != BridgePlayer.PlayerState.INGAME) {
            event.setCancelled(true);
            if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                event.setCancelled(false);
            }
        } else {
            if (event.getBlock().getType() == Material.REDSTONE_COMPARATOR) {
                event.setCancelled(true);
                return;
            } else {
                event.setCancelled(false);
            }
        }

        if (bridgePlayer.getState() == BridgePlayer.PlayerState.INGAME) {
            if (event.getBlockAgainst().getType() == Material.GOLD_PLATE || event.getBlockPlaced().getLocation().subtract(0, 1, 0).getBlock().getType() == Material.GOLD_PLATE) {
                event.setCancelled(true);
                return;
            }

            if (bridgePlayer.isPreview() && event.getBlock().getType() == Material.BARRIER) {
                event.setCancelled(true);
                return;
            }

            if (event.getBlock().getType() == Material.REDSTONE_COMPARATOR) {
                event.setCancelled(true);
                return;
            }

            var map = bridgePlayer.getMap();
            var position = map.getMapPosition();

            if(!position.isInMapPosition(event.getPlayer().getLocation(), true) || !position.isInMapPosition(event.getBlockPlaced().getLocation(), true)) {
                event.setCancelled(true);
                return;
            }


            var blocks = bridgePlayer.getBlocks();
            blocks.put(event.getBlock(), System.currentTimeMillis());
            if (!playerManagement.getPlayerTime().containsKey(event.getPlayer().getUniqueId())) {
                bridgePlayer.addGamesPlayed();
                playerManagement.getPlayerTime().put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
            }

            if (event.getPlayer().getInventory().getItemInHand().getType() == event.getBlockPlaced().getType()) {
                event.getPlayer().getInventory().getItemInHand().setAmount(event.getPlayer().getInventory().getItemInHand().getMaxStackSize());
                bridgePlayer.addPlacedBlock();
                playerManagement.updateHologram(bridgePlayer,false);
            }

        }
    }

}
