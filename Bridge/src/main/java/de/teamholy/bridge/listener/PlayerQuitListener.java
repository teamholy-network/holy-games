package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.service.BridgeMapService;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.service.BridgePlayerService;
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

    private final BridgePlayerService bridgePlayerService = Bridge.getInstance().getBridgePlayerService();
    private final BridgeMapService bridgeMapService = Bridge.getInstance().getBridgeMapService();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        var player = event.getPlayer();
        BridgePlayer bridgePlayer = bridgePlayerService.getBridgePlayer(event.getPlayer());

        if (bridgePlayer == null) {
            System.out.println("Player " + player.getName() + " is null on leave.");
            return;
        }

        var map = bridgePlayer.getMap();


        bridgePlayer.getHologram().delete();
        bridgePlayerService.getTopPlayer().forEach((type, players) -> players.remove(bridgePlayer));

        Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> bridgePlayerService.updateScoreboardForPlayer(map.getMapType()), 5);

        System.out.println("Player " + player.getName() + " has left the server.");

        if (!bridgePlayer.getBlocks().isEmpty()) {
            bridgePlayer.getBlocks().forEach((block, time) -> block.setType(Material.AIR));
        }


        bridgePlayer.getBlocks().clear();
        bridgePlayer.saveStats();

        bridgeMapService.resetMap(map);


        if (bridgePlayer.getState() == BridgePlayer.PlayerState.SPECTATOR) {
            bridgePlayer.setToSpectate(null);
            bridgePlayerService.stopSpectating(player, false);
        }
        if (!bridgePlayerService.getBridgePlayers().isEmpty()) {
            for (BridgePlayer bridgePlayer1 : bridgePlayerService.getBridgePlayers().values()) {
                if (bridgePlayer1.getToSpectate() == null) continue;
                if (bridgePlayer1.getToSpectate().getUniqueId().equals(player.getUniqueId())) {
                    bridgePlayerService.stopSpectating(bridgePlayer1.getPlayer(), true);
                }
            }
        }

        bridgePlayerService.removePlayer(event.getPlayer());
    }

}
