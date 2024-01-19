package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.map.loader.BridgeSchematicLoader;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.map.management.BridgeMapManagement;
import de.teamholy.bridge.player.management.PlayerManagement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PlayerJoinListener implements Listener {


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();

        Player player = event.getPlayer();
        playerManagement.addPlayer(player);

        Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> {
            getFreeBridgeForPlayer(playerManagement.getBridgePlayer(player));
        }, 3L);
    }

    private void getFreeBridgeForPlayer(BridgePlayer player) {
        final BridgeMapManagement bridgeMapManagement = Bridge.getInstance().getMapManagement();

        if (player.getMap() == null) {
            var random = ThreadLocalRandom.current().nextInt(bridgeMapManagement.getLoader().getMaps().size() - 1);
            var randomMap = bridgeMapManagement.getLoader().getMaps().get(random).clone();
            if (randomMap == null) return;

            randomMap.setMapType(BridgeMapType.SHORT); // standard type!
            randomMap.setMapPlayer(player.getPlayer());
            player.setMapType(BridgeMapType.SHORT); // standard type!
            player.setMap(randomMap);

            BridgeSchematicLoader bridgeSchematicLoader = new BridgeSchematicLoader("test");
            bridgeSchematicLoader.loadSchematic(player.getPlayer());
            //bridgeMapManagement.getLoader().loadMapForPlayer(player, randomMap);
        }

    }

}
