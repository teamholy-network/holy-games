package de.teamholy.knockbackffa.listeners;

import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.enums.PlayerState;
import de.teamholy.knockbackffa.models.MapEntry;
import de.teamholy.knockbackffa.models.PlayerEntry;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/* copyright by Yassino */
public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != null || event.getItem() != null || event.getItem().getType() != null || event.getItem().getType() != Material.AIR || event.getItem().getItemMeta() != null || event.getItem().getItemMeta().getDisplayName() != null) {
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

                Player player = event.getPlayer();
                PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(player.getUniqueId());

                if (event.getClickedBlock() != null) {
                    if (event.getClickedBlock().getState() instanceof Sign && !(player.getGameMode() == GameMode.CREATIVE)) {
                        Sign sign = (Sign) event.getClickedBlock().getState();

                        for (MapEntry mapEntry : KnockbackFFA.getInstance().getCacheHandler().getMapEntrys().values()) {

                            if (mapEntry.getSign().equals(sign)) {
                                if (playerEntry.getPlayerState() == PlayerState.LOBBY) {
                                    playerEntry.joinGame(mapEntry);
                                }
                            }

                        }

                    }

                }

                if (event.getItem() == null || event.getItem().getItemMeta() == null) {
                    return;
                }


                if (playerEntry.getPlayerState() == PlayerState.LOBBY) {
                    if (event.getItem().getType() == Material.ARMOR_STAND) {
                        playerEntry.openInventorySort();
                    } else if (event.getItem().getType() == Material.SLIME_BALL) {
                        player.kickPlayer(null);
                    } else if (event.getItem().getType() == Material.REDSTONE_COMPARATOR) {
                        playerEntry.openPerks();
                    }
                } else if (playerEntry.getPlayerState() == PlayerState.INGAME) {
                    if (event.getItem().getType() == Material.MAGMA_CREAM) {
                        playerEntry.leaveGame();
                    }
                } else if (playerEntry.getPlayerState() == PlayerState.SPECTATE) {
                    if (event.getItem().getType() == Material.COMPASS) {
                        Inventory inventory = new Inventory("§8» §6Maps",9);
                        int i = 0;
                        for (MapEntry mapEntry : KnockbackFFA.getInstance().getCacheHandler().getMapEntrys().values()) {
                            inventory.setItem(new ItemBuilder(Material.PAPER,Math.min(64,mapEntry.getPlayers().size())).setName("§8» §6" + mapEntry.getMapName()).build(),i, event1 -> {
                                Inventory users = new Inventory("§8» §6Spieler von " + mapEntry.getMapName(),54);
                                int usersInt = 0;
                                for (Player all : mapEntry.getPlayers()) {
                                    users.setItem(new ItemBuilder(Material.SKULL_ITEM,1,(byte) 3).setSkullOwner(all.getName()).setName("§8» " + BukkitCore.getInstance().getPlayerColor(all.getUniqueId(), true) + all.getName()).build(),usersInt, event2 -> {
                                        player.teleport(all);
                                    });
                                    usersInt++;
                                }

                                player.openInventory(users.getInventory());
                            });
                            i++;
                        }

                        player.openInventory(inventory.getInventory());
                    } else if (event.getItem().getType() == Material.SLIME_BALL) {
                        player.chat("/v");
                    }
                }

            }
        }
    }

}
