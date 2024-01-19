package de.teamholy.bridge.timer;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.management.PlayerManagement;
import de.teamholy.bridge.util.FormatTime;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class BridgeTimer extends BukkitRunnable {

    private final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();

    @Override
    public void run() {
        playerManagement.getBridgePlayer().values().forEach(bridgePlayer -> {
            var player = bridgePlayer.getPlayer();

            if (bridgePlayer.getState() == BridgePlayer.PlayerState.INGAME) {
                if (playerManagement.getPlayerTime().containsKey(player.getUniqueId())) {
                    long playerTime = playerManagement.getPlayerTime().get(player.getUniqueId());
                    long playerTimeCalc = (System.currentTimeMillis() - playerTime);
                    playerManagement.sendActionBar(player, "§7Time: §e" + FormatTime.formatTimeManually(playerTimeCalc));
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
