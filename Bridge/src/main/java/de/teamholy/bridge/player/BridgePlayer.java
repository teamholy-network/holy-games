package de.teamholy.bridge.player;

import com.google.common.collect.Maps;
import de.teamholy.api.bukkit.utils.scoreboard.ScoreboardAPI;
import de.teamholy.bridge.map.BridgeMap;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.settings.Settings;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bukkit.BukkitCore;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@Setter
public class BridgePlayer {

    private final Player player;
    private BridgeMap map;
    private PlayerState state;
    private HashMap<Block, Long> blocks;
    private ScoreboardAPI bridgeScoreboard;

    private HashMap<BridgeMapType, Long> localBestTime;

    private Settings settings;

    private long wins = 0L;
    private long placedBlocks = 0L;

    public BridgePlayer(Player player) {
        this.player = player;
        this.state = PlayerState.LOBBY; // initial normal state
        this.blocks = Maps.newHashMap();
        this.settings = new Settings();
        this.localBestTime = Maps.newHashMap();

        registerDatabaseEntry();
    }

    private void registerDatabaseEntry() {
        GameProfile statsProfile = BukkitCore.getAPI().getGameService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()));

        String gameKey = Gamemodes.BRIDGE.toString();

        if (!statsProfile.exists(gameKey)) {
            statsProfile.setStat(gameKey, StatsType.ALLTIME, "wins", 0);
            statsProfile.setStat(gameKey, StatsType.ALLTIME, "placedBlocks", 0L);

            statsProfile.setStat(gameKey, StatsType.ALLTIME, "shortBest", 0L);
            statsProfile.setStat(gameKey, StatsType.ALLTIME, "longBest", 0L);
            statsProfile.setStat(gameKey, StatsType.ALLTIME, "diagonalBest", 0L);

            statsProfile.setSetting(gameKey, "removeBlocks", "false");
            statsProfile.setSetting(gameKey, "removalTime", "0");
            statsProfile.setSetting(gameKey, "blockAnimationType", "NONE");

            BukkitCore.getAPI().getGameService().saveEntity(statsProfile, true, true);

        } else {

            this.wins = statsProfile.getStat(gameKey, StatsType.ALLTIME, "wins");
            this.placedBlocks = statsProfile.getStat(gameKey, StatsType.ALLTIME, "placedBlocks");

            System.out.println("wins: " + this.wins);
            if (statsProfile.getStat(gameKey, StatsType.ALLTIME, "shortBest") == 0L) return;
            System.out.println("shortBest: " + statsProfile.getStat(gameKey, StatsType.ALLTIME, "shortBest"));
            this.localBestTime.put(BridgeMapType.SHORT, statsProfile.getStat(gameKey, StatsType.ALLTIME, "shortBest"));

            if (statsProfile.getStat(gameKey, StatsType.ALLTIME, "longBest") == 0L) return;
            this.localBestTime.put(BridgeMapType.LONG, statsProfile.getStat(gameKey, StatsType.ALLTIME, "longBest"));

            if (statsProfile.getStat(gameKey, StatsType.ALLTIME, "diagonalBest") == 0L) return;
            this.localBestTime.put(BridgeMapType.DIAGONAL, statsProfile.getStat(gameKey, StatsType.ALLTIME, "diagonalBest"));

            this.settings.setRemoveBlocks(Boolean.parseBoolean(statsProfile.getSetting(gameKey, "removeBlocks")));
            this.settings.setRemovalTime(Long.parseLong(statsProfile.getSetting(gameKey, "removalTime")));
            this.settings.setBlockAnimationType(Settings.BlockAnimationType.valueOf(statsProfile.getSetting(gameKey, "blockAnimationType")));
        }
    }

    public void saveStats() {
        GameProfile statsProfile = BukkitCore.getAPI().getGameService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()));

        String gameKey = Gamemodes.BRIDGE.toString();

        statsProfile.setStat(gameKey, StatsType.ALLTIME, "wins", this.wins);
        statsProfile.setStat(gameKey, StatsType.ALLTIME, "placedBlocks", this.placedBlocks);

        statsProfile.setSetting(gameKey, "removeBlocks", String.valueOf(this.settings.isRemoveBlocks()));
        statsProfile.setSetting(gameKey, "removalTime", String.valueOf(this.settings.getRemovalTime()));
        statsProfile.setSetting(gameKey, "blockAnimationType", this.settings.getBlockAnimationType().name());

        if (this.localBestTime.isEmpty()) return;

        if (this.localBestTime.get(BridgeMapType.SHORT) == null || this.localBestTime.get(BridgeMapType.SHORT) == 0L)
            return;
        statsProfile.setStat(gameKey, StatsType.ALLTIME, "shortBest", this.localBestTime.get(BridgeMapType.SHORT));
        System.out.println("saved stats 1");
        if (this.localBestTime.get(BridgeMapType.LONG) == null || this.localBestTime.get(BridgeMapType.LONG) == 0L)
            return;
        statsProfile.setStat(gameKey, StatsType.ALLTIME, "longBest", this.localBestTime.get(BridgeMapType.LONG));
        System.out.println("saved stats 2");
        if (this.localBestTime.get(BridgeMapType.DIAGONAL) == null || this.localBestTime.get(BridgeMapType.DIAGONAL) == 0L)
            return;
        statsProfile.setStat(gameKey, StatsType.ALLTIME, "diagonalBest", this.localBestTime.get(BridgeMapType.DIAGONAL));
        System.out.println("saved stats");

        BukkitCore.getAPI().getGameService().saveEntity(statsProfile, true, true);
    }

    public void addWin() {
        this.wins++;
    }

    public void addPlacedBlock() {
        this.placedBlocks++;
    }

    public enum PlayerState {
        LOBBY, INGAME, SPECTATOR;
    }

    public long getLocalBestTime(BridgeMapType mapType) {
        return localBestTime.getOrDefault(mapType, 0L);
    }

    public void setLocalBestTime(BridgeMapType mapType, long time) {
        localBestTime.put(mapType, time);
    }
}
