package de.teamholy.sgffa.listeners;

import de.teamholy.api.events.bukkit.CloudChannelListenEvent;
import de.teamholy.sgffa.SGFFA;
import de.teamholy.sgffa.models.PlayerEntry;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

/* copyright by Yassino */
public class CloudListener implements Listener {

    @EventHandler
    public void onCloudChannel(CloudChannelListenEvent event) {
        if (event.getMessage().equalsIgnoreCase("report")) {
            Bukkit.getScheduler().runTaskLater(SGFFA.getInstance(),() -> {
                PlayerEntry playerEntry = SGFFA.getInstance().getCacheHandler().getPlayerEntryHashMap().get(event.getData().get("jumperUuid", UUID.class));
                PlayerEntry targetEntry = SGFFA.getInstance().getCacheHandler().getPlayerEntryHashMap().get(event.getData().get("targetUuid", UUID.class));
                if (playerEntry == null) return;

                if (targetEntry == null) {
                    playerEntry.getPlayer().sendMessage(SGFFA.PREFIX + "Dieser Spieler ist nicht mehr auf dem Server");
                    return;
                }

                playerEntry.vanish();
                playerEntry.getPlayer().teleport(targetEntry.getPlayer());
            },5);
        }


    }

}
