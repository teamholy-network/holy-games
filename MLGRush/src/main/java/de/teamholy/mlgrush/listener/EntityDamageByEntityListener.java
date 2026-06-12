package de.teamholy.mlgrush.listener;

import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.enums.GameType;
import de.teamholy.mlgrush.game.GameEntry;
import de.teamholy.mlgrush.player.PlayerEntry;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageByEntityListener implements Listener {

    @EventHandler
    public void onDamageAll(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID))
                event.setCancelled(true);
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE))
                event.setCancelled(true);
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK))
                event.setCancelled(true);
            if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION))
                event.setCancelled(true);
            if (event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION))
                event.setCancelled(true);
            if (event.getCause().equals(EntityDamageEvent.DamageCause.DROWNING))
                event.setCancelled(true);
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALLING_BLOCK))
                event.setCancelled(true);
          player.setHealth(20);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!(event.getDamager() instanceof Player attacker)) {
                return;
            }
            PlayerEntry attackerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(attacker.getUniqueId());
            PlayerEntry playerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(player.getUniqueId());
            // Vorher NPE bei fehlendem Cache-Eintrag (vom Event-Bus geschluckt, Handler brach ab) — gleiches Ergebnis per Early-Return
            if (attackerEntry == null || playerEntry == null) return;
            switch (attackerEntry.getPlayerState()) {
                case INGAME:
                    playerEntry.setGotLastHit(attackerEntry);
                    event.setCancelled(false);
                    event.setDamage(0);
                    break;
                case LOBBY:
                    event.setCancelled(true);
                    if (attacker.getItemInHand().getType() == Material.IRON_SWORD) {
                        if (playerEntry.getChallengedPlayer() != null && playerEntry.getChallengedPlayer() == attackerEntry) {
                            attackerEntry.setChallengedPlayer(null);
                            playerEntry.setChallengedPlayer(null);
                            if (MLGRush.getInstance().getQueueHandler().getQueue().containsKey(playerEntry) || MLGRush.getInstance().getQueueHandler().getQueue().containsKey(attackerEntry)) {
                                MLGRush.getInstance().getQueueHandler().getQueue().remove(playerEntry);
                                MLGRush.getInstance().getQueueHandler().getQueue().remove(attackerEntry);
                                MLGRush.getInstance().getQueueHandler().updateQueue();
                            }
                            attackerEntry.getPlayer().playSound(attackerEntry.getPlayer().getLocation(), Sound.CHEST_OPEN,2f,2f);
                            playerEntry.getPlayer().playSound(playerEntry.getPlayer().getLocation(),Sound.CHEST_OPEN,2f,2f);
                            GameEntry gameEntry = new GameEntry(playerEntry, attackerEntry,null,null);
                            attackerEntry.setGameEntry(gameEntry);
                            playerEntry.setGameEntry(gameEntry);
                            attackerEntry.openMapSelection(GameType.TWOxONE);
                            playerEntry.openMapSelection(GameType.TWOxONE);
                        }
                        else {
                            if (attackerEntry.getChallengedPlayer() == playerEntry) {
                                attacker.sendMessage(MLGRush.getInstance().getPrefix() + "You already invited this player");
                                attacker.playSound(attacker.getLocation(), Sound.NOTE_BASS,2f,2f);
                                return;
                            } else {
                                attackerEntry.setChallengedPlayer(playerEntry);
                                attacker.playSound(attacker.getLocation(), Sound.NOTE_STICKS,2f,2f);
                                player.playSound(player.getLocation(), Sound.NOTE_STICKS,2f,2f);
                                attacker.sendMessage(MLGRush.getInstance().getPrefix() + "You challenged " + BukkitCore.getInstance().getPlayerColor(player.getUniqueId(),true) + player.getDisplayName());
                                player.sendMessage(MLGRush.getInstance().getPrefix() + "You were challenged by " + BukkitCore.getInstance().getPlayerColor(attacker.getUniqueId(),true) + attacker.getDisplayName());
                            }
                        }
                    }
                    break;
                case SPECTATE:
                    event.setCancelled(true);
                    break;
            }
            if (event.getEntity().getType() == EntityType.ARMOR_STAND) {
                if (player.getGameMode() != GameMode.CREATIVE) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
