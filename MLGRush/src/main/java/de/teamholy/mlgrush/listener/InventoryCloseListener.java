package de.teamholy.mlgrush.listener;

import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.enums.Items;
import de.teamholy.mlgrush.game.GameEntry;
import de.teamholy.mlgrush.game.GameState;
import de.teamholy.mlgrush.player.PlayerEntry;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onHandle(InventoryCloseEvent event) {
        PlayerEntry playerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(event.getPlayer().getUniqueId());
        Player player = (Player) event.getPlayer();
        if (event.getInventory().getName().equals("§8» §6Inventory sort")) {
            if (player == null)
                return;
            if(Items.correctInventory(event.getInventory())){
                playerEntry.setInventory(event.getInventory());
                player.sendMessage(MLGRush.getInstance().getPrefix()+"Your inventory sort was saved");
                player.playSound(player.getLocation(), Sound.NOTE_PLING,2f,2f);
            } else {
                playerEntry.createInv();
                player.sendMessage(MLGRush.getInstance().getPrefix()+"Your inventory was not saved");
                player.playSound(player.getLocation(), Sound.ANVIL_BREAK,2f,2f);
            }
            Bukkit.getScheduler().runTaskLater(MLGRush.getInstance(), playerEntry::setItemsSpawn,1);
        } else if (event.getInventory().getName().equals("§8» §6Map Selection")) {
            if (playerEntry.getGameEntry() == null) {
                return;
            }
            if (playerEntry.getGameEntry().getGameState() == GameState.MAPSELECT) {
                GameEntry gameEntry = playerEntry.getGameEntry();
                playerEntry.setGameEntry(null);
                player.sendMessage(MLGRush.getInstance().getPrefix() + "Map selection was canceled");
                gameEntry.getPlayersPlaying().forEach(playerEntry1 -> {
                    if (playerEntry != playerEntry1) {
                        playerEntry1.getPlayer().closeInventory();
                    }
                });
            }
        }
    }
    
}
