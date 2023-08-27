package de.teamholy.clutches.playground.enums;

import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public enum PlaygroundItems {

    SWORD(0, new ItemBuilder(Material.WOOD_SWORD).setAttributs().setUnbreakable().build()),
    PICKAXE(1, new ItemBuilder(Material.WOOD_PICKAXE).setEnchantments(Enchantment.DIG_SPEED,2).setAttributs().setUnbreakable().build()),
    BLOCKS(2, new ItemBuilder(Material.SANDSTONE, 64).build()),
    AXE(3, new ItemBuilder(Material.WOOD_AXE).setEnchantments(Enchantment.DIG_SPEED,2).setAttributs().setUnbreakable().build()),
    STICK(4, new ItemBuilder(Material.STICK).setEnchantments(Enchantment.KNOCKBACK, 1).setAttributs().setUnbreakable().build()),
    SETTINGS(8, new ItemBuilder(Material.REDSTONE_COMPARATOR).setName("§8» §6Clutch §8(§7leftclick§8) §8︳ §6Settings §8(§7rightclick§8)").build());

    private final int slot;
    private final ItemStack itemStack;

    public static Inventory newInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, "inv");
        for (PlaygroundItems playgroundItems : PlaygroundItems.values()) {
            inventory.setItem(playgroundItems.getSlot(), playgroundItems.getItemStack());
        }
        return inventory;
    }

    public static final boolean correctInventory(final Inventory inventory) {
        for (PlaygroundItems gameItem : PlaygroundItems.values()) {
            if (!inventory.contains(gameItem.getItemStack())) {
                return false;
            }
        }
        return true;
    }
}
