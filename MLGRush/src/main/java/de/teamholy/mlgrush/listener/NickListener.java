package de.teamholy.mlgrush.listener;

import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.player.PlayerEntry;
import de.teamholy.mlgrush.player.PlayerState;
import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.events.PlayerPreNickEvent;
import eu.koboo.markup.events.PlayerPreUnnickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NickListener implements Listener {

    @EventHandler
    public void onNick(PlayerPreNickEvent event) {
        Player player = event.getPlayer();
        PlayerEntry playerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(player.getUniqueId());

        if (playerEntry == null || playerEntry.getPlayerState() != PlayerState.INGAME) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(MarkupAPI.NICK_PREFIX + "You can't nick while you are in a game!");

    }

    @EventHandler
    public void onNick(PlayerPreUnnickEvent event) {
        Player player = event.getPlayer();
        PlayerEntry playerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(player.getUniqueId());

        if (playerEntry == null || playerEntry.getPlayerState() != PlayerState.INGAME) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(MarkupAPI.NICK_PREFIX + "You can't unnick while you are in a game!");

    }
}
