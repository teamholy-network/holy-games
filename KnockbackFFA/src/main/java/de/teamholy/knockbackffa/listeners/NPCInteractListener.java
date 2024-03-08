package de.teamholy.knockbackffa.listeners;

import de.teamholy.core.bukkit.npc.event.PlayerInteractAtNPCEvent;
import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.enums.PlayerState;
import de.teamholy.knockbackffa.models.PlayerEntry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/* copyright by Yassino */
public class NPCInteractListener implements Listener {

    @EventHandler
    public void onNPC(PlayerInteractAtNPCEvent event) {
        PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(event.getPlayer().getUniqueId());
        if (playerEntry.getPlayerState() == PlayerState.LOBBY && event.getNpcEntry().getDisplayName().equalsIgnoreCase("§6§lInventory")) {
            playerEntry.openInventorySort();
        }
    }

}
