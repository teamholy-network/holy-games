package de.teamholy.bridge.player.settings;

import com.google.common.collect.Lists;
import de.teamholy.bridge.map.skin.BridgeMapSkin;
import de.teamholy.bridge.player.settings.sounds.BridgeSound;
import de.teamholy.core.bukkit.perks.enums.PerkRankType;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@Setter
public class BridgeSettings {

    private boolean removeBlocks = false;
    private long removalTime = 0;

    private BlockAnimationType blockAnimationType = BlockAnimationType.NONE;
    private TimerPlace timerPlace = TimerPlace.ACTION_BAR;

    private List<BridgeSound> sounds = Lists.newArrayList();
    private List<BridgeMapSkin> mapSkins = Lists.newArrayList();

    private HashMap<BridgeSoundEventType, BridgeSound> currentSounds = new HashMap<>();
    private HashMap<BridgeSound, BridgeSoundEventType> soundEvents = new HashMap<>();

    private int offsetZ = 0;

    @Getter @AllArgsConstructor
    public enum BlockAnimationType {

        NONE("None", PerkRankType.PLAYER, new ItemBuilder(Material.BARRIER).setName("§8» §fNone")),
        DROPPING("Dropping",PerkRankType.PREMIUM, new ItemBuilder(Material.EGG).setName("§8» §aDropping")),
        FALLING("Falling", PerkRankType.PREMIUM, new ItemBuilder(Material.FEATHER).setName("§8» §bFalling")),
        BREAK("Break",PerkRankType.VIP, new ItemBuilder(Material.ANVIL).setName("§8» §cBreak")),
        TNT("Explosion",PerkRankType.HOLY, new ItemBuilder(Material.TNT).setName("§8» §4Explosion"));
        //BLACK_HOLE("Lightning",PerkRankType.HOLY,
        // new ItemBuilder(Material.SKULL_ITEM,1,3).setSkullMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzQ2NWMxMjE5NThjMDUyMmUzZGNjYjNkMTRkN" +
               // "jg2MTJkNjMxN2NkMzgwYjBlNjQ2YjYxYjc0MjBiOTA0YWYwMiJ9fX0=","").setName("§8» §bLightning"));

        private final String name;
        private final PerkRankType rankType;
        private ItemBuilder itemBuilder;
    }

    @Getter @AllArgsConstructor
    public enum TimerPlace {
        ACTION_BAR("Actionbar"), TITLE("Title"), SCOREBOARD("Scoreboard");

        private final String name;
    }

    @Getter
    @AllArgsConstructor
    public enum BridgeSoundEventType {
        NEW_RECORD("§b§lRecord"), DEATH("§c§lDeath"), WIN("§a§lWin");

        private final String name;


    }
}
