package de.teamholy.clutches.listeners;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.npc.models.NPCEntry;
import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.player.PlayerEntry;
import de.teamholy.clutches.player.PlayerState;
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
        Bukkit.getScheduler().runTaskLater(Clutches.getInstance(), () -> {
            PlayerEntry playerEntry = new PlayerEntry(player);
            Clutches.getInstance().getPlayerEntryHandler().put(player.getUniqueId(), playerEntry);
            playerEntry.setScoreboard();
            playerEntry.performSpawn();
            player.teleport(BukkitHolyAPI.getInstance().getLocationManager().getLocation("lobby"));

            //04042384-cf5e-4f58-a128-6a87ede461b4
            if (player.hasPermission("teamholy.beta"))
            BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()).getNpcs().put("playground", new NPCEntry("§a§lPLAYGROUND", UUID.fromString("04042384-cf5e-4f58-a128-6a87ede461b4"), BukkitHolyAPI.getInstance().getLocationManager().getLocation("playground"), 100, 20, true, true).setPlayer(player));
            if (BukkitHolyAPI.getInstance().getLocationManager().getLocation("reduce") != null)
                BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()).getNpcs().put("reduce", new NPCEntry("§f§lREDUCE", UUID.fromString("aa89e99d-843a-4ab2-8221-4fe9bcafd8a8"), BukkitHolyAPI.getInstance().getLocationManager().getLocation("reduce"), 100, 20, true, true).setPlayer(player));
            if (BukkitHolyAPI.getInstance().getLocationManager().getLocation("clutch") != null) //"§d" + Bridge.getInstance().getDiagonal().size() + playerEntry.getLanguage().getTranslationByKey("player")
                BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()).getNpcs().put("clutch", new NPCEntry("§f§lCLUTCH", UUID.fromString("765542b0-aa7c-4b9b-818b-36b3d4a4f85f"), BukkitHolyAPI.getInstance().getLocationManager().getLocation("clutch"), 100, 20, true, true).setPlayer(player));
            if (BukkitHolyAPI.getInstance().getLocationManager().getLocation("multireduce") != null) //"§d" + Bridge.getInstance().getDiagonal().size() + playerEntry.getLanguage().getTranslationByKey("player")
                BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()).getNpcs().put("multireduce", new NPCEntry("§f§lMULTIREDUCE", UUID.fromString("6d40f495-d796-4244-9f45-964cdd7e685a"), BukkitHolyAPI.getInstance().getLocationManager().getLocation("multireduce"), 100, 20, true,true).setPlayer(player));
            if (BukkitHolyAPI.getInstance().getLocationManager().getLocation("diagonalclutch") != null) //"§d" + Bridge.getInstance().getDiagonal().size() + playerEntry.getLanguage().getTranslationByKey("player")
                BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()).getNpcs().put("diagonalclutch", new NPCEntry("§f§lDIAGONAL", UUID.fromString("03c55754-08fc-4a12-a451-e517c89a3f91"), BukkitHolyAPI.getInstance().getLocationManager().getLocation("diagonalclutch"), 100, 20, true,true).setPlayer(player));

        }, 1);
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
        }
        playerEntry.getPlaygroundPlayer().getPlayerTask().stopIfActive();
        playerEntry.checkQuit();
        playerEntry.saveData();
        playerEntry.leaveSpectator();
    }

}
