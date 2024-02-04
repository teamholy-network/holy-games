package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.position.MapPosition;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.management.SoundPerkManagement;
import de.teamholy.bridge.util.FireworkUtil;
import de.teamholy.bridge.util.FormatTime;
import de.teamholy.bridge.map.management.BridgeMapManagement;
import de.teamholy.bridge.player.management.PlayerManagement;

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

                if (current < beforeBestGlobal || beforeBestGlobal == 0) {

                    FireworkUtil.playFirework(player.getWorld(),player.getLocation(), FireworkUtil.getBlowupRandomEffect());
                    FireworkUtil.playFirework(player.getWorld(),player.getLocation(), FireworkUtil.getBlowupRandomEffect());
                    FireworkUtil.playFirework(player.getWorld(),player.getLocation(), FireworkUtil.getBlowupRandomEffect());
                    player.sendMessage("§8§m-----------§f§lCONGRATS§8§m--------------");
                    player.sendMessage("");
                    player.sendMessage(" §fYou have beaten your §c§lall-time §frecord!");
                    player.sendMessage("      §fYour new §2§ltime §fis §e" + FormatTime.formatTimeManually(current));
                    player.sendMessage("");
                    player.sendMessage("§8§m----------------------------------");

                } else if (current < beforeBestLocal || beforeBestLocal == 0) {

                    FireworkUtil.playFirework(player.getWorld(),player.getLocation(), FireworkUtil.getRandomEffect());
                    player.sendMessage("");
                    player.sendMessage("§aYou have beaten your §2session record! §7(§e" + FormatTime.formatTimeManually(current) + "§7)");
                    player.sendMessage("");

                } else {
                    player.sendMessage("§7Your time was §e" + FormatTime.formatTimeManually(current) +
                            "\n" + Bridge.PREFIX + "§7Your best §2session-time §7is §e" +
                            FormatTime.formatTimeManually(beforeBestLocal) + " §8» " + mapManagement.colorCodeByType(map.getMapType()) + map.getMapType().getName()
                            + "\n" + Bridge.PREFIX + "§7Your best §call-time §7is §e" + FormatTime.formatTimeManually(beforeBestGlobal) + " §8» " + mapManagement.colorCodeByType(map.getMapType()) + map.getMapType().getName());
                }

                player.teleport(bridgePlayer.getMapLocation());

                var bestLocal = playerManagement.checkBestTime(player, current, bridgePlayer.getLocalBestTime(bridgePlayer.getMap().getMapType()), false);
                var bestGlobal = playerManagement.checkBestTime(player, current, bridgePlayer.getGlobalBestTime(bridgePlayer.getMap().getMapType()), true);

                soundPerkManagement.playSoundPerk(bridgePlayer, true, current < bestLocal || current < bestGlobal);

                playerManagement.prepareIngamePlayer(player);



            }
        } else if (bridgePlayer.getState() == BridgePlayer.PlayerState.LOBBY) {
            if (event.getTo().getX() <= player.getWorld().getSpawnLocation().getX() - 100) {
                player.teleport(player.getWorld().getSpawnLocation());
            }
        }
    }

}
