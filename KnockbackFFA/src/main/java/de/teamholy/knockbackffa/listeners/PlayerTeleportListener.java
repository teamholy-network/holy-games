package de.teamholy.knockbackffa.listeners;

import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.enums.PlayerState;
import de.teamholy.knockbackffa.models.PlayerEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

/* copyright by Yassino */
public class PlayerTeleportListener implements Listener {
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(player.getUniqueId());
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            if (playerEntry.getPlayerState() != PlayerState.INGAME) return;
            if (player.isDead() || player.getLocation().getBlockY() > playerEntry.getActiveMap().getSpawnHight() || event.getTo().getBlockY() > playerEntry.getActiveMap().getSpawnHight()) {
                event.setCancelled(true);
            }
        }
    }

}
