package de.teamholy.clutches.listeners;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.player.PlayerEntry;
import de.teamholy.clutches.player.PlayerState;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.npc.NPCBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerJoinQuitListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Clutches.getInstance().getPlayerEntryHandler().values().forEach(all -> {
            if (all.isVanish()) {
                if (!player.hasPermission("teamholy.team")) {
                    player.hidePlayer(all.getPlayer());
                }
            }
        });

        Bukkit.getScheduler().runTaskLater(Clutches.getInstance(), () -> {
            PlayerEntry playerEntry = new PlayerEntry(player);
            Clutches.getInstance().getPlayerEntryHandler().put(player.getUniqueId(), playerEntry);
            playerEntry.setScoreboard();
            playerEntry.performSpawn();
            player.teleport(BukkitCore.getInstance().getLocationManager().getLocation("lobby"));

            //04042384-cf5e-4f58-a128-6a87ede461b4

        }, 1);

        Bukkit.getScheduler().runTaskLater(Clutches.getInstance(), () -> {
            if (BukkitCore.getInstance().getLocationManager().getLocation("playground") != null)
                new NPCBuilder("playground", "§a§lPLAYGROUND", UUID.fromString("04042384-cf5e-4f58-a128-6a87ede461b4"), 50, 10, true, true, BukkitCore.getInstance().getLocationManager().getLocation("playground")).build(player);

            if (BukkitCore.getInstance().getLocationManager().getLocation("reduce") != null)
                new NPCBuilder("reduce", "§f§lREDUCE", UUID.fromString("aa89e99d-843a-4ab2-8221-4fe9bcafd8a8"), 50, 10, true, true, BukkitCore.getInstance().getLocationManager().getLocation("reduce")).build(player);

            if (BukkitCore.getInstance().getLocationManager().getLocation("clutch") != null) //"§d" + Bridge.getInstance().getDiagonal().size() + playerEntry.getLanguage().getTranslationByKey("player")
                new NPCBuilder("clutch", "§f§lCLUTCH", UUID.fromString("765542b0-aa7c-4b9b-818b-36b3d4a4f85f"), 50, 10, true, true, BukkitCore.getInstance().getLocationManager().getLocation("clutch")).build(player);

            if (BukkitCore.getInstance().getLocationManager().getLocation("multireduce") != null) //"§d" + Bridge.getInstance().getDiagonal().size() + playerEntry.getLanguage().getTranslationByKey("player")
                new NPCBuilder("multireduce", "§f§lMULTIREDUCE", UUID.fromString("6d40f495-d796-4244-9f45-964cdd7e685a"), 50, 10, true, true, BukkitCore.getInstance().getLocationManager().getLocation("multireduce")).build(player);

            if (BukkitCore.getInstance().getLocationManager().getLocation("diagonalclutch") != null) //"§d" + Bridge.getInstance().getDiagonal().size() + playerEntry.getLanguage().getTranslationByKey("player")
                new NPCBuilder("diagonalclutch", "§f§lDIAGONAL", UUID.fromString("03c55754-08fc-4a12-a451-e517c89a3f91"), 50, 10, true, true, BukkitCore.getInstance().getLocationManager().getLocation("diagonalclutch")).build(player);

        }, 10);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        PlayerEntry playerEntry = Clutches.getInstance().getPlayerEntryHandler().remove(player.getUniqueId());
        if (playerEntry == null) {
            return;
        }
        if (playerEntry.getPlayerState() == PlayerState.PLAYGROUND) {
            playerEntry.getPlaygroundPlayer().quit();
        } else if (playerEntry.getPlayerState() == PlayerState.INGAME) {
            playerEntry.checkQuit(true);
        }
        playerEntry.getFirstHitDelay().setReceived(false);
        playerEntry.getPlaygroundPlayer().getPlayerTask().stopIfActive();
        playerEntry.saveData();
        playerEntry.leaveSpectator();
    }

}
