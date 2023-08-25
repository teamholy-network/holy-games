package de.teamholy.bedwars.utils;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.PerkType;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

/* copyright by Yassino */
public class PlayerShopListener {


    public static void buyItem(InventoryClickEvent event, Player player, ItemStack itemStack, Material setMaterial, int price) {
        if (player.getInventory().contains(setMaterial, price)) {
            int buyThings = 1;
            ItemStack toBuy = itemStack.clone();
            ItemMeta itemMeta = toBuy.getItemMeta();
            itemMeta.setLore(null);
            itemMeta.setDisplayName(null);
            toBuy.setItemMeta(itemMeta);


            if (event.isShiftClick() && event.getCurrentItem().getType() != BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.STICK).build().getType()) {
                ItemStack need = new ItemBuilder(setMaterial,price).build();
                int amount = getMaxBuyableAmount(player, need);


                if ((toBuy.getMaxStackSize() / toBuy.getAmount()) < amount) {
                    amount = toBuy.getMaxStackSize() / toBuy.getAmount();
                }

                toBuy.setAmount(amount * toBuy.getAmount());
                int possibleAmount = getPossibleSlotAmount(player,toBuy);

                if (possibleAmount < toBuy.getAmount()) {
                    if (possibleAmount <= 0) {
                        player.sendMessage(Bedwars.getInstance().getPrefix() +"§cYou dont have enough space in your inventory!");
                        return;
                    }
                    amount = possibleAmount;
                    toBuy.setAmount(amount * toBuy.getAmount());
                }


                ItemStack toRemove = need.clone();
                toRemove.setAmount(amount * toRemove.getAmount());

                System.out.println(toRemove);

                removeItems(player.getInventory(),toRemove.getType(),toRemove.getAmount());
                player.getInventory().addItem(toBuy);
                return;
            }

            if (setMaterial != null) {
                int itemAmount = getAmount(player.getInventory(), setMaterial);
                if (itemAmount >= price) {
                    int canBuy = itemAmount / price;
                    if (canBuy > buyThings)
                        canBuy = buyThings;
                    removeItems(player.getInventory(), setMaterial, price * canBuy);


                    if (event.getClick() == ClickType.NUMBER_KEY) {
                        for (int c = 0; c < canBuy; c++) {
                            ItemStack onPos = player.getInventory().getItem(event.getHotbarButton()) != null ? player.getInventory().getItem(event.getHotbarButton()).clone() : null;
                            player.getInventory().setItem(event.getHotbarButton(), toBuy);
                            if (onPos != null) {
                                player.getInventory().addItem(onPos);
                            }
                        }
                    } else {
                        for (int c = 0; c < canBuy; c++) {
                            player.getInventory().addItem(toBuy);
                        }
                    }
                    player.updateInventory();
                }
            }
        } else {
            player.sendMessage(Bedwars.getInstance().getPrefix() + "§cYou dont have enough materials");
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 2.0F, 2.0F);
            return;
        }



    }


    public static void removeItems(Inventory inventory, Material type, int amount) {
        if (amount <= 0) return;
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) continue;
            if (type == is.getType()) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    if (amount == 0) break;
                }
            }
        }
    }

    public static int getAmount(Inventory inv, Material m) {
        int count = 0;
        for (ItemStack s : inv.getContents()) {
            if (s != null &&
                    s.getType() == m)
                count += s.getAmount();
        }
        return count;
    }

    public static int getMaxBuyableAmount(Player player, ItemStack item) {
        PlayerInventory playerInventory = player.getInventory();

        int amount = 0;
        for (ItemStack itemStack : playerInventory.getContents()) {
            if (itemStack != null && item.getType() == itemStack.getType()) {
                amount += itemStack.getAmount();
            }
        }
        return amount / item.getAmount();
    }


    private static int getPossibleSlotAmount(final Player player, ItemStack item) {
        PlayerInventory inventory = player.getInventory();
        int amount = 0;

        for(int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack itemStack = inventory.getItem(slot);

            if (itemStack == null) {
                amount += item.getMaxStackSize();
                continue;
            }

            if (itemStack.getType() == Material.AIR) {
                amount += item.getMaxStackSize();
                continue;
            }

            if (itemStack.getType() == item.getType()) {
                amount += itemStack.getMaxStackSize() - itemStack.getAmount();
            }
        }

        return amount;
    }

}
