package de.teamholy.bedwars.listeners;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.enums.RushBWShopItems;
import de.teamholy.api.bukkit.npc.event.PlayerInteractAtNPCEvent;
import de.teamholy.api.bukkit.npc.event.action.InteractAction;
import de.teamholy.bedwars.enums.GameState;
import de.teamholy.bedwars.model.PlayerEntry;
import de.teamholy.bedwars.utils.PlayerShopListener;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.PerkManager;
import de.teamholy.core.bukkit.perks.PerkType;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/* copyright by Yassino */
public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != null || event.getItem() != null || event.getItem().getType() != null || event.getItem().getType() != Material.AIR || event.getItem().getItemMeta() != null || event.getItem().getItemMeta().getDisplayName() != null) {
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

                if (event.getItem() == null) return;
                PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(event.getPlayer().getUniqueId());
                Player player = event.getPlayer();
                if (Bedwars.getInstance().getGameState() == GameState.LOBBY) {
                    if (event.getItem().getType() == Material.DIAMOND) {
                        BukkitCore.getInstance().getPerkManager().openSecondPerkInventory(playerEntry.getPlayer(), PerkType.STICK, PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL);
                    } else if (event.getItem().getType() == Material.ARMOR_STAND) {

                        Inventory inventory = new Inventory("§8» §6Sort shop inventory",3*9, false);

                        inventory.getInventory().setContents(playerEntry.getShopInventory().getContents());
                        playerEntry.getPlayer().getInventory().clear();

                        inventory.setOnClose(inventoryCloseEvent -> {
                            if (RushBWShopItems.correctInventory(inventory.getInventory())) {
                                playerEntry.setShopInventory(inventory.getInventory());
                                player.sendMessage(Bedwars.getInstance().getPrefix() + "Your inventory sort was saved");
                                player.sendMessage(Bedwars.getInstance().getPrefix() + "§atype in §8/§7resetinv §aif you want to reset it ");
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 2f, 2f);
                            } else {
                                playerEntry.createInv();
                                player.sendMessage(Bedwars.getInstance().getPrefix() + "Your inventory was not saved");
                                player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 2f, 2f);
                            }
                            Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(), () -> {
                                if (Bedwars.getInstance().getGameState() == GameState.LOBBY) {
                                    playerEntry.setLobbyItems();
                                }
                            }, 1);
                        });


                        playerEntry.getPlayer().openInventory(inventory.getInventory());
                    } else if (event.getItem().getType() == Material.BED) {
                        playerEntry.getPlayer().openInventory(Bedwars.getInstance().getInventoryHandler().getTeamSelectInventory().inventory);
                    } else if (event.getItem().getType() == Material.PAPER) {

                        if (Bedwars.isRushMode()) {
                            openMapVoting(playerEntry.getPlayer());
                            return;
                        }

                        Inventory inventory = new Inventory("§8» §6Voting", 9);
                        for (int i = 0; i < 9; i++) {
                            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
                        }
                        inventory.setItem(new ItemBuilder(Material.GOLD_INGOT, 1, (byte) 0).setName("§8» §6Gold voting").build(), 2, event1 -> {
                            playerEntry.getPlayer().playSound(playerEntry.getPlayer().getLocation(), Sound.CLICK, 1.0F, 100.0F);
                            playerEntry.getPlayer().openInventory(Bedwars.getInstance().getInventoryHandler().getGoldVotingInventory().inventory);
                        });
                        inventory.setItem(new ItemBuilder(Material.PAPER, 1, (byte) 0).setName("§8» §6Map voting").build(), 6, event1 -> {
                            openMapVoting(playerEntry.getPlayer());
                        });
                        playerEntry.getPlayer().openInventory(inventory.getInventory());
                    }
                } else if (Bedwars.getInstance().getGameState() == GameState.END) {
                    if (event.getItem().getType() == Material.SLIME_BALL) {
                        event.getPlayer().kickPlayer(null);
                    } else if (event.getItem().getType() == Material.PAPER) {
                        playerEntry.quickJoin();
                    }
                } else {
                    if (Bedwars.getInstance().getSpectatePlayers().contains(playerEntry)) {
                        if (event.getItem().getType() == Material.SLIME_BALL) {
                            event.getPlayer().kickPlayer(null);
                        } else if (event.getItem().getType() == Material.PAPER) {
                            playerEntry.quickJoin();
                        }
                    }


                    if (playerEntry.getPlayer().getItemInHand().getType() == Material.BLAZE_POWDER) {
                        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            PlayerShopListener.removeItems(player.getInventory(), Material.BLAZE_POWDER, 1);
                            final Location loc = player.getLocation().subtract(0.0D, 3.0D, 0.0D);
                            final Location loc2 = player.getLocation().subtract(0.0D, 3.0D, 1.0D);
                            final Location loc3 = player.getLocation().subtract(1.0D, 3.0D, 0.0D);
                            final Location loc4 = player.getLocation().subtract(0.0D, 3.0D, 0.0D).add(0.0D, 0.0D, 1.0D);
                            final Location loc5 = player.getLocation().subtract(0.0D, 3.0D, 0.0D).add(1.0D, 0.0D, 0.0D);
                            if (player.getLocation().subtract(0.0D, 3.0D, 1.0D).getBlock().getType() == Material.AIR)
                                player.getLocation().subtract(0.0D, 3.0D, 1.0D).getBlock().setType(Material.SLIME_BLOCK);
                            if (player.getLocation().subtract(1.0D, 3.0D, 0.0D).getBlock().getType() == Material.AIR)
                                player.getLocation().subtract(1.0D, 3.0D, 0.0D).getBlock().setType(Material.SLIME_BLOCK);
                            if (player.getLocation().subtract(0.0D, 3.0D, 0.0D).add(1.0D, 0.0D, 0.0D).getBlock().getType() == Material.AIR)
                                player.getLocation().subtract(0.0D, 3.0D, 0.0D).add(1.0D, 0.0D, 0.0D).getBlock().setType(Material.SLIME_BLOCK);
                            if (player.getLocation().subtract(0.0D, 3.0D, 0.0D).add(0.0D, 0.0D, 1.0D).getBlock().getType() == Material.AIR)
                                player.getLocation().subtract(0.0D, 3.0D, 0.0D).add(0.0D, 0.0D, 1.0D).getBlock().setType(Material.SLIME_BLOCK);
                            if (player.getLocation().subtract(0.0D, 3.0D, 0.0D).getBlock().getType() == Material.AIR)
                                player.getLocation().subtract(0.0D, 3.0D, 0.0D).getBlock().setType(Material.SLIME_BLOCK);
                            Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(), () -> {
                                if (loc.getBlock().getType() == Material.SLIME_BLOCK)
                                    loc.getBlock().setType(Material.AIR);
                                if (loc2.getBlock().getType() == Material.SLIME_BLOCK)
                                    loc2.getBlock().setType(Material.AIR);
                                if (loc3.getBlock().getType() == Material.SLIME_BLOCK)
                                    loc3.getBlock().setType(Material.AIR);
                                if (loc4.getBlock().getType() == Material.SLIME_BLOCK)
                                    loc4.getBlock().setType(Material.AIR);
                                if (loc5.getBlock().getType() == Material.SLIME_BLOCK)
                                    loc5.getBlock().setType(Material.AIR);

                            }, 20 * 20L);
                        }
                    }


                }

                if (Bedwars.getInstance().getSpectatePlayers().contains(playerEntry)) {

                    if (event.getItem().getType() == Material.COMPASS) {

                        Inventory inventory = new Inventory("§8» §6Spectate", 9 * 3);
                        int i = 0;
                        for (PlayerEntry allEntry : Bedwars.getInstance().getIngamePlayers()) {
                            if (Bedwars.getInstance().getIngamePlayers().contains(allEntry)) {
                                inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setSkullOwner(allEntry.getPlayer().getName()).setName("§8» " + allEntry.getTeamEntry().getColorCode() + allEntry.getPlayer().getName()).build(), i, event2 -> {

                                    if (Bedwars.getInstance().getIngamePlayers().contains(allEntry)) {
                                        playerEntry.getPlayer().teleport(allEntry.getPlayer());
                                    } else {
                                        playerEntry.getPlayer().sendMessage(Bedwars.getInstance().getPrefix() + "The player is not living anymore");
                                    }

                                });
                                i++;
                            }
                        }
                        playerEntry.getPlayer().openInventory(inventory.getInventory());

                    }

                }

            }

        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
            event.setCancelled(true);
            PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(event.getPlayer().getUniqueId());
            ArmorStand armorStand = (ArmorStand) event.getRightClicked();
            if (Bedwars.getInstance().getIngamePlayers().contains(playerEntry) && Bedwars.getInstance().getGameState() == GameState.INGAME) {
                if (armorStand.getCustomName().toLowerCase().contains("shop")) {
                    Bedwars.getInstance().getInventoryHandler().getShopInventory().openShop(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onNPC(PlayerInteractAtNPCEvent event) {
        if (event.getInteractAction() != InteractAction.RIGHT_CLICK) return;
        PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(event.getPlayer().getUniqueId());
        if (Bedwars.getInstance().getIngamePlayers().contains(playerEntry) && Bedwars.getInstance().getGameState() == GameState.INGAME) {
            if (Bedwars.isRushMode()) {
                Bedwars.getInstance().getInventoryHandler().getShopInventory().openRushShop(event.getPlayer());
            } else {
                Bedwars.getInstance().getInventoryHandler().getShopInventory().openShop(event.getPlayer());
            }
        }
    }

    private void openMapVoting(Player player) {
        if (Bedwars.getInstance().getForceMap() != null) {
            player.sendMessage(Bedwars.getInstance().getPrefix() + "The Map was forced to §6" + Bedwars.getInstance().getForceMap().getName());
            player.closeInventory();
            return;
        }
        player.openInventory(Bedwars.getInstance().getInventoryHandler().getMapVotingInventory().inventory);
        player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
    }


}
