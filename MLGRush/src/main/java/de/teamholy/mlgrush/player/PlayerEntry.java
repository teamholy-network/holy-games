package de.teamholy.mlgrush.player;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.utils.InventoryUtils;
import de.teamholy.api.bukkit.utils.scoreboard.ScoreboardAPI;
import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.enums.BlockResetType;
import de.teamholy.mlgrush.enums.GameType;
import de.teamholy.mlgrush.enums.Items;
import de.teamholy.mlgrush.game.GameEntry;
import de.teamholy.mlgrush.map.MapEntry;
import de.teamholy.mlgrush.maptemplate.MapTemplateEntry;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.PerkType;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class PlayerEntry {

    private Player player;
    private PlayerEntry gotLastHit;
    private PlayerEntry challengedPlayer;
    private ScoreboardAPI scoreboardAPI;
    private org.bukkit.inventory.Inventory inventory;
    private PlayerState playerState = PlayerState.LOBBY;
    private GameEntry gameEntry;
    private Long queueCooldown = System.currentTimeMillis();

    private IngamePlayer ingamePlayer = new IngamePlayer();

    private boolean noHitDelay = false, onlyVerticalKnockback = false, noIngameMessage = false;
    private BlockResetType blockResetType = BlockResetType.OFF;
    private StatsType shownBoardStatsType = StatsType.ALLTIME;

    GameProfile statsProfile;



    public PlayerEntry(Player player) {
        this.player = player;

        statsProfile = BukkitCore.getAPI().getGameService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()));


        createInv();
        if (!statsProfile.exists(Gamemodes.MLGRUSH.toString())) {

            for (StatsType time : StatsType.values()) {
                for (String string : Gamemodes.MLGRUSH.getStatKeys())
                    statsProfile.setStat(Gamemodes.MLGRUSH.toString(), time, string, 0);
            }

            statsProfile.setSetting(Gamemodes.MLGRUSH.toString(), "invsort", InventoryUtils.inventoryToString(inventory));
            statsProfile.setSetting(Gamemodes.MLGRUSH.toString(), "noHitDelay", String.valueOf(noHitDelay));
            statsProfile.setSetting(Gamemodes.MLGRUSH.toString(), "onlyVerticalKnockback", String.valueOf(onlyVerticalKnockback));
            statsProfile.setSetting(Gamemodes.MLGRUSH.toString(), "noIngameMessages", String.valueOf(noIngameMessage));
            statsProfile.setSetting(Gamemodes.MLGRUSH.toString(), "resetBlocksOnDeath", blockResetType.toString());
            statsProfile.setSetting(Gamemodes.MLGRUSH.toString(), "scoreboardStats", shownBoardStatsType.toString());
            BukkitCore.getAPI().getGameService().saveEntity(statsProfile, true, true);
        } else {
            String inventory = statsProfile.getSetting(Gamemodes.MLGRUSH.toString(), "invsort");
            if (inventory.isEmpty()) {
                createInv();
            } else {
                setInventory(InventoryUtils.inventoryFromString(inventory));
            }

            noHitDelay = Boolean.parseBoolean(statsProfile.getSetting(Gamemodes.MLGRUSH.toString(), "noHitDelay"));
            onlyVerticalKnockback = Boolean.parseBoolean(statsProfile.getSetting(Gamemodes.MLGRUSH.toString(), "onlyVerticalKnockback"));
            noIngameMessage = Boolean.parseBoolean(statsProfile.getSetting(Gamemodes.MLGRUSH.toString(), "noIngameMessages"));
            blockResetType = BlockResetType.valueOf(statsProfile.getSetting(Gamemodes.MLGRUSH.toString(), "resetBlocksOnDeath"));
            shownBoardStatsType = StatsType.valueOf(statsProfile.getSetting(Gamemodes.MLGRUSH.toString(), "scoreboardStats"));
        }

        Bukkit.getScheduler().runTask(MLGRush.getInstance(), () -> {
            scoreboardAPI = new ScoreboardAPI().createScoreboard(player, "§6");
            setScoreboard();
        });

    }


    public void performSpawn() {
        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodLevel(20);
        player.setHealth(20);
        setItemsSpawn();
    }

    public void openMapSelection(GameType gameType) {
        MLGRush.getInstance().getQueueHandler().getQueue().remove(this);
        Inventory inventory = new Inventory("§8» §6Map Selection", 9);
        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        int i = 0;
        List<MapTemplateEntry> arrayList = MLGRush.getInstance().getMapTemplateEntryHandler().values().stream().filter(mapTemplateEntry -> mapTemplateEntry.getTemplatesCount().get(0).getGameType() == gameType).collect(Collectors.toList());
        for (MapTemplateEntry mapEntry : arrayList) {
            if (mapEntry.getFreeTemplatesCount().size() != 0) {
                inventory.setItem(new ItemBuilder(mapEntry.getMaterial(), mapEntry.getFreeTemplatesCount().size(), (byte) 0).setName("§8» §6" + mapEntry.getName()).build(), i, event -> {
                    if (mapEntry.getFreeTemplatesCount().size() == 0) {
                        player.sendMessage("§cAll arenas on the map are full");
                    } else {
                        MapEntry tempMap = mapEntry.getFreeArena();
                        if (tempMap != null) {
                            gameEntry.setupGame(tempMap);
                            MLGRush.getInstance().getGameEntryHandler().put(gameEntry.getMapEntry().getMapId(), gameEntry);
                        } else {
                            player.sendMessage("§cError while selecting map");
                        }
                    }
                });
                i++;
            }
        }
        player.openInventory(inventory.getInventory());
    }

    public void killPlayer() {
        if (getGotLastHit() != null) {
            PlayerEntry killer = gotLastHit;
            if (!isNoIngameMessage()) {
                player.sendMessage(MLGRush.getInstance().getPrefix() + "You were knocked down by §6" + killer.getPlayer().getDisplayName());
            }

            if (!killer.isNoIngameMessage()) {
                killer.getPlayer().sendMessage(MLGRush.getInstance().getPrefix() + "You knocked down §6" + player.getDisplayName());
            }
            killer.getIngamePlayer().setKills(killer.getIngamePlayer().getKills() + 1);
            BukkitHolyAPI.getInstance().getStatsManager().addStat(Gamemodes.MLGRUSH.toString(), "kills", killer.getPlayer().getUniqueId());

        } else {
            if (!isNoIngameMessage()) {
                player.sendMessage(MLGRush.getInstance().getPrefix() + "You fell down");
            }
        }
        ingamePlayer.setDeaths(ingamePlayer.getDeaths() + 1);
        gameEntry.teleportToSpawn(this);
        gameEntry.removeBlocks(player, gameEntry.getResetBlocksOnDeath());
        BukkitHolyAPI.getInstance().getStatsManager().addStat(Gamemodes.MLGRUSH.toString(), "deaths", player.getUniqueId());
        setIngameItems();
        setGotLastHit(null);
    }

    public void setItemsSpawn() {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItem(1, new ItemBuilder(Material.REDSTONE_COMPARATOR).setName("§8» §6Settings §8(§7rightclick§8)").build());
        player.getInventory().setItem(3, new ItemBuilder(Material.IRON_SWORD).setName("§8» §6Challenger §8(§7leftclick§8)").setUnbreakable().build());
        player.getInventory().setItem(5, new ItemBuilder(Material.EYE_OF_ENDER).setName("§8» §6Spectate §8(§7rightclick§8)").build());
        player.getInventory().setItem(7, new ItemBuilder(Material.SLIME_BALL).setName("§8» §6Leave §8(§7rightclick§8)").build());
    }

    public void setIngameItems() {
        player.getInventory().clear();
        int slot = 0;
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null && itemStack.getType() != null) {
                if (itemStack.getType() == Material.STICK) {
                    player.getInventory().setItem(slot, BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.STICK).setEnchantments(Enchantment.KNOCKBACK,1).build());
                } else if (itemStack.getType() == Material.SANDSTONE) {
                    player.getInventory().setItem(slot, BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.BLOCK).setAmount(64).build());

                } else {
                    this.player.getInventory().setItem(slot, itemStack);
                }
            }
            slot++;
        }
    }

    public void setBlocksItems() {
        int slot = 0;
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack != null && itemStack.getType() != null) {
                if (itemStack.getType() == Material.SANDSTONE)
                    player.getInventory().setItem(slot, BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.BLOCK).setAmount(64).build());
            }
            slot++;
        }
        slot++;
    }

    public void createInv() {
        inventory = Bukkit.createInventory(null, 9, "inv");
        for (Items items : Items.values()) {
            inventory.setItem(items.getSlot(), items.getItemStack());
        }
    }

    public void saveData() {
        statsProfile = BukkitCore.getAPI().getGameService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()));
        statsProfile.setSetting(Gamemodes.MLGRUSH.toString(), "invsort", InventoryUtils.inventoryToString(inventory));
        statsProfile.setSetting(Gamemodes.MLGRUSH.toString(), "noHitDelay", String.valueOf(noHitDelay));
        statsProfile.setSetting(Gamemodes.MLGRUSH.toString(), "onlyVerticalKnockback", String.valueOf(onlyVerticalKnockback));
        statsProfile.setSetting(Gamemodes.MLGRUSH.toString(), "noIngameMessages", String.valueOf(noIngameMessage));
        statsProfile.setSetting(Gamemodes.MLGRUSH.toString(), "resetBlocksOnDeath", blockResetType.toString());
        statsProfile.setSetting(Gamemodes.MLGRUSH.toString(), "scoreboardStats", shownBoardStatsType.toString());
        BukkitCore.getAPI().getGameService().saveEntity(statsProfile, true, true);
    }

    public void updateScoreBoard() {
        scoreboardAPI.updateLine(7, " §7Timer§8: §6" + MLGRush.getInstance().getPlayerUtils().formatSeconds(gameEntry.getTimeSinceStart()));
    }

    public void removeFromSpectator(boolean isInSpectator) {
        gameEntry.getPlayersInArena().remove(this);
        gameEntry = null;
        if (!isInSpectator)
            performSpawn();
        player.setFlying(false);
        player.setAllowFlight(false);
        player.spigot().setCollidesWithEntities(true);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.showPlayer(player);
        }
        setPlayerState(PlayerState.LOBBY);
        setScoreboard();
    }

    public void setSpectator(GameEntry gameEntry) {
        if (MLGRush.getInstance().getQueueHandler().getQueue().containsKey(this)) {
            MLGRush.getInstance().getQueueHandler().getQueue().remove(this);
            MLGRush.getInstance().getQueueHandler().updateQueue();
        }

        this.gameEntry = gameEntry;
        setPlayerState(PlayerState.SPECTATE);
        setScoreboard();
        setSpectatorItems();
        gameEntry.getPlayersPlaying().forEach(playerEntry -> playerEntry.getPlayer().hidePlayer(player));
        player.spigot().setCollidesWithEntities(false);
        gameEntry.getPlayersInArena().add(this);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 0));
        Bukkit.getScheduler().runTaskLater(MLGRush.getInstance(), () -> player.teleport(gameEntry.getPlayerOne().getPlayer().getLocation()), 1);
    }

    public void setSpectatorItems() {
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.getInventory().setItem(3, new ItemBuilder(Material.NETHER_STAR).setName("§8» §6Spectate §8(§7rightclick§8)").setUnbreakable().setAttributs().build());
        player.getInventory().setItem(5, new ItemBuilder(Material.MAGMA_CREAM).setName("§8» §6Back to Spawn §8(§7rightclick§8)").build());
    }


    public void setScoreboard() {
        if (playerState == PlayerState.LOBBY) {
            scoreboardAPI.clearScoreboard();
            BukkitCore.getAPI().getGameService().getEntityAsync(player.getUniqueId(),() -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()),gameProfile -> {
                scoreboardAPI.setLine(13, " §8§m--------------- ");
                scoreboardAPI.setLine(12,"§2");
                scoreboardAPI.setLine(11," §7Stats§8: " + shownBoardStatsType.toBeauty());
                scoreboardAPI.setLine(10,"§2");
                scoreboardAPI.setLine(9," §7Rank§8: §6#" + BukkitCore.getAPI().getRankingManager().getRankFromUUID(Gamemodes.MLGRUSH,shownBoardStatsType,player.getUniqueId()));
                scoreboardAPI.setLine(8," §7Kills§8: §6" + gameProfile.getStat(Gamemodes.MLGRUSH.toString(),shownBoardStatsType,"kills"));
                scoreboardAPI.setLine(7," §7Deaths§8: §6" + gameProfile.getStat(Gamemodes.MLGRUSH.toString(),shownBoardStatsType,"deaths"));
                scoreboardAPI.setLine(6," §7Games§8: §6" + gameProfile.getStat(Gamemodes.MLGRUSH.toString(),shownBoardStatsType,"played_games"));
                scoreboardAPI.setLine(5," §7Wins§8: §6" + gameProfile.getStat(Gamemodes.MLGRUSH.toString(),shownBoardStatsType,"won_games"));
                scoreboardAPI.setLine(4," §7Beds§8: §6" + gameProfile.getStat(Gamemodes.MLGRUSH.toString(),shownBoardStatsType,"destroyed_beds"));
                scoreboardAPI.setLine(3," §7K/D§8: §6" + BukkitHolyAPI.getInstance().getStatsManager().calculateKD(gameProfile.getStat(Gamemodes.MLGRUSH.toString(),shownBoardStatsType,"kills"), gameProfile.getStat(Gamemodes.MLGRUSH.toString(),shownBoardStatsType,"deaths")));
                scoreboardAPI.setLine(2, "§5");
                scoreboardAPI.setLine(1, " §8§m--------------- ");
                scoreboardAPI.setLine(0,"§o"+Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName());
                Bukkit.getScheduler().runTask(MLGRush.getInstance(),() -> scoreboardAPI.build());
            });
        } else {
            scoreboardAPI.clearScoreboard();
            scoreboardAPI.setLine(10, " §8§m--------------- ");
            scoreboardAPI.setLine(9, "§1");
            scoreboardAPI.setLine(8, " §7Map§8: §6" + gameEntry.getMapEntry().getMapId());
            scoreboardAPI.setLine(7, " §7Timer§8: §6" + MLGRush.getInstance().getPlayerUtils().formatSeconds(gameEntry.getTimeSinceStart()));
            scoreboardAPI.setLine(6, "§2");
            scoreboardAPI.setLine(5, " §7NoHitDelay§8: " + (gameEntry.isNoHitDelay() ? "§a✔" : "§c✘"));
            scoreboardAPI.setLine(4, " §7OnlyYKnock§8: " + (gameEntry.isOnlyVerticalKnockback() ? "§a✔" : "§c✘"));
            scoreboardAPI.setLine(3, " §7DeathReset§8: " + (gameEntry.getResetBlocksOnDeath() != BlockResetType.OFF ? "§a✔" : "§c✘"));
            scoreboardAPI.setLine(2, "§3");
            scoreboardAPI.setLine(1, " §8§m--------------- ");
            scoreboardAPI.setLine(0,"§o"+Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName());
            scoreboardAPI.build();
        }
    }

    public void openIngameSettings() {
        Inventory inventory = new Inventory("§8» §6Ingame Settings", 9);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        ItemBuilder noHitDealy = new ItemBuilder(Material.FEATHER, 1, (byte) 0).setName("§8» §6NoHitDelay").setAttributs().setLore("§8» §c✘");
        ItemBuilder verticalKnockback = new ItemBuilder(Material.DIAMOND_BOOTS, 1, (byte) 0).setName("§8» §6OnlyYKnock").setAttributs().setLore("§8» §c✘");
        ItemBuilder noIngameMessage = new ItemBuilder(Material.PAPER, 1, (byte) 0).setName("§8» §6No Ingame Message").setAttributs().setLore("§8» §c✘");
        ItemBuilder resetMapOnDeath = new ItemBuilder(Material.SANDSTONE, 1, (byte) 0).setName("§8» §6Reset Blocks on death").setAttributs().setLore("§8» §c✘");


        if (isNoHitDelay()) {
            noHitDealy.setLore("§8» §a✔");
            noHitDealy.setEnchantments(Enchantment.KNOCKBACK, 1);
        }

        if (isOnlyVerticalKnockback()) {
            verticalKnockback.setLore("§8» §a✔");
            verticalKnockback.setEnchantments(Enchantment.KNOCKBACK, 1);
        }

        if (isNoIngameMessage()) {
            noIngameMessage.setLore("§8» §a✔");
            noIngameMessage.setEnchantments(Enchantment.KNOCKBACK, 1);
        }

        {
            resetMapOnDeath.setLore(
                    " " + (blockResetType == BlockResetType.OFF ? "§c✘" : "§7✘"),
                    " "+(blockResetType == BlockResetType.THREE_SECONDS ? "§aAfter three seconds" : "§7After three seconds"),
                    " "+(blockResetType == BlockResetType.INSTANT_ANYONE ? "§aInstant if anyone dies" : "§7Instant if anyone dies"),
                    " "+(blockResetType == BlockResetType.INSTANT_VICTIM ? "§aInstant if victim dies" : "§7Instant if victim dies")
                    );
        }

        inventory.setItem(noHitDealy.build(), 1, event -> {
            setNoHitDelay(!isNoHitDelay());
            player.playSound(player.getLocation(), Sound.CLICK, 1F, 100F);
            openIngameSettings();
        });

        inventory.setItem(verticalKnockback.build(), 3, event -> {
            setOnlyVerticalKnockback(!isOnlyVerticalKnockback());
            player.playSound(player.getLocation(), Sound.CLICK, 1F, 100F);
            openIngameSettings();
        });

        inventory.setItem(noIngameMessage.build(), 5, event -> {
            setNoIngameMessage(!isNoIngameMessage());
            player.playSound(player.getLocation(), Sound.CLICK, 1F, 100F);
            openIngameSettings();
        });

        inventory.setItem(resetMapOnDeath.build(), 7, event -> {
            if (blockResetType == BlockResetType.OFF) {
                setBlockResetType(BlockResetType.THREE_SECONDS);
            } else if (blockResetType == BlockResetType.THREE_SECONDS) {
                setBlockResetType(BlockResetType.INSTANT_ANYONE);
            } else if (blockResetType == BlockResetType.INSTANT_ANYONE) {
                setBlockResetType(BlockResetType.INSTANT_VICTIM);
            } else if (blockResetType == BlockResetType.INSTANT_VICTIM) {
                setBlockResetType(BlockResetType.OFF);
            }
            player.playSound(player.getLocation(), Sound.CLICK, 1F, 100F);
            openIngameSettings();
        });
        player.openInventory(inventory.getInventory());
    }

    public void openSettingsInventory() {
        Inventory inventory = new Inventory("§8» §6Settings", 9);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }



        ItemBuilder scoreboardStats = new ItemBuilder(Material.PAPER).setName("§8» §6Scoreboard stats");

        scoreboardStats.setLore(Arrays.stream(StatsType.values())
                .map(value -> (shownBoardStatsType == value) ? value.toBeauty() : "§7" + value.toBeauty().substring(2))
                .collect(Collectors.toList()));


        inventory.setItem(scoreboardStats.build(), 5, event -> {
            if (shownBoardStatsType == StatsType.DAILY) {
                setShownBoardStatsType(StatsType.MONTHLY);
            } else if (shownBoardStatsType == StatsType.MONTHLY) {
                setShownBoardStatsType(StatsType.ALLTIME);
            } else if (shownBoardStatsType == StatsType.ALLTIME) {
                setShownBoardStatsType(StatsType.DAILY);
            }
            player.playSound(player.getLocation(), Sound.CLICK, 1F, 100F);
            player.closeInventory();
            player.sendMessage(MLGRush.getInstance().getPrefix() + "The scoreboard now shows your " + shownBoardStatsType.toBeauty() + " §7stats");
            setScoreboard();
        });

        inventory.setItem(new ItemBuilder(Material.REDSTONE_COMPARATOR, 1, (byte) 0).setName("§8» §6Ingame settings").build(), 1, event -> {
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
            openIngameSettings();
        });


        inventory.setItem(new ItemBuilder(Material.DIAMOND, 1, (byte) 0).setName("§8» §6Perks").build(), 6, event -> {
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
            BukkitCore.getInstance().getPerkManager().openMainPerkInventory(player);
        });

        inventory.setItem(new ItemBuilder(Material.ARMOR_STAND, 1, (byte) 0).setName("§8» §6Inventory sort").build(), 4, event -> {
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
            player.closeInventory();
            org.bukkit.inventory.Inventory sortInv = Bukkit.createInventory(null, 9, "§8» §6Inventory sort");
            sortInv.setContents(getInventory().getContents());
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
            player.openInventory(sortInv);
            player.getInventory().clear();
        });



        player.openInventory(inventory.getInventory());
    }

}
