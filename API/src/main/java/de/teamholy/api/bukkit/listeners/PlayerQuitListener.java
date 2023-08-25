package de.teamholy.api.bukkit.listeners;

import de.teamholy.api.BukkitHolyAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/* copyright by Yassino */
public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        BukkitHolyAPI.getInstance().getBukkitCacheHandler().getHolyPlayerHashMap().remove(player.getUniqueId());
    }

}
