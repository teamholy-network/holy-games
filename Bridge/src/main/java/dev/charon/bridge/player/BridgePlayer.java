package dev.charon.bridge.player;

import com.google.common.collect.Maps;
import de.teamholy.api.bukkit.utils.scoreboard.ScoreboardAPI;
import dev.charon.bridge.map.BridgeMap;
import dev.charon.bridge.map.BridgeMapType;
import dev.charon.bridge.player.settings.Settings;
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
    private long localBestTime;

    private BridgeMapType mapType;

    private Settings settings;

    public BridgePlayer(Player player) {
        this.player = player;
        this.state = PlayerState.LOBBY; // initial normal state
        this.blocks = Maps.newHashMap();
        this.settings = new Settings();
    }

    public enum PlayerState {
        LOBBY, INGAME, SPECTATOR;
    }
}
