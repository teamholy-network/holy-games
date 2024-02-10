package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.position.MapPosition;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.management.SoundPerkManagement;
import de.teamholy.bridge.player.settings.BridgeSettings;
import de.teamholy.bridge.util.FireworkUtil;
import de.teamholy.bridge.util.FormatTime;
import de.teamholy.bridge.player.management.PlayerManagement;

import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.GameMode;
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

                if (!bridgePlayer.getBlocks().isEmpty()) {
                    playerManagement.spawnBlockAnimation(bridgePlayer);
                    if (bridgePlayer.getPlayer().getTicksLived() > 10) soundPerkManagement.playSoundPerk(bridgePlayer, BridgeSettings.BridgeSoundEventType.DEATH);
                }


                playerManagement.getScoreboard(player).updateLine(2, "§8");
                bridgePlayer.getBlocks().clear();
                playerManagement.updateHologram(bridgePlayer,false);
                playerManagement.prepareIngamePlayer(player);
                playerManagement.getPlayerTime().remove(player.getUniqueId());
            }

            if (player.getLocation().getBlock().getType() == Material.GOLD_PLATE
                    || player.getLocation().clone().subtract(0, 1, 0).getBlock().getType() == Material.DIAMOND_BLOCK) {
                if (!playerManagement.getPlayerTime().containsKey(player.getUniqueId())) {
                    return;
                }

                if (!bridgePlayer.getBlocks().isEmpty()) {
                    playerManagement.spawnBlockAnimation(bridgePlayer);
                }

                bridgePlayer.getBlocks().clear();

                final var current = (System.currentTimeMillis() - playerManagement.getPlayerTime().remove(player.getUniqueId()));

                var beforeBestLocal = bridgePlayer.getLocalBestTime(bridgePlayer.getMap().getMapType());
                var beforeBestGlobal = bridgePlayer.getGlobalBestTime(bridgePlayer.getMap().getMapType());

                String newTime = FormatTime.formatTimeManually(current);
                playerManagement.getScoreboard(player).updateLine(2, "§8");

                playerManagement.sendTitle(player,"§fTime §8» §a" + newTime,"§a+ §e2 Coins",10,20,10);
                BukkitCore.getAPI().getCoinManager().addCoins(player.getUniqueId(), 2, true);

                if (current < beforeBestGlobal || beforeBestGlobal == 0) {
                    String timerDifference = FormatTime.formatTimeManually(beforeBestGlobal - current);

                    FireworkUtil.playFirework(player.getWorld(),player.getLocation().add(0,-3,0), FireworkUtil.getBlowupRandomEffect());
                    FireworkUtil.playFirework(player.getWorld(),player.getLocation().add(0,-3,0), FireworkUtil.getBlowupRandomEffect());
                    FireworkUtil.playFirework(player.getWorld(),player.getLocation().add(0,-3,0), FireworkUtil.getBlowupRandomEffect());
                    player.teleport(bridgePlayer.getMapLocation());
                    player.sendMessage("§8§m-----------§f§lCONGRATS§8§m--------------");
                    player.sendMessage("");
                    player.sendMessage(" §fYou have beaten your §c§lall-time §frecord!");
                    player.sendMessage("      §fYour new §atime §fis §e" + newTime + (beforeBestGlobal != 0 ? " §8︳ §a-" + timerDifference + "§2 difference" : ""));
                    player.sendMessage("");
                    player.sendMessage("§8§m----------------------------------");

                    bridgePlayer.setGlobalBestTime(bridgePlayer.getMap().getMapType(), current);
                    bridgePlayer.setLocalBestTime(bridgePlayer.getMap().getMapType(), current);

                    playerManagement.addBestTime(bridgePlayer, current);

                    soundPerkManagement.playSoundPerk(bridgePlayer, BridgeSettings.BridgeSoundEventType.NEW_RECORD);
                } else if (current < beforeBestLocal || beforeBestLocal == 0) {
                    String timerDifference = FormatTime.formatTimeManually(beforeBestLocal - current);

                    FireworkUtil.playFirework(player.getWorld(),player.getLocation(), FireworkUtil.getBlowupRandomEffect());
                    player.teleport(bridgePlayer.getMapLocation());
                    player.sendMessage("");
                    player.sendMessage(" §fYou have beaten your §2§lsession record!");
                    player.sendMessage("       §fYour new §atime §fis §e" + newTime + (beforeBestLocal != 0 ? " §8︳ §a-" + timerDifference + "§2 difference" : ""));
                    player.sendMessage("");

                    bridgePlayer.setLocalBestTime(bridgePlayer.getMap().getMapType(), current);

                    playerManagement.addBestTime(bridgePlayer, current);
                    soundPerkManagement.playSoundPerk(bridgePlayer, BridgeSettings.BridgeSoundEventType.NEW_RECORD);

                } else {
                    player.teleport(bridgePlayer.getMapLocation());
                    soundPerkManagement.playSoundPerk(bridgePlayer, BridgeSettings.BridgeSoundEventType.WIN);
                }
                bridgePlayer.getBestTimes().get(bridgePlayer.getMap().getMapType()).add(current);

                bridgePlayer.addWin();
                playerManagement.updateScoreboard(bridgePlayer);

                playerManagement.prepareIngamePlayer(player);
                playerManagement.updateHologram(bridgePlayer,false);
            }
        } else if (bridgePlayer.getState() == BridgePlayer.PlayerState.LOBBY) {
            if (event.getTo().getX() <= player.getWorld().getSpawnLocation().getX() - 100) {
                player.teleport(player.getWorld().getSpawnLocation());
            }
        }
    }

}
