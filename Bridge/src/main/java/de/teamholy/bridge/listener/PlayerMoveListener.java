package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.map.position.MapPosition;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.util.FormatTime;
import de.teamholy.bridge.map.management.BridgeMapManagement;
import de.teamholy.bridge.player.management.PlayerManagement;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
        /*if ((event.getFrom().getBlockX() == event.getTo().getBlockX())
                && (event.getFrom().getBlockY() == event.getTo().getBlockY())
                && (event.getFrom().getBlockZ() == event.getTo().getBlockZ())
                && (event.getFrom().getWorld() == event.getTo().getWorld()))
            return;*/
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) return;
        var player = event.getPlayer();


        BridgePlayer bridgePlayer = playerManagement.getBridgePlayer(player);
        if (bridgePlayer.getState() == BridgePlayer.PlayerState.INGAME) {
            if (bridgePlayer.getMap() == null) {
                return;
            }
            var map = bridgePlayer.getMap().clone();

            MapPosition mapPosition = map.getMapPosition();
            if (mapPosition == null) return;

            if (!mapPosition.isInMapPosition(event.getTo(), true)) {
                if (bridgePlayer.getMapLocation() != null) {
                    player.teleport(bridgePlayer.getMapLocation());
                }

                if (!bridgePlayer.getBlocks().isEmpty()) {
                    playerManagement.spawnBlockAnimation(bridgePlayer);
                }

                bridgePlayer.getBlocks().clear();
                playerManagement.refillBlocks(player);
                playerManagement.getPlayerTime().remove(player.getUniqueId());
            }

            if (player.getLocation().getBlock().getType() == Material.GOLD_PLATE
                    || player.getLocation().clone().subtract(0, 1, 0).getBlock().getType() == Material.DIAMOND_BLOCK) {
                if (!playerManagement.getPlayerTime().containsKey(player.getUniqueId())) {
                    return;
                }

                player.teleport(bridgePlayer.getMapLocation());

                if (!bridgePlayer.getBlocks().isEmpty()) {
                    playerManagement.spawnBlockAnimation(bridgePlayer);
                }
                bridgePlayer.getBlocks().clear();

                var current = (System.currentTimeMillis() - playerManagement.getPlayerTime().remove(player.getUniqueId()));
                var bestLocal = playerManagement.checkBestTime(player, current, bridgePlayer.getLocalBestTime(bridgePlayer.getMap().getMapType()));
                var bestGlobal = playerManagement.checkBestTime(player, current, bridgePlayer.getGlobalBestTime(bridgePlayer.getMap().getMapType()));

                playerManagement.refillBlocks(player);
                player.sendMessage(Bridge.PREFIX + "§7Your time was §e" + FormatTime.formatTimeManually(current) +
                        "\n" + Bridge.PREFIX + "§7Your best §esession-time §7is §e" +
                        FormatTime.formatTimeManually(bestLocal) + " §8» " + mapManagement.colorCodeByType(map.getMapType()) + map.getMapType().getName()
                        + "\n" + Bridge.PREFIX + "§7Your best §aall-time §7is §e" + FormatTime.formatTimeManually(bestGlobal) + " §8» " + mapManagement.colorCodeByType(map.getMapType()) + map.getMapType().getName());
            }
        } else if (bridgePlayer.getState() == BridgePlayer.PlayerState.LOBBY) {
            if (event.getTo().getX() <= player.getWorld().getSpawnLocation().getX() - 100) {
                player.teleport(player.getWorld().getSpawnLocation());
            }
        }
    }

}
