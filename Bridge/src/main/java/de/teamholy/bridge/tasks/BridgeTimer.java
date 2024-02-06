package de.teamholy.bridge.tasks;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.management.PlayerManagement;
import de.teamholy.bridge.util.FormatTime;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class BridgeTimer implements Runnable {

    private final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();

    @Override
    public void run() {
        playerManagement.getBridgePlayers().values().forEach(bridgePlayer -> {
            var player = bridgePlayer.getPlayer();

            if (bridgePlayer.getState() == BridgePlayer.PlayerState.INGAME) {
                if (playerManagement.getPlayerTime().containsKey(player.getUniqueId())) {
                    long playerTime = (System.currentTimeMillis() - playerManagement.getPlayerTime().get(player.getUniqueId()));
                    String timer = FormatTime.formatTimeManually(playerTime);

                    switch (bridgePlayer.getSettings().getTimerPlace()) {
                        case ACTION_BAR -> playerManagement.sendActionBar(player, "§7Time §8» §e" + timer);
                        case TITLE -> playerManagement.sendTitle(player, "", "§7Time §8» §e" + timer,0,20,0);
                        case SCOREBOARD -> playerManagement.getScoreboard(player).updateLine(2, "  §7Time §8» §e" + timer);
                    }
                }

                if (bridgePlayer.getSettings().isRemoveBlocks()) {
                    long blockTime = bridgePlayer.getSettings().getRemovalTime();
                    HashMap<Block, Long> blocks = (HashMap<Block, Long>) bridgePlayer.getBlocks().clone();


                    if (blocks.isEmpty() || blockTime < 1) return;

                    blocks.forEach((block, time) -> {
                        if ((System.currentTimeMillis() - time) / 1000 >= blockTime) {
                            block.setType(Material.AIR);
                            bridgePlayer.getBlocks().remove(block);
                        }
                    });
                }
            }
        });
    }
}
