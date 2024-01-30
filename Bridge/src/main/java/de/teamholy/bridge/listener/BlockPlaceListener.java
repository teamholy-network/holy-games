package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.management.PlayerManagement;
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
        BridgePlayer bridgePlayer = playerManagement.getBridgePlayer().get(event.getPlayer().getUniqueId());

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

            var high = bridgePlayer.getMap().getLocation().clone().add(0, 3, 0);
            var givenSpace = bridgePlayer.getMap().getGivenSpace();

            var highLocation = bridgePlayer.getMap().getLocation().clone().add(givenSpace, 0, 0).add(8, 3, 2).toVector();
            var bottom = bridgePlayer.getMap().getLocation()
                    .clone().add(givenSpace, 0, 0)
                    .subtract(5, 2, bridgePlayer.getMap().getMapType() == BridgeMapType.DIAGONAL ? 25 : 5).toVector();

            /*
            if (event.getBlock().getLocation().getY() >= high.getY() ||
                    event.getBlock().getLocation().getY() <= bridgePlayer.getMap().getMapPosition().getEndBottom().getY()) {
                event.setCancelled(true);
                return;
            }*/
            /*

            if(!event.getBlock().getLocation().toVector().isInAABB(bottom, highLocation)) {
                event.setCancelled(true);
                return;
            }*/

            var blocks = bridgePlayer.getBlocks();
            blocks.put(event.getBlock(), System.currentTimeMillis());
            if (!playerManagement.getPlayerTime().containsKey(event.getPlayer().getUniqueId())) {
                playerManagement.getPlayerTime().put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
            }

            //var blocksPlaced = blocks.size();
        }
    }

}
