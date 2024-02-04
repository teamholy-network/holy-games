package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.map.management.BridgeMapManagement;
import de.teamholy.bridge.player.management.PlayerManagement;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    private final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        final BridgeMapManagement mapManagement = Bridge.getInstance().getMapManagement();
        BridgePlayer bridgePlayer = playerManagement.getBridgePlayer(event.getPlayer());
        var map = bridgePlayer.getMap();


        bridgePlayer.getHologram().delete();
        playerManagement.getTopPlayer().forEach((type, players) -> players.remove(bridgePlayer));

        Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> playerManagement.updateScoreboardForPlayer(map.getMapType()), 5);

        mapManagement.getLoader().unloadMap(bridgePlayer, false);

        if (!bridgePlayer.getBlocks().isEmpty()) {
            bridgePlayer.getBlocks().forEach((block, time) -> block.setType(Material.AIR));
        }

        if (!mapManagement.getChangedLocations().isEmpty() && mapManagement.getChangedLocations().containsKey(bridgePlayer.getPlayer().getUniqueId())) {
            for (Location location : mapManagement.getChangedLocations().get(bridgePlayer.getPlayer().getUniqueId())) {
                location.getBlock().setType(Material.AIR);
            }
        }

        bridgePlayer.getBlocks().clear();
        bridgePlayer.saveStats();

        playerManagement.removePlayer(event.getPlayer());
    }

}
