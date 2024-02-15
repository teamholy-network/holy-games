package de.teamholy.bridge.player.service;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.google.common.collect.Lists;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.utils.scoreboard.ScoreboardAPI;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.settings.BridgeSettings;
import de.teamholy.bridge.player.settings.items.BridgeItems;
import de.teamholy.bridge.player.settings.sounds.BridgeSoundType;
import de.teamholy.bridge.util.FireworkUtil;
import de.teamholy.bridge.util.FormatTime;
import de.teamholy.bridge.util.ItemBuilder;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.PerkManager;
import de.teamholy.core.bukkit.perks.PerkType;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class BridgePlayerService {

    private final HashMap<UUID, BridgePlayer> bridgePlayers = new HashMap<>();
    private final Map<UUID, Long> playerTime = new HashMap<>();
    private final HashMap<BridgeMapType, HashMap<BridgePlayer, Long>> topPlayer = new HashMap<>();

    public BridgePlayer getBridgePlayer(Player player) {
        return bridgePlayers.get(player.getUniqueId());
    }

    public void addPlayer(Player player) {
        if (!bridgePlayers.containsKey(player.getUniqueId())) {
            bridgePlayers.put(player.getUniqueId(), new BridgePlayer(player.getUniqueId()));
        }
        createScoreboard(getBridgePlayer(player));
        player.getInventory().clear();

        Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> prepareIngamePlayer(player), 10);
    }

    public void removePlayer(Player player) {
        playerTime.remove(player.getUniqueId());
        bridgePlayers.remove(player.getUniqueId());
    }

    public void preparePlayer(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setExp(0);
        player.setLevel(0);
    }


    public void prepareIngamePlayer(Player player) {
        preparePlayer(player);
        player.setExp(0);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        BridgePlayer bridgePlayer = getBridgePlayer(player);

        var i = 0;
        for (ItemStack content : bridgePlayer.getInventory().getContents()) {
            if (content != null && content.getType() != null) {

                if (content.getType() == Material.SANDSTONE) {
                    var perk = BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.BLOCK);
                    if (perk != null) {
                        player.getInventory().setItem(i, perk.setAmount(64).setName("§8» §6Blocks §8(§7rightclick§8)").build());
                    } else {
                        player.getInventory().setItem(i, new ItemBuilder(Material.SANDSTONE).amount(64).name("§8» §6Blocks §8(§7rightclick§8)").build());
                    }
                    player.getInventory().setHeldItemSlot(i);
                } else {
                    if (player.getInventory().getItem(i) == content) {
                        continue;
                    }
                    player.getInventory().setItem(i, content);
                }

            }



            i++;
        }
    }

    private void createScoreboard(BridgePlayer bridgePlayer) {
        var player = bridgePlayer.getPlayer();
        if (getScoreboard(player) != null) {
            return;
        }
        ScoreboardAPI bridgeScoreboard = new ScoreboardAPI();
        bridgeScoreboard.createScoreboard(player, "§e");

        bridgePlayer.setBridgeScoreboard(bridgeScoreboard);
    }

    public void setScoreboard(BridgePlayer bridgePlayer) {
        ScoreboardAPI bridgeScoreboard = bridgePlayer.getBridgeScoreboard();
        if (bridgeScoreboard.contains(13)) {
            return;
        }

        bridgeScoreboard.setLine(14, "§8§m-----------------");
        bridgeScoreboard.setLine(13, " §7Best Time");
        bridgeScoreboard.setLine(12, " §c-/-");
        bridgeScoreboard.setLine(11, "§1");

        bridgeScoreboard.setLine(10, " §6Top 5");
        bridgeScoreboard.setLine(9, "§f    §oSession");
        bridgeScoreboard.setLine(8, "§r");
        bridgeScoreboard.setLine(2, "§8");
        bridgeScoreboard.setLine(1, "§8§m-----------------");
        bridgeScoreboard.setLine(0, "§f§o" + Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName());
        bridgeScoreboard.build();
    }

    public void updateHologram(BridgePlayer bridgePlayer, boolean updateMapType) {
        var hologram = bridgePlayer.getHologram();
        var mapType = bridgePlayer.getMap().getMapType();
        var holoLocation = bridgePlayer.getUneditedLocation().clone().add(mapType.getHologramCords().xADD(), mapType.getHologramCords().yADD(), mapType.getHologramCords().zADD());

        if (hologram == null) {
            hologram = HologramsAPI.createHologram(Bridge.getInstance(), holoLocation);
            bridgePlayer.setHologram(hologram);

            hologram.appendItemLine(new de.teamholy.core.bukkit.utils.ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setSkullMeta(bridgePlayer.getSkinProfile().getValue(), bridgePlayer.getSkinProfile().getSignature()).build());
            hologram.appendTextLine("");
            hologram.appendTextLine("§fStats of " + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(bridgePlayer.getPlayer().getUniqueId()) + bridgePlayer.getPlayer().getName());
            hologram.appendTextLine("");
            hologram.appendTextLine("§c§lGLOBAL");
            hologram.appendTextLine("§fWins §8» §e" + BukkitCore.getAPI().getCoinManager().formatInteger(bridgePlayer.getWins()));
            hologram.appendTextLine("§fTries §8» §e" + BukkitCore.getAPI().getCoinManager().formatInteger(bridgePlayer.getGamesPlayed()));
            hologram.appendTextLine("§fPlaced blocks §8» §e" + BukkitCore.getAPI().getCoinManager().formatInteger(bridgePlayer.getPlacedBlocks()));
            hologram.appendTextLine("");
            hologram.appendTextLine("§6§l" + bridgePlayer.getMap().getMapType());
            hologram.appendTextLine("§fBest §2§lsession §ftime §8» §e" + checkBestTimeString(bridgePlayer.getLocalBestTime(mapType)));
            hologram.appendTextLine("§fBest §c§lall-time §ftime §8» §e" + checkBestTimeString(bridgePlayer.getGlobalBestTime(mapType)));
            hologram.appendTextLine("§fAverage §c§lall-time §ftime §8» §e" + checkBestTimeString(getAverageTime(bridgePlayer, bridgePlayer.getMap().getMapType())));

        } else {
            ((TextLine) hologram.getLine(5)).setText("§fWins §8» §e" + BukkitCore.getAPI().getCoinManager().formatInteger(bridgePlayer.getWins()));
            ((TextLine) hologram.getLine(6)).setText("§fTries §8» §e" + BukkitCore.getAPI().getCoinManager().formatInteger(bridgePlayer.getGamesPlayed()));
            ((TextLine) hologram.getLine(7)).setText("§fPlaced blocks §8» §e" + BukkitCore.getAPI().getCoinManager().formatInteger(bridgePlayer.getPlacedBlocks()));
            ((TextLine) hologram.getLine(10)).setText("§fBest §2§lsession §ftime §8» §e" + checkBestTimeString(bridgePlayer.getLocalBestTime(mapType)));
            ((TextLine) hologram.getLine(11)).setText("§fBest §c§lall-time §ftime §8» §e" + checkBestTimeString(bridgePlayer.getGlobalBestTime(mapType)));
            ((TextLine) hologram.getLine(12)).setText("§fAverage §c§lall-time §ftime §8» §e" + checkBestTimeString(getAverageTime(bridgePlayer, bridgePlayer.getMap().getMapType())));
        }

        if (updateMapType) {
            ((TextLine) hologram.getLine(9)).setText("§6§l" + bridgePlayer.getMap().getMapType());
            hologram.teleport(holoLocation);
        }


    }

    public void updateScoreboard(BridgePlayer bridgePlayer) {
        ScoreboardAPI bridgeScoreboard = bridgePlayer.getBridgeScoreboard();
        bridgeScoreboard.updateLine(13, " §7Best Time §8(§e" + (bridgePlayer.getMap() != null ? bridgePlayer.getMap().getMapType().getName() : "All time") + "§8)");
        bridgeScoreboard.updateLine(12, " §e" + checkBestTimeString(bridgePlayer.getGlobalBestTime(bridgePlayer.getMap().getMapType())));

        String top5 = " §6Top 5 §8(§e" + (bridgePlayer.getMap() != null ? bridgePlayer.getMap().getMapType().getName() : "All time") + "§8)";
        bridgeScoreboard.updateLine(10, top5);

        updateScoreboardForPlayer(bridgePlayer.getMap().getMapType());
    }


    public void ingameSettingsInventory(Player player) {
        BridgePlayer bridgePlayer = getBridgePlayer(player);

        de.teamholy.core.bukkit.utils.Inventory inventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Settings", 5 * 9);

        for (int i = 0; i < 9 * 5; i++) {
            inventory.setItem(new de.teamholy.core.bukkit.utils.ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        inventory.setItem(new ItemBuilder(Material.SANDSTONE).amount(1).name("§8» §6Blocks").build(), 10, event -> {
            BukkitCore.getInstance().getPerkManager().openSecondPerkInventory(player, PerkType.BLOCK, PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL);
        });
        inventory.setItem(new ItemBuilder(Material.ANVIL).amount(1).name("§8» §6Block Break Settings").build(), 11, event -> {
            player.openInventory(blockSettingsInventory(getBridgePlayer(player)));
            player.playSound(player.getLocation(), Sound.CLICK, 2, 100);
        });

        inventory.setItem(new ItemBuilder(Material.ARMOR_STAND).amount(1).name("§8» §6Inventory sort").build(), 13, event -> {

            player.closeInventory();

            de.teamholy.core.bukkit.utils.Inventory sort = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Inventory sort", 9, false);
            sort.getInventory().setContents(bridgePlayer.getInventory().getContents());
            player.getInventory().clear();

            sort.setOnClose(inventoryCloseEvent -> {
                if (BridgeItems.correctInventory(sort.getInventory())) {
                    bridgePlayer.setInventory(sort.getInventory());
                    player.sendMessage(Bridge.PREFIX + "Your inventory sort was saved");
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 2f, 2f);
                } else {
                    bridgePlayer.setInventory(bridgePlayer.createInventory());
                    player.sendMessage(Bridge.PREFIX + "Your inventory was not saved");
                    player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 2f, 2f);
                }
                player.getInventory().clear();
                Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> prepareIngamePlayer(player), 1);
            });

            player.openInventory(sort.getInventory());


        });

        //inventory.setItem(4, new ItemBuilder(Material.SLIME_BALL).name("§cIsland Moving")/*.lore("§c§lSOON")*/.lore((bridgePlayer.getSettings().isIslandMoving() ? "§aYes" : "§cNo")).build());
        inventory.setItem(new ItemBuilder(Material.RECORD_8).name("§8» §6Sounds").build(), 14, event -> {
            var soundPerkInventory = Bridge.getInstance().getBridgeSoundPerkService().openSoundInventory(bridgePlayer, BridgeSoundType.ALL, PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL, 1).getInventory();

            player.openInventory(soundPerkInventory);
            player.playSound(player.getLocation(), Sound.CLICK, 2, 100);
        });

        String colorOffset = (bridgePlayer.getBridgeSettings().getOffsetZ() >= 0 ? "§a+" : "§c");


        inventory.setItem(new ItemBuilder(Material.ENDER_PEARL).name("§8» §6Offset spawn §8(§cZ§8)")
                .lore(Arrays.
                        asList("§7Current offset§8: " + colorOffset + bridgePlayer.getBridgeSettings().getOffsetZ(),
                                "",
                                "§f§o- is forwards",
                                "§f§o+ is backwards",
                                "",
                                "§7§oLeftclick to increase §6offset",
                                "§7§oRightclick to decrease §6offset")).build(), 12, event -> {

            int currentOffset = bridgePlayer.getBridgeSettings().getOffsetZ();

            Location mapLoc = bridgePlayer.getUneditedLocation().clone();

            if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.SHIFT_LEFT) {
                if (currentOffset >= 3) return;
                bridgePlayer.getBridgeSettings().setOffsetZ(bridgePlayer.getBridgeSettings().getOffsetZ() + 1);
            } else if (event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) {
                if (currentOffset <= -1) return;
                bridgePlayer.getBridgeSettings().setOffsetZ(bridgePlayer.getBridgeSettings().getOffsetZ() - 1);
            }

            String newColorOffset = (bridgePlayer.getBridgeSettings().getOffsetZ() >= 0 ? "§a+" : "§c");
            inventory.getInventory().getItem(12).getItemMeta().setLore(Lists.newArrayList());
            inventory.setItem(new ItemBuilder(Material.ENDER_PEARL).name("§8» §6Offset spawn §8(§cZ§8)")
                    .lore(Arrays.
                            asList("§7Current offset§8: " + newColorOffset + bridgePlayer.getBridgeSettings().getOffsetZ(),
                                    "",
                                    "§f§o- is forwards",
                                    "§f§o+ is backwards",
                                    "",
                                    "§7§oLeftclick to increase §6offset",
                                    "§7§oRightclick to decrease §6offset")).build(), 12);
            player.updateInventory();
            bridgePlayer.setMapLocation(mapLoc.clone().add(0, 0, bridgePlayer.getBridgeSettings().getOffsetZ()));
            player.playSound(player.getLocation(), Sound.CLICK, 2, 100);
        });


        ItemBuilder timer = new ItemBuilder(Material.WATCH).name("§8» §6Timer place");

        timer.lore(Arrays.stream(BridgeSettings.TimerPlace.values())
                .map(value -> (bridgePlayer.getBridgeSettings().getTimerPlace() == value) ? "§a" + value.getName() : "§7" + value.getName())
                .collect(Collectors.toList()));

        inventory.setItem(timer.build(), 15, event -> {
            bridgePlayer.getBridgeSettings().setTimerPlace(BridgeSettings.TimerPlace.values()[(bridgePlayer.getBridgeSettings().getTimerPlace().ordinal() + 1) % BridgeSettings.TimerPlace.values().length]);
            ingameSettingsInventory(player);
            player.playSound(player.getLocation(), Sound.CLICK, 2, 100);
        });

        inventory.setItem(new ItemBuilder(Material.EYE_OF_ENDER).name("§8» §6Spectate").build(), 16, event -> {
            openSpectateInventory(player);
            player.playSound(player.getLocation(), Sound.CLICK, 2, 100);
        });

        inventory.setItem(new de.teamholy.core.bukkit.utils.ItemBuilder(Material.SKULL_ITEM, 1, 3)
                .setSkullMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDUyO" +
                        "GVkNDU4MDI0MDBmNDY1YjVjNGUzYTZiN2E5ZjJiNmE1YjNkNDc4YjZmZDg0OTI1Y2M1ZDk4ODM5MWM3ZCJ9fX0=", "")
                .setName("§8» §6Maps §8(§fIsland skins§8)").build(), 33, event -> {

            player.openInventory(Bridge.getInstance().getBridgeMapSkinPerkService().openMapInventory(bridgePlayer, bridgePlayer.getMap().getMapType(), PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL, 1).getInventory());
            player.playSound(player.getLocation(), Sound.CLICK, 2, 100);
        });


        int index = 29;
        for (BridgeMapType mapType : BridgeMapType.values()) {
            inventory.setItem(new ItemBuilder(mapType.getIcon())

                    .amount(mapType.getLength())
                    .name("§8» §6" + mapType.getName())
                    .lore("§7Distance§8: §e" + mapType.getLength()).build(), index, event -> {


                if (bridgePlayer.getMap().getMapType() == mapType) {
                    player.sendMessage(Bridge.PREFIX + "§cYou are already on this maptype!");
                    return;
                }

                if (bridgePlayer.getCooldown() > System.currentTimeMillis()) {
                    player.sendMessage(Bridge.PREFIX + "§cPlease wait before changing the maptype again!");
                    return;
                }


                if (!bridgePlayer.getBlocks().isEmpty()) {
                    bridgePlayer.getBlocks().forEach((block, time) -> block.setType(Material.AIR));
                }


                if (Bridge.getInstance().getBridgeMapService().getFreeMap(mapType) == null) {
                    player.sendMessage(Bridge.PREFIX + "§cNo map found for you, please try again later.");
                    return;
                } else Bridge.getInstance().getBridgeMapService().resetMap(bridgePlayer.getMap());

                bridgePlayer.setCooldown(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3));


                bridgePlayer.getBlocks().clear();
                player.closeInventory();
                player.sendMessage(Bridge.PREFIX + "Changed Map type to §6" + mapType.getName());
                player.playSound(player.getLocation(), Sound.CLICK, 2, 100);


                Bridge.getInstance().getBridgeMapService().findMapForPlayer(mapType, bridgePlayer);


            });

            if (index++ == 34) return;
        }


        player.openInventory(inventory.getInventory());
    }

    private void openSpectateInventory(Player player) {
        if (Bukkit.getOnlinePlayers().size() < 2) {
            player.sendMessage(Bridge.PREFIX + "§cNo players to spectate");
            return;
        }

        de.teamholy.core.bukkit.utils.Inventory inventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Spectate", 6 * 9);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer == player) continue;
            var bridgePlayer = getBridgePlayer(onlinePlayer);
            if (bridgePlayer.getState() == BridgePlayer.PlayerState.SPECTATOR) continue;
            inventory.setItem(new de.teamholy.core.bukkit.utils.ItemBuilder(Material.SKULL_ITEM, 1, 3)
                    .setSkullOwner(onlinePlayer.getName())
                    .setName("§8» §6" + onlinePlayer.getName())
                    .setLore("§7Click to spectate").build(), inventory.getInventory().firstEmpty(), event -> {

                startSpectating(player, onlinePlayer);

                player.sendMessage(Bridge.PREFIX + "§7You are now spectating §6" + onlinePlayer.getName());
                player.playSound(player.getLocation(), Sound.CLICK, 2, 100);
            });
        }

        player.openInventory(inventory.getInventory());
    }

    public boolean isPlayerBeingSpectated(Player player) {
        return bridgePlayers.values().stream().anyMatch(bridgePlayer -> bridgePlayer.getToSpectate() == player);
    }

    public Player getPlayerWhoSpectates(Player player) {
        return bridgePlayers.values().stream().filter(bridgePlayer -> bridgePlayer.getToSpectate() == player).findFirst().map(BridgePlayer::getPlayer).orElse(null);
    }

    public void startSpectating(Player player, Player target) {
        var playerWhoSpectates = getPlayerWhoSpectates(player);

        if (playerWhoSpectates != null) {
            stopSpectating(playerWhoSpectates, true); // stop the player who is spectating the player
        }

        BridgePlayer bridgePlayer = getBridgePlayer(player);
        bridgePlayer.setToSpectate(target);
        bridgePlayer.setState(BridgePlayer.PlayerState.SPECTATOR);
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.spigot().setCollidesWithEntities(false);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setExp(0);
        player.setLevel(0);
        player.getInventory().clear();
        player.getInventory().setItem(4, new ItemBuilder(Material.SLIME_BALL).name("§8» §cLeave Spectator").build());

        //  player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.hidePlayer(player);
        }
        player.teleport(target);
    }

    public void stopSpectating(Player player, boolean teleport) {
        BridgePlayer bridgePlayer = getBridgePlayer(player);
        bridgePlayer.setToSpectate(null);
        bridgePlayer.setState(BridgePlayer.PlayerState.INGAME);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.spigot().setCollidesWithEntities(true);
        player.getInventory().clear();
        prepareIngamePlayer(player);

        getScoreboard(player).updateLine(2, "§8");

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer != player) {
                onlinePlayer.showPlayer(player);
            }
        }

        if (teleport) player.teleport(bridgePlayer.getMapLocation());

        //  player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    public Inventory blockSettingsInventory(BridgePlayer bridgePlayer) {

        de.teamholy.core.bukkit.utils.Inventory inventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Block Settings", 9);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(new de.teamholy.core.bukkit.utils.ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        var i = 1;
        for (BridgeSettings.BlockAnimationType blockBreakAnimations : BridgeSettings.BlockAnimationType.values()) {

            var isSelected = bridgePlayer.getBridgeSettings().getBlockAnimationType() == blockBreakAnimations;
            inventory.setItem(blockBreakAnimations.getItemBuilder().setLore(
                    (isSelected ? "§2Selected" :

                            bridgePlayer.getPlayer().hasPermission(blockBreakAnimations.getRankType().getPermission()) ? "§aClick to select" :
                                    "§7Available for " + blockBreakAnimations.getRankType().getRankName() + "§7 and above"
                    )


            ).withGlow(isSelected).build(), i, inventoryClickEvent -> {
                if (bridgePlayer.getPlayer().hasPermission(blockBreakAnimations.getRankType().getPermission())) {
                    bridgePlayer.getBridgeSettings().setBlockAnimationType(blockBreakAnimations);
                    bridgePlayer.getPlayer().closeInventory();
                    bridgePlayer.getPlayer().playSound(bridgePlayer.getPlayer().getLocation(), Sound.NOTE_PLING, 2, 2);
                    bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "§7You have selected §e" + blockBreakAnimations.getName());
                } else {
                    bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "§7You need to be " + blockBreakAnimations.getRankType().getRankName() + "§7 or above to use this feature");
                }
            });

            i++;
        }

        inventory.setItem(new ItemBuilder(Material.WATCH).amount(1).name("§8» §eRemove Blocks while bridging")
                .lore("")
                .lore(" §8» §7Current delay§8: §e" +
                        (bridgePlayer.getBridgeSettings().getRemovalTime() == 0 ? "Not set" : bridgePlayer.getBridgeSettings().getRemovalTime() + " §eseconds"))
                .lore("")
                .lore("§7Currently " + (bridgePlayer.getBridgeSettings().isRemoveBlocks() ? "§aenabled" : "§cdisabled"))
                .lore("§7§oShift Click to enable / disable").build(), 7, event -> {


            if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                bridgePlayer.getBridgeSettings().setRemoveBlocks(!bridgePlayer.getBridgeSettings().isRemoveBlocks());
                bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + (bridgePlayer.getBridgeSettings().isRemoveBlocks() ? "§aActivated" : "§cDisabled") + " the Block Timer!");
            } else if (event.getClick() == ClickType.LEFT) {
                bridgePlayer.getBridgeSettings().setRemovalTime((bridgePlayer.getBridgeSettings().getRemovalTime() + 1) > 8 ? 8 : bridgePlayer.getBridgeSettings().getRemovalTime() + 1);
                bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "Changed the removal time to §e" + bridgePlayer.getBridgeSettings().getRemovalTime());
            } else if (event.getClick() == ClickType.RIGHT) {
                bridgePlayer.getBridgeSettings().setRemovalTime((bridgePlayer.getBridgeSettings().getRemovalTime() - 1) < 1 ? 1 : bridgePlayer.getBridgeSettings().getRemovalTime() - 1);
                bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "Changed the removal time to §e" + bridgePlayer.getBridgeSettings().getRemovalTime());
            }
            bridgePlayer.getPlayer().openInventory(blockSettingsInventory(bridgePlayer));

        });
        return inventory.getInventory();
    }

    public String checkBestTimeString(long bestTime) {
        return (bestTime == 0) ? "§c-/-" : FormatTime.formatTimeManually(bestTime);
    }

    public void addBestTime(BridgePlayer bridgePlayer, long localByType) {
        var mapType = bridgePlayer.getMap().getMapType();

        if (!topPlayer.get(mapType).containsKey(bridgePlayer)) {
            topPlayer.get(mapType).put(bridgePlayer, localByType);
        } else if (topPlayer.get(mapType).get(bridgePlayer) > localByType) {
            topPlayer.get(mapType).remove(bridgePlayer);
            topPlayer.get(mapType).put(bridgePlayer, localByType);
        }

        updateScoreboardForPlayer(bridgePlayer.getMap().getMapType());
    }

    public void updateScoreboardForPlayer(BridgeMapType bridgeMapType) {
        var bridgePlayersFiltered = bridgePlayers
                .values()
                .stream()
                .filter(player -> player.getMap().getMapType() == bridgeMapType)
                .toList();

        bridgePlayersFiltered.forEach(bridgePlayer -> {

            var bridgeScoreboard = bridgePlayer.getBridgeScoreboard();

            for (int j = 7; j >= 3; j--) {
                if (!bridgeScoreboard.contains(j)) bridgeScoreboard.setLine(j, " §cNo one");
                else bridgeScoreboard.updateLine(j, " §cNo one");
            }
        });

        bridgePlayersFiltered
                .forEach(bridgePlayer -> {
                    AtomicInteger i = new AtomicInteger(7);

                    topPlayer.get(bridgeMapType).entrySet().stream().sorted(Comparator.comparingLong(Map.Entry::getValue)).limit(5).toList().forEach(bridgePlayerLongEntry -> {

                        if (i.get() < 3) return;

                        var playerEntry = bridgePlayerLongEntry.getKey().getPlayer();
                        var string = " " +
                                BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(playerEntry.getUniqueId()) + playerEntry.getName() + " §8» §7" +
                                checkBestTimeString(bridgePlayerLongEntry.getKey().getLocalBestTime(bridgeMapType));

                        bridgePlayer.getBridgeScoreboard().updateLine(i.getAndDecrement(), string);

                    });


                });

    }


    public ScoreboardAPI getScoreboard(Player player) {
        return getBridgePlayer(player).getBridgeScoreboard();
    }

    public void spawnBlockAnimation(BridgePlayer bridgePlayer) {
        var blockAnimationType = bridgePlayer.getBridgeSettings().getBlockAnimationType();
        HashMap<Block, Long> blocks = (HashMap<Block, Long>) bridgePlayer.getBlocks().clone();

        switch (blockAnimationType) {
            case FALLING -> {
                blocks.forEach((block, time) -> {
                    bridgePlayer.getBlocks().remove(block);

                    FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
                    block.setType(Material.AIR);
                    Bukkit.getScheduler().runTaskLater(Bridge.getInstance(),
                            () -> getBlocksInRadius(block.getLocation(), 3).forEach(block1 -> bridgePlayer.getPlayer().sendBlockChange(block1.getLocation(), Material.AIR, (byte) 0)), 15);
                    fallingBlock.setDropItem(false);
                    fallingBlock.setHurtEntities(false);


                    Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), fallingBlock::remove, 15L);
                });
            }
            case BREAK -> blocks.forEach((block, time) -> {
                BlockPosition position = new BlockPosition(block.getX(), block.getY(), block.getZ());
                bridgePlayer.getBlocks().remove(block);

                final AtomicInteger atomicInteger = new AtomicInteger(0);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (atomicInteger.get() >= 11) {
                            cancel();
                            block.setType(Material.AIR);

                            Bukkit.getScheduler().runTaskLater(Bridge.getInstance(),
                                    () -> getBlocksInRadius(block.getLocation(), 3).forEach(block1 -> bridgePlayer.getPlayer().sendBlockChange(block1.getLocation(), Material.AIR, (byte) 0)), 15);
                            atomicInteger.set(0);
                        }

                        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(getBlockEntityId(block), position, (byte) atomicInteger.getAndIncrement());
                        for (Player allPlayer : Bukkit.getOnlinePlayers()) {
                            ((CraftPlayer) allPlayer).getHandle().playerConnection.sendPacket(packet);
                        }
                    }

                }.runTaskTimer(Bridge.getInstance(), 0L, 1L);

            });
            case TNT -> {
                blocks.forEach((block, time) -> {
                    bridgePlayer.getBlocks().remove(block);

                    FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
                    fallingBlock.setDropItem(false);
                    fallingBlock.setHurtEntities(false);
                    fallingBlock.setVelocity(new Vector().setX(Math.random() - 0.5).setY(Math.random()).setZ(Math.random() - 0.5));
                    block.setType(Material.AIR);

                    Bukkit.getScheduler().runTaskLater(Bridge.getInstance(),
                            () -> getBlocksInRadius(block.getLocation(), 3).forEach(block1 -> bridgePlayer.getPlayer().sendBlockChange(block1.getLocation(), Material.AIR, (byte) 0)), 15);


                    Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), fallingBlock::remove, 40);
                });
            }
