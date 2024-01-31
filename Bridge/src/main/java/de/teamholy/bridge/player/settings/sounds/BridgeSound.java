package de.teamholy.bridge.player.settings.sounds;

import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bukkit.perks.PerkRankType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Sound;

import javax.annotation.Nullable;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@AllArgsConstructor
public class BridgeSound {

    private String name;
    private String displayName;
    private BridgeSoundType soundType;

    @Nullable
    private Sound bukkitSound;
    private float volume;
    private float pitch;

    private long price;
    private PerkRankType perkRankType;

    private final String permission = "bridge.sound." + name;



}
