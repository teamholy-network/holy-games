package de.teamholy.knockbackffa.enums;


import de.slikey.effectlib.util.ParticleEffect;
import de.teamholy.core.bukkit.perks.enums.PerkRankType;
import de.teamholy.core.bukkit.perks.model.Perk;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;


@AllArgsConstructor
@Getter
public enum BowTrail {

    NONE("No killstreak effect", Material.BARRIER, ParticleEffect.DAMAGE_INDICATOR ,  PerkRankType.PLAYER),

    WATER("Water",Material.WATER_BUCKET,ParticleEffect.WATER_DROP, PerkRankType.PREMIUM),
    LAVA("Lava",Material.LAVA_BUCKET,ParticleEffect.DRIP_LAVA, PerkRankType.PREMIUM),
    MUSIC("Music",Material.NOTE_BLOCK,ParticleEffect.NOTE, PerkRankType.PREMIUM),

    ENCHANTMENT_FONT("Enchantment font",Material.ENCHANTMENT_TABLE,ParticleEffect.ENCHANTMENT_TABLE, PerkRankType.VIP),
    RAINBOW("Rainbow",Material.REDSTONE,ParticleEffect.REDSTONE, PerkRankType.VIP),
    HEART("Heart",Material.RED_ROSE,ParticleEffect.HEART, PerkRankType.VIP),

    BARRIER("Barrier",Material.BARRIER,ParticleEffect.BARRIER, PerkRankType.HOLY);

    private final String name;
    private Material material;
    private ParticleEffect particleEffect;
    private PerkRankType perkRankType;


}
