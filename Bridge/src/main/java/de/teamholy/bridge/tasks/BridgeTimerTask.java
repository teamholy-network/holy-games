package de.teamholy.bridge.tasks;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.management.PlayerManagement;
import de.teamholy.bridge.player.settings.BridgeSettings;
import de.teamholy.bridge.util.FormatTime;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;

import java.util.HashMap;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class BridgeTimerTask implements Runnable {

    private final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();

    @Override
    public void run() {
        try {

            for (BridgePlayer bridgePlayer : playerManagement.getBridgePlayers().values()) {
                var player = bridgePlayer.getPlayer();

                if (bridgePlayer.getState() == BridgePlayer.PlayerState.INGAME) {
                    if (playerManagement.getPlayerTime().containsKey(player.getUniqueId())) {
                        if (playerManagement.getPlayerTime().get(player.getUniqueId()) == null) continue;
                        long playerTime = (System.currentTimeMillis() - playerManagement.getPlayerTime().get(player.getUniqueId()));
                        String timer = FormatTime.formatTimeManually(playerTime);

                        displayTimer(bridgePlayer, timer, bridgePlayer.getBridgeSettings().getTimerPlace());

                        playerManagement.stopTimer(bridgePlayer, timer, playerTime);
                    }

                    if (bridgePlayer.getBridgeSettings().isRemoveBlocks()) {
                        long blockTime = bridgePlayer.getBridgeSettings().getRemovalTime();
                        HashMap<Block, Long> blocks = (HashMap<Block, Long>) bridgePlayer.getBlocks().clone();

                        if (!blocks.isEmpty() || blockTime > 1) {
                            blocks.forEach((block, time) -> {
                                if ((System.currentTimeMillis() - time) / 1000 >= blockTime) {

                                    Bukkit.getScheduler().runTask(Bridge.getInstance(), () -> {
                                        FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
                                        block.setType(Material.AIR);
                                        fallingBlock.setDropItem(false);
                                        fallingBlock.setHurtEntities(false);
                                        Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), fallingBlock::remove, 40L);
                                    });


                                    bridgePlayer.getBlocks().remove(block);
                                }
                            });
                        }
                    }


                } else if (bridgePlayer.getState() == BridgePlayer.PlayerState.SPECTATOR) {
                    if (bridgePlayer.getToSpectate() != null) {
                        var toSpectate = bridgePlayer.getToSpectate();
                        if (toSpectate.isOnline()) {
                            if (playerManagement.getPlayerTime().containsKey(toSpectate.getUniqueId())) {
                                long playerTime = (System.currentTimeMillis() - playerManagement.getPlayerTime().get(toSpectate.getUniqueId()));
                                String timer = FormatTime.formatTimeManually(playerTime);

                                displayTimer(bridgePlayer, timer, bridgePlayer.getBridgeSettings().getTimerPlace());
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void displayTimer(BridgePlayer bridgePlayer, String timer, BridgeSettings.TimerPlace timerPlace) {
        var player = bridgePlayer.getPlayer();

        BridgeMapType.InMapCords inMapCords = bridgePlayer.getMap().getMapType().getFinishLine();
        Location spawnLocation = bridgePlayer.getMap().getSpawnLocation().clone();
        Location finishLineLocation = spawnLocation.clone().add(inMapCords.xADD(),inMapCords.yADD(),inMapCords.zADD());

        double maxDistance = spawnLocation.distance(finishLineLocation);
        double currentDistance = player.getLocation().distance(finishLineLocation);
        float exp = (float) ((maxDistance - currentDistance) / maxDistance);

        player.setExp(exp);

        if (bridgePlayer.getState() == BridgePlayer.PlayerState.INGAME) {
            switch (timerPlace) {
                case ACTION_BAR -> playerManagement.sendActionBar(player, "§7Time §8» §e" + timer);
                case TITLE -> playerManagement.sendTitle(player, "", "§7Time §8» §e" + timer, 0, 20, 0);
                case SCOREBOARD -> playerManagement.getScoreboard(player).updateLine(2, "  §7Time §8» §e" + timer);
            }
        } else {
            var toSpec = bridgePlayer.getToSpectate();
            var rankColor = BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(toSpec.getUniqueId());

            switch (timerPlace) {
                case ACTION_BAR ->
                        playerManagement.sendActionBar(player, rankColor + toSpec.getName() + " §8» §e" + timer);
                case TITLE ->
                        playerManagement.sendTitle(player, "", rankColor + toSpec.getName() + " §8» §e" + timer, 0, 20, 0);
                case SCOREBOARD ->
                        playerManagement.getScoreboard(player).updateLine(2, rankColor + toSpec.getName() + " §8» §e" + timer);
            }
        }
    }

}
