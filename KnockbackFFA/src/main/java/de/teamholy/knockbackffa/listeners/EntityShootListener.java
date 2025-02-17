package de.teamholy.knockbackffa.listeners;

import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.enums.BowTrail;
import de.teamholy.knockbackffa.managers.ActiveEnderPearlManager;
import de.teamholy.knockbackffa.models.PlayerEntry;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/* copyright by Yassino */
public class EntityShootListener implements Listener {

    private final ArrayList<UUID> arrows = new ArrayList<>();

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getProjectile().getType() == EntityType.ARROW) {
                Player player = (Player) event.getEntity();
                PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(player.getUniqueId());
                World world = player.getWorld();
                if (player.getLocation().getY() > playerEntry.getActiveMap().getSpawnHight()) {
                    event.getProjectile().remove();
                    return;
                }
                if (playerEntry.getBowTrail() != BowTrail.NONE) {
                    arrows.add(event.getProjectile().getUniqueId());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!arrows.contains(event.getProjectile().getUniqueId())) {
                                cancel();
                            }

                            if (!player.isOnline() || player.getWorld() != world || world.getPlayers().isEmpty()) {
                                arrows.remove(event.getProjectile().getUniqueId());
                                cancel();
                            }

                            List<Player> players = new ArrayList<>();
                            KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().forEach((uuid, playerEntry1) -> {
                                if (playerEntry1.isSeeEffects() && world == playerEntry1.getPlayer().getWorld()) players.add(playerEntry1.getPlayer());
                            });

                            if (!players.isEmpty()) playerEntry.getBowTrail().getParticleEffect().display(0.0F, 0.0F, 0.0F, 0.1F, 1, event.getProjectile().getLocation(),players);
                        }
                    }.runTaskTimer(KnockbackFFA.getInstance(),0,10);
                }
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            EnderPearl pearl = (EnderPearl) event.getEntity();
            if (pearl.getShooter() instanceof Player) {
                Player player = (Player) pearl.getShooter();
                ActiveEnderPearlManager.add(player.getUniqueId(), pearl);
            }
        }
    }

    @EventHandler
    public void onLand(ProjectileHitEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            EnderPearl pearl = (EnderPearl) event.getEntity();
            if(pearl.getShooter() instanceof Player) {
                Player shooter = (Player) pearl.getShooter();
                ActiveEnderPearlManager.remove(shooter.getUniqueId());
            }
        }
        if (event.getEntity() instanceof Arrow) {
            arrows.remove(event.getEntity().getUniqueId());
        }
    }

}
