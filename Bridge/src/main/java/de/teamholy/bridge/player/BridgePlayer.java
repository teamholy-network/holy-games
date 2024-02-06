package de.teamholy.bridge.player;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.utils.scoreboard.ScoreboardAPI;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMap;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.management.SoundPerkManagement;
import de.teamholy.bridge.player.settings.Settings;
import de.teamholy.bridge.player.settings.sounds.BridgeSound;
import de.teamholy.bridge.player.settings.sounds.BridgeSounds;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.skin.SkinProfile;
import de.teamholy.core.api.manager.CoinManager;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.api.utility.Pagifier;
import de.teamholy.core.bukkit.BukkitCore;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static de.teamholy.bridge.player.management.SoundPerkManagement.MAX_SOUNDS_PER_PAGE;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@Setter
public class BridgePlayer {

    private final UUID uuid;
    private final Player player;

    private Gson gson = new Gson();

    private BridgeMap map;

    private Location mapLocation;

    private PlayerState state;
    private HashMap<Block, Long> blocks;
    private ScoreboardAPI bridgeScoreboard;

    private HashMap<BridgeMapType, Long> localBestTime;
    private HashMap<BridgeMapType, Long> globalBestTime;

    private Settings settings;

    private long wins = 0L;
    private long placedBlocks = 0L;
    private long gamesPlayed = 0L;
    private Map<BridgeMapType, List<Long>> bestTimes = new HashMap<>();

    private Hologram hologram;

    private PerkPlayerProfile perkPlayerProfile;
    private SkinProfile skinProfile;

    public BridgePlayer(UUID uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
        this.state = PlayerState.INGAME; // initial normal state -> lobby got removed
        this.blocks = Maps.newHashMap();
        this.settings = new Settings();
        this.localBestTime = Maps.newHashMap();
        this.globalBestTime = Maps.newHashMap();

        registerDatabaseEntry();


    }

