package de.teamholy.bridge.player.settings;

import com.google.common.collect.Lists;
import de.teamholy.bridge.player.settings.sounds.BridgeSound;
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
public class Settings {

    private Material blockMaterial = Material.SANDSTONE;
    private boolean removeBlocks = false;
    private long removalTime = 0;

    private BlockAnimationType blockAnimationType = BlockAnimationType.NONE;

    private List<BridgeSound> sounds = Lists.newArrayList();
    private BridgeSound currentSound = null;

    private HashMap<BridgeSoundEventType, BridgeSound> soundEvents = new HashMap<>();

    public enum BlockAnimationType {
        NONE,
        FALLING,
        DROPPING,
        BREAK;
    }

    public enum BridgeSoundEventType {

        NEW_RECORD, DEATH, WIN;

    }
}
