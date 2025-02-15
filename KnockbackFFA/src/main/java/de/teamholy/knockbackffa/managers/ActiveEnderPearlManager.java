package de.teamholy.knockbackffa.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.EnderPearl;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import de.teamholy.knockbackffa.KnockbackFFA;

/**
 * Manages active Ender Pearls for players.
 * Stores and verifies Ender Pearl activity.
 */
public class ActiveEnderPearlManager {
    
    // Changed to ConcurrentHashMap for thread safety.
    private static final Map<UUID, EnderPearl> activePearls = new ConcurrentHashMap<>();

    /**
     * Adds an Ender Pearl for the given player and schedules its removal after 5 seconds if still active.
     * 
     * @param playerId the player's unique ID
     * @param pearl the Ender Pearl entity to add
     */
    public static void add(UUID playerId, EnderPearl pearl) {
        activePearls.put(playerId, pearl);
        // Schedule removal after 5 seconds (100 ticks)
        Bukkit.getScheduler().scheduleSyncDelayedTask(KnockbackFFA.getInstance(), () -> {
            // Remove the pearl if it still matches the one added.
            if (activePearls.get(playerId) == pearl) {
                activePearls.remove(playerId);
            }
        }, 100L);
    }

    /**
     * Removes the active Ender Pearl for the given player.
     * 
     * @param playerId the player's unique ID
     */
    public static void remove(UUID playerId) {
        activePearls.remove(playerId);
    }

    /**
     * Checks if the player has an active Ender Pearl.
     * 
     * @param playerId the player's unique ID
     * @return true if the player has an Ender Pearl that is not dead, false otherwise
     */
    public static boolean hasActive(UUID playerId) {
        EnderPearl pearl = activePearls.get(playerId);
        return pearl != null && !pearl.isDead();
    }
}
