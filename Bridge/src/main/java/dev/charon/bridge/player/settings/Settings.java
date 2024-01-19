package dev.charon.bridge.player.settings;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@Setter
public class Settings {

    private boolean islandMoving = false;
    private Material blockMaterial = Material.SANDSTONE;
    private boolean removeBlocks = false;
    private long removalTime = 0;

    private BlockAnimationType blockAnimationType = BlockAnimationType.NONE;

    public enum BlockAnimationType {
        NONE,
        FALLING,
        DROPPING,
        BREAK;
    }
}
