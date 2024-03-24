package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.map.service.BridgeMapService;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.service.BridgePlayerService;
import de.teamholy.core.bukkit.event.CachedPlayerJoinEvent;
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
    public void onPlayerJoin(CachedPlayerJoinEvent event) {

        final BridgePlayerService bridgePlayerService = Bridge.getInstance().getBridgePlayerService();

        Player player = event.getCachedBukkitPlayer().getPlayer();
        if (player == null || !player.isOnline()) return;
        bridgePlayerService.addPlayer(player);

        Bridge.getInstance().getBridgeMapService().findMapForPlayer(BridgeMapType.SHORT, bridgePlayerService.getBridgePlayer(player));

        if (!bridgePlayerService.getBridgePlayers().isEmpty()) {
            for (BridgePlayer bridgePlayer : bridgePlayerService.getBridgePlayers().values()) {
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
