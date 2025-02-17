package de.teamholy.bedwars.listeners;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.commands.NPCShopCommand;
import de.teamholy.bedwars.enums.GameState;
import de.teamholy.bedwars.model.PlayerEntry;
import de.teamholy.bedwars.task.LobbyTask;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.bukkit.BukkitCore;
import eu.koboo.markup.MarkupAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/* copyright by Yassino */
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (Bedwars.getInstance().getGameState() == GameState.END) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Bedwars.getInstance().getPrefix() + "The games is already ending");
        } else if (Bedwars.getInstance().getGameState() == GameState.LOBBY && Bukkit.getOnlinePlayers().size() == Bedwars.getInstance().getMaxPlayers()) {
            Player player = event.getPlayer();

            if (!player.hasPermission("teamholy.fulljoin")) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Bedwars.getInstance().getPrefix() + "You need at least §6Premium §7and higher to join a full Bedwars game! §ashop.teamholy.de");
                return;
            }

            int i = 0;

            for (Player all : Bukkit.getOnlinePlayers()) {

                if (!all.hasPermission("teamholy.fulljoin")) {
                    all.kickPlayer(Bedwars.getInstance().getPrefix() + "You were kicked by a §6Premium §7player or higher!");
                    event.allow();
                    return;
                }

                i++;

                if (i == Bedwars.getInstance().getMaxPlayers()) {
                    event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, Bedwars.getInstance().getPrefix() + "You can't join the server because everyone has at least §6Premium §7and higher");
                }

            }

        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setLevel(0);
        player.setExp(0);
        int count = Bukkit.getOnlinePlayers().size();

        if (Bedwars.getInstance().getCacheHandler().getPlayerEntries().containsKey(player.getUniqueId())) {
            player.kickPlayer("§cPlease rejoin the server!");
            return;
        }

        PlayerEntry playerEntry = new PlayerEntry(player.getPlayer());
        Bedwars.getInstance().getCacheHandler().getPlayerEntries().put(player.getUniqueId(), new PlayerEntry(player.getPlayer()));
        if (Bedwars.getInstance().getGameState() == GameState.LOBBY) {
            MarkupAPI.updateNameTag(player);
            Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(), () -> Bukkit.broadcastMessage(Bedwars.getInstance().getPrefix() + BukkitCore.getInstance().getPlayerColor(player.getUniqueId(), true) + player.getDisplayName() + " §7has joined §8(§a" + count + "§8/§c" + Bedwars.getInstance().getMaxPlayers() + "§8)"), 7);

            playerEntry.performSpawn();

            if (Bukkit.getOnlinePlayers().size() == Bedwars.getInstance().getMaxPlayers() && LobbyTask.count > 10) {
                LobbyTask.count = 10;
            }

        } else if (Bedwars.getInstance().getGameState() == GameState.INGAME) {
            Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(), () -> {
                if (NPCShopCommand.NPCSHOP) {
                    playerEntry.setNpcShops();
                }
                playerEntry.setSpectator();
            }, 5);
            player.teleport(BukkitCore.getInstance().getLocationManager().getLocation("lobby"));
        }
        Bedwars.getInstance().updateData();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId());
        if (Bedwars.getInstance().getGameState() == GameState.LOBBY) {
            Bukkit.broadcastMessage(Bedwars.getInstance().getPrefix() + BukkitCore.getInstance().getPlayerColor(player.getUniqueId(), true) + player.getName() + " §7has left §8(§a" + (Bukkit.getOnlinePlayers().size() - 1) + "§8/§c" + Bedwars.getInstance().getMaxPlayers() + "§8)");
            if (Bedwars.getInstance().getInventoryHandler().getGoldVotingInventory().getWithoutGold().remove(player) || Bedwars.getInstance().getInventoryHandler().getGoldVotingInventory().getWithGold().remove(player)) {
                Bedwars.getInstance().getInventoryHandler().getGoldVotingInventory().removingVotes(player);
                Bedwars.getInstance().getInventoryHandler().getGoldVotingInventory().updateInventory();
            }

            if (playerEntry.getTeamEntry() != null) {
                playerEntry.getTeamEntry().getPlayers().remove(player);
                Bedwars.getInstance().getInventoryHandler().getTeamSelectInventory().updateInventory();
            }

            if (playerEntry.getVotedMap() != null) {
                playerEntry.getVotedMap().removeVote();
            }

            if ((Bukkit.getOnlinePlayers().size() - 1) == 0 && Bedwars.getInstance().getForceMap() != null) {
                Bedwars.getInstance().setForceMap(null);
                Bukkit.getLogger().info("Forcemap RESET");
            }

            if (Wrapper.getInstance().getCurrentServiceInfoSnapshot().getConfiguration().getGroups()[0].equalsIgnoreCase("BWC2x1") && (Bukkit.getOnlinePlayers().size() - 1) == 1) {
                Bukkit.getOnlinePlayers().forEach(player1 -> player1.kickPlayer(Bedwars.getInstance().getPrefix() + "You were kicked because " + BukkitCore.getInstance().getPlayerColor(player.getUniqueId(), true) + player.getName() + " §7left the game!"));
            }

        } else if (Bedwars.getInstance().getGameState() == GameState.INGAME) {

            if (Bedwars.getInstance().getIngamePlayers().contains(playerEntry)) {
                EntityDamageEvent entityDamageEvent = player.getLastDamageCause();
                PlayerEntry damager = null;

                if (entityDamageEvent != null && entityDamageEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) entityDamageEvent;
                    if (entityDamageByEntityEvent.getDamager().getType() == EntityType.PLAYER) {
                        damager = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(entityDamageByEntityEvent.getDamager().getUniqueId());
                    }
                }

                if (damager == null) {
                    Bukkit.broadcastMessage(Bedwars.getInstance().getPrefix() + playerEntry.getTeamEntry().getColorCode() + player.getName() + " §7has left");
                } else {
                    Bukkit.broadcastMessage(Bedwars.getInstance().getPrefix() + playerEntry.getTeamEntry().getColorCode() + player.getName() + " §7has left §8(" + damager.getTeamEntry().getColorCode() + damager.getPlayer().getName() + " §7got the kill§8)");
                    BukkitCore.getInstance().getStatsManager().addStat(Bedwars.MODE.toString(), "kills", damager.getPlayer().getUniqueId());
                    damager.setKills(damager.getKills() + 1);
                    BukkitCore.getAPI().getCoinManager().addCoins(damager.getPlayer().getUniqueId(), 10, true);
                    damager.getPlayer().playSound(damager.getPlayer().getLocation(), Sound.LEVEL_UP, 1, 1);
                }

                BukkitCore.getInstance().getStatsManager().addStat(Bedwars.MODE.toString(), "deaths", player.getUniqueId());
                Bedwars.getInstance().getIngamePlayers().remove(playerEntry);
                playerEntry.checkTeams(null);
                playerEntry.checkWin();
                playerEntry.setDead(true);
                Bedwars.getInstance().getCacheHandler().getPlayerEntries().values().forEach(PlayerEntry::updateScoreboard);
            } else Bedwars.getInstance().getSpectatePlayers().remove(playerEntry);

        }
        if (Bedwars.isRushMode()) playerEntry.saveData();
        Bedwars.getInstance().updateMotd();
        Bedwars.getInstance().getCacheHandler().getPlayerEntries().remove(player.getUniqueId());
    }
}
