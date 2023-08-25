package de.teamholy.knockbackffa.handlers;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.knockbackffa.enums.ArmorColor;
import de.teamholy.knockbackffa.enums.BowTrail;
import de.teamholy.knockbackffa.enums.KillStreakEffect;
import de.teamholy.knockbackffa.models.PlayerEntry;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;

/* copyright by Yassino */
public class PerkInventoriesHandler {

    public void openBowTrails(PlayerEntry playerEntry) {
        Inventory inventory = new Inventory("§8» §6Bow trails", 9);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }
        int i = 0;
        for (BowTrail bowTrail : BowTrail.values()) {
            ItemBuilder itemBuilder = new ItemBuilder(bowTrail.getMaterial());
            itemBuilder.setName("§8» §6" + bowTrail.getName());
            if (playerEntry.getBowTrail() == bowTrail) {
                itemBuilder.setLore("§2selected");
                itemBuilder.setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,2);
                itemBuilder.setAttributs();
            } else {
                itemBuilder.setLore("§7Available for " + bowTrail.getPerkRankType().getRankName() + "§7 and above");
            }
            inventory.setItem(itemBuilder.build(),i,event -> {
                if (!playerEntry.getPlayer().hasPermission(bowTrail.getPerkRankType().getPermission()))
                    return;
                playerEntry.getPlayer().sendMessage(BukkitHolyAPI.getInstance().getPrefix() + "Bow trail selected!");
                playerEntry.getPlayer().playSound(playerEntry.getPlayer().getLocation(), Sound.NOTE_PLING,2f,2f);
                playerEntry.setBowTrail(bowTrail);
                playerEntry.getPlayer().closeInventory();
            });
            i++;
        }

        playerEntry.getPlayer().openInventory(inventory.getInventory());
    }

    public void openKillStreakEffects(PlayerEntry playerEntry) {
        Inventory inventory = new Inventory("§8» §6KillStreak effects", 9);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }
        int i = 0;
        for (KillStreakEffect killeffect : KillStreakEffect.values()) {
            ItemBuilder itemBuilder = new ItemBuilder(killeffect.getMaterial());
            itemBuilder.setName("§8» §6" + killeffect.getName());
            if (playerEntry.getKillStreakEffect() == killeffect) {
                itemBuilder.setLore("§2selected");
                itemBuilder.setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,2);
                itemBuilder.setAttributs();
            } else {
                itemBuilder.setLore("§7Available for " + killeffect.getPerkRankType().getRankName() + "§7 and above");
            }
            inventory.setItem(itemBuilder.build(),i,event -> {
                if (!playerEntry.getPlayer().hasPermission(killeffect.getPerkRankType().getPermission()))
                    return;
                playerEntry.getPlayer().sendMessage(BukkitHolyAPI.getInstance().getPrefix() + "KillStreak effect selected!");
                playerEntry.getPlayer().playSound(playerEntry.getPlayer().getLocation(), Sound.NOTE_PLING,2f,2f);
                playerEntry.setKillStreakEffect(killeffect);
                playerEntry.getPlayer().closeInventory();
            });
            i++;
        }

        playerEntry.getPlayer().openInventory(inventory.getInventory());
    }

    public void openArmorColor(PlayerEntry playerEntry) {
        Inventory inventory = new Inventory("§8» §6Armor Color", 9);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }
        int i = 0;
        for (ArmorColor armorColor : ArmorColor.values()) {
            ItemBuilder itemBuilder = new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherColor(armorColor.getColor());
            itemBuilder.setName("§8» §6" + armorColor.getName());
            if (playerEntry.getArmorColor() == armorColor) {
                itemBuilder.setLore("§2selected");
                itemBuilder.setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,2);
                itemBuilder.setAttributs();
            } else {
                itemBuilder.setLore("§7Available for " + armorColor.getPerkRankType().getRankName() + "§7 and above");
            }
            inventory.setItem(itemBuilder.build(),i,event -> {
                if (!playerEntry.getPlayer().hasPermission(armorColor.getPerkRankType().getPermission()))
                    return;
                playerEntry.getPlayer().sendMessage(BukkitHolyAPI.getInstance().getPrefix() + "Armor color selected!");
                playerEntry.getPlayer().playSound(playerEntry.getPlayer().getLocation(), Sound.NOTE_PLING,2f,2f);
                playerEntry.setArmorColor(armorColor);
                playerEntry.getPlayer().closeInventory();
            });
            i++;
        }

        playerEntry.getPlayer().openInventory(inventory.getInventory());
    }

}
