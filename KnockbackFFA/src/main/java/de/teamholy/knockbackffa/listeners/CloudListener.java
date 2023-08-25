package de.teamholy.knockbackffa.listeners;

import de.teamholy.api.events.bukkit.CloudChannelListenEvent;
import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.enums.PlayerState;
import de.teamholy.knockbackffa.models.PlayerEntry;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/* copyright by Yassino */
public class CloudListener implements Listener {

    @EventHandler
    public void onCloudChannel(CloudChannelListenEvent event) {
        if (event.getMessage().equalsIgnoreCase("report")) {
            Bukkit.getScheduler().runTaskLater(KnockbackFFA.getInstance(),() -> {
                PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(event.getData().get("jumperUuid", UUID.class));
                PlayerEntry targetEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(event.getData().get("targetUuid", UUID.class));
                if (playerEntry == null) return;

                if (targetEntry == null) {
                    playerEntry.getPlayer().sendMessage(KnockbackFFA.getInstance().getPrefix() + "Dieser Spieler ist nicht mehr auf dem Server");
                    return;
                }

                if (targetEntry.getPlayerState() != PlayerState.INGAME) {
                    playerEntry.getPlayer().sendMessage(KnockbackFFA.getInstance().getPrefix() + "Dieser Spieler ist nicht am spielen");
                    return;
                }

                Player player = playerEntry.getPlayer();

                if (playerEntry.getPlayerState() == PlayerState.INGAME) playerEntry.leaveGame();
                playerEntry.setPlayerState(PlayerState.SPECTATE);
                player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "Du bist nun im Vanish");
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (!all.hasPermission("teamholy.team")) {
                        all.hidePlayer(player);
                    }
                }
                player.setAllowFlight(true);
                player.setFlying(true);
                player.getInventory().clear();
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,99999,2));
                player.getInventory().setItem(4,new ItemBuilder(Material.COMPASS).setName("§8» §6Vanish Menü").build());

                Bukkit.getScheduler().runTaskLater(KnockbackFFA.getInstance(),() -> {
                    if (targetEntry.getPlayer().isOnline() && targetEntry.getPlayer() != null) {
                        player.teleport(targetEntry.getPlayer());
                    }
                },1);

            },5);
        }


    }
}
