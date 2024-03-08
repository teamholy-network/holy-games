package de.teamholy.mlgrush.listener;

import de.teamholy.core.bukkit.event.CloudChannelListenEvent;
import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.player.PlayerEntry;
import de.teamholy.mlgrush.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

/* copyright by Yassino */
public class CloudListener implements Listener {

    @EventHandler
    public void onCloudChannel(CloudChannelListenEvent event) {
        if (event.getMessage().equalsIgnoreCase("report")) {
            Bukkit.getScheduler().runTaskLater(MLGRush.getInstance(),() -> {
                PlayerEntry playerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(event.getData().get("jumperUuid", UUID.class));
                PlayerEntry targetEntry = MLGRush.getInstance().getPlayerEntryHandler().get(event.getData().get("targetUuid", UUID.class));
                if (playerEntry == null) return;

                if (targetEntry == null) {
                    playerEntry.getPlayer().sendMessage(MLGRush.getInstance().getPrefix() + "Dieser Spieler ist nicht mehr auf dem Server");
                    return;
                }

                if (targetEntry.getPlayerState() != PlayerState.INGAME) {
                    playerEntry.getPlayer().sendMessage(MLGRush.getInstance().getPrefix() + "Dieser Spieler ist nicht am spielen");
                    return;
                }

                playerEntry.setSpectator(targetEntry.getGameEntry());
            },5);
        }


    }

}
