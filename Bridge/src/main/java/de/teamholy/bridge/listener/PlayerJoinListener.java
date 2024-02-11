package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.map.managment.BridgeMapManagment;
import de.teamholy.bridge.player.management.PlayerManagement;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.concurrent.ThreadLocalRandom;

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

        if (!BridgeMapManagment.MAPS_PASTED) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST,"§cThe server is still starting, please try again in a few seconds.");
        }

    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();

        Player player = event.getPlayer();
        playerManagement.addPlayer(player);

        Bridge.getInstance().getBridgeMapLoader().findMapForPlayer(BridgeMapType.SHORT, playerManagement.getBridgePlayer(player));
    }


}
