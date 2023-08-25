package de.teamholy.clutches.enums;

import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public enum Items {

    SWORD(0, new ItemBuilder(Material.STICK).setEnchantments(Enchantment.KNOCKBACK, 1).setAttributs().setUnbreakable().build()),
    STICK(1, new ItemBuilder(Material.SANDSTONE, 64).build()),
    BLOCKS1(3, new ItemBuilder(Material.REDSTONE).setName("§8» §6Pause §8(§7hold§8)").build()),
    SETTINGS(4, new ItemBuilder(Material.REDSTONE_COMPARATOR).setName("§8» §6Settings §8(§7rightclick§8)").build()),
    ROD(8, new ItemBuilder(Material.MAGMA_CREAM).setName("§8» §6Leave §8(§7rightclick§8)").setUnbreakable().build());

    private final int slot;
    private final ItemStack itemStack;

    public static final boolean correctInventory(final Inventory inventory) {
        for (Items gameItem : Items.values()) {
            if (!inventory.contains(gameItem.getItemStack())) {
                return false;
            }
        }
        return true;
    }
}
