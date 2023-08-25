package de.teamholy.api.bukkit.listeners;

import de.teamholy.core.bukkit.BukkitCore;
import eu.koboo.markup.events.PlayerPostNickEvent;
import eu.koboo.markup.events.PlayerPostUnnickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

/* copyright by Yassino */
public class NickListener implements Listener {

    @EventHandler
    public void onPostNick(PlayerPostNickEvent event) {
        BukkitCore.getAPI().getNickManager().addNick(event.getPlayer().getUniqueId(),event.getPlayerMeta().getNickName());
    }

    @EventHandler
    public void onPostUnnick(PlayerPostUnnickEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        BukkitCore.getAPI().getNickManager().removeNick(uuid);
    }

}
