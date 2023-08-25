package de.teamholy.bedwars.listeners;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.bedwars.commands.NPCShopCommand;
import de.teamholy.bedwars.model.PlayerEntry;
import de.teamholy.bedwars.model.TeamEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/* copyright by Yassino */
public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        event.getDrops().clear();
        event.setDroppedExp(0);
        event.setDeathMessage(null);

        PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId());
        TeamEntry teamEntry = playerEntry.getTeamEntry();

        if (!teamEntry.isHasBed()) {
            Bedwars.getInstance().getIngamePlayers().remove(playerEntry);
        }

        try {
            if (killer != null) {
                PlayerEntry killerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(killer.getUniqueId());
                String healthString = getHealthColor(killer.getHealth()) + String.valueOf(Math.round(killer.getHealth() / 2D));
                Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(Bedwars.getInstance().getPrefix() + killerEntry.getTeamEntry().getColorCode() + killer.getName() + " §7killed " + playerEntry.getTeamEntry().getColorCode() + player.getName() + " §8(" + healthString + "§c❤§8)"));
            } else {
                Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(Bedwars.getInstance().getPrefix() + teamEntry.getColorCode() + player.getName() + " §7has died"));
            }
        } catch (Exception ignored) { }

        playerEntry.checkTeams(killer);
        playerEntry.checkWin();

        Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(), () -> {

            if (player.isOnline()) {
                if (NPCShopCommand.NPCSHOP) {
                    playerEntry.setNpcShops();
                }
                player.spigot().respawn();
            }

        },2);

    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId());
        TeamEntry teamEntry = playerEntry.getTeamEntry();
        if (teamEntry.isHasBed()) {
            event.setRespawnLocation(teamEntry.getSpawn());
        } else {
            playerEntry.setSpectator();
            playerEntry.checkWin();
            event.setRespawnLocation(BukkitHolyAPI.getInstance().getLocationManager().getLocation("lobby"));
        }
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
