package de.teamholy.bridge.player.settings.sounds;

import de.teamholy.core.bukkit.perks.PerkRankType;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public enum BridgeSounds {

    DEATH_WITHER(new BridgeSound(5000,"death_wither", "§c§lWither Death",
            BridgeSoundType.DEATH, Sound.WITHER_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER, Material.NETHER_STAR)),

    DEATH_ENDER_DRAGON(new BridgeSound(5001,"death_ender_dragon", "§c§lEnderdragon Death",
            BridgeSoundType.DEATH, Sound.ENDERDRAGON_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER, Material.DRAGON_EGG)),

    DEATH_ENDERMAN(new BridgeSound(5002,"death_enderman", "§c§lEnderman Death",
            BridgeSoundType.DEATH, Sound.ENDERMAN_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER, Material.ENDER_PEARL)),

    DEATH_GHAST(new BridgeSound(5003,"death_ghast", "§c§lGhast Death",
            BridgeSoundType.DEATH, Sound.GHAST_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER, Material.GHAST_TEAR)),

    DEATH_ZOMBIE(new BridgeSound(5004,"death_zombie", "§c§lZombie Death",
            BridgeSoundType.DEATH, Sound.ZOMBIE_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER, Material.ROTTEN_FLESH)),

    DEATH_SKELETON(new BridgeSound(5005,"death_skeleton", "§c§lSkeleton Death",
            BridgeSoundType.DEATH, Sound.SKELETON_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER, Material.SKULL_ITEM)),

    DEATH_SPIDER(new BridgeSound(5006,"death_spider", "§c§lSpider Death",
            BridgeSoundType.DEATH, Sound.SPIDER_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER, Material.SPIDER_EYE)),

    DEATH_CREEPER(new BridgeSound(5007,"death_creeper", "§c§lCreeper Death",
            BridgeSoundType.DEATH, Sound.CREEPER_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER, Material.SULPHUR)),

    DEATH_BLAZE(new BridgeSound(5008,"death_blaze", "§c§lBlaze Death",
            BridgeSoundType.DEATH, Sound.BLAZE_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER, Material.BLAZE_POWDER)),

    DEATH_IRON_GOLEM(new BridgeSound(5009,"death_iron_golem", "§c§lGolem Death",
            BridgeSoundType.DEATH, Sound.IRONGOLEM_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER, Material.IRON_INGOT)),

    DEATH_VILLAGER(new BridgeSound(5010,"death_villager", "§c§lVillager Death",
            BridgeSoundType.DEATH, Sound.VILLAGER_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER, Material.EMERALD)),

    DEATH_WOLF(new BridgeSound(5011,"death_wolf", "§c§lWolf Death",
            BridgeSoundType.DEATH, Sound.WOLF_DEATH, 1.0F, 1.0F, 100, PerkRankType.PLAYER, Material.BONE)),

    LEVEL_UP(new BridgeSound(5020,"level_up","§a§lLevel Up",
            BridgeSoundType.WIN, Sound.LEVEL_UP, 1.0F, 1.0F, 100, PerkRankType.PLAYER, Material.EXP_BOTTLE)),

    SONG_7thElement(
            new BridgeSound(5050,"song_7thelement",
            "§f§l7thelement Song", BridgeSoundType.SONG,
            null, 1.0F, 1.0F, 100, PerkRankType.HOLY, Material.RECORD_3)),
    SONG_A_Little_Piece_of_Heaven(
            new BridgeSound(5051,"song_alittlepieceofheaven",
            "§f§lA Little Piece of Heaven Song", BridgeSoundType.SONG,
            null, 1.0F, 1.0F, 100, PerkRankType.HOLY, Material.RECORD_4)),;

    private final BridgeSound bridgeSound;

    BridgeSounds(BridgeSound bridgeSound) {
        this.bridgeSound = bridgeSound;
    }

    public static BridgeSound getBridgeSound(int id) {
        for (BridgeSounds bridgeSounds : values()) {
            if (bridgeSounds.getBridgeSound().getPerkId() == id) {
                return bridgeSounds.getBridgeSound();
            }
        }
        return null;
    }
}
