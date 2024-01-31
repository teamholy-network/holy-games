package de.teamholy.bridge.player.settings.sounds;

import de.teamholy.core.bukkit.perks.PerkRankType;
import lombok.Getter;
import org.bukkit.Sound;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public enum BridgeSounds {

    DEATH_WITHER(new BridgeSound("death_wither", "", BridgeSoundType.DEATH, Sound.WITHER_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER)),
    DEATH_ENDER_DRAGON(new BridgeSound("death_ender_dragon", "", BridgeSoundType.DEATH, Sound.ENDERDRAGON_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER)),
    DEATH_ENDERMAN(new BridgeSound("death_enderman", "", BridgeSoundType.DEATH, Sound.ENDERMAN_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER)),
    DEATH_GHAST(new BridgeSound("death_ghast", "", BridgeSoundType.DEATH, Sound.GHAST_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER)),
    DEATH_ZOMBIE(new BridgeSound("death_zombie", "", BridgeSoundType.DEATH, Sound.ZOMBIE_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER)),
    DEATH_SKELETON(new BridgeSound("death_skeleton", "", BridgeSoundType.DEATH, Sound.SKELETON_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER)),
    DEATH_SPIDER(new BridgeSound("death_spider", "", BridgeSoundType.DEATH, Sound.SPIDER_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER)),
    DEATH_CREEPER(new BridgeSound("death_creeper", "", BridgeSoundType.DEATH, Sound.CREEPER_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER)),
    DEATH_BLAZE(new BridgeSound("death_blaze", "", BridgeSoundType.DEATH, Sound.BLAZE_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER)),
    DEATH_IRON_GOLEM(new BridgeSound("death_iron_golem", "", BridgeSoundType.DEATH, Sound.IRONGOLEM_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER)),
    DEATH_VILLAGER(new BridgeSound("death_villager", "", BridgeSoundType.DEATH, Sound.VILLAGER_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER)),
    DEATH_WOLF(new BridgeSound("death_wolf", "", BridgeSoundType.DEATH, Sound.WOLF_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER)),

    WIN_LEVEL_UP(new BridgeSound("win_level_up","§aLevel Up", BridgeSoundType.WIN, Sound.LEVEL_UP, 1.0F, 1.0F, 100, PerkRankType.PLAYER)),

    SONG_NEVER_GONNA_GIVE_YOU_UP(
            new BridgeSound("song_never_gonna_give_you_up",
            "§aNever Gonna Give You Up", BridgeSoundType.SONG,
            null, 1.0F, 1.0F, 100, PerkRankType.HOLY)),
    ;

    private BridgeSound bridgeSound;

    BridgeSounds(BridgeSound bridgeSound) {
        this.bridgeSound = bridgeSound;
    }
}
