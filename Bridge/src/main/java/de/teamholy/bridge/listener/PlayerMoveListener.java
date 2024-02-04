package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.position.MapPosition;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.management.SoundPerkManagement;
import de.teamholy.bridge.util.FireworkUtil;
import de.teamholy.bridge.util.FormatTime;
import de.teamholy.bridge.map.management.BridgeMapManagement;
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
    private final BridgeMapManagement mapManagement = Bridge.getInstance().getMapManagement();
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

            if (!mapPosition.isInMapPosition(event.getTo(), true)) {
                if (!bridgePlayer.getBlocks().isEmpty()) {
                    playerManagement.spawnBlockAnimation(bridgePlayer);
                    if (bridgePlayer.getPlayer().getTicksLived() > 10) soundPerkManagement.playSoundPerk(bridgePlayer, false, false);
                }

                if (bridgePlayer.getMapLocation() != null) {
                    player.teleport(bridgePlayer.getMapLocation());
                }

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

                var current = (System.currentTimeMillis() - playerManagement.getPlayerTime().remove(player.getUniqueId()));

                var beforeBestLocal = bridgePlayer.getLocalBestTime(bridgePlayer.getMap().getMapType());
                var beforeBestGlobal = bridgePlayer.getGlobalBestTime(bridgePlayer.getMap().getMapType());

                playerManagement.sendTitle(player,"§fTime §8» §a" + FormatTime.formatTimeManually(current),"§a+ §e2 Coins",10,20,10);
                BukkitCore.getAPI().getCoinManager().addCoins(player.getUniqueId(), 2, true);

                if (current < beforeBestGlobal) {

                    FireworkUtil.playFirework(player.getWorld(),player.getLocation().add(0,-3,0), FireworkUtil.getBlowupRandomEffect());
                    FireworkUtil.playFirework(player.getWorld(),player.getLocation().add(0,-3,0), FireworkUtil.getBlowupRandomEffect());
                    FireworkUtil.playFirework(player.getWorld(),player.getLocation().add(0,-3,0), FireworkUtil.getBlowupRandomEffect());
                    player.sendMessage("§8§m-----------§f§lCONGRATS§8§m--------------");
                    player.sendMessage("");
                    player.sendMessage(" §fYou have beaten your §c§lall-time §frecord!");
                    player.sendMessage("      §fYour new §atime §fis §e" + FormatTime.formatTimeManually(current) + " §8︳ §a-" + FormatTime.formatTimeManually(beforeBestGlobal - current) + "§2 difference");
                    player.sendMessage("");
                    player.sendMessage("§8§m----------------------------------");

                } else if (current < beforeBestLocal) {

                    FireworkUtil.playFirework(player.getWorld(),player.getLocation(), FireworkUtil.getBlowupRandomEffect());
                    player.sendMessage("");
                    player.sendMessage(" §fYou have beaten your §2§lsession record!");
                    player.sendMessage("       §fYour new §atime §fis §e" + FormatTime.formatTimeManually(current) + " §8︳ §a-" + FormatTime.formatTimeManually(beforeBestLocal - current) + "§2 difference");
                    player.sendMessage("");

                }

                player.teleport(bridgePlayer.getMapLocation());

                var bestLocal = playerManagement.checkBestTime(player, current, bridgePlayer.getLocalBestTime(bridgePlayer.getMap().getMapType()), false);
                var bestGlobal = playerManagement.checkBestTime(player, current, bridgePlayer.getGlobalBestTime(bridgePlayer.getMap().getMapType()), true);

                bridgePlayer.getBestTimes().get(bridgePlayer.getMap().getMapType()).add(current);

                soundPerkManagement.playSoundPerk(bridgePlayer, true, current < bestLocal || current < bestGlobal);

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
