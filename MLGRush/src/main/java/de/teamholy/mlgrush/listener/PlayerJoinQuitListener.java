package de.teamholy.mlgrush.listener;

import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.event.CachedPlayerJoinEvent;
import de.teamholy.core.bukkit.npc.NPCBuilder;
import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.player.PlayerEntry;
import de.teamholy.mlgrush.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;

public class PlayerJoinQuitListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(CachedPlayerJoinEvent event) {
        Player player = event.getCachedBukkitPlayer().getPlayer();
        Bukkit.getScheduler().runTaskLaterAsynchronously(MLGRush.getInstance(), () -> {
            PlayerEntry playerEntry = new PlayerEntry(player);
            MLGRush.getInstance().getPlayerEntryHandler().put(player.getUniqueId(),playerEntry);

            Bukkit.getScheduler().runTask(MLGRush.getInstance(), () -> {
                playerEntry.performSpawn();
                for (PotionEffect potionEffectType : playerEntry.getPlayer().getActivePotionEffects())
                    playerEntry.getPlayer().removePotionEffect(potionEffectType.getType());
                if (MLGRush.getInstance().getLobby() != null)
                    player.teleport(MLGRush.getInstance().getLobby());

                new NPCBuilder("queue4x1","§6§lQueue 4x1",UUID.fromString("1cff8006-9714-4231-997c-7b37a69dfff0"),100,20,true,true, BukkitCore.getInstance().getLocationManager().getLocation("spectate")).build(player);
                new NPCBuilder("queue","§6§lQueue 2x1",UUID.fromString("03c55754-08fc-4a12-a451-e517c89a3f91"),100,20,true,true,BukkitCore.getInstance().getLocationManager().getLocation("queue")).build(player);
            });


        },1);

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        PlayerEntry playerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(player.getUniqueId());
        if (playerEntry == null)
            return;
        if (playerEntry.getPlayerState() == PlayerState.INGAME) {
            playerEntry.getGameEntry().finishGame( false );
        }

        if (MLGRush.getInstance().getQueueHandler().getQueue().containsKey(playerEntry)) {
            MLGRush.getInstance().getQueueHandler().getQueue().remove(playerEntry);
            MLGRush.getInstance().getQueueHandler().updateQueue();
        }
        playerEntry.saveData();
        MLGRush.getInstance().getPlayerEntryHandler().remove(player.getUniqueId());
    }

}
