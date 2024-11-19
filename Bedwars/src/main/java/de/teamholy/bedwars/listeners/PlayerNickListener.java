package de.teamholy.bedwars.listeners;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.model.PlayerEntry;
import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.events.PlayerPreNickEvent;
import eu.koboo.markup.events.PlayerPreUnnickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerNickListener implements Listener {

    @EventHandler
    public void onNick(PlayerPreNickEvent event) {
        Player player = event.getPlayer();
        PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId());

        if (!Bedwars.getInstance().getIngamePlayers().contains(playerEntry)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(MarkupAPI.NICK_PREFIX + "You can't nick while you are in a game!");

    }

    @EventHandler
    public void onNick(PlayerPreUnnickEvent event) {
        Player player = event.getPlayer();
        PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId());

        if (!Bedwars.getInstance().getIngamePlayers().contains(playerEntry)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(MarkupAPI.NICK_PREFIX + "You can't unnick while you are in a game!");

    }

}
