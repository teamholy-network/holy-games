package de.teamholy.knockbackffa.enums;

import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public enum Items {
    SWORD(0,new ItemBuilder(Material.WOOD_SWORD).setEnchantments(Enchantment.DAMAGE_ALL, 1).setUnbreakable().build()),
    STICK(1,new ItemBuilder(Material.STICK).setEnchantments(Enchantment.KNOCKBACK,1).build()),
    BOW(2,new ItemBuilder(Material.BOW).setUnbreakable().build()),
    BLOCKS(3,new ItemBuilder(Material.SANDSTONE,64).build()),
    ARROW(6,new ItemBuilder(Material.ARROW,16).build()),
    COBWEB(7,new ItemBuilder(Material.WEB).build()),
    PEARL(8,new ItemBuilder(Material.ENDER_PEARL).build());

    private final int slot;
    private final ItemStack itemStack;

    public static final boolean correctInventory(final Inventory inventory) {
        for(Items gameItem : Items.values()) {
            if(!inventory.contains(gameItem.getItemStack())) {
                return false;
            }
        }
        return true;
    }
}