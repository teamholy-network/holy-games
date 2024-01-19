package dev.charon.bridge.listener;

import dev.charon.bridge.Bridge;
import dev.charon.bridge.map.management.BridgeMapManagement;
import dev.charon.bridge.player.BridgePlayer;
import dev.charon.bridge.player.management.PlayerManagement;
import dev.charon.bridge.util.FormatTime;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PlayerMoveListener implements Listener {

    private final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();
    private final BridgeMapManagement mapManagement = Bridge.getInstance().getMapManagement();


    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        var player = event.getPlayer();


        BridgePlayer bridgePlayer = playerManagement.getBridgePlayer().get(player.getUniqueId());
        if (bridgePlayer.getState() == BridgePlayer.PlayerState.INGAME) {
            if (bridgePlayer.getMap() == null) {
                return;
            }
            var map = bridgePlayer.getMap().clone();

            var mapPosition = map.getMapPosition().clone();
            var givenSpace = map.getGivenSpace();
            var highLocation = mapPosition.getHigh().clone().add(givenSpace, 0, 0).add(8, 8, 2).toVector();
            var bottom = mapPosition.getEndBottom().clone().add(givenSpace, 0, 0).subtract((map.getName().endsWith("-Normal") ? 7 : 1), 10, 2).toVector();

            var settings = bridgePlayer.getSettings();
            if (settings.isIslandMoving()) {
                if (map.getMapPlayer() != player) {
                    return;
                }
                mapManagement.moveIslandForPlayer(bridgePlayer, map.clone(), event);
            }
                /*

                for (CustomBlock customBlock : mapPosition.getBlocksEnd()) {
                    customBlock.setLocation(customBlock.getLocation().clone().add(givenSpace, 0, 0));
                    var location = customBlock.getLocation();
                    var block = location.getBlock();
                    var type = block.getType();

                    var newLocation = new Location(location.getWorld(), location.getX(), player.getLocation().subtract(0, 1,0).getY(), location.getZ());
                    if (type == Material.GOLD_PLATE) {
                        newLocation = newLocation.add(0, 0.5, 0);
                    }
                    var newBlock = newLocation.getBlock();
                    if (newLocation != location) {
                        if (type == Material.SANDSTONE) {
                            block.setType(Material.AIR);
                            newBlock.setType(type);
                        }
                    }
                }*/

            Vector vector = player.getLocation().toVector();

            if (event.getTo() == null) {
                Bukkit.broadcastMessage("event.getTo() == null");
            }

            if (!vector.isInAABB(bottom, highLocation)) {
                if (bridgePlayer.getMap().getMapPosition().getTransientSpawn() != null) {
                    player.teleport(bridgePlayer.getMap().getMapPosition().getTransientSpawn());
                }

                if (!bridgePlayer.getBlocks().isEmpty()) {
                    playerManagement.spawnBlockAnimation(bridgePlayer);
                }

                bridgePlayer.getBlocks().clear();
                playerManagement.refillBlocks(player);
                playerManagement.getPlayerTime().remove(player.getUniqueId());
            }

            if (player.getLocation().getBlock().getType() == Material.GOLD_PLATE || player.getLocation().clone().subtract(0, 1, 0).getBlock().getType() == Material.DIAMOND_BLOCK) {
                if (!playerManagement.getPlayerTime().containsKey(player.getUniqueId())) {
                    return;
                }

                player.teleport(bridgePlayer.getMap().getMapPosition().getTransientSpawn());

                if (!bridgePlayer.getBlocks().isEmpty()) {
                    playerManagement.spawnBlockAnimation(bridgePlayer);
                }
                bridgePlayer.getBlocks().clear();

                var current = (System.currentTimeMillis() - playerManagement.getPlayerTime().remove(player.getUniqueId()));
                var best = playerManagement.checkBestTime(player, current, bridgePlayer.getLocalBestTime());

                playerManagement.refillBlocks(player);
                player.sendMessage("§7Your time was §e" + FormatTime.formatTimeManually(current) + " §8/ §7Best time: §e" + FormatTime.formatTimeManually(best));
            }
        } else if (bridgePlayer.getState() == BridgePlayer.PlayerState.LOBBY) {
            if (event.getTo().getX() <= player.getWorld().getSpawnLocation().getX() - 100) {
                player.teleport(player.getWorld().getSpawnLocation());
            }
        }
    }

}
