package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.position.MapPosition;
import de.teamholy.bridge.map.service.BridgeMapService;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.service.BridgeSoundPerkService;
import de.teamholy.bridge.player.settings.BridgeSettings;
import de.teamholy.bridge.player.service.BridgePlayerService;

import de.teamholy.bridge.util.FormatTime;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PlayerMoveListener implements Listener {

    private final BridgePlayerService bridgePlayerService = Bridge.getInstance().getBridgePlayerService();
    private final BridgeSoundPerkService bridgeSoundPerkService = Bridge.getInstance().getBridgeSoundPerkService();

    private final BridgeMapService bridgeMapService = Bridge.getInstance().getBridgeMapService();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        /*if ((event.getFrom().getBlockX() == event.getTo().getBlockX())
                && (event.getFrom().getBlockY() == event.getTo().getBlockY())
                && (event.getFrom().getBlockZ() == event.getTo().getBlockZ())
                && (event.getFrom().getWorld() == event.getTo().getWorld()))
            return;*/
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) return;
        var player = event.getPlayer();

        BridgePlayer bridgePlayer = bridgePlayerService.getBridgePlayer(player);
        if (bridgePlayer.getState() == BridgePlayer.PlayerState.SPECTATOR) {
            var bukkitToSpectate = bridgePlayer.getToSpectate();
            if (bukkitToSpectate == null) {
                bridgePlayerService.stopSpectating(player, true);
            } else {
                var toSpectate = bridgePlayerService.getBridgePlayer(bukkitToSpectate);
                if (toSpectate == null) {
                    bridgePlayerService.stopSpectating(player, true);
                    return;
                }
                var map = toSpectate.getMap().clone();

                MapPosition mapPosition = map.getMapPosition();
                if (mapPosition == null) return;

                if (!player.hasPermission("teamholy.team")) {
                    if (!mapPosition.isInMapPosition(event.getTo(), true) && player.getGameMode() != GameMode.CREATIVE) {
                        if (bridgePlayer.getMapLocation() != null) {
                            player.teleport(toSpectate.getMapLocation());
                        }
                    }
                }
            }
            return;
        }

        if (bridgePlayer.getState() == BridgePlayer.PlayerState.INGAME) {
            if (bridgePlayer.getMap() == null) {
                return;
            }
            var map = bridgePlayer.getMap().clone();

            MapPosition mapPosition = map.getMapPosition();
            if (mapPosition == null) return;

            if (!mapPosition.isInMapPosition(event.getTo(), true) && player.getGameMode() != GameMode.CREATIVE) {

                if (bridgePlayer.getMapLocation() != null) {
                    player.teleport(bridgePlayer.getMapLocation());
                }

                if (bridgePlayer.isPreview()) {
                    bridgePlayerService.prepareIngamePlayer(player);
                    bridgeMapService.loadMap(bridgePlayer.getMap(), false, bridgePlayer.getMapLocation().clone().add(-0.5,0,-0.5), bridgePlayer.getSelectedSkins().get(bridgePlayer.getMap().getMapType()), true);
                    bridgePlayer.setPreview(false);
                    return;
                }

                if (!bridgePlayer.getBlocks().isEmpty()) {
                    bridgePlayerService.spawnBlockAnimation(bridgePlayer);
                    if (bridgePlayer.getPlayer().getTicksLived() > 10) bridgeSoundPerkService.playSoundPerk(bridgePlayer, BridgeSettings.BridgeSoundEventType.DEATH);
                }


                bridgePlayerService.getScoreboard(player).updateLine(2, "§8");
                bridgePlayer.getBlocks().clear();
                player.setExp(0);
                bridgePlayerService.updateHologram(bridgePlayer,false);
                bridgePlayerService.prepareIngamePlayer(player);
                bridgePlayerService.getPlayerTime().remove(player.getUniqueId());

            }
//            if (bridgePlayerService.getPlayerTime().containsKey(player.getUniqueId())) {
//                long playerTime = (System.currentTimeMillis() - bridgePlayerService.getPlayerTime().get(player.getUniqueId()));
//                String timer = FormatTime.formatTimeManually(playerTime);
//                bridgePlayerService.stopTimer(bridgePlayer, timer, playerTime);
//            }
        }
    }

}
