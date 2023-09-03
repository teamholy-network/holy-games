package de.teamholy.lobby.leaderboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.skin.SkinProfile;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.api.utility.TrophieLeague;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import de.teamholy.lobby.Lobby;
import de.teamholy.lobby.lobbyplayer.LobbyPlayer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.redisson.api.RScoredSortedSet;
import org.redisson.client.protocol.ScoredEntry;

import java.text.DecimalFormat;
import java.util.*;

/* copyright by Yassino */
public class LeaderboardInventory {


    public HashMap<Gamemodes, HashMap<StatsType, List<TopEntry>>> topEntries = Maps.newHashMap();

    public LeaderboardInventory() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Lobby.getInstance(), () -> {
            long time = System.currentTimeMillis();
            topEntries.clear();
            for (Gamemodes value : Gamemodes.values()) {

                HashMap<StatsType, List<TopEntry>> hashMap = Maps.newHashMap();
                topEntries.put(value, hashMap);
                for (StatsType statsType : StatsType.values()) {

                    List<TopEntry> topEntryList = Lists.newArrayList();
                    RScoredSortedSet scoredSortedSet = BukkitCore.getAPI().getRedissonManager().getRedissonClient().getScoredSortedSet(value.toString() + "_" + statsType.toString());

                    scoredSortedSet.entryRange(0, 4).forEach(o -> {
                        ScoredEntry<UUID> scoredEntry = (ScoredEntry<UUID>) o;

                        System.out.println(scoredEntry.getScore());
                        if (scoredEntry.getScore() > 1000) {
                            TopEntry topEntry = new TopEntry();

                            GameProfile gameProfile = BukkitCore.getAPI().getGameService().getEntity(scoredEntry.getValue(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(scoredEntry.getValue()));
                            SkinProfile skinProfile = BukkitCore.getAPI().getSkinService().getEntity(scoredEntry.getValue(), () -> BukkitCore.getAPI().getSkinService().getRepository().findFirstById(scoredEntry.getValue()));
                            PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService().getEntity(scoredEntry.getValue(), () -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(scoredEntry.getValue()));

                            topEntry.setGameProfile(gameProfile);
                            topEntry.setSkinProfile(skinProfile);
                            topEntry.setNameWithColor(PlayerRank.valueOf(playerProfile.getRank()).getColorCode() + playerProfile.getPlayerName());

                            topEntryList.add(topEntry);

                            System.out.println(playerProfile.getPlayerName() + " - " + statsType + " - " + value + " - " + scoredEntry.getScore());
                        }

                    });
                    Collections.reverse(topEntryList);
                    hashMap.put(statsType, topEntryList);

                }

                topEntries.put(value, hashMap);

            }

            System.out.println("Ended leaderboard cache in " + (System.currentTimeMillis() - time) + "ms");


        }, 0, 20 * 60 * 10);
    }

    public void open(Player player, Gamemodes gamemode) {
        Inventory inventory = new Inventory("§8» §6Leaderboard in §" + gamemode.getColor() + gamemode.toString().toLowerCase(), 6 * 9);
        GameProfile gameProfile = Lobby.getInstance().getLobbyPlayerEntryHandler().get(player.getUniqueId()).getGameProfile();
        player.playSound(player.getLocation(), Sound.CLICK, 50, 50);


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

        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, 1, 3)
                .setSkullMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM0ZTQ0MWVhYzg4NG" +
                        "RlMzM0N2E4Nzc1YTA3YTY2YmJjNGM4MmEyNGVkMmQwY2ZlYjFhY2FmNmNlOTlkNTNiNiJ9fX0=", "")
                .setName("§6Infos")
                .setLore(" ", " §f§lSTATSRESET", " §7The §adaily §7stats will be reset at 00:00 CET", " §7The §cmonthly §7stats will be reset at the first of the month"
                        , " ", " §3§lCHAMPION §f§lRANK", " §7The §a#1 §7from every mode §adaily", " §7will get the §3Champion§7 rank for §c24 hours§7!", " "
                )
                .build(), 49);


        int daily = 38;
        for (TopEntry topEntry : topEntries.get(gamemode).get(StatsType.DAILY)) {
            inventory.setItem(
                    new ItemBuilder(Material.SKULL_ITEM, 1, 3).setSkullMeta(topEntry.getSkinProfile().getValue(), "")
                            .setLore(statsLore(topEntry.getGameProfile(), gamemode, StatsType.DAILY)).setName(topEntry.getNameWithColor()).build()
                    , daily);
            daily++;
        }

        int monthly = 29;
        for (TopEntry topEntry : topEntries.get(gamemode).get(StatsType.MONTHLY)) {
            inventory.setItem(
                    new ItemBuilder(Material.SKULL_ITEM, 1, 3).setSkullMeta(topEntry.getSkinProfile().getValue(), "")
                            .setLore(statsLore(topEntry.getGameProfile(), gamemode, StatsType.MONTHLY)).setName(topEntry.getNameWithColor()).build()
                    , monthly);
            monthly++;
        }

        int alltime = 20;
        for (TopEntry topEntry : topEntries.get(gamemode).get(StatsType.ALLTIME)) {
            inventory.setItem(
                    new ItemBuilder(Material.SKULL_ITEM, 1, 3).setSkullMeta(topEntry.getSkinProfile().getValue(), "")
                            .setLore(statsLore(topEntry.getGameProfile(), gamemode, StatsType.ALLTIME)).setName(topEntry.getNameWithColor()).build()
                    , alltime);
            alltime++;
        }


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
            if (!statKey.getName().equalsIgnoreCase("trophies"))
                lore.add(" §7" + toFancy(statKey.getName()) + "§8: §" + gamemodes.getColor() + gameProfile.getStat(gamemodes.toString(), statsType, statKey.getName()) + " ");
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

    @Getter
    @NoArgsConstructor
    @Setter
    public class TopEntry {

        private GameProfile gameProfile;
        private SkinProfile skinProfile;
        private String nameWithColor;


    }

}
