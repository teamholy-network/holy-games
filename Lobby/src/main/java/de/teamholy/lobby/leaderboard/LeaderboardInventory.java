package de.teamholy.lobby.leaderboard;

import com.google.common.collect.Lists;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.api.utility.TrophieLeague;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import de.teamholy.lobby.Lobby;
import de.teamholy.lobby.lobbyplayer.LobbyPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

/* copyright by Yassino */
public class LeaderboardInventory {


    public void open(Player player, Gamemodes gamemode) {
        Inventory inventory = new Inventory("§8» §6Leaderboard in §" + gamemode.getColor() + gamemode.toString().toLowerCase(), 6 * 9);
        GameProfile gameProfile = Lobby.getInstance().getLobbyPlayerEntryHandler().get(player.getUniqueId()).getGameProfile();
        player.playSound(player.getLocation(), Sound.CLICK,50,50);


        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                if (row == 1 || col == 1 || col == 7 || row == 5) {
                    inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), row * 9 + col);
                }
            }
        }

        int colorslot = 11;
        switch (gamemode) {
            case KNOCKBACKFFA -> colorslot = 12;
            case BEDWARS -> colorslot = 13;
            case RUSHBW -> colorslot = 14;
            case SGFFA -> colorslot = 15;
        }

        inventory.getInventory().getItem(colorslot).setDurability((short) 0);

        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, 1, 3).setSkullOwner(player.getName())
                .setLore(statsLore(gameProfile, gamemode, StatsType.ALLTIME))
                .setName("§7Your §c§lALLTIME §" + gamemode.getColor() + gamemode.toString().toLowerCase() + " §7stats").build(), 26);
        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, 1, 3).setSkullOwner(player.getName())
                .setLore(statsLore(gameProfile, gamemode, StatsType.MONTHLY))
                .setName("§7Your §e§lMONTHLY §" + gamemode.getColor() + gamemode.toString().toLowerCase() + " §7stats").build(), 35);
        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, 1, 3).setSkullOwner(player.getName())
                .setLore(statsLore(gameProfile, gamemode, StatsType.DAILY))
                .setName("§7Your §a§lDAILY §" + gamemode.getColor() + gamemode.toString().toLowerCase() + " §7stats").build(), 44);


        String clickToOpen = "§7Click to show leaderboard";
        inventory.setItem(new ItemBuilder(Material.STICK).setLore(clickToOpen).setName("§8» §6MLGRush").build(), 2, event -> open(player, Gamemodes.MLGRUSH));
        inventory.setItem(new ItemBuilder(Material.SANDSTONE).setLore(clickToOpen).setName("§8» §6KnockbackFFA").build(), 3, event -> open(player, Gamemodes.KNOCKBACKFFA));
        inventory.setItem(new ItemBuilder(Material.BED).setLore(clickToOpen).setName("§8» §6Bedwars").build(), 4, event -> open(player, Gamemodes.BEDWARS));
        inventory.setItem(new ItemBuilder(Material.BLAZE_ROD).setLore(clickToOpen).setName("§8» §6Rush-Bedwars").build(), 5, event -> open(player, Gamemodes.RUSHBW));
        inventory.setItem(new ItemBuilder(Material.IRON_SWORD).setLore(clickToOpen).setName("§8» §6SGFFA").build(), 6, event -> open(player, Gamemodes.SGFFA));


        inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 5).setName("§a§lDAILY §f§lTOP 5 §8»").build(), 36);
        inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 4).setName("§e§lMONTHLY §f§lTOP 5 §8»").build(), 27);
        inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 14).setName("§c§lALLTIME §f§lTOP 5 §8»").build(), 18);


        int[] dailySlots = new int[]{38,39,40,41,42};
        int[] monthly = new int[]{29,30,31,32,33};
        int[] alltimeSlots = new int[]{20,21,22,23,24};





        player.openInventory(inventory.getInventory());
    }

    private List<String> statsLore(GameProfile gameProfile, Gamemodes gamemodes, StatsType statsType) {
        List<String> lore = Lists.newArrayList();

        if (!gameProfile.exists(gamemodes.toString())) {
            return Arrays.asList("§cno stats found!");
        }

        lore.add(" ");
        int rank = BukkitCore.getAPI().getRankingManager().getRankFromUUID(gamemodes, statsType, gameProfile.getPlayerId());
        if (rank == -1) {
            lore.add(" §cYou dont have a rank yet!");
        } else
        lore.add(" §7Rank §" + gamemodes.getColor() + "§l#" + BukkitCore.getAPI().getRankingManager().getRankFromUUID(gamemodes, statsType, gameProfile.getPlayerId()) + " " + statsType.toBeauty().toLowerCase() + " ");
        int trophies = (int) gameProfile.getStat(gamemodes.toString(), statsType, "trophies");
        lore.add(" §7Trophies§8: §" + gamemodes.getColor() + trophies + " §8(" + TrophieLeague.getEloRank(trophies).getName() + "§8) ");
        lore.add(" ");
        gamemodes.getStatKeys().forEach(statKey -> {
            if (!statKey.getName().equalsIgnoreCase("trophies")) lore.add(" §7" + toFancy(statKey.getName()) + "§8: §" + gamemodes.getColor() + gameProfile.getStat(gamemodes.toString(), statsType, statKey.getName()) + " ");
        });
        StringBuilder stringBuilder = new StringBuilder(" ");
        stringBuilder.append("§7K§8/§7D§8: §" + gamemodes.getColor() +
                calculateKD((int) gameProfile.getStat(gamemodes.toString(), statsType, "kills"), (int) gameProfile.getStat(gamemodes.toString(), statsType, "deaths")) + " ");
        if (gamemodes.getStatKeys().stream().anyMatch(statKey -> statKey.getName().equalsIgnoreCase("played_games"))) {


            int games = (int) gameProfile.getStat(gamemodes.toString(), statsType, "played_games");
            int wins = (int) gameProfile.getStat(gamemodes.toString(), statsType, "won_games");


            double current = 0;
            String show;
            if (games == 0 && wins == 0) {
                show = "§c-/-";
            } else {
                double percent = (100.0 / games);
                current = percent * wins;
                show = getWinrateColor((int) current) + String.valueOf(current) + "% ";
            }

            stringBuilder.append("§8︳ §7Winrate§8: " + show);
        }
        lore.add(stringBuilder.toString());
        lore.add(" ");

        return lore;
    }

    private String calculateKD(int kills, int deaths) {
        String KD;
        if (kills != 0 && deaths != 0) {
            double killsdeaths = (double) kills / (double) deaths;
            KD = new DecimalFormat("0.00").format(killsdeaths);
        } else {
            KD = "§c-/-";
        }
        return KD;
    }

    private String toFancy(String string) {

        switch (string) {
            case "kills":
                return "Kills";

            case "deaths":
                return "Deaths";

            case "played_games":
                return "Played games";

            case "won_games":
                return "Won games";

            case "destroyed_beds":
                return "Destroyed beds";
        }
        return "null";
    }

    private ChatColor getWinrateColor(int winrate) {
        if (winrate >= 90) {
            return ChatColor.DARK_GREEN;
        }
        if (winrate >= 70) {
            return ChatColor.GREEN;
        }
        if (winrate >= 50) {
            return ChatColor.YELLOW;
        }
        if (winrate >= 30) {
            return ChatColor.GOLD;
        }
        if (winrate >= 10) {
            return ChatColor.RED;
        }
        return ChatColor.DARK_RED;
    }

}
