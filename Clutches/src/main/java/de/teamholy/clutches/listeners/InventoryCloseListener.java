package de.teamholy.clutches.listeners;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.enums.Items;
import de.teamholy.clutches.player.PlayerEntry;
import de.teamholy.clutches.player.PlayerState;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

/* copyright by Yassino */
public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onHandle(InventoryCloseEvent event) {
        PlayerEntry playerEntry = Clutches.getInstance().getPlayerEntryHandler().get(event.getPlayer().getUniqueId());
        Player player = (Player) event.getPlayer();
        if (playerEntry == null) return;
        playerEntry.setPause(false);
        if (playerEntry.getPlayerState() == PlayerState.PLAYGROUND) return;
        if (event.getInventory().getName().equals("§8» §6Inventory Sort")) {
            if (Items.correctInventory(event.getInventory())) {
                playerEntry.setInventory(event.getInventory());
                player.sendMessage(Clutches.PREFIX + "Your inventory sort was saved");
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 2f, 2f);
                playerEntry.setItemsSpawn();
            } else {
                playerEntry.createInv();
                player.sendMessage(Clutches.PREFIX + "Your inventory was not saved");
                player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 2f, 2f);
                playerEntry.setItemsSpawn();
            }
        }
    }

}
