package de.teamholy.bedwars.listeners;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.enums.GameState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/* copyright by Yassino */
public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE && Bedwars.getInstance().getGameState() != GameState.INGAME) {
            event.setCancelled(false);
            return;
        }
        if (Bedwars.getInstance().getGameState() != GameState.INGAME) {
            event.setCancelled(true);
        } else {
            if (event.getBlock().getType() == Material.TNT) {
                event.setCancelled(false);
                event.getBlock().setType(Material.AIR);
                event.getPlayer().getWorld().spawn(event.getBlock().getLocation(), TNTPrimed.class);
                return;
            }
            Bedwars.getInstance().getPlacedBlocks().add(event.getBlock().getLocation());
            event.setCancelled(false);
        }
    }

}
