package de.teamholy.knockbackffa.listeners;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.npc.NPCBuilder;
import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.enums.PlayerState;
import de.teamholy.knockbackffa.models.DamagedPlayer;
import de.teamholy.knockbackffa.models.MapEntry;
import de.teamholy.knockbackffa.models.PlayerEntry;
import de.teamholy.core.api.utility.Gamemodes;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/* copyright by Yassino */
public class PlayerJoinQuitListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        KnockbackFFA.getInstance().getCacheHandler().getMapEntrys().values().forEach(MapEntry::updateSign);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);

        KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().values().forEach(playerEntry -> {
            if (playerEntry.getPlayerState() == PlayerState.SPECTATE) {
                if (!player.hasPermission("teamholy.team")) {
                    player.hidePlayer(playerEntry.getPlayer());
                }
            }
        });

        Bukkit.getScheduler().runTaskLaterAsynchronously(KnockbackFFA.getInstance(),() -> {
            PlayerEntry playerEntry = new PlayerEntry(player);
            KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().put(player.getUniqueId(),playerEntry);
            Bukkit.getScheduler().runTask(KnockbackFFA.getInstance(), () -> {
                playerEntry.performSpawn();
                player.setGameMode(GameMode.SURVIVAL);
                new NPCBuilder("invsort","§6§lInventory",UUID.fromString("fa44c187-80dd-4171-bb5a-2e694c4c8b4f"),100,10,true,false,BukkitHolyAPI.getInstance().getLocationManager().getLocation("invsort")).build(player);
            });
        },1);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(player.getUniqueId());
        if (playerEntry.getPlayerState() == PlayerState.INGAME) {
            playerEntry.leaveGame();
        }
        if (playerEntry.getTeamEntry() != null) {
            KnockbackFFA.getInstance().getTeamingHandler().leaveFromTeam(playerEntry);
        }

        if (EntityDamageByEntityListener.COMBATLOG.containsKey(playerEntry.getPlayer().getUniqueId())) {
            DamagedPlayer damagedPlayer = EntityDamageByEntityListener.COMBATLOG.remove(playerEntry.getPlayer().getUniqueId());
            if (damagedPlayer.getHitTime() > System.currentTimeMillis()) {
                PlayerEntry killerEntry = damagedPlayer.getDamager();
                Player killer = killerEntry.getPlayer();
                killer.sendMessage(KnockbackFFA.getInstance().getPrefix() + "You killed " + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(player.getUniqueId()) + player.getName() + " §7(§cleave§7)");
                killer.setLevel(killer.getLevel()+1);
                killer.setHealth(20);
                killer.playSound(killer.getLocation(), Sound.NOTE_PLING,2f,2f);

                BukkitHolyAPI.getInstance().getStatsManager().addStat(Gamemodes.KNOCKBACKFFA.toString(),"kills",killer.getUniqueId());
                BukkitHolyAPI.getInstance().getStatsManager().addStat(Gamemodes.KNOCKBACKFFA.toString(),"deaths",player.getUniqueId());

                PlayerDeathListener.killStreak(killer,player);
                killerEntry.updateScoreboard();
            }
        }

        playerEntry.saveData();
        KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().remove(player.getUniqueId());
    }

}
