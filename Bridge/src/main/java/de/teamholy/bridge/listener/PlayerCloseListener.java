package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

/* copyright by Yassino */
public class PlayerCloseListener implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        var player = (Player) event.getPlayer();
        var inventory = event.getInventory();


        if (inventory == null) return;
        if (!inventory.getName().toLowerCase().contains("perks")) return;

        Bridge.getInstance().getPlayerService().prepareIngamePlayer(player);


    }
}
