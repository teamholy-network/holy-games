package de.teamholy.clutches.playground.task;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.player.PlayerState;
import de.teamholy.clutches.playground.enums.ArmorColor;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.concurrent.atomic.AtomicInteger;

/* copyright by Yassino */
public class ArmorColorRainbowTask {

    public int r, g, b = 20;
    public int time = 59;

    public ArmorColorRainbowTask(Clutches instance) {
        AtomicInteger i = new AtomicInteger(0);

        Bukkit.getScheduler().runTaskTimer(instance, () -> {
            i.getAndIncrement();
            switch(time) {
                case 59:
                    r = 255;
                    g = 0;
                    b = 0;
                    time-=1;
                    break;
                case 58:
                    r = 255;
                    g = 68;
                    b = 0;
                    time-=1;
                    break;
                case 57:
                    r = 255;
                    g = 111;
                    b = 0;
                    time-=1;
                    break;
                case 56:
                    r = 255;
                    g = 171;
                    b = 0;
                    time-=1;
                    break;
                case 55:
                    r = 255;
                    g = 255;
                    b = 0;
                    time-=1;
                    break;
                case 54:
                    r = 188;
                    g = 255;
                    b = 0;
                    time-=1;
                    break;
                case 53:
                    r = 128;
                    g = 255;
                    b = 0;
                    time-=1;
                    break;
                case 52:
                    r = 43;
                    g = 255;
                    b = 0;
                    time-=1;
                    break;
                case 51:
                    r = 0;
                    g = 255;
                    b = 9;
                    time-=1;
                    break;
                case 50:
                    r = 0;
                    g = 255;
                    b = 51;
                    time-=1;
                    break;
                case 49:
                    r = 0;
                    g = 255;
                    b = 111;
                    time-=1;
                    break;
                case 48:
                    r = 0;
                    g = 255;
                    b = 162;
                    time-=1;
                    break;
                case 47:
                    r = 0;
                    g = 255;
                    b = 230;
                    time-=1;
                    break;
                case 46:
                    r = 0;
                    g = 239;
                    b = 255;
                    time-=1;
                    break;
                case 45:
                    r = 0;
                    g = 196;
                    b = 255;
                    time-=1;
                    break;
                case 44:
                    r = 0;
                    g = 173;
                    b = 255;
                    time-=1;
                    break;
                case 43:
                    r = 0;
                    g = 162;
                    b = 255;
                    time-=1;
                    break;
                case 42:
                    r = 0;
                    g = 137;
                    b = 255;
                    time-=1;
                    break;
                case 41:
                    r = 0;
                    g = 100;
                    b = 255;
                    time-=1;
                    break;
                case 40:
                    r = 0;
                    g = 77;
                    b = 255;
                    time-=1;
                    break;
                case 39:
                    r = 0;
                    g = 34;
                    b = 255;
                    time-=1;
                    break;
                case 38:
                    r = 17;
                    g = 0;
                    b = 255;
                    time-=1;
                    break;
                case 37:
                    r = 37;
                    g = 0;
                    b = 255;
                    time-=1;
                    break;
                case 36:
                    r = 68;
                    g = 0;
                    b = 255;
                    time-=1;
                    break;
                case 35:
                    r = 89;
                    g = 0;
                    b = 255;
                    time-=1;
                    break;
                case 34:
                    r = 102;
                    g = 0;
                    b = 255;
                    time-=1;
                    break;
                case 33:
                    r = 124;
                    g = 0;
                    b = 255;
                    time-=1;
                    break;
                case 32:
                    r = 154;
                    g = 0;
                    b = 255;
                    time-=1;
                    break;
                case 31:
                    r = 222;
                    g = 0;
                    b = 255;
                    time-=1;
                    break;
                case 30:
                    r = 255;
                    g = 0;
                    b = 247;
                    time-=1;
                    break;
                case 29:
                    r = 255;
                    g = 0;
                    b = 179;
                    time-=1;
                    break;
                case 28:
                    r = 255;
                    g = 0;
                    b = 128;
                    time = 59;
                    break;
            }
            Color c = Color.fromRGB(r, g, b);



            Clutches.getInstance().getPlayerEntryHandler().forEach((uuid, playerEntry) -> {

                if (playerEntry.getPlayer().getOpenInventory() != null && playerEntry.getPlayer().getOpenInventory().getTitle().equalsIgnoreCase("§8» §6Armor color")) {
                    InventoryView inventory = playerEntry.getPlayer().getOpenInventory();
                    ItemStack itemStack = inventory.getItem(7);

                    LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)inventory.getItem(7).getItemMeta();
                    leatherArmorMeta.setColor(c);
                    itemStack.setItemMeta(leatherArmorMeta);
                    playerEntry.getPlayer().getOpenInventory().setItem(7,itemStack);
                }

                if (playerEntry.getPlayerState() == PlayerState.PLAYGROUND && playerEntry.getPlaygroundPlayer().getArmorColor() == ArmorColor.RAINBOW && playerEntry.getPlayer().getInventory().getChestplate() != null) {
                    playerEntry.getPlayer().getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 4).setUnbreakable().setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(c).build());
                    playerEntry.getPlayer().getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 4).setUnbreakable().setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(c).build());
                    playerEntry.getPlayer().getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 4).setUnbreakable().setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(c).build());
                    playerEntry.getPlayer().getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 4).setUnbreakable().setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(c).build());
                }


            });
        }, 0, 2);
    }

}
