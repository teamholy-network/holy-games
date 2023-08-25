package de.teamholy.bedwars.listeners;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.enums.GameState;
import de.teamholy.bedwars.model.PlayerEntry;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/* copyright by Yassino */
public class TeamChestListener implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (Bedwars.getInstance().getGameState() != GameState.INGAME) return;
        Player player = (Player) event.getPlayer();
        PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId());
        if (!Bedwars.getInstance().getIngamePlayers().contains(playerEntry)) return;
        if (event.getInventory().getName().equals("§8» " + playerEntry.getTeamEntry().getColorCode() + playerEntry.getTeamEntry().getName())) {
            playerEntry.getTeamEntry().setTeamChest(event.getInventory());
            player.playSound(player.getLocation(), Sound.CHEST_CLOSE, 1.0F, 1.0F);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        try {
            Player player = event.getPlayer();
            PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId());
            if (event.getClickedBlock().getType() == Material.ENDER_CHEST && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                player.openInventory(playerEntry.getTeamEntry().getTeamChest());
                player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F);
                event.setCancelled(true);
            }
        } catch (Exception ignored) {

        }
    }

}
