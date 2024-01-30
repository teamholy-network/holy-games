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

    private long wins = 0;
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
        if (!statsProfile.exists(Gamemodes.BRIDGE.toString())) {
            String gameKey = Gamemodes.BRIDGE.toString();

            statsProfile.setStat(gameKey, StatsType.ALLTIME, "wins", 0);
            statsProfile.setStat(gameKey, StatsType.ALLTIME, "placedBlocks", 0L);

            statsProfile.setSetting(gameKey, "removeBlocks", "false");
            statsProfile.setSetting(gameKey, "removalTime", "0");
            statsProfile.setSetting(gameKey, "blockAnimationType", "NONE");

            statsProfile.setSetting(gameKey, "shortBest", "0");
            statsProfile.setSetting(gameKey, "longBest", "0");
            statsProfile.setSetting(gameKey, "diagonalBest", "0");

        } else {
            this.wins = statsProfile.getStat(Gamemodes.BRIDGE.toString(), StatsType.ALLTIME, "wins");
            this.placedBlocks = statsProfile.getStat(Gamemodes.BRIDGE.toString(), StatsType.ALLTIME, "placedBlocks");

            this.settings.setRemoveBlocks(Boolean.parseBoolean(statsProfile.getSetting(Gamemodes.BRIDGE.toString(), "removeBlocks")));
            this.settings.setRemovalTime(Long.parseLong(statsProfile.getSetting(Gamemodes.BRIDGE.toString(), "removalTime")));
            this.settings.setBlockAnimationType(Settings.BlockAnimationType.valueOf(statsProfile.getSetting(Gamemodes.BRIDGE.toString(), "blockAnimationType")));

            this.localBestTime.put(BridgeMapType.SHORT, Long.parseLong(statsProfile.getSetting(Gamemodes.BRIDGE.toString(), "shortBest")));
            this.localBestTime.put(BridgeMapType.LONG, Long.parseLong(statsProfile.getSetting(Gamemodes.BRIDGE.toString(), "longBest")));
            this.localBestTime.put(BridgeMapType.DIAGONAL, Long.parseLong(statsProfile.getSetting(Gamemodes.BRIDGE.toString(), "diagonalBest")));

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

        statsProfile.setSetting(gameKey, "shortBest", String.valueOf(this.localBestTime.get(BridgeMapType.SHORT)));
        statsProfile.setSetting(gameKey, "longBest", String.valueOf(this.localBestTime.get(BridgeMapType.LONG)));
        statsProfile.setSetting(gameKey, "diagonalBest", String.valueOf(this.localBestTime.get(BridgeMapType.DIAGONAL)));
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
