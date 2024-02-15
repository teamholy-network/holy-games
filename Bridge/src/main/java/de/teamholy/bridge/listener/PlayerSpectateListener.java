package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.service.PlayerService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PlayerSpectateListener implements Listener {

    private final PlayerService playerService = Bridge.getInstance().getPlayerService();

    @EventHandler
    public void onChangeWorldEvent(PlayerChangedWorldEvent event) {
        var player = event.getPlayer();

        for (BridgePlayer bridgePlayer : playerService.getBridgePlayers().values()) {
            if (bridgePlayer.getToSpectate() != null) {
                if (bridgePlayer.getToSpectate().getUniqueId().equals(player.getUniqueId())) {
                    playerService.startSpectating(bridgePlayer.getPlayer(), player);
                }
            }
        }
    }

}
