package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.management.PlayerManagement;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PlayerInteractAtItemListener implements Listener {

    private final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (player.getLocation().getY() <= 96) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(!event.getPlayer().isOp());

        BridgePlayer bridgePlayer = playerManagement.getBridgePlayers().get(player.getUniqueId());


        if (bridgePlayer.getState() == BridgePlayer.PlayerState.LOBBY) {
            if (item == null) return;

            if (item.getItemMeta() != null
                    && item.getItemMeta().getDisplayName() != null) {


                if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§cLeave")) {
                    player.kickPlayer(null);
                }

            }
        } else if (bridgePlayer.getState() == BridgePlayer.PlayerState.INGAME) {


            event.setCancelled(false);

            if (item == null) return;

            if (item.getItemMeta() != null
                    && item.getItemMeta().getDisplayName() != null) {
                if (item.getItemMeta().getDisplayName().startsWith("§8» §cQuit")) {
           /*         bridgePlayer.setState(BridgePlayer.PlayerState.LOBBY);
                    playerManagement.loadLobbyInventory(player);
                    mapManagement.getLoader().unloadMap(bridgePlayer);

                    if (!bridgePlayer.getBlocks().isEmpty()) {
                        bridgePlayer.getBlocks().forEach((block, time) -> block.setType(Material.AIR));
                    }

                    bridgePlayer.getBlocks().clear();

*/

                    player.kickPlayer(null);

                } else if (item.getItemMeta().getDisplayName().startsWith("§8» §6Settings")) {

                    if (playerManagement.getPlayerTime().containsKey(player.getUniqueId())) {
                        player.sendMessage(Bridge.PREFIX + "§cYou can't open the settings while bridging!");
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                        event.setCancelled(true);
                        return;
                    }

                    event.setCancelled(true);

                    playerManagement.ingameSettingsInventory(player);
                } else if (item.getItemMeta().getDisplayName().toLowerCase().contains("leave preview")) {
                    playerManagement.prepareIngamePlayer(player);
                    player.teleport(bridgePlayer.getMapLocation());
                    bridgePlayer.getMap()
                            .loadMap(
                                    false,
                                    bridgePlayer.getMapLocation().add(-0.5,0,-0.5),
                                    bridgePlayer.getSelectedSkins().get(bridgePlayer.getMap().getMapType()),
                                    true);

                    bridgePlayer.setPreview(false);
                }
            }
        }
    }


}
