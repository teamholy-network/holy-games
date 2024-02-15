package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.service.BridgeMapService;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.service.PlayerService;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PlayerInteractAtItemListener implements Listener {

    private final PlayerService playerService = Bridge.getInstance().getPlayerService();
    private final BridgeMapService bridgeMapService = Bridge.getInstance().getBridgeMapService();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        ItemStack item = event.getItem();

        var bridgePlayer = playerService.getBridgePlayers().get(player.getUniqueId());
        if (bridgePlayer.getToSpectate() == null) {
            if (player.getLocation().getY() <= 96) {
                event.setCancelled(true);
                return;
            }
        }

        event.setCancelled(!event.getPlayer().isOp());

        if (bridgePlayer.getState() == BridgePlayer.PlayerState.LOBBY) {
            if (item == null) return;

            if (item.getItemMeta() != null
                    && item.getItemMeta().getDisplayName() != null) {


                if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§cLeave")) {
                    player.kickPlayer(null);
                }

            }
        } else if (bridgePlayer.getState() == BridgePlayer.PlayerState.SPECTATOR) {
            if (event.getClickedBlock() != null) {
                event.setCancelled(true);
            }

            if (item == null) return;

            if (item.getItemMeta() != null
                    && item.getItemMeta().getDisplayName() != null) {
                if (item.getItemMeta().getDisplayName().startsWith("§8» §cLeave Spectator")) {
                    event.setCancelled(false);

                    playerService.stopSpectating(player, true);
                }
            } else {
                event.setCancelled(true);
            }
        } else if (bridgePlayer.getState() == BridgePlayer.PlayerState.INGAME) {


            event.setCancelled(false);

            if (item == null) return;

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
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

                        if (playerService.getPlayerTime().containsKey(player.getUniqueId())) {
                            player.sendMessage(Bridge.PREFIX + "§cYou can't open the settings while bridging!");
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                            event.setCancelled(true);
                            return;
                        }

                        event.setCancelled(true);

                        playerService.ingameSettingsInventory(player);
                    } else if (item.getItemMeta().getDisplayName().toLowerCase().contains("leave preview")) {
                        player.getInventory().clear();

                        playerService.prepareIngamePlayer(player);
                        player.teleport(bridgePlayer.getMapLocation());

                        bridgeMapService.loadMap(bridgePlayer.getMap(),
                                        false,
                                        bridgePlayer.getMapLocation().clone().add(-0.5, 0, -0.5),
                                        bridgePlayer.getSelectedSkins().get(bridgePlayer.getMap().getMapType()),
                                        true);

                        bridgePlayer.setPreview(false);
                    }
                }
            }
        }
    }


}
