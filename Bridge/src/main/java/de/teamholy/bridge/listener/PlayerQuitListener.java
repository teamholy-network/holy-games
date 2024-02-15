package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.service.BridgeMapService;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.service.PlayerService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PlayerQuitListener implements Listener {

    private final PlayerService playerService = Bridge.getInstance().getPlayerService();
    private final BridgeMapService bridgeMapService = Bridge.getInstance().getBridgeMapService();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        var player = event.getPlayer();
        BridgePlayer bridgePlayer = playerService.getBridgePlayer(event.getPlayer());

        if (bridgePlayer == null) return;

        var map = bridgePlayer.getMap();


        bridgePlayer.getHologram().delete();
        playerService.getTopPlayer().forEach((type, players) -> players.remove(bridgePlayer));

        Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> playerService.updateScoreboardForPlayer(map.getMapType()), 5);


        if (!bridgePlayer.getBlocks().isEmpty()) {
            bridgePlayer.getBlocks().forEach((block, time) -> block.setType(Material.AIR));
        }


        bridgePlayer.getBlocks().clear();
        bridgePlayer.saveStats();

        bridgeMapService.resetMap(map);


        if (bridgePlayer.getState() == BridgePlayer.PlayerState.SPECTATOR) {
            bridgePlayer.setToSpectate(null);
            playerService.stopSpectating(player, false);
        }
        if (!playerService.getBridgePlayers().isEmpty()) {
            for (BridgePlayer bridgePlayer1 : playerService.getBridgePlayers().values()) {
                if (bridgePlayer1.getToSpectate() == null) continue;
                if (bridgePlayer1.getToSpectate().getUniqueId().equals(player.getUniqueId())) {
                    playerService.stopSpectating(bridgePlayer1.getPlayer(), true);
                }
            }
        }

        playerService.removePlayer(event.getPlayer());
    }

}
