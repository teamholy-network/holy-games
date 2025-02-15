package de.teamholy.bedwars.listeners;

import de.teamholy.bedwars.Bedwars;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldChangeListener implements Listener {
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(), () -> {
                player.setGameMode(GameMode.ADVENTURE);
                new Thread(() -> {
                    try {
                        /*
                         * Set the player to spectator mode Async triggers the AsyncCatcher in Spigot
                         * and causes the server to give a bugged GameMode to the Player that is not
                         * obtainable
                         * by the API itself only via NMS
                         */
                        player.setGameMode(GameMode.SPECTATOR);
                    } catch (Exception e) {
                        // ignore this is only to prevent the AsyncCatcher message in the console
                    }
                }).start();
            }, 5L);
        }
    }
}
