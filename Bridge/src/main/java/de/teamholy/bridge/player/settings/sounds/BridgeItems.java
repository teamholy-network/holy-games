package de.teamholy.bridge.player.settings.sounds;

import de.teamholy.bridge.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public enum BridgeItems {
    BLOCKS(0,new ItemBuilder(Material.SANDSTONE).amount(64).name("§8» §6Blocks §8(§7rightclick§8)").build()),
    MENU(4,new de.teamholy.bridge.util.ItemBuilder(Material.REDSTONE_COMPARATOR).name("§8» §6Settings §8& §6Menu §8(§7rightclick§8)").build()),
    LEAVE(8,new de.teamholy.bridge.util.ItemBuilder(Material.SLIME_BALL).name("§8» §cQuit §8(§7rightclick§8)").build());

    private final int slot;
    private final ItemStack itemStack;

    public static final boolean correctInventory(final Inventory inventory) {
        for(BridgeItems gameItem : BridgeItems.values()) {
            if(!inventory.contains(gameItem.getItemStack())) {
                return false;
            }
        }
        return true;
    }
}