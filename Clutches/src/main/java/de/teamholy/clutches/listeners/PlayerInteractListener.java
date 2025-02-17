package de.teamholy.clutches.listeners;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.player.PlayerEntry;
import de.teamholy.clutches.player.PlayerState;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
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
                } else if (!playerEntry.isVanish() && event.getItem().getType() == Material.EYE_OF_ENDER) {
                    event.setCancelled(true);
                    playerEntry.openSpectator();
                } else if (event.getItem().getType() == Material.MAGMA_CREAM && playerEntry.getPlayerState() == PlayerState.INGAME) {
                    playerEntry.checkQuit(false);
                } else if (event.getItem().getType() == Material.MAGMA_CREAM && playerEntry.getPlayerState() == PlayerState.SPECTATE) {
                    playerEntry.leaveSpectator();
                } else if (event.getItem().getType() == Material.REDSTONE_COMPARATOR) {
                    if (playerEntry.getPlayerState() == PlayerState.PLAYGROUND) return;
                    if (playerEntry.getArenaEntry() != null) {
                        playerEntry.openIngameSettings();
                    } else {
                        playerEntry.openSettings();
                    }
                } else if (playerEntry.isVanish() && event.getItem().getType() == Material.EYE_OF_ENDER) {
                    event.setCancelled(true);
                    Inventory inventory = new Inventory("§8» §cStalk", 54);
                    int i = 0;
                    for (PlayerEntry all : Clutches.getInstance().getPlayerEntryHandler().values()) {
                        ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setSkullOwner(all.getPlayer().getName())
                                .setName("§8» " + BukkitCore.getInstance().getPlayerColor(all.getPlayer().getUniqueId(), true) + all.getPlayer().getName());
                        item.setLore("§7State §8» §6" + all.getPlayerState());

                        inventory.setItem(item.build(), i, event2 -> player.teleport(all.getPlayer()));
                        i++;
                    }
                    player.openInventory(inventory.getInventory());
                }
            }

        } catch (Exception e) {

        }
    }

}
