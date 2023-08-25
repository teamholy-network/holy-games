package de.teamholy.bedwars.inventoriers;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.model.PlayerEntry;
import de.teamholy.bedwars.utils.PlayerShopListener;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.PerkType;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/* copyright by Yassino */
public class ShopInventory implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() == null) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.AIR) return;
        if (event.getClickedInventory() == null) return;
        Player player = (Player) event.getWhoClicked();
        PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId());
        if (!event.getInventory().getName().startsWith("§8» §6Shop")) return;
        if (event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) return;
        if (event.getCurrentItem() == null) return;
        if (!(event.getRawSlot() < event.getInventory().getSize())) return;
        player.playSound(player.getLocation(), Sound.CLICK, 1f, 100f);
        if (event.getSlot() <= 8 && !Bedwars.isRushMode()) return;
        if (!Bedwars.getInstance().getIngamePlayers().contains(playerEntry)) return;
        String lore = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(0)).replace("× ", "");
        String[] strings = lore.split(" ");
        PlayerShopListener.buyItem(event, player, new ItemBuilder(event.getCurrentItem()).build(), playerEntry.getMaterial(strings[1]), Integer.parseInt(strings[0]));
        event.setCancelled(true);
    }

    public void openRushShop(Player player) {
        Inventory inventory = new Inventory("§8» §6Shop", 9*3);
        for (int i = 0; i < 9*3; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(),i);
        }
        PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId());

        int slot = 0;

        for (ItemStack itemStack : playerEntry.getShopInventory()) {
            if (itemStack != null && itemStack.getType() != null) {


                if (itemStack.getType() == Material.STICK) {
                    inventory.setItem(BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.STICK).setLore("§7× §c8 Bronze").setEnchantments(Enchantment.KNOCKBACK,1).setName("§6Stick").build(),slot);

                } else if (itemStack.getType() == Material.LEATHER_BOOTS) {
                    inventory.setItem(new ItemBuilder(Material.LEATHER_BOOTS).setLore("§7× §c1 Bronze")
                            .setLeatherColor(playerEntry.getTeamEntry().getColor())
                            .setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,1)
                            .setEnchantments(Enchantment.DURABILITY,1).setName("§6Leather boots").build(),slot);
                }
                else if (itemStack.getType() == Material.LEATHER_HELMET) {
                    inventory.setItem(new ItemBuilder(Material.LEATHER_HELMET).setLore("§7× §c1 Bronze")
                            .setLeatherColor(playerEntry.getTeamEntry().getColor())
                            .setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,1)
                            .setEnchantments(Enchantment.DURABILITY,1).setName("§6Leather helmet").build(),slot);
                }
                else if (itemStack.getType() == Material.LEATHER_LEGGINGS) {
                    inventory.setItem(new ItemBuilder(Material.LEATHER_LEGGINGS).setLore("§7× §c1 Bronze")
                            .setLeatherColor(playerEntry.getTeamEntry().getColor())
                            .setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,1)
                            .setEnchantments(Enchantment.DURABILITY,1).setName("§6Leather leggings").build(),slot);
                } else {
                    inventory.setItem(itemStack,slot);
                }

            } else {
                inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), slot);
            }
            slot++;
        }

        player.openInventory(inventory.getInventory());
    }

    public void openShop(Player player) {
        Inventory inventory = new Inventory("§8» §6Shop", 18);
        inventory.setItem(new ItemBuilder(Material.SANDSTONE).setName("§6Building stuff").build(), 0, event -> openBuildShop(inventory));
        inventory.setItem(new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).setName("§6Armor").build(), 1, event -> openArmorShop(inventory, player));
        inventory.setItem(new ItemBuilder(Material.IRON_PICKAXE).setName("§6Pickaxes").build(), 2, event -> openPickaxeShop(inventory));
        inventory.setItem(new ItemBuilder(Material.GOLD_SWORD).setName("§6Swords").build(), 3, event -> openSwordShop(inventory, player));
        inventory.setItem(new ItemBuilder(Material.BOW).setName("§6Bows").build(), 4, event -> openBowShop(inventory));
        inventory.setItem(new ItemBuilder(Material.APPLE).setName("§6Food").build(), 5, event -> openFoodShop(inventory));
        inventory.setItem(new ItemBuilder(Material.CHEST).setName("§6Chests").build(), 6, event -> openChestShop(inventory));
        inventory.setItem(new ItemBuilder(Material.GLASS_BOTTLE).setName("§6Potion").build(), 7, event -> openPotionShop(inventory));
        inventory.setItem(new ItemBuilder(Material.REDSTONE_COMPARATOR).setName("§6Special items").build(), 8, event -> openSpecialShop(inventory));
        for (int i = 9; i < 18; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(),i);
        }
        player.openInventory(inventory.getInventory());
    }

    public void openBuildShop(Inventory inventory) {
        for (int i = 9; i < 18; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(),i);
        }
        inventory.setItem(new ItemBuilder(Material.SANDSTONE).setName("§6Building stuff").setAttributs().setEnchantments(Enchantment.KNOCKBACK, 1).build(), 0);
        inventory.setItem(new ItemBuilder(Material.SANDSTONE, 2).setLore("§7× §c1 Bronze").setName("§6Sandstone").build(), 10);
        inventory.setItem(new ItemBuilder(Material.ENDER_STONE, 1).setLore("§7× §c7 Bronze").setName("§6Endstone").build(), 11);
        inventory.setItem(new ItemBuilder(Material.IRON_BLOCK, 1).setLore("§7× §f3 Iron").setName("§6Iron block").build(), 12);
        inventory.setItem(new ItemBuilder(Material.GLASS, 1).setLore("§7× §c5 Bronze").setName("§6Glass block").build(), 14);
        inventory.setItem(new ItemBuilder(Material.WEB, 1).setLore("§7× §c16 Bronze").setName("§6Cobweb").build(), 15);
        inventory.setItem(new ItemBuilder(Material.LADDER, 1).setLore("§7× §c4 Bronze").setName("§6Ladder").build(), 16);
    }

    public void openArmorShop(Inventory inventory,Player player) {
        for (int i = 9; i < 18; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(),i);
        }
        inventory.setItem(new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).setName("§6Armor").setAttributs().setEnchantments(Enchantment.KNOCKBACK, 1).build(), 1);
        inventory.setItem(new ItemBuilder(Material.LEATHER_HELMET).setLore("§7× §c1 Bronze").setLeatherColor(Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId()).getTeamEntry().getColor()).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,1).setEnchantments(Enchantment.DURABILITY,1).setName("§6Leather helmet").build(),10);
        inventory.setItem(new ItemBuilder(Material.LEATHER_LEGGINGS).setLore("§7× §c1 Bronze").setLeatherColor(Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId()).getTeamEntry().getColor()).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,1).setEnchantments(Enchantment.DURABILITY,1).setName("§6Leather leggings").build(),11);
        inventory.setItem(new ItemBuilder(Material.LEATHER_BOOTS).setLore("§7× §c1 Bronze").setLeatherColor(Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId()).getTeamEntry().getColor()).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,1).setEnchantments(Enchantment.DURABILITY,1).setName("§6Leather boots").build(),12);
        inventory.setItem(new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).setLore("§7× §f1 Iron").setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,1).setEnchantments(Enchantment.DURABILITY,1).setName("§6Chainmail chestplate I").build(),14);
        inventory.setItem(new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).setLore("§7× §f3 Iron").setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,2).setEnchantments(Enchantment.DURABILITY,1).setName("§6Chainmail chestplate II").build(),15);
        inventory.setItem(new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).setLore("§7× §f7 Iron").setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,3).setEnchantments(Enchantment.DURABILITY,1).setName("§6Chainmail chestplate III").build(),16);
    }

    public void openPickaxeShop(Inventory inventory) {
        for (int i = 9; i < 18; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(),i);
        }
        inventory.setItem(new ItemBuilder(Material.IRON_PICKAXE).setName("§6Pickaxes").setAttributs().setEnchantments(Enchantment.KNOCKBACK, 1).build(), 2);
        inventory.setItem(new ItemBuilder(Material.WOOD_PICKAXE).setLore("§7× §c4 Bronze").setEnchantments(Enchantment.DURABILITY,1).setEnchantments(Enchantment.DIG_SPEED,1).setName("§6Wooden Pickaxe").build(),11);
        inventory.setItem(new ItemBuilder(Material.STONE_PICKAXE).setLore("§7× §f2 Iron").setEnchantments(Enchantment.DURABILITY,1).setEnchantments(Enchantment.DIG_SPEED,1).setName("§6Stone Pickaxe").build(),13);
        inventory.setItem(new ItemBuilder(Material.IRON_PICKAXE).setLore("§7× §61 Gold").setEnchantments(Enchantment.DURABILITY,1).setEnchantments(Enchantment.DIG_SPEED,1).setName("§6Iron Pickaxe").build(),15);
    }

    public void openSwordShop(Inventory inventory,Player player) {
        for (int i = 9; i < 18; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(),i);
        }
        inventory.setItem(new ItemBuilder(Material.GOLD_SWORD).setName("§6Swords").setAttributs().setEnchantments(Enchantment.KNOCKBACK, 1).build(), 3);
        inventory.setItem(BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.STICK).setLore("§7× §c8 Bronze").setEnchantments(Enchantment.KNOCKBACK,1).setName("§6Stick").build(),11);
        inventory.setItem(new ItemBuilder(Material.GOLD_SWORD).setEnchantments(Enchantment.DAMAGE_ALL,1).setLore("§7× §f1 Iron").setName("§6Gold sword I").build(),13);
        inventory.setItem(new ItemBuilder(Material.GOLD_SWORD).setEnchantments(Enchantment.DURABILITY,1).setEnchantments(Enchantment.DAMAGE_ALL,2).setLore("§7× §f3 Iron").setName("§6Gold sword II").build(),14);
        inventory.setItem(new ItemBuilder(Material.IRON_SWORD).setEnchantments(Enchantment.DURABILITY,1).setEnchantments(Enchantment.KNOCKBACK,1).setEnchantments(Enchantment.DAMAGE_ALL,1).setLore("§7× §65 Gold").setName("§6Iron sword").build(),15);
    }

    public void openBowShop(Inventory inventory) {
        for (int i = 9; i < 18; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(),i);
        }
        inventory.setItem(new ItemBuilder(Material.BOW).setName("§6Bows").setAttributs().setEnchantments(Enchantment.KNOCKBACK, 1).build(), 4);
        inventory.setItem(new ItemBuilder(Material.BOW).setName("§6Bow I").setLore("§7× §63 Gold").setEnchantments(Enchantment.ARROW_INFINITE,1).build(),11);
        inventory.setItem(new ItemBuilder(Material.BOW).setName("§6Bow II").setLore("§7× §67 Gold").setEnchantments(Enchantment.ARROW_INFINITE,1).setEnchantments(Enchantment.ARROW_DAMAGE,1).build(),12);
        inventory.setItem(new ItemBuilder(Material.BOW).setName("§6Bow III").setLore("§7× §613 Gold").setEnchantments(Enchantment.ARROW_INFINITE,1).setEnchantments(Enchantment.ARROW_KNOCKBACK,1).setEnchantments(Enchantment.ARROW_DAMAGE,2).build(),13);
        inventory.setItem(new ItemBuilder(Material.ARROW).setName("§6Arrow").setLore("§7× §61 Gold").build(),15);
    }

    public void openFoodShop(Inventory inventory) {
        for (int i = 9; i < 18; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(),i);
        }
        inventory.setItem(new ItemBuilder(Material.APPLE).setName("§6Food").setAttributs().setEnchantments(Enchantment.KNOCKBACK, 1).build(),5);
        inventory.setItem(new ItemBuilder(Material.APPLE).setLore("§7× §c1 Bronze").setName("§6Apple").build(),10);
        inventory.setItem(new ItemBuilder(Material.COOKED_BEEF).setLore("§7× §c2 Bronze").setName("§6Steak").build(),12);
        inventory.setItem(new ItemBuilder(Material.CAKE).setLore("§7× §f1 Iron").setName("§6Cake").build(),14);
        inventory.setItem(new ItemBuilder(Material.GOLDEN_APPLE).setLore("§7× §62 Gold").setName("§6Golden apple").build(),16);
    }

    public void openChestShop(Inventory inventory) {
        for (int i = 9; i < 18; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(),i);
        }
        inventory.setItem(new ItemBuilder(Material.CHEST).setName("§6Chest").setAttributs().setEnchantments(Enchantment.KNOCKBACK, 1).build(),6);
        inventory.setItem(new ItemBuilder(Material.CHEST).setName("§6Chest").setLore("§7× §f1 Iron").build(),12);
        inventory.setItem(new ItemBuilder(Material.ENDER_CHEST).setLore("§7× §61 Gold").setName("§6Ender Chest").build(),14);
    }

    public void openPotionShop(Inventory inventory) {
        for (int i = 9; i < 18; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(),i);
        }
        inventory.setItem(new ItemBuilder(Material.GLASS_BOTTLE).setName("§6Potions").setAttributs().setEnchantments(Enchantment.KNOCKBACK, 1).build(),7);
        inventory.setItem(new ItemBuilder(Material.POTION,1,(byte) 8194).setLore("§7× §f4 Iron").setName("§6Speed potion").build(),10);
        inventory.setItem(new ItemBuilder(Material.POTION,1,(byte) 8261).setLore("§7× §f3 Iron").setName("§6Heal potion I").build(),12);
        inventory.setItem(new ItemBuilder(Material.POTION,1,(byte) 8229).setLore("§7× §f5 Iron").setName("§6Heal potion II").build(),14);
        inventory.setItem(new ItemBuilder(Material.POTION,1,(byte) 8201).setLore("§7× §66 Gold").setName("§6Strength potion").build(),16);
    }

    public void openSpecialShop(Inventory inventory) {
        for (int i = 9; i < 18; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(),i);
        }
        inventory.setItem(new ItemBuilder(Material.REDSTONE_COMPARATOR).setName("§6Special items").setAttributs().setEnchantments(Enchantment.KNOCKBACK, 1).build(),8);
        inventory.setItem(new ItemBuilder(Material.FISHING_ROD).setLore("§7× §f7 Iron").setName("§6Rod").build(),10);
        inventory.setItem(new ItemBuilder(Material.ENDER_PEARL).setLore("§7× §612 Gold").setName("§6Enderpearl").build(),12);
        inventory.setItem(new ItemBuilder(Material.TNT).setLore("§7× §63 Gold").setName("§6Instant TNT").build(),14);
        inventory.setItem(new ItemBuilder(Material.BLAZE_POWDER).setLore("§7× §63 Gold").setName("§6Rescue platform").build(),16);
    }

}