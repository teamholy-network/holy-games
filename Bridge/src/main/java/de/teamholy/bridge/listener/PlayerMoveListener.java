package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.map.position.MapPosition;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.management.SoundPerkManagement;
import de.teamholy.bridge.player.settings.BridgeSettings;
import de.teamholy.bridge.util.FireworkUtil;
import de.teamholy.bridge.util.FormatTime;
import de.teamholy.bridge.player.management.PlayerManagement;

import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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

    private final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();
    private final SoundPerkManagement soundPerkManagement = Bridge.getInstance().getSoundPerkManagement();


    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        /*if ((event.getFrom().getBlockX() == event.getTo().getBlockX())
                && (event.getFrom().getBlockY() == event.getTo().getBlockY())
                && (event.getFrom().getBlockZ() == event.getTo().getBlockZ())
                && (event.getFrom().getWorld() == event.getTo().getWorld()))
            return;*/
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) return;
        var player = event.getPlayer();

        BridgePlayer bridgePlayer = playerManagement.getBridgePlayer(player);
        if (bridgePlayer.getState() == BridgePlayer.PlayerState.SPECTATOR) {
            var bukkitToSpectate = bridgePlayer.getToSpectate();
            if (bukkitToSpectate == null) {
                playerManagement.stopSpectating(player, true);
            } else {
                var toSpectate = playerManagement.getBridgePlayer(bukkitToSpectate);
                if (toSpectate == null) {
                    playerManagement.stopSpectating(player, true);
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
                    playerManagement.prepareIngamePlayer(player);
                    bridgePlayer.getMap()
                            .loadMap(
                                    false,
                                    bridgePlayer.getMapLocation().clone().add(-0.5,0,-0.5),
                                    bridgePlayer.getSelectedSkins().get(bridgePlayer.getMap().getMapType()),
                                    true);

                    bridgePlayer.setPreview(false);
                    return;
                }

                if (!bridgePlayer.getBlocks().isEmpty()) {
                    playerManagement.spawnBlockAnimation(bridgePlayer);
                    if (bridgePlayer.getPlayer().getTicksLived() > 10) soundPerkManagement.playSoundPerk(bridgePlayer, BridgeSettings.BridgeSoundEventType.DEATH);
                }


                playerManagement.getScoreboard(player).updateLine(2, "§8");
                bridgePlayer.getBlocks().clear();
                player.setExp(0);
                playerManagement.updateHologram(bridgePlayer,false);
                playerManagement.prepareIngamePlayer(player);
                playerManagement.getPlayerTime().remove(player.getUniqueId());
            }
        }
    }

}
