package de.teamholy.clutches.playground;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.listeners.PlayerInteractListener;
import de.teamholy.clutches.player.PlayerEntry;
import de.teamholy.clutches.player.PlayerState;
import de.teamholy.clutches.playground.model.PlaygroundPlayer;
import de.teamholy.clutches.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/* copyright by Yassino */
public class PlaygroundListener implements Listener {


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        try {
            PlayerEntry playerEntry = Clutches.getInstance().getPlayerEntryHandler().get(event.getPlayer().getUniqueId());
            Player player = event.getPlayer();

            if (event.getItem().getType() != Material.REDSTONE_COMPARATOR) return;
            if (playerEntry.getPlayerState() == PlayerState.PLAYGROUND) {
                PlaygroundPlayer playgroundPlayer = playerEntry.getPlaygroundPlayer();
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    playgroundPlayer.openSettings();
                } else if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {

                    if (playgroundPlayer.getSettings().getSelectedPreset() == null) {
                        player.playSound(player.getLocation(), Sound.ITEM_BREAK,2,2);
                        PlayerUtils.sendTitle(player,"","§cno preset selected",0,10,0);
                    } else {
                        if (playgroundPlayer.getSettings().getSelectedPreset().getHitMap().size() == 0) {
                            PlayerUtils.sendTitle(player,"","§cno hits added yet",0,20,0);
                            player.playSound(player.getLocation(), Sound.ITEM_BREAK,2,2);
                            return;
                        }
                        if (playgroundPlayer.getPlayerTask().stopIfActive()) {
                            PlayerUtils.sendTitle(player,"","§cstopped",0,10,0);
                            player.playSound(player.getLocation(), Sound.ITEM_BREAK,2,2);
                        } else playgroundPlayer.getPlayerTask().playCountdownTask(playgroundPlayer.getSettings().getSelectedPreset());
                    }
                }
                return;
            }

        } catch (Exception ignored) {}
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        if (event.getInventory().getName() != null && (event.getInventory().getName().toLowerCase().contains("perks") || event.getInventory().getName().toLowerCase().contains("armor"))) {
            PlaygroundPlayer playgroundPlayer = Clutches.getInstance().getPlayerEntryHandler().get(player.getUniqueId()).getPlaygroundPlayer();
            if (playgroundPlayer == null) return;
            playgroundPlayer.setItems();
        }

    }

}
