package de.teamholy.sgffa.listeners;

import de.teamholy.core.bukkit.event.CachedPlayerJoinEvent;
import de.teamholy.sgffa.SGFFA;
import de.teamholy.sgffa.models.PlayerEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/* copyright by Yassino */
public class PlayerJoinQuitListener implements Listener {

    @EventHandler
    public void onJoin(CachedPlayerJoinEvent event) {
        Player player = event.getCachedBukkitPlayer().getPlayer();

        SGFFA.getInstance().getCacheHandler().getPlayerEntryHashMap().put(player.getUniqueId(),new PlayerEntry(player));

        SGFFA.getInstance().getCacheHandler().getPlayerEntryHashMap().values().forEach(all -> {
            if (all.isVanish()) {
                if (!player.hasPermission("teamholy.team")) {
                    player.hidePlayer(all.getPlayer());
                }
            }
        });

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        SGFFA.getInstance().getCacheHandler().getPlayerEntryHashMap().remove(player.getUniqueId());
    }

}
