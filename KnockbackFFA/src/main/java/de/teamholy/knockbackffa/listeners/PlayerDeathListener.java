package de.teamholy.knockbackffa.listeners;


import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.manager.StatsManager;
import de.teamholy.core.api.utility.TrophieLeague;
import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.models.PlayerEntry;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.MusicEffect;
import de.slikey.effectlib.effect.TornadoEffect;
import de.slikey.effectlib.util.ParticleEffect;
import de.slikey.effectlib.util.RandomUtils;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.List;

/* copyright by Yassino */
public class PlayerDeathListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.setDroppedExp(0);
        Player player = event.getEntity();
        Player killer = player.getKiller();
        PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(player.getUniqueId());
        event.setDeathMessage(null);
        BukkitHolyAPI.getInstance().getStatsManager().addStat(Gamemodes.KNOCKBACKFFA.toString(),"deaths",player.getUniqueId());

        Bukkit.getScheduler().runTaskLater(KnockbackFFA.getInstance(), () -> {
            if (player.isOnline()) {
                player.spigot().respawn();
                playerEntry.updateScoreboard();
                player.playSound(player.getLocation(),Sound.ANVIL_BREAK,50,50);
            }
        },  1L);

        if (killer == null || killer == player) {
            player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "You died! §8(§c-2 §6trophies§8)");
            player.setLevel(0);
            BukkitHolyAPI.getInstance().getStatsManager().handleTrophie(player.getUniqueId(),Gamemodes.KNOCKBACKFFA.toString(), StatsManager.TrophieAdjustType.MINUS,2);
            playerEntry.setAlltimeTrophies(playerEntry.getAlltimeTrophies() -2);
        } else {
            PlayerEntry killerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(killer.getUniqueId());

            int difference = playerEntry.getAlltimeTrophies() - killerEntry.getAlltimeTrophies();

            int killerTrophies = BukkitHolyAPI.getInstance().getStatsManager().handleTrophie(killer.getUniqueId(),Gamemodes.KNOCKBACKFFA.toString(), StatsManager.TrophieAdjustType.PLUS,
                    TrophieLeague.calculateRange(difference,1,4));

            int playerTrophies = BukkitHolyAPI.getInstance().getStatsManager().handleTrophie(player.getUniqueId(),Gamemodes.KNOCKBACKFFA.toString(), StatsManager.TrophieAdjustType.MINUS,
                    TrophieLeague.calculateRange(difference,1,4));

            killerEntry.setAlltimeTrophies(killerEntry.getAlltimeTrophies() + killerTrophies);
            playerEntry.setAlltimeTrophies(playerEntry.getAlltimeTrophies() - playerTrophies);

            killer.sendMessage(KnockbackFFA.getInstance().getPrefix() + "You killed " + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(player.getUniqueId()) + player.getName()
                    + " §8(§a+" + killerTrophies

                    + " §6trophies§8)"
            );
            String healthString = getHealthColor(killer.getHealth()) + String.valueOf(Math.round(killer.getHealth() / 2D));
            player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "You have been killed by " + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(killer.getUniqueId()) + killer.getName()
                    + " §8(" + healthString + "§c❤§8)"+
                    " §8(§c-" + playerTrophies + " §6trophies§8)"
            );
            killer.setLevel(killer.getLevel() + 1);
            killer.setHealth(20);
            if (killer.getLocation().getBlockY() < killerEntry.getActiveMap().getSpawnHight()) {
                killerEntry.setIngameItems();
            }
            killer.playSound(killer.getLocation(), Sound.NOTE_PLING, 2f, 2f);
            BukkitHolyAPI.getInstance().getStatsManager().addStat(Gamemodes.KNOCKBACKFFA.toString(),"kills",killer.getUniqueId());
            killStreak(killer,player);
            killerEntry.updateScoreboard();
            EntityDamageByEntityListener.COMBATLOG.remove(player.getUniqueId());
        }
    }

    public static void killStreak(Player killer, Player player) {
        PlayerEntry killerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(killer.getUniqueId());
        if (killer.getLevel() % 5 == 0) {
            BukkitCore.getAPI().getCoinManager().addCoins(killer.getUniqueId(),20,true);
            Bukkit.getOnlinePlayers().forEach(all -> {
                all.sendMessage(KnockbackFFA.getInstance().getPrefix() + "The Player " + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(killer.getUniqueId()) + killer.getName() + " §7has made §c" + killer.getLevel() + " §7kills in a row!");
                all.getPlayer().playSound(all.getPlayer().getLocation(), Sound.ENDERDRAGON_GROWL,50,50);
            });
            Location location = new Location(killer.getPlayer().getLocation().getWorld(), killer.getPlayer().getLocation().getX(), killer.getPlayer().getLocation().getY() + 1.0D, killer.getPlayer().getLocation().getZ());
            List<Player> players = new ArrayList<>();
            KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().forEach((uuid, playerEntry1) -> {
                if (playerEntry1.isSeeEffects() && player.getWorld() == playerEntry1.getPlayer().getWorld()) players.add(playerEntry1.getPlayer());
            });
            if (!players.isEmpty()) {
                switch (killerEntry.getKillStreakEffect()) {
                    case WATER:
                        ParticleEffect.WATER_WAKE.display(0.5F, 0.5F, 0.5F, 0.1F, 200, location, players);
                        ParticleEffect.WATER_DROP.display(0.5F, 0.5F, 0.5F, 0.1F, 200, location, players);
                        ParticleEffect.WATER_DROP.display(0.5F, 0.5F, 0.5F, 0.1F, 200, location, players);
                        ParticleEffect.WATER_SPLASH.display(0.5F, 0.5F, 0.5F, 0.1F, 200, location, players);
                        ParticleEffect.DRIP_WATER.display(0.5F, 0.5F, 0.5F, 0.1F, 200, location, players);
                        ParticleEffect.DRIP_WATER.display(0.5F, 0.5F, 0.5F, 0.1F, 200, location, players);
                        break;
                    case LIGHTNING:
                        location.getWorld().strikeLightningEffect(location);
                        break;
                    case TORNADO:
                        TornadoEffect tornadoEffect = new TornadoEffect(KnockbackFFA.getInstance().getEffectManager());
                        tornadoEffect.setLocation(location);
                        tornadoEffect.period = 1;
                        tornadoEffect.tornadoHeight = 2.0F;
                        tornadoEffect.maxTornadoRadius = 1.5F;
                        tornadoEffect.type = EffectType.INSTANT;
                        tornadoEffect.start();
                        break;
                    case FIRE:
                        ParticleEffect.FLAME.display(0.5F, 0.5F, 0.5F, 1.1F, 100, location, players);
                        ParticleEffect.LAVA.display(0.5F, 0.5F, 0.5F, 1.1F, 100, location, players);
                        ParticleEffect.LAVA.display(0.5F, 0.5F, 0.5F, 1.1F, 100, location, players);
                        ParticleEffect.DRIP_LAVA.display(0.5F, 0.5F, 0.5F, 1.1F, 100, location, players);
                        ParticleEffect.DRIP_LAVA.display(0.5F, 0.5F, 0.5F, 1.1F, 100, location, players);
                        break;
                    case MUSIC:
                        ParticleEffect.NOTE.display(0.5F, 0.5F, 0.5F, 1.1F, 50, location, players);
                        ParticleEffect.NOTE.display(0.5F, 0.5F, 0.5F, 1.1F, 50, location, players);
                        ParticleEffect.NOTE.display(0.5F, 0.5F, 0.5F, 1.1F, 50, location, players);
                        MusicEffect musicEffect = new MusicEffect(KnockbackFFA.getInstance().getEffectManager());
                        musicEffect.setLocation(location);
                        musicEffect.setTargetLocation(location);
                        musicEffect.start();
                        break;
                    case EXPLOSION:
                        int amount = 25;
                        float speed = 0.5F;
                        Sound sound = Sound.EXPLODE;
                        location.getWorld().playSound(location, sound, 4.0F, (1.0F + (RandomUtils.random.nextFloat() - RandomUtils.random.nextFloat()) * 0.2F) * 0.7F);
                        ParticleEffect.EXPLOSION_HUGE.display(0.0F, -0.5F, 0.0F, speed, amount,location,players);
                        ParticleEffect.EXPLOSION_NORMAL.display(0.0F, -0.5F, 0.0F, speed, amount,location,players);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(player.getUniqueId());
        if (playerEntry.getActiveMap() == null) {
            playerEntry.performSpawn();
            return;
        }
        event.setRespawnLocation(playerEntry.getActiveMap().getSpawn());
        player.getInventory().clear();
        player.getInventory().setItem(4,new ItemBuilder(Material.MAGMA_CREAM).setName("§8» §6Back to lobby §8(§7rightclick§8)").build());
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
