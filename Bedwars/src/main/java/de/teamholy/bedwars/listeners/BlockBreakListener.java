package de.teamholy.bedwars.listeners;

import de.teamholy.api.manager.StatsManager;
import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.enums.GameState;
import de.teamholy.bedwars.model.TeamEntry;
import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.bedwars.model.PlayerEntry;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.api.utility.TrophieLeague;
import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/* copyright by Yassino */
public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId());

        if (player.getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(false);
            return;
        }

        if (Bedwars.getInstance().getGameState() == GameState.INGAME) {

            if (Bedwars.getInstance().getPlacedBlocks().contains(event.getBlock().getLocation())) {
                Bedwars.getInstance().getPlacedBlocks().remove(event.getBlock().getLocation());
                if (event.getBlock().getType() == Material.WEB) {
                    event.getBlock().setType(Material.AIR);
                }
                return;
            }

            for (TeamEntry teamEntry : Bedwars.getInstance().getCacheHandler().getTeamEntries()) {

                if (event.getBlock().getLocation().distance(teamEntry.getBed()) < 3 && event.getBlock().getType() == Material.BED_BLOCK) {

                    if (teamEntry.getPlayers().contains(player)) {
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "You cannot destroy your own bed!");
                        event.setCancelled(true);
                        return;
                    }

                    if (teamEntry.isHasBed()) {
                        teamEntry.setHasBed(false);

                        Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(), () -> {
                            for (Entity ent : Bukkit.getWorld(event.getBlock().getWorld().getName()).getEntities()) {
                                if (ent.getLocation().distance(event.getBlock().getLocation()) < 7.0D && ent.getType() == EntityType.DROPPED_ITEM) {
                                    Item e = (Item) ent;
                                    if (e.getItemStack().getType() == Material.BED || e.getItemStack().getType() == Material.BED_BLOCK)
                                        ent.remove();
                                }
                            }
                        }, 1);

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            all.sendMessage(Bedwars.getInstance().getPrefix() + "The bed of " + teamEntry.getColorCode() + teamEntry.getName() + "§7 was destroyed by " + playerEntry.getTeamEntry().getColorCode() + player.getName());
                            all.playSound(all.getLocation(), Sound.WITHER_DEATH, 1f, 1f);
                            Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(all.getUniqueId()).updateScoreboard();
                        }

                        int difference = (int) teamEntry.getAllPlayers().stream().mapToInt(PlayerEntry::getAlltimeTrophies).count() / teamEntry.getSize();

                        int killerTrophies = BukkitHolyAPI.getInstance().getStatsManager().handleTrophie(player.getUniqueId(), Bedwars.MODE.toString(), StatsManager.TrophieAdjustType.PLUS,
                                TrophieLeague.calculateRange(difference - playerEntry.getAlltimeTrophies(),3,9));



                        player.sendTitle("","§a+" + killerTrophies + " §6trophies");

                        playerEntry.setBeds(playerEntry.getBeds() + 1);
                        playerEntry.updateScoreboard();
                        BukkitCore.getAPI().getCoinManager().addCoins(player.getUniqueId(),20,true);
                        BukkitHolyAPI.getInstance().getStatsManager().addStat(Bedwars.MODE.toString(),"destroyed_beds", player.getUniqueId());

                        for (Player teamPlayers : teamEntry.getPlayers()) {
                            teamPlayers.sendTitle("§cYour bed", "§cwas §4destroyed!");
                            teamPlayers.playSound(teamPlayers.getLocation(), Sound.ANVIL_BREAK, 20f, 20f);
                            teamPlayers.sendTitle("","§c-" +
                                    BukkitHolyAPI.getInstance().getStatsManager().handleTrophie(teamPlayers.getUniqueId(), Bedwars.MODE.toString(), StatsManager.TrophieAdjustType.MINUS,
                                            TrophieLeague.calculateRange(difference - playerEntry.getAlltimeTrophies(),2,8))
                                    + " §6trophies");

                        }

                        Bedwars.getInstance().updateData();
                        return;
                    } else {
                        event.setCancelled(true);
                    }
                }

            }
            event.setCancelled(true);

        }

    }

    @EventHandler
    public void onBreakObsidian(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.ENDER_CHEST) {
            event.setCancelled(true);
            event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
        }
    }


}
