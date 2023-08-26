package de.teamholy.mlgrush.listener;

import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.player.PlayerEntry;
import de.teamholy.mlgrush.player.PlayerState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        try {
            Player player = (Player) event.getWhoClicked();
            PlayerEntry playerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(player.getUniqueId());
            if (playerEntry.getPlayerState() == PlayerState.LOBBY || playerEntry.getPlayerState() == PlayerState.SPECTATE) {
                if (player.getGameMode() != GameMode.CREATIVE) {
                    if (!event.getClickedInventory().getName().equals("§8» §6Inventory sort")) {
                        event.setCancelled(true);
                    }
                    if (event.getClickedInventory().getName().equals("§8» §6Spectate")) {
                        String map = event.getCurrentItem().getItemMeta().getDisplayName().replace("§8» §6", "");
                        if (MLGRush.getInstance().getGameEntryHandler().get(map) == null)
                            return;
                        if (playerEntry.getPlayerState() == PlayerState.SPECTATE) {
                            playerEntry.removeFromSpectator(true);
                        }
                        playerEntry.setSpectator(MLGRush.getInstance().getGameEntryHandler().get(map));
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

}
