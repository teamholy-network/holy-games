package de.teamholy.clutches.listeners;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.player.PlayerEntry;
import de.teamholy.clutches.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/* copyright by Yassino */
public class HotbarSwitchListener implements Listener {

    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
        Inventory inv = event.getPlayer().getInventory();
        PlayerEntry playerEntry = Clutches.getInstance().getPlayerEntryHandler().get(event.getPlayer().getUniqueId());
        if (playerEntry == null)
            return;
        int slotId = event.getNewSlot();
        if (slotId >= 0 && slotId < inv.getSize()) {
            ItemStack stack = inv.getItem(slotId);
            if (stack != null) {
                if (stack.getType() == Material.REDSTONE) {
                    PlayerUtils.sendBar(playerEntry.getPlayer(), "§c§lPAUSE");
                    playerEntry.setPause(true);
                } else {
                    playerEntry.setPause(false);
                }
            } else {
                playerEntry.setPause(false);
            }
        }
    }

}
