package de.teamholy.bridge.player.settings.sounds;

import lombok.Getter;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public enum BridgeSoundType {

    DEATH("Sad Sound"),
    WIN("Happy Sound"),
    SONG("Music");

    private final String name;

    BridgeSoundType(String name) {
        this.name = name;
    }
}
