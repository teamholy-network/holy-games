package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.map.service.BridgeMapService;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.service.PlayerService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onSpawn(PlayerSpawnLocationEvent event) {
        event.setSpawnLocation(new Location(event.getPlayer().getWorld(), 0,0,0));
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {

        if (!BridgeMapService.MAPS_PASTED) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST,"§cThe server is still starting, please try again in a few seconds.");
        }

    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        final PlayerService playerService = Bridge.getInstance().getPlayerService();

        Player player = event.getPlayer();
        playerService.addPlayer(player);

        Bridge.getInstance().getBridgeMapService().findMapForPlayer(BridgeMapType.SHORT, playerService.getBridgePlayer(player));

        if (!playerService.getBridgePlayers().isEmpty()) {
            for (BridgePlayer bridgePlayer : playerService.getBridgePlayers().values()) {
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


}
