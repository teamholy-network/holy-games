package de.teamholy.mlgrush.listener;

import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.player.PlayerEntry;
import de.teamholy.mlgrush.player.PlayerState;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onHandle(PlayerInteractEvent event) {
        try {
            Player player = event.getPlayer();
            PlayerEntry playerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(player.getUniqueId());
            // Vorher NPE bei fehlendem Cache-Eintrag (lokal geschluckt, Methode brach ab) — gleiches Ergebnis per Early-Return
            if (playerEntry == null) return;
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                if (event.getItem() == null || event.getItem().getItemMeta() == null)
                    return;
                if (playerEntry.getPlayerState() == PlayerState.LOBBY) {
                    if (event.getItem().getType().equals(Material.SLIME_BALL)) {
                        player.kickPlayer(null);
                    } else if (event.getItem().getType().equals(Material.REDSTONE_COMPARATOR)) {
                        playerEntry.openSettingsInventory();
                    } else if (event.getItem().getType() == Material.EYE_OF_ENDER) {
                        playerEntry.getPlayer().openInventory(MLGRush.getInstance().getInventoryManager().spectateInv);
                        event.setCancelled(true);
                    }
                } else if (playerEntry.getPlayerState() == PlayerState.SPECTATE) {
                    if (event.getItem().getType().equals(Material.MAGMA_CREAM)) {
                        player.teleport(MLGRush.getInstance().getLobby());
                        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT,2f,2f);
                        playerEntry.removeFromSpectator(false);
                    } else if (event.getItem().getType() == Material.NETHER_STAR) {
                        player.openInventory(MLGRush.getInstance().getInventoryManager().spectateInv);
                    }
                    // Vorher NPE bei RIGHT_CLICK_AIR ohne Block (lokal geschluckt) — gleiches Ergebnis per Null-Check
                    if (event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.BED || event.getClickedBlock().getType() == Material.BED_BLOCK)) {
                        event.setCancelled(true);
                    }
                } else if (playerEntry.getPlayerState() == PlayerState.INGAME) {
                    if (event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.BED || event.getClickedBlock().getType() == Material.BED_BLOCK)) {
                        event.setCancelled(true);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

}
