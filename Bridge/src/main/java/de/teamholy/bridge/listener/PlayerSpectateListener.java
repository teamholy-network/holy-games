package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.management.PlayerManagement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PlayerSpectateListener implements Listener {

    private final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();

        if (!playerManagement.getBridgePlayers().isEmpty()) {
            for (BridgePlayer bridgePlayer : playerManagement.getBridgePlayers().values()) {
                if (bridgePlayer.getToSpectate() != null) {
                    for (Player bukkit : Bukkit.getOnlinePlayers()) {
                        if (bukkit == bridgePlayer.getPlayer()) {
                            player.hidePlayer(bukkit);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChangeWorldEvent(PlayerChangedWorldEvent event) {
        var player = event.getPlayer();

        for (BridgePlayer bridgePlayer : playerManagement.getBridgePlayers().values()) {
            if (bridgePlayer.getToSpectate() != null) {
                if (bridgePlayer.getToSpectate().getUniqueId().equals(player.getUniqueId())) {
                    playerManagement.startSpectating(bridgePlayer.getPlayer(), player);
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();

        var bridgePlayer = playerManagement.getBridgePlayer(player);
        if (bridgePlayer != null) {
            if (bridgePlayer.getState() == BridgePlayer.PlayerState.SPECTATOR) {
                bridgePlayer.setToSpectate(null);
                playerManagement.stopSpectating(player, false);
            }
        }
        if (!playerManagement.getBridgePlayers().isEmpty()) {
            for (BridgePlayer bridgePlayer1 : playerManagement.getBridgePlayers().values()) {
                if (bridgePlayer1.getToSpectate().getUniqueId().equals(player.getUniqueId())) {
                    playerManagement.stopSpectating(bridgePlayer1.getPlayer(), true);
                }
            }
        }
    }
}