    private void registerDatabaseEntry() {
        GameProfile statsProfile = BukkitCore.getAPI().getGameService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()));
        skinProfile = BukkitCore.getAPI().getSkinService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getSkinService().getRepository().findFirstById(player.getUniqueId()));
        for (BridgeMapType bridgeMapType : BridgeMapType.values()) {
            bestTimes.put(bridgeMapType, Lists.newArrayList());
        }

        String gameKey = Gamemodes.BRIDGE.toString();
        if (!statsProfile.exists(gameKey)) {
            statsProfile.setStat(gameKey, StatsType.ALLTIME, "lol", 0);


            statsProfile.setSetting(gameKey, "wins", String.valueOf(0L));
            statsProfile.setSetting(gameKey, "placedBlocks", String.valueOf(0L));

            statsProfile.setSetting(gameKey, "shortBest", String.valueOf(0L));
            statsProfile.setSetting(gameKey, "longBest", String.valueOf(0L));
            statsProfile.setSetting(gameKey, "diagonalBest", String.valueOf(0L));

            statsProfile.setSetting(gameKey, "removeBlocks", "false");
            statsProfile.setSetting(gameKey, "removalTime", "0");
            statsProfile.setSetting(gameKey, "blockAnimationType", "NONE");

            statsProfile.setSetting(gameKey, "shortBestTimes", gson.toJson(Lists.newArrayList()));
            statsProfile.setSetting(gameKey, "longBestTimes", gson.toJson(Lists.newArrayList()));
            statsProfile.setSetting(gameKey, "diagonalBestTimes", gson.toJson(Lists.newArrayList()));
            statsProfile.setSetting(gameKey, "gamesPlayed", String.valueOf(gamesPlayed));
            statsProfile.setSetting(gameKey,"timerPlace", settings.getTimerPlace().name());

            BukkitCore.getAPI().getGameService().saveEntity(statsProfile, true, true);
        } else {

            if (statsProfile.getSetting(gameKey, "wins") != null) {
                this.wins = Long.parseLong(statsProfile.getSetting(gameKey, "wins"));
            }

            if (statsProfile.getSetting(gameKey, "placedBlocks") != null) {
                this.placedBlocks = Long.parseLong(statsProfile.getSetting(gameKey, "placedBlocks"));
            }

            if (statsProfile.getSetting(gameKey, "shortBest") != null) {
                this.globalBestTime.put(BridgeMapType.SHORT, Long.valueOf(statsProfile.getSetting(gameKey, "shortBest")));
            }

            if (statsProfile.getSetting(gameKey, "longBest") != null) {
                this.globalBestTime.put(BridgeMapType.LONG, Long.valueOf(statsProfile.getSetting(gameKey, "longBest")));
            }
            if (statsProfile.getSetting(gameKey, "diagonalBest") != null) {
                this.globalBestTime.put(BridgeMapType.DIAGONAL, Long.valueOf(statsProfile.getSetting(gameKey, "diagonalBest")));
            }
            if (statsProfile.getSetting(gameKey, "removeBlocks") != null) {
                this.settings.setRemoveBlocks(Boolean.parseBoolean(statsProfile.getSetting(gameKey, "removeBlocks")));
            }
            if (statsProfile.getSetting(gameKey, "removalTime") != null) {
                this.settings.setRemovalTime(Long.parseLong(statsProfile.getSetting(gameKey, "removalTime")));
            }
            if (statsProfile.getSetting(gameKey, "blockAnimationType") != null) {
                this.settings.setBlockAnimationType(Settings.BlockAnimationType.valueOf(statsProfile.getSetting(gameKey, "blockAnimationType")));
            }

            if (statsProfile.getSetting(gameKey, "timerPlace") != null) {
                this.settings.setTimerPlace(Settings.TimerPlace.valueOf(statsProfile.getSetting(gameKey, "timerPlace")));
            }

            Type listType = new TypeToken<List<Long>>() {
            }.getType();
            if (statsProfile.getSetting(gameKey, "shortBestTimes") != null) {
                this.bestTimes.put(BridgeMapType.SHORT, gson.fromJson(statsProfile.getSetting(gameKey, "shortBestTimes"), listType));
            }
            if (statsProfile.getSetting(gameKey, "longBestTimes") != null) {
                this.bestTimes.put(BridgeMapType.LONG, gson.fromJson(statsProfile.getSetting(gameKey, "longBestTimes"), listType));
            }
            if (statsProfile.getSetting(gameKey, "diagonalBestTimes") != null) {
                this.bestTimes.put(BridgeMapType.DIAGONAL, gson.fromJson(statsProfile.getSetting(gameKey, "diagonalBestTimes"), listType));
            }

            if (statsProfile.getSetting(gameKey, "gamesPlayed") != null) {
                this.gamesPlayed = Long.parseLong(statsProfile.getSetting(gameKey, "gamesPlayed"));
            }


            if (statsProfile.getSetting(gameKey, "selectedSound") != null) {
                BridgeSound bridgeSound = BridgeSounds.getBridgeSound(Integer.parseInt(statsProfile.getSetting(gameKey, "selectedSound")));
                if (bridgeSound != null) {
                    this.settings.setCurrentSound(bridgeSound);
                }
            }

            for (var settingsMap :
                    statsProfile.getSettingsMap().entrySet()) {
                if (settingsMap.getKey().equals(gameKey)) {
                    var value = settingsMap.getValue();

                    for (var setting :
                            value.entrySet()) {
                        if (setting.getKey().endsWith("_soundEvent")) {
                            int id = Integer.parseInt(setting.getKey().replace("_soundEvent", ""));
                            BridgeSound bridgeSound = BridgeSounds.getBridgeSound(id);
                            if (bridgeSound != null) {
                                this.settings.getSoundEvents().put(bridgeSound, Settings.BridgeSoundEventType.valueOf(setting.getValue()));
                            }
                        }
                    }
                }
            }
        }
        perkPlayerProfile = BukkitCore.getInstance().getCoreAPI().getPerkPlayerService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getPerkPlayerService().getRepository().findFirstById(player.getUniqueId()));

        loadPerks();
    }

    private void loadPerks() {
        for (int id : perkPlayerProfile.getOwnedPerks()) {
            if (id >= 5000 && id < 5500) {
                BridgeSound bridgeSound = BridgeSounds.getBridgeSound(id);
                if (bridgeSound != null) {
                    settings.getSounds().add(bridgeSound);
                }
            }

            // TODO: Add map perks
            if (id > 5500 && id <= 6000) {
            }
        }
    }

    public void saveStats() {
        GameProfile statsProfile = BukkitCore.getAPI().getGameService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()));

        String gameKey = Gamemodes.BRIDGE.toString();

        statsProfile.setSetting(gameKey, "wins", String.valueOf(this.wins));
        statsProfile.setSetting(gameKey, "placedBlocks", String.valueOf(this.placedBlocks));

        statsProfile.setSetting(gameKey, "removeBlocks", String.valueOf(this.settings.isRemoveBlocks()));
        statsProfile.setSetting(gameKey, "removalTime", String.valueOf(this.settings.getRemovalTime()));
        statsProfile.setSetting(gameKey, "blockAnimationType", this.settings.getBlockAnimationType().name());

        statsProfile.setSetting(gameKey, "shortBestTimes", gson.toJson(this.bestTimes.get(BridgeMapType.SHORT)));
        statsProfile.setSetting(gameKey, "longBestTimes", gson.toJson(this.bestTimes.get(BridgeMapType.LONG)));
        statsProfile.setSetting(gameKey, "diagonalBestTimes", gson.toJson(this.bestTimes.get(BridgeMapType.DIAGONAL)));

        statsProfile.setSetting(gameKey, "gamesPlayed", String.valueOf(this.gamesPlayed));
        statsProfile.setSetting(gameKey, "timerPlace", settings.getTimerPlace().name());

        if (this.settings.getCurrentSound() != null)
            statsProfile.setSetting(gameKey, "selectedSound", String.valueOf(this.settings.getCurrentSound().getPerkId()));

        if (globalBestTime != null) {
            if (this.globalBestTime.get(BridgeMapType.SHORT) != null) {
                statsProfile.setSetting(gameKey, "shortBest", String.valueOf(this.globalBestTime.get(BridgeMapType.SHORT)));
            }

            if (this.globalBestTime.get(BridgeMapType.LONG) != null) {
                statsProfile.setSetting(gameKey, "longBest", String.valueOf(this.globalBestTime.get(BridgeMapType.LONG)));
            }

            if (this.globalBestTime.get(BridgeMapType.DIAGONAL) != null) {
                statsProfile.setSetting(gameKey, "diagonalBest", String.valueOf(this.globalBestTime.get(BridgeMapType.DIAGONAL)));
            }
        }

        for (var set : settings.getSoundEvents().entrySet()) {
            statsProfile.setSetting(gameKey, set.getKey().getPerkId() + "_soundEvent", set.getValue().name());
        }

        BukkitCore.getAPI().
                getGameService().
                saveEntity(statsProfile, true, true);
    }

    public void addWin() {
        this.wins++;
    }

    public void addPlacedBlock() {
        this.placedBlocks++;
    }

    public void addGamesPlayed() { this.gamesPlayed++; }

    public enum PlayerState {
        LOBBY, INGAME, SPECTATOR;
    }

    public long getLocalBestTime(BridgeMapType mapType) {
        return localBestTime.getOrDefault(mapType, 0L);
    }

    public long getGlobalBestTime(BridgeMapType mapType) {
        return globalBestTime.getOrDefault(mapType, 0L);
    }

    public void setLocalBestTime(BridgeMapType mapType, long time) {
        localBestTime.put(mapType, time);
    }

    public void setGlobalBestTime(BridgeMapType mapType, long time) {
        globalBestTime.put(mapType, time);
    }
}
