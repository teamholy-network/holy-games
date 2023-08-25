package de.teamholy.bedwars.enums;

import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public enum RushBWShopItems {
    STICK(10,new ItemBuilder(Material.STICK).setLore("§7× §c8 Bronze").setEnchantments(Enchantment.KNOCKBACK, 1).build()),
    WOOD_PICKAXE(11,new ItemBuilder(Material.WOOD_PICKAXE).setLore("§7× §c4 Bronze").setEnchantments(Enchantment.DURABILITY,1)
            .setEnchantments(Enchantment.DIG_SPEED,1).setName("§6Wooden Pickaxe").build()),
    SANDSTONE(12,new ItemBuilder(Material.SANDSTONE, 2).setLore("§7× §c1 Bronze").setName("§6Sandstone").build()),
    ENDSTONE(13,new ItemBuilder(Material.ENDER_STONE, 1).setLore("§7× §c7 Bronze").setName("§6Endstone").build()),
    HELMET(5,new ItemBuilder(Material.LEATHER_HELMET).setLore("§7× §c1 Bronze")
            .setLeatherColor(Color.GRAY)
            .setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,1).setEnchantments(Enchantment.DURABILITY,1).setName("§6Leather helmet").build()),
    LEGGINGS(14,new ItemBuilder(Material.LEATHER_LEGGINGS).setLore("§7× §c1 Bronze")
            .setLeatherColor(Color.GRAY)
            .setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,1).setEnchantments(Enchantment.DURABILITY,1).setName("§6Leather leggings").build()),
    BOOTS(23,new ItemBuilder(Material.LEATHER_BOOTS).setLore("§7× §c1 Bronze")
            .setLeatherColor(Color.GRAY)
            .setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,1).setEnchantments(Enchantment.DURABILITY,1).setName("§6Leather boots").build()),
    LADDER(15,new ItemBuilder(Material.LADDER, 1).setLore("§7× §c4 Bronze").setName("§6Ladder").build()),
    STEAK(16,new ItemBuilder(Material.COOKED_BEEF).setLore("§7× §c2 Bronze").setName("§6Steak").build());

    private final int slot;
    private final ItemStack itemStack;

    public static final boolean correctInventory(final Inventory inventory) {
        for(RushBWShopItems gameItem : RushBWShopItems.values()) {
            if(!inventory.contains(gameItem.getItemStack())) {
                return false;
            }
        }
        return true;
    }
}
