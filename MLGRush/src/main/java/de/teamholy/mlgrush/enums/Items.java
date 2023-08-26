package de.teamholy.mlgrush.enums;

import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
@Getter @AllArgsConstructor
public enum Items {
    STICK(0,new ItemBuilder(Material.STICK).setEnchantments(Enchantment.KNOCKBACK, 1).build()),
    BLOCKS(2,new ItemBuilder(Material.SANDSTONE,64).build()),
    PICKAXE(1,new ItemBuilder(Material.STONE_PICKAXE).setEnchantments(Enchantment.DIG_SPEED,2).setUnbreakable().build());

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
