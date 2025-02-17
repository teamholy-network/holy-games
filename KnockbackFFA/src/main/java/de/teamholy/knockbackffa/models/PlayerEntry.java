package de.teamholy.knockbackffa.models;

import de.teamholy.core.api.utility.TrophieLeague;
import de.teamholy.core.bukkit.perks.enums.PerkType;
import de.teamholy.core.bukkit.utils.InventoryUtils;
import de.teamholy.core.bukkit.utils.ScoreboardAPI;
import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.enums.*;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.PerkManager;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/* copyright by Yassino */
@Getter
@Setter
public class PlayerEntry {

    private Player player;
    private GameProfile statsProfile;

    private org.bukkit.inventory.Inventory inventory;
    private PlayerState playerState = PlayerState.LOBBY;
    private ScoreboardAPI scoreboardAPI;
    private MapEntry activeMap;

    private StatsType shownBoardStatsType = StatsType.ALLTIME;
    private KillStreakEffect killStreakEffect = KillStreakEffect.NONE;
    private ArmorColor armorColor = ArmorColor.GREY;
    private BowTrail bowTrail = BowTrail.NONE;
    private boolean seeEffects = true;

    private TeamEntry teamEntry;

    private int alltimeTrophies = 1000;

    @Getter
    public final Map<UUID, GameProfile> gameProfileCache = new HashMap<>();

