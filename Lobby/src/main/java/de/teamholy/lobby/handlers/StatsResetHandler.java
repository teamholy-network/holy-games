package de.teamholy.lobby.handlers;

import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Locale;

/* copyright by Yassino */
public class StatsResetHandler {


    public void openStatsReset(Player player) {
        Inventory inventory = new Inventory("§8» §6Statsreset", 3 * 9);

        for (int i = 0; i < 9 * 3; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService().getRedisCache().get(player.getUniqueId());
        if (playerProfile == null) return;


        long tokens = playerProfile.getStatsResetTokens();
        if (tokens == 0) {
            inventory.setItem(new ItemBuilder(Material.BARRIER).setName("§cNo statsreset tokens").setLore("§7Do you want to buy statsreset tokens? §ashop.teamholy.de").build(), 13);
        } else {

            ItemBuilder mlgrush = new ItemBuilder(Material.STICK).setName("§8» §6MLGRush");
            ItemBuilder kbffa = new ItemBuilder(Material.SANDSTONE).setName("§8» §6KnockbackFFA");
            ItemBuilder bw = new ItemBuilder(Material.BED).setName("§8» §6Bedwars");
            ItemBuilder rbw = new ItemBuilder(Material.BLAZE_ROD).setName("§8» §6Rush-Bedwars");
            ItemBuilder sgffa = new ItemBuilder(Material.IRON_SWORD).setName("§8» §6SGFFA");

            inventory.setItem(new ItemBuilder(Material.PAPER).setName("§6You currently have §a" + tokens + " §6statsreset tokens!").setLore("§7Click on the mode where you want to delete your stats").build(), 4);
            inventory.setItem(mlgrush.build(), 11, clickEvent -> openStatsResetConfirm(player, playerProfile, mlgrush, Gamemodes.MLGRUSH));
            inventory.setItem(kbffa.build(), 12, clickEvent -> openStatsResetConfirm(player, playerProfile, kbffa,Gamemodes.KNOCKBACKFFA));
            inventory.setItem(bw.build(), 13, clickEvent -> openStatsResetConfirm(player, playerProfile, bw, Gamemodes.BEDWARS));
            inventory.setItem(sgffa.build(), 14, clickEvent -> openStatsResetConfirm(player, playerProfile, sgffa, Gamemodes.SGFFA));
            inventory.setItem(rbw.build(), 15, clickEvent -> openStatsResetConfirm(player, playerProfile, rbw, Gamemodes.RUSHBW));
        }

        player.openInventory(inventory.getInventory());
    }

    private void openStatsResetConfirm(Player player, PlayerProfile playerProfile, ItemBuilder itemBuilder, Gamemodes gamemode) {
        Inventory inventory = new Inventory("§8» §6Statsreset in " +gamemode.getColor() + gamemode.toString().toLowerCase(Locale.ROOT), 9);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        inventory.setItem(itemBuilder.setLore("§7Do you want to reset your stats in " + gamemode.getColor() + gamemode.toString().toLowerCase(Locale.ROOT) + "§7?").build(), 4);

        GameProfile gameProfile = BukkitCore.getAPI().getGameService().getEntity(player.getUniqueId(),() -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()));


        inventory.setItem(new ItemBuilder(Material.INK_SACK, 1, (byte) 10).setName("§8» §aYes").build(), 2, event -> {

            if (gameProfile.exists(gamemode.toString())) {
                gameProfile.delete(gamemode.toString());
                player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 50f, 50f);
                player.sendMessage("§aYou deleted your stats in §" + gamemode.getColor() + gamemode.toString().toLowerCase(Locale.ROOT) + "§a!");
                BukkitCore.getAPI().getGameService().saveEntity(gameProfile,true,true);
                playerProfile.setStatsResetTokens(playerProfile.getStatsResetTokens() - 1);
                BukkitCore.getAPI().getPlayerService().saveEntity(playerProfile,true,true);


            } else {
                player.sendMessage("§cYou dont have any stats in §" + gamemode.getColor() + gamemode.toString().toLowerCase(Locale.ROOT));
            }
            player.closeInventory();
        });

        inventory.setItem(new ItemBuilder(Material.INK_SACK, 1, (byte) 1).setName("§8» §cNo").build(), 6, event -> {
            player.closeInventory();
            player.sendMessage("§cAborted stats reset!");
        });

        player.openInventory(inventory.getInventory());

    }

}
