package de.teamholy.bridge.player;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.teamholy.bridge.map.BridgeMap;
import de.teamholy.bridge.map.skin.BridgeMapSkin;
import de.teamholy.bridge.map.skin.BridgeMapSkins;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.settings.BridgeSettings;
import de.teamholy.bridge.player.settings.items.BridgeItems;
import de.teamholy.bridge.player.settings.sounds.BridgeSound;
import de.teamholy.bridge.player.settings.sounds.BridgeSounds;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.entities.skin.SkinProfile;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.InventoryUtils;
import de.teamholy.core.bukkit.utils.ScoreboardAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

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

    private Location uneditedLocation;

    private PlayerState state;
    private HashMap<Block, Long> blocks;
    private ScoreboardAPI bridgeScoreboard;

    private HashMap<BridgeMapType, Long> localBestTime;
    private HashMap<BridgeMapType, Long> globalBestTime;

    private BridgeMapType lastPlayedMap = BridgeMapType.SHORT;

    private BridgeSettings bridgeSettings;
    private Inventory inventory = createInventory();

    private long wins = 0L;
    private long placedBlocks = 0L;
    private long gamesPlayed = 0L;
    private Map<BridgeMapType, List<Long>> bestTimes = new HashMap<>();
    private Map<BridgeMapType, BridgeMapSkin> selectedSkins = new HashMap<>();

    private boolean preview = false;

    private Hologram hologram;

    private PerkPlayerProfile perkPlayerProfile;
    private SkinProfile skinProfile;

    private long cooldown = System.currentTimeMillis() + 3000L;

    private Player toSpectate = null;

    public BridgePlayer(UUID uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
        this.state = PlayerState.INGAME; // initial normal state -> lobby got removed
        this.blocks = Maps.newHashMap();
        this.bridgeSettings = new BridgeSettings();
        this.localBestTime = Maps.newHashMap();
        this.globalBestTime = Maps.newHashMap();

        registerDatabaseEntry();


    }

    public Inventory createInventory() {
        inventory = Bukkit.createInventory(null, 9, "inv");
        for (BridgeItems bridgeItems : BridgeItems.values()) {
            inventory.setItem(bridgeItems.getSlot(), bridgeItems.getItemStack());
        }
        return inventory;
    }

    private void registerDatabaseEntry() {
        GameProfile statsProfile = BukkitCore.getAPI().getGameService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()));
        skinProfile = BukkitCore.getAPI().getSkinService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getSkinService().getRepository().findFirstById(player.getUniqueId()));
        for (BridgeMapType bridgeMapType : BridgeMapType.values()) {
            bestTimes.put(bridgeMapType, Lists.newArrayList());
            selectedSkins.put(bridgeMapType, BridgeMapSkins.getDefaultSkin(bridgeMapType));
        }

        String gameKey = Gamemodes.BRIDGE.toString();
        if (!statsProfile.exists(gameKey)) {
            statsProfile.setStat(gameKey, StatsType.ALLTIME, "lol", 0);


            statsProfile.setSetting(gameKey, "wins", String.valueOf(0L));
            statsProfile.setSetting(gameKey, "placedBlocks", String.valueOf(0L));
            statsProfile.setSetting(gameKey,"lastPlayedMap", lastPlayedMap.toString().toLowerCase());


            statsProfile.setSetting(gameKey, "removeBlocks", "false");
            statsProfile.setSetting(gameKey, "removalTime", "0");
            statsProfile.setSetting(gameKey, "blockAnimationType", "NONE");

            statsProfile.setSetting(gameKey, "gamesPlayed", String.valueOf(gamesPlayed));
            statsProfile.setSetting(gameKey,"timerPlace", bridgeSettings.getTimerPlace().name());
            statsProfile.setSetting(gameKey,"inventory", InventoryUtils.inventoryToString(inventory));

            for (BridgeMapType bridgeMapType : BridgeMapType.values()) {
                statsProfile.setSetting(gameKey, bridgeMapType.name().toLowerCase() + "Best", String.valueOf(0L));
                statsProfile.setSetting(gameKey, bridgeMapType.name().toLowerCase() + "BestTimes", gson.toJson(Lists.newArrayList()));
                statsProfile.setSetting(gameKey, bridgeMapType.name().toLowerCase() + "Selected", String.valueOf(BridgeMapSkins.getDefaultSkin(bridgeMapType).id()));
            }

            statsProfile.setSetting(gameKey, "offsetZ", String.valueOf(bridgeSettings.getOffsetZ()));

            BukkitCore.getAPI().getGameService().saveEntity(statsProfile, true, true);
        } else {

            for (BridgeMapType bridgeMapType : BridgeMapType.values()) {

                if (statsProfile.getSetting(gameKey, bridgeMapType.name().toLowerCase() + "Best") == null) {
                    statsProfile.setSetting(gameKey, bridgeMapType.name().toLowerCase() + "Best", String.valueOf(0L));
                    statsProfile.setSetting(gameKey, bridgeMapType.name().toLowerCase() + "BestTimes", gson.toJson(Lists.newArrayList()));
                    statsProfile.setSetting(gameKey, bridgeMapType.name().toLowerCase() + "Selected", String.valueOf(BridgeMapSkins.getDefaultSkin(bridgeMapType).id()));
                }

                this.globalBestTime.put(bridgeMapType, Long.valueOf(statsProfile.getSetting(gameKey, bridgeMapType.name().toLowerCase() + "Best")));
                this.bestTimes.put(bridgeMapType, gson.fromJson(statsProfile.getSetting(gameKey, bridgeMapType.name().toLowerCase() + "BestTimes"), new TypeToken<List<Long>>() {}.getType()));
                this.selectedSkins.put(bridgeMapType, BridgeMapSkins.getById(Integer.parseInt(statsProfile.getSetting(gameKey, bridgeMapType.name().toLowerCase() + "Selected"))));
            }

            if (statsProfile.getSetting(gameKey, "lastPlayedMap") != null) {
                this.lastPlayedMap = BridgeMapType.mapType(statsProfile.getSetting(gameKey, "lastPlayedMap"));
            }

            if (statsProfile.getSetting(gameKey, "wins") != null) {
                this.wins = Long.parseLong(statsProfile.getSetting(gameKey, "wins"));
            }

            if (statsProfile.getSetting(gameKey, "placedBlocks") != null) {
                this.placedBlocks = Long.parseLong(statsProfile.getSetting(gameKey, "placedBlocks"));
            }

            if (statsProfile.getSetting(gameKey, "removeBlocks") != null) {
                this.bridgeSettings.setRemoveBlocks(Boolean.parseBoolean(statsProfile.getSetting(gameKey, "removeBlocks")));
            }
            if (statsProfile.getSetting(gameKey, "removalTime") != null) {
                this.bridgeSettings.setRemovalTime(Long.parseLong(statsProfile.getSetting(gameKey, "removalTime")));
            }
            if (statsProfile.getSetting(gameKey, "blockAnimationType") != null) {
                this.bridgeSettings.setBlockAnimationType(BridgeSettings.BlockAnimationType.valueOf(statsProfile.getSetting(gameKey, "blockAnimationType")));
            }

            if (statsProfile.getSetting(gameKey, "timerPlace") != null) {
                this.bridgeSettings.setTimerPlace(BridgeSettings.TimerPlace.valueOf(statsProfile.getSetting(gameKey, "timerPlace")));
            }


            if (statsProfile.getSetting(gameKey, "gamesPlayed") != null) {
                this.gamesPlayed = Long.parseLong(statsProfile.getSetting(gameKey, "gamesPlayed"));
            }

            if (statsProfile.getSetting(gameKey, "inventory") != null && !statsProfile.getSetting(gameKey, "inventory").isEmpty()) {
                this.inventory = InventoryUtils.inventoryFromString(statsProfile.getSetting(gameKey, "inventory"));
                if (!BridgeItems.correctInventory(inventory)) {
                    this.inventory = createInventory();
                }
            }


            if (statsProfile.getSetting(gameKey, "offsetZ") != null) {
                this.bridgeSettings.setOffsetZ(Integer.parseInt(statsProfile.getSetting(gameKey, "offsetZ")));
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
                                this.bridgeSettings.getSoundEvents().put(bridgeSound, BridgeSettings.BridgeSoundEventType.valueOf(setting.getValue()));
                            }
                        }
                        if (setting.getValue().endsWith("_selectedSound")) {
                            int id = Integer.parseInt(setting.getValue().replace("_selectedSound", ""));
                            BridgeSound bridgeSound = BridgeSounds.getBridgeSound(id);
                            if (bridgeSound != null) {
                                this.bridgeSettings.getCurrentSounds().put(BridgeSettings.BridgeSoundEventType.valueOf(setting.getKey()), bridgeSound);
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
                    bridgeSettings.getSounds().add(bridgeSound);
                }
            }

            // TODO: Add map perks
            if (id > 5500 && id <= 6000) {
                Arrays.stream(BridgeMapSkins.values())
                        .filter(bridgeMapSkins -> bridgeMapSkins.getBridgeMapSkin().id() == id)
                        .toList()
                        .forEach(bridgeMapSkins -> bridgeSettings.getMapSkins().add(bridgeMapSkins.getBridgeMapSkin()));
            }
        }
    }

    public void saveStats() {
        GameProfile statsProfile = BukkitCore.getAPI().getGameService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()));

        String gameKey = Gamemodes.BRIDGE.toString();

        statsProfile.setSetting(gameKey, "wins", String.valueOf(this.wins));
        statsProfile.setSetting(gameKey, "placedBlocks", String.valueOf(this.placedBlocks));
        statsProfile.setSetting(gameKey, "lastPlayedMap", lastPlayedMap.toString().toLowerCase());

        statsProfile.setSetting(gameKey, "removeBlocks", String.valueOf(this.bridgeSettings.isRemoveBlocks()));
        statsProfile.setSetting(gameKey, "removalTime", String.valueOf(this.bridgeSettings.getRemovalTime()));
        statsProfile.setSetting(gameKey, "blockAnimationType", this.bridgeSettings.getBlockAnimationType().name());

        for (BridgeMapType bridgeMapType : BridgeMapType.values()) {
            statsProfile.setSetting(gameKey, bridgeMapType.name().toLowerCase() + "BestTimes", gson.toJson(this.bestTimes.get(bridgeMapType)));
            statsProfile.setSetting(gameKey, bridgeMapType.name().toLowerCase() + "Selected", String.valueOf(Arrays.stream(BridgeMapSkins.values())
                    .filter(bridgeMapSkins -> bridgeMapSkins.getBridgeMapSkin() == getSelectedSkins().get(bridgeMapType)).findFirst().get().getBridgeMapSkin().id()));
        }

        if (globalBestTime != null) {
            for (BridgeMapType bridgeMapType : BridgeMapType.values()) {
                if (this.globalBestTime.get(bridgeMapType) != null) {
                    statsProfile.setSetting(gameKey, bridgeMapType.name().toLowerCase() + "Best", String.valueOf(this.globalBestTime.get(bridgeMapType)));
                }
            }
        }

        statsProfile.setSetting(gameKey, "gamesPlayed", String.valueOf(this.gamesPlayed));
        statsProfile.setSetting(gameKey, "timerPlace", bridgeSettings.getTimerPlace().name());

        statsProfile.setSetting(gameKey, "inventory", InventoryUtils.inventoryToString(inventory));

        statsProfile.setSetting(gameKey, "offsetZ", String.valueOf(bridgeSettings.getOffsetZ()));


        for (var set : bridgeSettings.getSoundEvents().entrySet()) {
            statsProfile.setSetting(gameKey, set.getKey().getPerkId() + "_soundEvent", set.getValue().name());
        }

        for (var set : bridgeSettings.getCurrentSounds().entrySet()) {
            statsProfile.setSetting(gameKey, set.getKey().name(), set.getValue().getPerkId() + "_selectedSound");
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
        LOBBY, INGAME, SPECTATOR
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
