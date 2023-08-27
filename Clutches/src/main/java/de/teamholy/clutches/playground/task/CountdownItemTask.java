package de.teamholy.clutches.playground.task;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.player.PlayerState;
import de.teamholy.clutches.playground.enums.ArmorColor;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;

/* copyright by Yassino */
public class CountdownItemTask extends BukkitRunnable {

    public static int i = 3;


    @AllArgsConstructor
    @Getter
    public enum Heads {

        NULL(0,"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWYxMGJhZmQ2NzJkYWIzZWIzZDllNjQ4Mjg2YjY2NTg4NmVhNzVlNTNlOWFiMWVlYmNhYWJlMjI0ODUwNzZjYSJ9fX0="),
        ONE(1,"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTBiNDZhYzQ5OTc2YjUwYTQ4OWU5OWJlODE3OGUxNDhhZmFkZmY2MDBhNzY5YThjMmZhMTA1ZTk3YTdjMWIzOSJ9fX0="),
        TWO(2,"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWYwNTRjNDFkMzNkYWIzNjRlMTMzNWVmMmI4NjFmYjE2YTUyMjk1MDc5ZTkxNDhkYzk2MWI1ZGU0ZDk0NGQ3NyJ9fX0="),
        THREE(3,"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI5MDI1MDQ3YTQ5NDE4MTZkMTY3YzYwOTRmNzU4OGYxMjlhNDRmMzRhZjk0ZTMwODU2Y2I3OGRjODAxYjZlYiJ9fX0=");


        private final int number;
        private final String texture;


        public static String getCurrentHead() {
            return Arrays.stream(Heads.values()).filter(head -> head.getNumber() == i).findFirst().get().getTexture();
        }

    }

    @Override
    public void run() {

        if (i == 0) i = 3;

        Clutches.getInstance().getPlayerEntryHandler().forEach((uuid, playerEntry) -> {

            if (playerEntry.getPlayer().getOpenInventory() != null && playerEntry.getPlayer().getOpenInventory().getTitle().equalsIgnoreCase("§8» §6Settings")) {
                InventoryView inventory = playerEntry.getPlayer().getOpenInventory();
                ItemStack itemStack = inventory.getItem(41);

                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                GameProfile gameProfile = new GameProfile(UUID.randomUUID(), (String)null);
                gameProfile.getProperties().put("textures", new Property("textures", Heads.getCurrentHead(), ""));

                try {
                    Field profileField = skullMeta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(skullMeta, gameProfile);
                    itemStack.setItemMeta(skullMeta);
                } catch (Exception e) {}

            }

        });


        i--;
    }
}
