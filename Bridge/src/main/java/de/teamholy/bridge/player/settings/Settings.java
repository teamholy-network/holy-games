package de.teamholy.bridge.player.settings;

import com.google.common.collect.Lists;
import de.teamholy.bridge.player.settings.sounds.BridgeSound;
import de.teamholy.bridge.player.settings.sounds.BridgeSoundType;
import de.teamholy.core.bukkit.perks.PerkRankType;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.checkerframework.checker.units.qual.A;

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
public class Settings {

    private boolean removeBlocks = false;
    private long removalTime = 0;

    private BlockAnimationType blockAnimationType = BlockAnimationType.NONE;
    private TimerPlace timerPlace = TimerPlace.ACTION_BAR;

    private List<BridgeSound> sounds = Lists.newArrayList();
    private BridgeSound currentSound = null;

    private HashMap<BridgeSound, BridgeSoundEventType> soundEvents = new HashMap<>();

    @Getter @AllArgsConstructor
    public enum BlockAnimationType {

        NONE("None", PerkRankType.PLAYER, new ItemBuilder(Material.BARRIER).setName("§8» §cNone")),
        DROPPING("Dropping",PerkRankType.PREMIUM, new ItemBuilder(Material.EGG).setName("§8» §aDropping")),
        FALLING("Falling", PerkRankType.VIP, new ItemBuilder(Material.FEATHER).setName("§8» §bFalling")),
        BREAK("Break",PerkRankType.HOLY, new ItemBuilder(Material.ANVIL).setName("§8» §4Break"));

        private final String name;
        private final PerkRankType rankType;
        private ItemBuilder itemBuilder;
    }

    @Getter @AllArgsConstructor
    public enum TimerPlace {
        ACTION_BAR("Actionbar"), TITLE("Title"), SCOREBOARD("Scoreboard");

        private final String name;
    }

    public enum BridgeSoundEventType {
        NEW_RECORD, DEATH, WIN;
    }
}
