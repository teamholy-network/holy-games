package de.teamholy.clutches.listeners;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.player.PlayerEntry;
import de.teamholy.clutches.player.PlayerState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        try {
            Player player = event.getPlayer();
            PlayerEntry playerEntry = Clutches.getInstance().getPlayerEntryHandler().get(player.getUniqueId());
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                if (event.getItem() == null || event.getItem().getItemMeta() == null)
                    return;
                if (event.getItem().getType() == Material.SLIME_BALL) {
                    player.kickPlayer(null);
                } else if (event.getItem().getType() == Material.EYE_OF_ENDER) {
                    event.setCancelled(true);
                    playerEntry.openSpectator();
                } else if (event.getItem().getType() == Material.MAGMA_CREAM && playerEntry.getPlayerState() == PlayerState.INGAME) {
                    playerEntry.checkQuit();
                } else if (event.getItem().getType() == Material.MAGMA_CREAM && playerEntry.getPlayerState() == PlayerState.SPECTATE) {
                    playerEntry.leaveSpectator();
                } else if (event.getItem().getType() == Material.REDSTONE_COMPARATOR) {
                    if (playerEntry.getPlayerState() == PlayerState.PLAYGROUND) {
                        playerEntry.getPlaygroundPlayer().openSettings();
                        return;
                    }
                    if (playerEntry.getArenaEntry() != null) {
                        playerEntry.openIngameSettings();
                    } else {
                        playerEntry.openSettings();
                    }
                }
            }

        } catch (Exception e) {

        }
    }

}
