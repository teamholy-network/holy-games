package de.teamholy.clutches.playground;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.playground.model.PlaygroundPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

/* copyright by Yassino */
public class PlaygroundListener implements Listener {


    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        if (event.getInventory().getName() != null && (event.getInventory().getName().toLowerCase().contains("perks") || event.getInventory().getName().toLowerCase().contains("armor"))) {
            PlaygroundPlayer playgroundPlayer = Clutches.getInstance().getPlayerEntryHandler().get(player.getUniqueId()).getPlaygroundPlayer();
            if (playgroundPlayer == null) return;
            playgroundPlayer.setItems();
        }

    }

}
