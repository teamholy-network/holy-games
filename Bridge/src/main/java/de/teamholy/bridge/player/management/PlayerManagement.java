package de.teamholy.bridge.player.management;

import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.utils.scoreboard.ScoreboardAPI;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.util.FormatTime;
import de.teamholy.bridge.util.ItemBuilder;
import de.teamholy.bridge.player.settings.Settings;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
            bridgePlayers.put(player.getUniqueId(), new BridgePlayer(player));
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
        player.setGameMode(GameMode.ADVENTURE);

        player.teleport(player.getWorld().getSpawnLocation());

        player.getInventory().setItem(2, new ItemBuilder(Material.PAPER).name("§bMaps").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.REDSTONE_COMPARATOR).name("§eSettings").build());
        player.getInventory().setItem(6, new ItemBuilder(Material.SLIME_BALL).name("§cLeave").build());
    }

    public void prepareIngamePlayer(Player player) {
        var bridgePlayer = getBridgePlayer(player);

        preparePlayer(player);
        player.setGameMode(GameMode.SURVIVAL);

        player.getInventory().setItem(1, new ItemBuilder(bridgePlayer.getSettings().getBlockMaterial()).amount(64).name("§6Blocks").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.REDSTONE_COMPARATOR).name("§eSettings").build());
        player.getInventory().setItem(7, new ItemBuilder(Material.SLIME_BALL).name("§cQuit").build());
    }

    public void refillBlocks(Player player) {
        var bridgePlayer = getBridgePlayer(player);
        player.getInventory().setItem(1, new ItemBuilder(bridgePlayer.getSettings().getBlockMaterial()).amount(64).name("§6Blocks").build());
    }

    public void ingameSettingsInventory(Player player) {
        var bridgePlayer = getBridgePlayer(player);
        Inventory inventory = Bukkit.createInventory(null, 9, "§8» §eSettings");
        inventory.setItem(1, new ItemBuilder(bridgePlayer.getSettings().getBlockMaterial()).amount(1).name("§6Blocks").build());
        inventory.setItem(2, new ItemBuilder(Material.ANVIL).amount(1).name("§6Block Settings").build());

        inventory.setItem(4, new ItemBuilder(Material.SLIME_BALL).name("§cIsland Moving")/*.lore("§c§lSOON")*/.lore((bridgePlayer.getSettings().isIslandMoving() ? "§aYes" : "§cNo")).build());

        inventory.setItem(6, new ItemBuilder(Material.PAPER).name("§bMaps").build());
        inventory.setItem(7, new ItemBuilder(Material.ANVIL).name("§bMap Settings").build());

        player.openInventory(inventory);
    }

    public Inventory blocksInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, "§8» §6Blocks");
        inventory.setItem(0, new ItemBuilder(Material.SANDSTONE).amount(1).name("§6Sandstone").build());
        inventory.setItem(1, new ItemBuilder(Material.GLASS).amount(1).name("§6Glass").build());
        inventory.setItem(2, new ItemBuilder(Material.WOOL).amount(1).name("§6Wool").build());
        inventory.setItem(3, new ItemBuilder(Material.BARRIER).amount(1).name("§6Barrier").build());
        inventory.setItem(4, new ItemBuilder(Material.DIRT).amount(1).name("§6Dirt").build());
        inventory.setItem(5, new ItemBuilder(Material.GRASS).amount(1).name("§6Grass").build());
        inventory.setItem(6, new ItemBuilder(Material.COBBLESTONE).amount(1).name("§6Cobblestone").build());
        inventory.setItem(7, new ItemBuilder(Material.STONE).amount(1).name("§6Stone").build());
        inventory.setItem(8, new ItemBuilder(Material.BEDROCK).amount(1).name("§6Bedrock").build());
        return inventory;
    }

    public Inventory blockSettingsInventory(BridgePlayer bridgePlayer) {
        Inventory inventory = Bukkit.createInventory(null, 9, "§8» §6Block Settings");

        inventory.setItem(3, new ItemBuilder(Material.BEACON).amount(1).name("§6Animation").build());
        inventory.setItem(5, new ItemBuilder(Material.WATCH).amount(1).name("§eRemove Timer")
                .lore("§8» §7Current Time: §e" +
                        (bridgePlayer.getSettings().getRemovalTime() == 0 ? "Not set" : bridgePlayer.getSettings().getRemovalTime()))
                .lore("")
                .lore("§7Currently " + (bridgePlayer.getSettings().isRemoveBlocks() ? "§aenabled" : "§cdisabled"))
                .lore("§7§oShift Click to enable / disable").build());
        return inventory;
    }

    public Inventory blockAnimationInventory(BridgePlayer bridgePlayer) {
        Inventory inventory = Bukkit.createInventory(null, 9, "§8» §6Block Animation");

        inventory.setItem(3, new ItemBuilder(Material.SAND).amount(1).name("§6Falling").withGlow(bridgePlayer.getSettings().getBlockAnimationType() == Settings.BlockAnimationType.FALLING).build());
        inventory.setItem(4, new ItemBuilder(Material.DIRT).amount(1).name("§6Dropping").withGlow(bridgePlayer.getSettings().getBlockAnimationType() == Settings.BlockAnimationType.DROPPING).build());
        inventory.setItem(5, new ItemBuilder(Material.SAND).amount(1).name("§6Breaking").withGlow(bridgePlayer.getSettings().getBlockAnimationType() == Settings.BlockAnimationType.BREAK).build());
        inventory.setItem(8, new ItemBuilder(Material.BARRIER).amount(1).name("§c§lClear").build());
        return inventory;
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

        bridgeScoreboard.setLine(13, "§8§m-----------------");
        bridgeScoreboard.setLine(12, " §7Best Time");
        bridgeScoreboard.setLine(11, " §c-/-");
        bridgeScoreboard.setLine(10, "§7");

        bridgeScoreboard.setLine(9, " §6Top 5");
        bridgeScoreboard.setLine(8, "§7§r");
        bridgeScoreboard.setLine(2, "§8");
        bridgeScoreboard.setLine(1, "§8§m-----------------");
        bridgeScoreboard.setLine(0, "§f§o" + Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName());
        bridgeScoreboard.build();
    }

    public void updateScoreboard(BridgePlayer bridgePlayer) {
        ScoreboardAPI bridgeScoreboard = bridgePlayer.getBridgeScoreboard();
        bridgeScoreboard.updateLine(12, " §7Best Time §8(§e" + (bridgePlayer.getMap() != null ? bridgePlayer.getMap().getMapType().getName() : "All time") + "§8)");
        bridgeScoreboard.updateLine(11, " §e" + checkBestTime(bridgePlayer.getLocalBestTime(bridgePlayer.getMap().getMapType())));

        String top5 = " §6Top 5 §8(§e" + (bridgePlayer.getMap() != null ? bridgePlayer.getMap().getMapType().getName() : "All time") + "§8)";
        bridgeScoreboard.updateLine(9, top5);

        updateScoreboardForPlayer(bridgePlayer.getMap().getMapType());
    }

    public long checkBestTime(Player player, long current, long bestTime) {
        var bridgePlayer = getBridgePlayer(player);

        if (bestTime == 0 || current < bestTime) {
            bridgePlayer.setLocalBestTime(bridgePlayer.getMap().getMapType(), current);
            bridgePlayer.getBridgeScoreboard().setLine(11, checkBestTime(current));

            addBestTime(bridgePlayer);
            updateScoreboard(bridgePlayer);

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
        AtomicInteger i = new AtomicInteger(7);

        for (Player player : Bukkit.getOnlinePlayers()) {
            var bridgeScoreboard = getScoreboard(player);

            for (int j = 7; j >= 3; j--) {
                if (!bridgeScoreboard.contains(j)) bridgeScoreboard.setLine(j, " §cNo one");
                else bridgeScoreboard.updateLine(j, " §cNo one");
            }
        }

        topPlayer.get(bridgeMapType).entrySet().stream().sorted(Comparator.comparingLong(Map.Entry::getValue)).limit(5).toList().forEach(bridgePlayerLongEntry -> {

            if (i.get() < 3) {
                return;
            }

            Player playerEntry = bridgePlayerLongEntry.getKey().getPlayer();
            String string = " "+
                    BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(playerEntry.getUniqueId()) + playerEntry.getName() + " §8* §7" +
                    FormatTime.formatTimeManually(bridgePlayerLongEntry.getKey().getLocalBestTime(bridgeMapType));

            for (Player player : Bukkit.getOnlinePlayers()) {
                var bridgeScoreboard = getScoreboard(player);
                var bridgePlayer = getBridgePlayer(player);

                if (bridgePlayer.getMap().getMapType() == bridgeMapType) {
                    bridgeScoreboard.updateLine(i.get(), string);
                }
            }
            i.getAndDecrement();
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
                FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
                fallingBlock.setDropItem(false);

                Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> {
                    fallingBlock.remove();
                    block.setType(Material.AIR);
                    bridgePlayer.getBlocks().remove(block);
                }, 25L);
            });
            case BREAK -> blocks.forEach((block, time) -> {
                BlockPosition position = new BlockPosition(block.getX(), block.getY(), block.getZ());

                final AtomicInteger atomicInteger = new AtomicInteger(0);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (atomicInteger.get() == 11) {
                            cancel();
                            block.setType(Material.AIR);
                            bridgePlayer.getBlocks().remove(block);
                            atomicInteger.set(0);
                        }

                        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(getBlockEntityId(block), position, (byte) atomicInteger.getAndIncrement());
                        for (Player allPlayer : Bukkit.getOnlinePlayers()) {
                            ((CraftPlayer) allPlayer).getHandle().playerConnection.sendPacket(packet);
                        }
                    }
                }.runTaskTimer(Bridge.getInstance(), 0L, 1L);
            });
            case DROPPING -> blocks.forEach((block, time) -> {
                Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), block::breakNaturally, 10L);
                bridgePlayer.getBlocks().remove(block);
            });
            default -> blocks.forEach((block, time) -> {
                block.setType(Material.AIR);
                bridgePlayer.getBlocks().remove(block);
            });
        }
    }

    public void sendActionBar(Player player, String text) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(text), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    private int getBlockEntityId(Block block) {
        // There will be some overlap here, but these effects are very localized, so it should be OK.
        return ((block.getX() & 0xFFF) << 20)
                | ((block.getZ() & 0xFFF) << 8)
                | (block.getY() & 0xFF);
    }

}
