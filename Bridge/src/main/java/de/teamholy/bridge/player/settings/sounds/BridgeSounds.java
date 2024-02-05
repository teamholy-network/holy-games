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

    // ids of sounds max to 5500

    DEATH_WITHER(new BridgeSound(5000,"death_wither", "§c§lWither Death",
            BridgeSoundType.SAD, Sound.WITHER_DEATH, 1.0F, 1.0F, 0, PerkRankType.PREMIUM, Material.NETHER_STAR,null)),

    DEATH_ENDER_DRAGON(new BridgeSound(5001,"death_ender_dragon", "§c§lEnderdragon Death",
            BridgeSoundType.SAD, Sound.ENDERDRAGON_DEATH, 1.0F, 1.0F, 100, PerkRankType.VIP, Material.DRAGON_EGG,null)),

    DEATH_ENDERMAN(new BridgeSound(5002,"death_enderman", "§c§lEnderman Death",
            BridgeSoundType.SAD, Sound.ENDERMAN_DEATH, 1.0F, 1.0F, 100, null, Material.ENDER_PEARL,null)),

    DEATH_GHAST(new BridgeSound(5003,"death_ghast", "§c§lGhast Death",
            BridgeSoundType.SAD, Sound.GHAST_DEATH, 1.0F, 1.0F, 100, null, Material.GHAST_TEAR,null)),

    DEATH_ZOMBIE(new BridgeSound(5004,"death_zombie", "§c§lZombie Death",
            BridgeSoundType.SAD, Sound.ZOMBIE_DEATH, 1.0F, 1.0F, 50, null, Material.ROTTEN_FLESH,null)),

    DEATH_SKELETON(new BridgeSound(5005,"death_skeleton", "§c§lSkeleton Death",
            BridgeSoundType.SAD, Sound.SKELETON_DEATH, 1.0F, 1.0F, 50, null, Material.SKULL_ITEM,null)),

    DEATH_SPIDER(new BridgeSound(5006,"death_spider", "§c§lSpider Death",
            BridgeSoundType.SAD, Sound.SPIDER_DEATH, 1.0F, 1.0F, 100, null, Material.SPIDER_EYE,null)),

    DEATH_CREEPER(new BridgeSound(5007,"death_creeper", "§c§lCreeper Death",
            BridgeSoundType.SAD, Sound.CREEPER_DEATH, 1.0F, 1.0F, 100, null, Material.SULPHUR,null)),

    DEATH_BLAZE(new BridgeSound(5008,"death_blaze", "§c§lBlaze Death",
            BridgeSoundType.SAD, Sound.BLAZE_DEATH, 1.0F, 1.0F, 100, null, Material.BLAZE_POWDER,null)),

    DEATH_IRON_GOLEM(new BridgeSound(5009,"death_iron_golem", "§c§lGolem Death",
            BridgeSoundType.SAD, Sound.IRONGOLEM_DEATH, 1.0F, 1.0F, 100, PerkRankType.PREMIUM, Material.IRON_INGOT,null)),

    DEATH_VILLAGER(new BridgeSound(5010,"death_villager", "§c§lVillager Death",
            BridgeSoundType.SAD, Sound.VILLAGER_DEATH, 1.0F, 1.0F, 100, PerkRankType.VIP, Material.EMERALD,null)),

    DEATH_WOLF(new BridgeSound(5011,"death_wolf", "§c§lWolf Death",
            BridgeSoundType.SAD, Sound.WOLF_DEATH, 1.0F, 1.0F, 100, null, Material.BONE,null)),

    LEVEL_UP(new BridgeSound(5020,"level_up","§a§lLevel Up",
            BridgeSoundType.HAPPY, Sound.LEVEL_UP, 1.0F, 1.0F, 100, null, Material.EXP_BOTTLE,null)),

    SONG_7thElement(
            new BridgeSound(5050,"song_7thelement",
            "§f§l7thelement Song", BridgeSoundType.SONG,
            null, 1.0F, 1.0F, 100, PerkRankType.HOLY, Material.RECORD_3,null)),
    SONG_A_Little_Piece_of_Heaven(
            new BridgeSound(5051,"song_alittlepieceofheaven",
            "§f§lA Little Piece of Heaven Song", BridgeSoundType.SONG,
            null, 1.0F, 1.0F, 200, null, Material.RECORD_4,null)),;

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
