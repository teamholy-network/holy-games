package de.teamholy.sgffa.listeners;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.manager.StatsManager;
import de.teamholy.core.api.utility.TrophieLeague;
import de.teamholy.sgffa.SGFFA;
import de.teamholy.sgffa.models.PlayerEntry;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bukkit.BukkitCore;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

import java.util.Random;

/* copyright by Yassino */
public class PlayerDeathRespawnListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.setDroppedExp(0);
        Player player = event.getEntity();
        Player killer = player.getKiller();
        PlayerEntry playerEntry = SGFFA.getInstance().getCacheHandler().getPlayerEntryHashMap().get(player.getUniqueId());
        event.setDeathMessage(null);
        BukkitHolyAPI.getInstance().getStatsManager().addStat(Gamemodes.SGFFA.toString(),"deaths",player.getUniqueId());

        Bukkit.getScheduler().runTaskLater(SGFFA.getInstance(), () -> {
            if (player.isOnline() && player != null) {
                (((CraftPlayer)player).getHandle()).playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
                playerEntry.updateScoreboard();
                player.playSound(player.getLocation(), Sound.ANVIL_BREAK,50,50);
            }
        },  1L);

        if (killer == null || killer == player) {
            player.sendMessage(SGFFA.PREFIX + "You died! §8(§c-2 §6trophies§8)");
            player.setLevel(0);
            BukkitHolyAPI.getInstance().getStatsManager().handleTrophie(player.getUniqueId(),Gamemodes.SGFFA.toString(), StatsManager.TrophieAdjustType.MINUS,2);
            playerEntry.setAlltimeTrophies(playerEntry.getAlltimeTrophies() -2);
            return;
        } else {


            PlayerEntry killerEntry = SGFFA.getInstance().getCacheHandler().getPlayerEntryHashMap().get(killer.getUniqueId());

            int difference = playerEntry.getAlltimeTrophies() - killerEntry.getAlltimeTrophies();

            int killerTrophies = BukkitHolyAPI.getInstance().getStatsManager().handleTrophie(killer.getUniqueId(),Gamemodes.SGFFA.toString(), StatsManager.TrophieAdjustType.PLUS,
                    TrophieLeague.calculateRange(difference,3,5));

            int playerTrophies = BukkitHolyAPI.getInstance().getStatsManager().handleTrophie(player.getUniqueId(),Gamemodes.SGFFA.toString(), StatsManager.TrophieAdjustType.MINUS,
                    TrophieLeague.calculateRange(difference,1,4));

            killerEntry.setAlltimeTrophies(killerEntry.getAlltimeTrophies() + killerTrophies);
            playerEntry.setAlltimeTrophies(playerEntry.getAlltimeTrophies() - playerTrophies);

            killer.sendMessage(SGFFA.PREFIX + "You killed " + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(player.getUniqueId()) + player.getName()
            + " §8(§a+" + killerTrophies

                             + " §6trophies§8)"
            );
            String healthString = getHealthColor(killer.getHealth()) + String.valueOf(Math.round(killer.getHealth() / 2D));
            player.sendMessage(SGFFA.PREFIX + "You have been killed by " + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(killer.getUniqueId()) + killer.getName()
                    + " §8(" + healthString + "§c❤§8)"+
                    " §8(§c-" + playerTrophies + " §6trophies§8)"
            );
            killer.setLevel(killer.getLevel()+1);
            killer.setHealth(20);

            killer.playSound(killer.getLocation(),Sound.NOTE_PLING,2f,2f);
            BukkitHolyAPI.getInstance().getStatsManager().addStat(Gamemodes.SGFFA.toString(),"kills",killer.getUniqueId());
            if (killer.getLevel() % 5 == 0) {
                BukkitCore.getAPI().getCoinManager().addCoins(killer.getUniqueId(),30,true);
                Bukkit.getOnlinePlayers().forEach(all -> {
                    all.sendMessage(SGFFA.PREFIX + "The Player " + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(killer.getUniqueId()) + killer.getName() + " §7has made §c" + killer.getLevel() + " §7kills in a row!");
                    all.getPlayer().playSound(all.getPlayer().getLocation(), Sound.ENDERDRAGON_GROWL,50,50);
                });
            }
            killerEntry.updateScoreboard();
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerEntry playerEntry = SGFFA.getInstance().getCacheHandler().getPlayerEntryHashMap().get(player.getUniqueId());
        playerEntry.performSpawn();
        player.setVelocity(new Vector(0,0,0));
    }

    private ChatColor getHealthColor(double health) {
        if(health >= 16D) {
            return ChatColor.GREEN;
        }
        if(health >= 14D) {
            return ChatColor.YELLOW;
        }
        if(health >= 9D) {
            return ChatColor.GOLD;
        }
        if(health >= 5D) {
            return ChatColor.RED;
        }
        return ChatColor.DARK_RED;
    }
}
