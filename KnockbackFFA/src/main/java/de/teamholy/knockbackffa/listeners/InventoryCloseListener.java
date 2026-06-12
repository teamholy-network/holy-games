package de.teamholy.knockbackffa.listeners;

import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.enums.Items;
import de.teamholy.knockbackffa.models.PlayerEntry;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

/* copyright by Yassino */
public class InventoryCloseListener implements Listener {
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(event.getPlayer().getUniqueId());
        // Vorher NPE bei fehlendem Cache-Eintrag (vom Event-Bus geschluckt, Handler brach ab) — gleiches Ergebnis per Early-Return
        if (playerEntry == null) return;
        if (event.getInventory().getName().equals("§8» §6Inventory Sort")) {
            if(Items.correctInventory(event.getInventory())){
                playerEntry.setInventory(event.getInventory());
                player.sendMessage(KnockbackFFA.getInstance().getPrefix()+"Your inventory sort was saved");
                player.sendMessage(KnockbackFFA.getInstance().getPrefix()+"if you want to reset your inventory do");
                player.sendMessage(KnockbackFFA.getInstance().getPrefix()+" §7-> §8/§7resetinv");
                player.playSound(player.getLocation(), Sound.NOTE_PLING,2f,2f);
            } else {
                playerEntry.createInv();
                player.sendMessage(KnockbackFFA.getInstance().getPrefix()+"Your inventory was not saved");
                player.playSound(player.getLocation(), Sound.ANVIL_BREAK,2f,2f);
            }
            Bukkit.getScheduler().runTaskLater(KnockbackFFA.getInstance(), playerEntry::setLobbyItems,1);
        }
    }
}
