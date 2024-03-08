package de.teamholy.knockbackffa.enums;

import de.teamholy.core.bukkit.perks.enums.PerkRankType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum KillStreakEffect {

    NONE("No killstreak effect", Material.BARRIER, PerkRankType.PLAYER),

    WATER("Water",Material.WATER_BUCKET, PerkRankType.PREMIUM),
    FIRE("Fire",Material.LAVA_BUCKET, PerkRankType.PREMIUM),
    MUSIC("Music",Material.NOTE_BLOCK, PerkRankType.PREMIUM),

    TORNADO("Tornado",Material.BLAZE_ROD, PerkRankType.VIP),
    EXPLOSION("Explosion",Material.TNT, PerkRankType.VIP),

    LIGHTNING("Lightning",Material.BLAZE_POWDER, PerkRankType.HOLY);




    private final String name;
    private Material material;
    private PerkRankType perkRankType;

}
