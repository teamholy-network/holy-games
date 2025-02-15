package de.teamholy.knockbackffa.listeners;

import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.enums.PlayerState;
import de.teamholy.knockbackffa.models.DamagedPlayer;
import de.teamholy.knockbackffa.models.PlayerEntry;
import de.teamholy.knockbackffa.managers.ActiveEnderPearlManager;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.HashMap;
import java.util.UUID;

/* copyright by Yassino */
public class EntityDamageByEntityListener implements Listener {

    public static final HashMap<UUID, DamagedPlayer> COMBATLOG = new HashMap<>();

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))  return;
        Player player = (Player) event.getEntity();
        // Cancel void damage if an enderpearl is still active
        if (event.getCause() == DamageCause.VOID && ActiveEnderPearlManager.hasActive(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(player.getUniqueId());
        if (playerEntry.getActiveMap() == null) return;
        if (player.getLocation().getY() > playerEntry.getActiveMap().getSpawnHight()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamae(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getDamager().getType() != EntityType.PLAYER) return;
            if (event.getEntity().getType() == EntityType.ARMOR_STAND) {
                event.setCancelled(true);
                return;
            }
            PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(event.getEntity().getUniqueId());
            PlayerEntry targetEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(event.getDamager().getUniqueId());
            if (playerEntry.getPlayerState() == PlayerState.LOBBY || playerEntry.getPlayerState() == PlayerState.SPECTATE) {
                event.setCancelled(true);
            } else if (playerEntry.getPlayerState() == PlayerState.INGAME) {
                if (playerEntry.getPlayer().getLocation().getBlockY() > playerEntry.getActiveMap().getSpawnHight()) {
                    event.setCancelled(true);
                }

                if (targetEntry.getTeamEntry() == null || playerEntry.getTeamEntry() == null) {
                    COMBATLOG.put(playerEntry.getPlayer().getUniqueId(),new DamagedPlayer(targetEntry,playerEntry,System.currentTimeMillis() + 15000));
                    return;
                }

                if (targetEntry.getTeamEntry() == playerEntry.getTeamEntry()) {
                    event.setCancelled(true);
                } else {
                    COMBATLOG.put(playerEntry.getPlayer().getUniqueId(),new DamagedPlayer(targetEntry,playerEntry,System.currentTimeMillis() + 15000));
                }

            }
        }
    }

    @EventHandler
    public void onArrowDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow)) return;
        Arrow arrow = (Arrow) event.getDamager();
        if (!(arrow.getShooter() instanceof Player)) return;
        PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(event.getEntity().getUniqueId());
        PlayerEntry attackerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(((Player) arrow.getShooter()).getUniqueId());
        if (playerEntry == attackerEntry) return;
        if (playerEntry.getPlayerState() == PlayerState.SPECTATE) {
            event.setCancelled(true);
            return;
        }
        if (playerEntry.getTeamEntry() == null || attackerEntry.getTeamEntry() == null) {
            COMBATLOG.put(playerEntry.getPlayer().getUniqueId(),new DamagedPlayer(attackerEntry,playerEntry,System.currentTimeMillis() + 15000));
            return;
        }
        if (playerEntry.getTeamEntry() == attackerEntry.getTeamEntry()) {
            event.setCancelled(true);
        } else {
            COMBATLOG.put(playerEntry.getPlayer().getUniqueId(),new DamagedPlayer(attackerEntry,playerEntry,System.currentTimeMillis() + 15000));
        }

    }

}
