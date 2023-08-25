package de.teamholy.bedwars.listeners;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.enums.GameState;
import de.teamholy.bedwars.model.MapEntry;
import de.teamholy.bedwars.model.PlayerEntry;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/* copyright by Yassino */
public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        try {
            Player player = (Player) event.getWhoClicked();
            PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId());
            if (player.getGameMode() == GameMode.CREATIVE) return;
            if (Bedwars.getInstance().getGameState() == GameState.LOBBY) {
                if (event.getInventory().getName().toLowerCase().contains("team")) {
                    playerEntry.addPlayerTeam(event);
                }  else if (event.getInventory().getName().toLowerCase().contains("gold")) {
                    if (event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("yes")) {
                        Bedwars.getInstance().getInventoryHandler().getGoldVotingInventory().removingVotes(player);
                        if (player.hasPermission("teamholy.goldvoting.double")) {
                            Bedwars.getInstance().getInventoryHandler().getGoldVotingInventory().getWithGold().add(player);
                        }
                        Bedwars.getInstance().getInventoryHandler().getGoldVotingInventory().getWithGold().add(player);
                        player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
                        Bedwars.getInstance().getInventoryHandler().getGoldVotingInventory().updateInventory();
                    } else if (event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("no")) {
                        Bedwars.getInstance().getInventoryHandler().getGoldVotingInventory().removingVotes(player);
                        if (player.hasPermission("teamholy.goldvoting.double")) {
                            Bedwars.getInstance().getInventoryHandler().getGoldVotingInventory().getWithoutGold().add(player);
                        }
                        Bedwars.getInstance().getInventoryHandler().getGoldVotingInventory().getWithoutGold().add(player);
                        player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
                        Bedwars.getInstance().getInventoryHandler().getGoldVotingInventory().updateInventory();
                    }
                } else if (event.getInventory().getName().equalsIgnoreCase("§8» §6Map voting")) {
                    if (event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) return;
                    player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
                    String mapName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().replace("» " , ""));
                    MapEntry mapEntry = Bedwars.getInstance().getCacheHandler().getMapEntries().get(mapName);
                    if (playerEntry.getVotedMap() != null) {

                        if (playerEntry.getVotedMap() == mapEntry) {
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "You unvoted §6" + mapName);
                            mapEntry.removeVote();
                            playerEntry.setVotedMap(null);
                            return;
                        }

                        player.sendMessage(Bedwars.getInstance().getPrefix() + "You voted §a" + mapName);
                        playerEntry.getVotedMap().removeVote();
                        playerEntry.setVotedMap(mapEntry);
                        mapEntry.addVote();
                    } else {
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "You voted §a" + mapName);
                        playerEntry.setVotedMap(mapEntry);
                        mapEntry.addVote();

                    }
                }
            } else if (Bedwars.getInstance().getGameState() == GameState.INGAME && Bedwars.getInstance().getSpectatePlayers().contains(playerEntry)) {
                event.setCancelled(true);
            }

        } catch (Exception ignored) {}
    }

}