    public PlayerEntry(Player player) {
        this.player = player;
        statsProfile = BukkitCore.getAPI().getGameService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()));



        createInv();
        if (!statsProfile.exists(Gamemodes.KNOCKBACKFFA.toString())) {
            for (StatsType time : StatsType.values()) {
                for (Gamemodes.StatKey statKey : Gamemodes.KNOCKBACKFFA.getStatKeys()) statsProfile.setStat(Gamemodes.KNOCKBACKFFA.toString(), time, statKey.getName(), statKey.getDefaultValue());
            }



            statsProfile.setSetting(Gamemodes.KNOCKBACKFFA.toString(),"invsort", InventoryUtils.inventoryToString(inventory));
            statsProfile.setSetting(Gamemodes.KNOCKBACKFFA.toString(),"seeEffects", String.valueOf(seeEffects));
            statsProfile.setSetting(Gamemodes.KNOCKBACKFFA.toString(),"killStreakEffect", killStreakEffect.toString());
            statsProfile.setSetting(Gamemodes.KNOCKBACKFFA.toString(),"armorColor", armorColor.toString());
            statsProfile.setSetting(Gamemodes.KNOCKBACKFFA.toString(),"bowTrail", bowTrail.toString());
            statsProfile.setSetting(Gamemodes.KNOCKBACKFFA.toString(),"scoreboardStats", shownBoardStatsType.toString());


            BukkitCore.getAPI().getGameService().saveEntity(statsProfile, true, true);
        } else {

            String inventory = statsProfile.getSetting(Gamemodes.KNOCKBACKFFA.toString(), "invsort");
            if (inventory.isEmpty()) {
                createInv();
            } else {
                setInventory(InventoryUtils.inventoryFromString(inventory));
            }

            alltimeTrophies = (int) statsProfile.getStat(Gamemodes.KNOCKBACKFFA.toString(),StatsType.ALLTIME,"trophies");
            seeEffects = Boolean.valueOf(statsProfile.getSetting(Gamemodes.KNOCKBACKFFA.toString(),"seeEffects"));
            killStreakEffect = KillStreakEffect.valueOf(statsProfile.getSetting(Gamemodes.KNOCKBACKFFA.toString(),"killStreakEffect"));
            bowTrail = BowTrail.valueOf(statsProfile.getSetting(Gamemodes.KNOCKBACKFFA.toString(),"bowTrail"));
            armorColor = ArmorColor.valueOf(statsProfile.getSetting(Gamemodes.KNOCKBACKFFA.toString(),"armorColor"));
            shownBoardStatsType = StatsType.valueOf(statsProfile.getSetting(Gamemodes.KNOCKBACKFFA.toString(),"scoreboardStats"));


            boolean needUpdate = false;
            if (!player.hasPermission("teamholy.perk.premium") && getKillStreakEffect() != KillStreakEffect.NONE) needUpdate = true;
            if (!player.hasPermission("teamholy.perk.premium") && getArmorColor() != ArmorColor.GREY) needUpdate = true;
            if (!player.hasPermission("teamholy.perk.premium") && getBowTrail() != BowTrail.NONE) needUpdate = true;
            if (needUpdate) {
                setKillStreakEffect(KillStreakEffect.NONE);
                setArmorColor(ArmorColor.GREY);
                setBowTrail(BowTrail.NONE);
            }

        }


        Bukkit.getScheduler().runTask(KnockbackFFA.getInstance(), () -> {
            scoreboardAPI = new ScoreboardAPI().createScoreboard(player, "§e");
            setScoreboard();
        });
    }

    public void leaveGame() {
        activeMap.getPlayers().remove(player);
        activeMap.updateSign();
        activeMap = null;
        performSpawn();
        setScoreboard();
    }

    public void joinGame(MapEntry mapEntry) {
        activeMap = mapEntry;
        player.teleport(mapEntry.getSpawn());
        mapEntry.getPlayers().add(player);
        player.getInventory().clear();
        player.getInventory().setItem(4, new ItemBuilder(Material.MAGMA_CREAM).setName("§8» §6Back to lobby §8(§7rightclick§8)").build());
        playerState = PlayerState.INGAME;
        mapEntry.updateSign();
        updateMapScoreboard();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(player);
        }

        player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "Use /quit to return to the lobby");
    }


    public void performSpawn() {
        player.setHealth(20);
        playerState = PlayerState.LOBBY;
        player.teleport(BukkitCore.getInstance().getLocationManager().getLocation("lobby"));
        setLobbyItems();
    }

    public void saveData() {
        statsProfile = BukkitCore.getAPI().getGameService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()));
        statsProfile.setSetting(Gamemodes.KNOCKBACKFFA.toString(),"invsort",InventoryUtils.inventoryToString(inventory));
        statsProfile.setSetting(Gamemodes.KNOCKBACKFFA.toString(),"seeEffects", String.valueOf(seeEffects));
        statsProfile.setSetting(Gamemodes.KNOCKBACKFFA.toString(),"killStreakEffect", killStreakEffect.toString());
        statsProfile.setSetting(Gamemodes.KNOCKBACKFFA.toString(),"armorColor", armorColor.toString());
        statsProfile.setSetting(Gamemodes.KNOCKBACKFFA.toString(),"bowTrail", bowTrail.toString());
        statsProfile.setSetting(Gamemodes.KNOCKBACKFFA.toString(),"scoreboardStats", shownBoardStatsType.toString());
        BukkitCore.getAPI().getGameService().saveEntity(statsProfile, true, true);
    }

    public void setIngameItems() {
        player.getInventory().clear();
        int slot = 0;
        player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 4).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(armorColor.getColor()).build());
        player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 4).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(armorColor.getColor()).build());
        player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 4).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(armorColor.getColor()).build());
        player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 4).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(armorColor.getColor()).build());
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null && itemStack.getType() != null) {
                if (itemStack.getType() == Material.STICK) {
                    player.getInventory().setItem(slot, BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.STICK).setUnbreakable().setEnchantments(Enchantment.KNOCKBACK,1).build());
                } else if (itemStack.getType() == Material.SANDSTONE) {
                    player.getInventory().setItem(slot, BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.BLOCK).setAmount(64).build());
                } else {
                    this.player.getInventory().setItem(slot, itemStack);
                }
            }
            slot++;
        }
    }

    public void setLobbyItems() {
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.getInventory().setItem(2, new ItemBuilder(Material.ARMOR_STAND).setName("§8» §6Inventory §8(§7rightclick§8)").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.REDSTONE_COMPARATOR).setName("§8» §6Perks & Settings §8(§7rightclick§8)").build());
        player.getInventory().setItem(6, new ItemBuilder(Material.SLIME_BALL).setName("§8» §6Leave §8(§7rightclick§8)").build());
    }

    public void createInv() {
        inventory = Bukkit.createInventory(null, 9, "inv");
        for (Items items : Items.values()) {
            inventory.setItem(items.getSlot(), items.getItemStack());
        }
    }

    public void openInventorySort() {
        org.bukkit.inventory.Inventory sortInv = Bukkit.createInventory(null, 9, "§8» §6Inventory Sort");
        sortInv.setContents(getInventory().getContents());
        player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
        player.openInventory(sortInv);
        player.getInventory().clear();
    }

    public void openPerks() {
        de.teamholy.core.bukkit.utils.Inventory inventory = new Inventory("§8» §6Perks", 9);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        inventory.setItem(new ItemBuilder(Material.STICK,1).setName("§8» §6Stick").build(),1,(event) -> {
            BukkitCore.getInstance().getPerkManager().openSecondPerkInventory(player,PerkType.STICK, PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL);
        });

        inventory.setItem(new ItemBuilder(Material.SANDSTONE,1).setName("§8» §6Block").build(),2,(event) -> {
            BukkitCore.getInstance().getPerkManager().openSecondPerkInventory(player,PerkType.BLOCK, PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL);
        });

        inventory.setItem(new ItemBuilder(Material.BLAZE_POWDER).setName("§8» §6KillStreak effects").build(), 3, inventoryClickEvent -> KnockbackFFA.getInstance().getPerkInventoriesHandler().openKillStreakEffects(this));
        inventory.setItem(new ItemBuilder(Material.LEATHER_CHESTPLATE).setName("§8» §6Armor color").build(), 4, inventoryClickEvent -> KnockbackFFA.getInstance().getPerkInventoriesHandler().openArmorColor(this));
        inventory.setItem(new ItemBuilder(Material.BOW).setName("§8» §6Bow trail").build(), 5, inventoryClickEvent -> KnockbackFFA.getInstance().getPerkInventoriesHandler().openBowTrails(this));

        ItemBuilder scoreboardStats = new ItemBuilder(Material.PAPER).setName("§8» §6Scoreboard stats");

        scoreboardStats.setLore(Arrays.stream(StatsType.values())
                .map(value -> (shownBoardStatsType == value) ? value.toBeauty() : "§7" + value.toBeauty().substring(2))
                .collect(Collectors.toList()));


        inventory.setItem(scoreboardStats.build(), 7, event -> {
            if (shownBoardStatsType == StatsType.DAILY) {
                setShownBoardStatsType(StatsType.MONTHLY);
            } else if (shownBoardStatsType == StatsType.MONTHLY) {
                setShownBoardStatsType(StatsType.ALLTIME);
            } else if (shownBoardStatsType == StatsType.ALLTIME) {
                setShownBoardStatsType(StatsType.DAILY);
            }
            player.playSound(player.getLocation(), Sound.CLICK, 1F, 100F);
            player.closeInventory();
            player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "The scoreboard now shows your " + shownBoardStatsType.toBeauty() + " §7stats");
            setScoreboard();
        });

        inventory.setItem(new ItemBuilder(Material.INK_SACK,1,(byte)(seeEffects ? 10 : 1))
                .setName("§8» §6Allow trails & effects").setLore("§7Disable if you have lags","§7or the particles are annoying you","§cnote: not working on lightning and tornado").build(),8,event -> {
            setSeeEffects(!seeEffects);
            player.playSound(player.getLocation(), Sound.NOTE_PLING,2f,2f);
            player.closeInventory();
        });


        player.openInventory(inventory.getInventory());
    }

    public void setScoreboard() {
        scoreboardAPI.clearScoreboard();
        BukkitCore.getAPI().getGameService().getEntityAsync(player.getUniqueId(),() -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()),gameProfile -> {
            scoreboardAPI.setLine(13, " §8§m--------------- ");
            scoreboardAPI.setLine(12,"§4");
            scoreboardAPI.setLine(11," §7Map§8: §e-/-");
            scoreboardAPI.setLine(10," §7Team§8: §e-/-");
            scoreboardAPI.setLine(9,"§3");
            scoreboardAPI.setLine(8," §7Stats§8: " + shownBoardStatsType.toBeauty());
            scoreboardAPI.setLine(7,"§2");
            scoreboardAPI.setLine(6," §7Rank§8: §e#" + BukkitCore.getAPI().getRankingManager().getRankFromUUID(Gamemodes.KNOCKBACKFFA,shownBoardStatsType,player.getUniqueId()));
            int elo = (int) gameProfile.getStat(Gamemodes.KNOCKBACKFFA.toString(),shownBoardStatsType,"trophies");
            scoreboardAPI.setLine(5," §7Trophies§8: §e"  + elo + " " + TrophieLeague.getEloRank(elo).getShortName());
            scoreboardAPI.setLine(4," §7Kills§8: §e" + gameProfile.getStat(Gamemodes.KNOCKBACKFFA.toString(),shownBoardStatsType,"kills"));
            scoreboardAPI.setLine(3," §7Deaths§8: §e" + gameProfile.getStat(Gamemodes.KNOCKBACKFFA.toString(),shownBoardStatsType,"deaths"));
            scoreboardAPI.setLine(2," §7K/D§8: §e" + BukkitCore.getInstance().getStatsManager().calculateKD(gameProfile.getStat(Gamemodes.KNOCKBACKFFA.toString(),shownBoardStatsType,"kills"), gameProfile.getStat(Gamemodes.KNOCKBACKFFA.toString(),shownBoardStatsType,"deaths")));
            scoreboardAPI.setLine(1, "§5");
            scoreboardAPI.setLine(0, " §8§m--------------- ");
            gameProfileCache.put(player.getUniqueId(),gameProfile);
            Bukkit.getScheduler().runTask(KnockbackFFA.getInstance(),() -> scoreboardAPI.build());
        });
    }

    private void updateMapScoreboard() {
        if (playerState == PlayerState.LOBBY) {
            scoreboardAPI.updateLine(11," §7Map§8: §e-/-");
        } else if (playerState == PlayerState.INGAME) {
            scoreboardAPI.updateLine(11," §7Map§8: §e" + getActiveMap().getMapName());
        }
    }

    public void updateTeamScore() {
        if (teamEntry == null) {
            scoreboardAPI.updateLine(10," §7Team§8: §e-/-");
        } else {
            scoreboardAPI.updateLine(10," §7Team§8: §e" + teamEntry.getTag());
        }
    }

    public void updateScoreboard() {
        BukkitCore.getAPI().getGameService().getEntityAsync(player.getUniqueId(),() -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()),gameProfile -> {
            scoreboardAPI.updateLine(6," §7Rank§8: §e#" + BukkitCore.getAPI().getRankingManager().getRankFromUUID(Gamemodes.KNOCKBACKFFA,shownBoardStatsType,player.getUniqueId()));
            int elo = (int) gameProfile.getStat(Gamemodes.KNOCKBACKFFA.toString(),shownBoardStatsType,"trophies");
            scoreboardAPI.updateLine(5," §7Trophies§8: §e"  + elo + " " + TrophieLeague.getEloRank(elo).getShortName());
            scoreboardAPI.updateLine(4," §7Kills§8: §e" + gameProfile.getStat(Gamemodes.KNOCKBACKFFA.toString(),shownBoardStatsType,"kills"));
            scoreboardAPI.updateLine(3," §7Deaths§8: §e" + gameProfile.getStat(Gamemodes.KNOCKBACKFFA.toString(),shownBoardStatsType,"deaths"));
            scoreboardAPI.updateLine(2," §7K/D§8: §e" + BukkitCore.getInstance().getStatsManager().calculateKD(gameProfile.getStat(Gamemodes.KNOCKBACKFFA.toString(),shownBoardStatsType,"kills"), gameProfile.getStat(Gamemodes.KNOCKBACKFFA.toString(),shownBoardStatsType,"deaths")));
            Bukkit.getScheduler().runTask(KnockbackFFA.getInstance(),() -> scoreboardAPI.build());
        });
    }
}
