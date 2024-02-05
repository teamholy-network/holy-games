package de.teamholy.bridge.player.management;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.utils.scoreboard.ScoreboardAPI;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.settings.Settings;
import de.teamholy.bridge.util.FormatTime;
import de.teamholy.bridge.util.ItemBuilder;
import de.teamholy.core.bukkit.BukkitCore;
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

import java.util.*;
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
public class PlayerManagement {

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
        loadLobbyInventory(player);
    }

    public void removePlayer(Player player) {
        bridgePlayers.remove(player.getUniqueId());
    }

    public void preparePlayer(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setExp(0);
        player.setLevel(0);
        player.getInventory().clear();
    }

    public void loadLobbyInventory(Player player) {
        preparePlayer(player);
        player.getInventory().setItem(4, new ItemBuilder(Material.REDSTONE_COMPARATOR).name("§8» §6Settings §8(§7rightclick§8)").build());
        player.getInventory().setItem(8, new ItemBuilder(Material.SLIME_BALL).name("§8» §cQuit §8(§7rightclick§8)").build());
    }

    public void prepareIngamePlayer(Player player) {
        //  preparePlayer(player);
        player.setGameMode(GameMode.SURVIVAL);

        var perk = BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.BLOCK);
        if (perk != null) {
            player.getInventory().setItem(0, perk.setAmount(64).setName("§8» §6Blocks §8(§7rightclick§8)").build());
        } else {
            player.getInventory().setItem(0, new ItemBuilder(Material.SANDSTONE).amount(64).name("§8» §6Blocks §8(§7rightclick§8)").build());
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
        bridgeScoreboard.setLine(11, "§7");

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
        var holoLocation = bridgePlayer.getMapLocation().clone().add(mapType.getHologramCords().xADD(), mapType.getHologramCords().yADD(), mapType.getHologramCords().zADD());

        if (hologram == null) {
            hologram = HologramsAPI.createHologram(Bridge.getInstance(), holoLocation);
            bridgePlayer.setHologram(hologram);

            hologram.appendItemLine(new de.teamholy.core.bukkit.utils.ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setSkullMeta(bridgePlayer.getSkinProfile().getValue(), bridgePlayer.getSkinProfile().getSignature()).build());
            hologram.appendTextLine("");
            hologram.appendTextLine("§fStats of " + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(bridgePlayer.getPlayer().getUniqueId()) + bridgePlayer.getPlayer().getName());
            hologram.appendTextLine("");
            hologram.appendTextLine("§c§lGLOBAL");
            hologram.appendTextLine("§fWins §8» §e" + bridgePlayer.getWins());
            hologram.appendTextLine("§fPlaced blocks §8» §e" + bridgePlayer.getPlacedBlocks());
            hologram.appendTextLine("");
            hologram.appendTextLine("§6§l" + bridgePlayer.getMap().getMapType());
            hologram.appendTextLine("§fBest §2§lsession §ftime §8» §e" + checkBestTime(bridgePlayer.getGlobalBestTime(mapType)));
            hologram.appendTextLine("§fBest §c§lall-time §ftime §8» §e" + checkBestTime(bridgePlayer.getLocalBestTime(mapType)));
            hologram.appendTextLine("§fAverage §c§lall-time §ftime §8» §e" + checkBestTime(getAverageTime(bridgePlayer, bridgePlayer.getMap().getMapType())));

        } else {
            ((TextLine) hologram.getLine(2)).setText("§fStats of " + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(bridgePlayer.getPlayer().getUniqueId()) + bridgePlayer.getPlayer().getName());
            ((TextLine) hologram.getLine(5)).setText("§fWins §8» §e" + bridgePlayer.getWins());
            ((TextLine) hologram.getLine(6)).setText("§fPlaced blocks §8» §e" + bridgePlayer.getPlacedBlocks());
            ((TextLine) hologram.getLine(9)).setText("§fBest §2§lsession §ftime §8» §e" + checkBestTime(bridgePlayer.getGlobalBestTime(mapType)));
            ((TextLine) hologram.getLine(10)).setText("§fBest §c§lall-time §ftime §8» §e" + checkBestTime(bridgePlayer.getLocalBestTime(mapType)));
            ((TextLine) hologram.getLine(11)).setText("§fAverage §c§lall-time §ftime §8» §e" + checkBestTime(getAverageTime(bridgePlayer, bridgePlayer.getMap().getMapType())));
        }

        if (updateMapType) {
            ((TextLine) hologram.getLine(8)).setText("§6§l" + bridgePlayer.getMap().getMapType());
            hologram.teleport(holoLocation);
        }


    }

    public void updateScoreboard(BridgePlayer bridgePlayer) {
        ScoreboardAPI bridgeScoreboard = bridgePlayer.getBridgeScoreboard();
        bridgeScoreboard.updateLine(13, " §7Best Time §8(§e" + (bridgePlayer.getMap() != null ? bridgePlayer.getMap().getMapType().getName() : "All time") + "§8)");
        bridgeScoreboard.updateLine(12, " §e" + checkBestTime(bridgePlayer.getGlobalBestTime(bridgePlayer.getMap().getMapType())));

        String top5 = " §6Top 5 §8(§e" + (bridgePlayer.getMap() != null ? bridgePlayer.getMap().getMapType().getName() : "All time") + "§8)";
        bridgeScoreboard.updateLine(10, top5);

        updateScoreboardForPlayer(bridgePlayer.getMap().getMapType());
    }


    public void ingameSettingsInventory(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §eSettings");

        for (int i = 0; i < 9 * 3; i++) {
            inventory.setItem(i, new de.teamholy.core.bukkit.utils.ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build());
        }

        inventory.setItem(10, new ItemBuilder(Material.SANDSTONE).amount(1).name("§8» §6Blocks").build());
        inventory.setItem(11, new ItemBuilder(Material.ANVIL).amount(1).name("§8» §6Block Break Settings").build());

        //inventory.setItem(4, new ItemBuilder(Material.SLIME_BALL).name("§cIsland Moving")/*.lore("§c§lSOON")*/.lore((bridgePlayer.getSettings().isIslandMoving() ? "§aYes" : "§cNo")).build());
        inventory.setItem(13, new ItemBuilder(Material.RECORD_8).name("§8» §6Sounds").build());

        inventory.setItem(15, new ItemBuilder(Material.PAPER).name("§8» §6Maps").build());
        inventory.setItem(16, new ItemBuilder(Material.ANVIL).name("§8» §6Map Type").build());

        player.openInventory(inventory);
    }


    public Inventory blockSettingsInventory(BridgePlayer bridgePlayer) {

        de.teamholy.core.bukkit.utils.Inventory inventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Block Settings", 9);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(new de.teamholy.core.bukkit.utils.ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        var i = 1;
        for (Settings.BlockAnimationType blockBreakAnimations : Settings.BlockAnimationType.values()) {

            var isSelected = bridgePlayer.getSettings().getBlockAnimationType() == blockBreakAnimations;
            inventory.setItem(blockBreakAnimations.getItemBuilder().setLore(
                    (isSelected ? "§2Selected" :

                            bridgePlayer.getPlayer().hasPermission(blockBreakAnimations.getRankType().getPermission()) ? "§aClick to select" :
                                    "§7Available for " + blockBreakAnimations.getRankType().getRankName() + "§7 and above"
                    )


            ).withGlow(isSelected).build(), i, inventoryClickEvent -> {
                if (bridgePlayer.getPlayer().hasPermission(blockBreakAnimations.getRankType().getPermission())) {
                    bridgePlayer.getSettings().setBlockAnimationType(blockBreakAnimations);
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
                .lore(" §8* §7Current delay: §e" +
                        (bridgePlayer.getSettings().getRemovalTime() == 0 ? "Not set" : bridgePlayer.getSettings().getRemovalTime() + " §eseconds"))
                .lore("")
                .lore("§7Currently " + (bridgePlayer.getSettings().isRemoveBlocks() ? "§aenabled" : "§cdisabled"))
                .lore("§7§oShift Click to enable / disable").build(), 7, event -> {


            if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                bridgePlayer.getSettings().setRemoveBlocks(!bridgePlayer.getSettings().isRemoveBlocks());
                bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + (bridgePlayer.getSettings().isRemoveBlocks() ? "§aActivated" : "§cDisabled") + " the Block Timer!");
            } else if (event.getClick() == ClickType.LEFT) {
                bridgePlayer.getSettings().setRemovalTime((bridgePlayer.getSettings().getRemovalTime() + 1) > 8 ? 8 : bridgePlayer.getSettings().getRemovalTime() + 1);
                bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "Changed the removal time to §e" + bridgePlayer.getSettings().getRemovalTime());
            } else if (event.getClick() == ClickType.RIGHT) {
                bridgePlayer.getSettings().setRemovalTime((bridgePlayer.getSettings().getRemovalTime() - 1) < 1 ? 1 : bridgePlayer.getSettings().getRemovalTime() - 1);
                bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "Changed the removal time to §e" + bridgePlayer.getSettings().getRemovalTime());
            }
            bridgePlayer.getPlayer().openInventory(blockSettingsInventory(bridgePlayer));

        });
        return inventory.getInventory();
    }


    public long checkBestTime(Player player, long current, long bestTime, boolean global) {
        var bridgePlayer = getBridgePlayer(player);

        if (bestTime == 0 || current < bestTime) {
            if (global) {
                bridgePlayer.setGlobalBestTime(bridgePlayer.getMap().getMapType(), current);
            }

            bridgePlayer.setLocalBestTime(bridgePlayer.getMap().getMapType(), current);

            bridgePlayer.getBridgeScoreboard().setLine(11, checkBestTime(bridgePlayer.getGlobalBestTime(bridgePlayer.getMap().getMapType())));

            addBestTime(bridgePlayer);
            updateScoreboard(bridgePlayer);

            if (global) {
                BukkitCore.getAPI().getCoinManager().addCoins(player.getUniqueId(), 20, true);
                sendActionBar(player, "§a+ §e20 Coins");
            } else {
                BukkitCore.getAPI().getCoinManager().addCoins(player.getUniqueId(), 10, true);
                sendActionBar(player, "§a+ §e10 Coins");
            }

            bridgePlayer.addWin();
        }

        if (bridgePlayer.getWins() % 10 == 0) {
            BukkitCore.getAPI().getCoinManager().addCoins(player.getUniqueId(), 30, true);
            sendActionBar(player, "§a+ §e30 Coins");
        }

        return (bestTime == 0 || current < bestTime) ? current : bestTime;
    }

    public String checkBestTime(long bestTime) {
        return (bestTime == 0) ? "§c-/-" : " §e" + FormatTime.formatTimeManually(bestTime);
    }

    private void addBestTime(BridgePlayer bridgePlayer) {
        var mapType = bridgePlayer.getMap().getMapType();

        if (!topPlayer.get(mapType).containsKey(bridgePlayer)) {
            topPlayer.get(mapType).put(bridgePlayer, bridgePlayer.getLocalBestTime(mapType));
        } else if (topPlayer.get(mapType).get(bridgePlayer) > bridgePlayer.getLocalBestTime(mapType)) {
            topPlayer.get(mapType).remove(bridgePlayer);
            topPlayer.get(mapType).put(bridgePlayer, bridgePlayer.getLocalBestTime(mapType));
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
                                FormatTime.formatTimeManually(bridgePlayerLongEntry.getKey().getLocalBestTime(bridgeMapType));

                        bridgePlayer.getBridgeScoreboard().updateLine(i.getAndDecrement(), string);

                    });


                });

    }


    public ScoreboardAPI getScoreboard(Player player) {
        return getBridgePlayer(player).getBridgeScoreboard();
    }

    public void spawnBlockAnimation(BridgePlayer bridgePlayer) {
        var blockAnimationType = bridgePlayer.getSettings().getBlockAnimationType();
        HashMap<Block, Long> blocks = (HashMap<Block, Long>) bridgePlayer.getBlocks().clone();

        switch (blockAnimationType) {
            case FALLING -> blocks.forEach((block, time) -> {
                bridgePlayer.getBlocks().remove(block);

                FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
                block.setType(Material.AIR);
                Bukkit.getScheduler().runTaskLater(Bridge.getInstance(),
                        () -> getBlocksInRadius(block.getLocation(), 2).forEach(block1 -> bridgePlayer.getPlayer().sendBlockChange(block1.getLocation(), Material.AIR, (byte) 0)), 5L);
                fallingBlock.setDropItem(false);
                fallingBlock.setHurtEntities(false);


                Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), fallingBlock::remove, 15L);
            });
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
                                    () -> getBlocksInRadius(block.getLocation(), 3).forEach(block1 -> bridgePlayer.getPlayer().sendBlockChange(block1.getLocation(), Material.AIR, (byte) 0)), 5L);
                            atomicInteger.set(0);
                        }

                        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(getBlockEntityId(block), position, (byte) atomicInteger.getAndIncrement());
                        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getData());
                        for (Player allPlayer : Bukkit.getOnlinePlayers()) {
                            ((CraftPlayer) allPlayer).getHandle().playerConnection.sendPacket(packet);
                        }
                    }

                }.runTaskTimer(Bridge.getInstance(), 0L, 1L);

            });
            case DROPPING -> blocks.forEach((block, time) -> {
                if (block.getType() == Material.AIR) return;

                var toDrop = dropItem(block.getLocation().add(0, 1, 0), new ItemStack(block.getType(), 1, block.getData()));

                bridgePlayer.getBlocks().remove(block);
                block.setType(Material.AIR);

                Bukkit.getScheduler().runTaskLater(Bridge.getInstance(),
                        () -> getBlocksInRadius(block.getLocation(), 2).forEach(block1 -> bridgePlayer.getPlayer().sendBlockChange(block1.getLocation(), Material.AIR, (byte) 0)), 5L);
                Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), toDrop::die, 15L);

            });
            default -> blocks.forEach((block, time) -> {
                block.setType(Material.AIR);

                Bukkit.getScheduler().runTaskLater(Bridge.getInstance(),
                        () -> getBlocksInRadius(block.getLocation(), 2).forEach(block1 -> bridgePlayer.getPlayer().sendBlockChange(block1.getLocation(), Material.AIR, (byte) 0)), 5L);
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
        // There will be some overlap here, but these effects are very localized, so it should be OK.
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
}
