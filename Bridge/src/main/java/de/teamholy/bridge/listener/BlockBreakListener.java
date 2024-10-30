package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.player.BridgePlayer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBreak(BlockDamageEvent event) {
        if (event.getPlayer() == null || event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Player player = event.getPlayer();

        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.GOLD_PICKAXE) {
            event.setCancelled(true);
            return;
        }

        BridgePlayer bridgePlayer = Bridge.getInstance().getBridgePlayerService().getBridgePlayer(event.getPlayer());
        if (bridgePlayer == null) {
            event.setCancelled(true);
            return;
        }

        if (bridgePlayer.getBlocks().containsKey(event.getBlock())) {
            // Remove the block from the player's block list, wouldnt work otherwise
            bridgePlayer.getBlocks()
                    .keySet()
                    .stream()
                    .filter(block -> block.getLocation() == event.getBlock().getLocation())
                    .forEach(block -> bridgePlayer.getBlocks().remove(block));

            player.playEffect(event.getBlock().getLocation(), org.bukkit.Effect.STEP_SOUND, event.getBlock().getType());

            event.getBlock().setType(Material.AIR);
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPhysics(BlockPhysicsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFrom(BlockFromToEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onForm(BlockFormEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onForm(EntityBlockFormEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onForm(EntityChangeBlockEvent event) {
        if ((event.getEntity().getType() == EntityType.FALLING_BLOCK))
            event.setCancelled(true);
    }
}