/*            case BLACK_HOLE -> {


                double middleX = blocks.keySet().stream().mapToDouble(block -> block.getLocation().getX()).average().orElse(0);
                double middleY = blocks.keySet().stream().mapToDouble(block -> block.getLocation().getY()).average().orElse(0);
                double middleZ = blocks.keySet().stream().mapToDouble(block -> block.getLocation().getZ()).average().orElse(0);
                Location middleLocation = new Location(bridgePlayer.getPlayer().getWorld(), middleX, middleY, middleZ);


                middleLocation.getWorld().strikeLightningEffect(middleLocation);
                List<Block> fireBlock = Lists.newArrayList();
                blocks.forEach((block, time) -> {
                    Block aboveBlock = block.getRelative(BlockFace.UP);
                    if (aboveBlock.getType() == Material.AIR) {
                        aboveBlock.setType(Material.FIRE);
                        fireBlock.add(aboveBlock);
                    }
                });


                List<List<Map.Entry<Block, Long>>> blockGroups = Lists.partition( blocks.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue()).toList(), 5);

                AtomicInteger delay = new AtomicInteger(1);
                int totalGroups = blockGroups.size();
                blockGroups.forEach(blockGroup -> {
                    Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> {
                        blockGroup.forEach(entry -> {
                            Block block = entry.getKey();
                            bridgePlayer.getBlocks().remove(block);
                            block.setType(Material.AIR);
                            block.getRelative(BlockFace.UP).setType(Material.AIR);
                        });

                    }, delay.getAndIncrement());
                });

            }*/
            case DROPPING -> {
                blocks.forEach((block, time) -> {
                    if (block.getType() == Material.AIR) return;

                    var toDrop = dropItem(block.getLocation().add(0, 1, 0), new de.teamholy.core.bukkit.utils.ItemBuilder(block.getType(), 1, block.getData()).setName(String.valueOf(UUID.randomUUID())).build());

                    bridgePlayer.getBlocks().remove(block);
                    block.setType(Material.AIR);

                    Bukkit.getScheduler().runTaskLater(Bridge.getInstance(),
                            () -> getBlocksInRadius(block.getLocation(), 3).forEach(block1 -> bridgePlayer.getPlayer().sendBlockChange(block1.getLocation(), Material.AIR, (byte) 0)), 15);
                    Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), toDrop::die, 60L);

                });
            }
            default -> blocks.forEach((block, time) -> {
                block.setType(Material.AIR);


                Bukkit.getScheduler().runTaskLater(Bridge.getInstance(),
                        () -> getBlocksInRadius(block.getLocation(), 3).forEach(block1 -> bridgePlayer.getPlayer().sendBlockChange(block1.getLocation(), Material.AIR, (byte) 0)), 5L);
                bridgePlayer.getBlocks().remove(block);
            });
        }
    }

    public void sendActionBar(Player player, String text) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(text), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public List<Block> getBlocksInRadius(Location center, int radius) {
        return IntStream.rangeClosed(center.getBlockX() - radius, center.getBlockX() + radius)
                .boxed()
                .flatMap(x -> IntStream.rangeClosed(center.getBlockY() - radius, center.getBlockY() + radius)
                        .mapToObj(y -> Pair.of(x, y)))
                .flatMap(pair -> IntStream.rangeClosed(center.getBlockZ() - radius, center.getBlockZ() + radius)
                        .mapToObj(z -> center.getWorld().getBlockAt(pair.getKey(), pair.getValue(), z)))
                .filter(block -> block.getType() == Material.AIR)
                .collect(Collectors.toList());
    }

    public void sendTitle(Player player, String title, String subtitle, int fadein, int stay, int fadeout) {
        PacketPlayOutTitle timepacket = new PacketPlayOutTitle(fadein, stay, fadeout);
        PacketPlayOutTitle titlepacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}"));
        PacketPlayOutTitle subtitlepacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}"));

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(timepacket);
        connection.sendPacket(titlepacket);
        connection.sendPacket(subtitlepacket);
    }

    private int getBlockEntityId(Block block) {
        return ((block.getX() & 0xFFF) << 20)
                | ((block.getZ() & 0xFFF) << 8)
                | (block.getY() & 0xFF);
    }

    public EntityItem dropItem(Location loc, ItemStack item) {
        if (loc.getChunk().getEntities().length > 64 * 4) return null;
        EntityItem entity = new EntityItem(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ(), CraftItemStack.asNMSCopy(item));
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(entity);
        return entity;
    }

    public long getAverageTime(BridgePlayer bridgePlayer, BridgeMapType bridgeMapType) {
        double averageTime = bridgePlayer.getBestTimes().get(bridgeMapType)
                .stream()
                .mapToDouble(Long::doubleValue)
                .average()
                .orElse(0.0);


        return Math.round(averageTime);
    }

    private final BridgeSoundPerkService bridgeSoundPerkService = Bridge.getInstance().getBridgeSoundPerkService();

    public void stopTimer(BridgePlayer bridgePlayer, String newTime, long current) {
        var player = bridgePlayer.getPlayer();
        if (player.getLocation().getBlock().getType() == Material.GOLD_PLATE
                || player.getLocation().clone().subtract(0, 1, 0).getBlock().getType() == Material.DIAMOND_BLOCK) {
            if (!getPlayerTime().containsKey(player.getUniqueId())) {
                return;
            }
            getPlayerTime().remove(player.getUniqueId());

            sendTitle(player, "§fTime §8» §a" + newTime, "§a+ §e2 Coins", 10, 20, 10);
            BukkitCore.getAPI().getCoinManager().addCoins(player.getUniqueId(), 2, true);


            Location location = player.getLocation().clone();

            var beforeBestLocal = bridgePlayer.getLocalBestTime(bridgePlayer.getMap().getMapType());
            var beforeBestGlobal = bridgePlayer.getGlobalBestTime(bridgePlayer.getMap().getMapType());

            getScoreboard(player).updateLine(2, "§8");


            Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> {
                if (!bridgePlayer.getBlocks().isEmpty()) {
                    spawnBlockAnimation(bridgePlayer);
                }

                bridgePlayer.getBlocks().clear();
                player.teleport(bridgePlayer.getMapLocation());
            }, 1);


            if (current < beforeBestGlobal || beforeBestGlobal == 0) {
                String timerDifference = FormatTime.formatTimeManually(beforeBestGlobal - current);

                Bukkit.getScheduler().runTask(Bridge.getInstance(), () -> {
                    FireworkUtil.playFirework(player.getWorld(), location.add(0, -3, 0), FireworkUtil.getBlowupRandomEffect());
                    FireworkUtil.playFirework(player.getWorld(), location.add(0, -3, 0), FireworkUtil.getBlowupRandomEffect());
                    FireworkUtil.playFirework(player.getWorld(), location.add(0, -3, 0), FireworkUtil.getBlowupRandomEffect());
                });
                player.sendMessage("§8§m-----------§f§lCONGRATS§8§m--------------");
                player.sendMessage("");
                player.sendMessage(" §fYou have beaten your §c§lall-time §frecord!");
                player.sendMessage("      §fYour new §atime §fis §e" + newTime + (beforeBestGlobal != 0 ? " §8︳ §a-" + timerDifference + "§2 difference" : ""));
                player.sendMessage("");
                player.sendMessage("§8§m----------------------------------");

                bridgePlayer.setGlobalBestTime(bridgePlayer.getMap().getMapType(), current);
                bridgePlayer.setLocalBestTime(bridgePlayer.getMap().getMapType(), current);

                addBestTime(bridgePlayer, current);

                bridgeSoundPerkService.playSoundPerk(bridgePlayer, BridgeSettings.BridgeSoundEventType.NEW_RECORD);
            } else if (current < beforeBestLocal || beforeBestLocal == 0) {
                String timerDifference = FormatTime.formatTimeManually(beforeBestLocal - current);

                Bukkit.getScheduler().runTask(Bridge.getInstance(), () -> FireworkUtil.playFirework(player.getWorld(), location, FireworkUtil.getBlowupRandomEffect()));
                player.sendMessage("");
                player.sendMessage(" §fYou have beaten your §2§lsession record!");
                player.sendMessage("       §fYour new §atime §fis §e" + newTime + (beforeBestLocal != 0 ? " §8︳ §a-" + timerDifference + "§2 difference" : ""));
                player.sendMessage("");

                bridgePlayer.setLocalBestTime(bridgePlayer.getMap().getMapType(), current);

                addBestTime(bridgePlayer, current);
                bridgeSoundPerkService.playSoundPerk(bridgePlayer, BridgeSettings.BridgeSoundEventType.NEW_RECORD);

            } else {
                bridgeSoundPerkService.playSoundPerk(bridgePlayer, BridgeSettings.BridgeSoundEventType.WIN);
            }

            bridgePlayer.getBestTimes().get(bridgePlayer.getMap().getMapType()).add(current);

            bridgePlayer.addWin();

            Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> {
                updateScoreboard(bridgePlayer);

                prepareIngamePlayer(player);
                updateHologram(bridgePlayer, false);
            }, 1);
        }
    }
}
