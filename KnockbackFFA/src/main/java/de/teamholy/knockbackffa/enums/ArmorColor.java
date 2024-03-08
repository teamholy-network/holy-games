package de.teamholy.knockbackffa.enums;

import de.teamholy.core.bukkit.perks.enums.PerkRankType;
import de.teamholy.core.bukkit.perks.model.Perk;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;

@Getter
@AllArgsConstructor
public enum ArmorColor {

    GREY("Grey", PerkRankType.PLAYER, Color.GRAY),
    ORANGE("Orange",PerkRankType.PREMIUM,Color.ORANGE),
    BLUE("Blue",PerkRankType.PREMIUM,Color.BLUE),
    GREEN("Green",PerkRankType.PREMIUM,Color.GREEN),
    RED("Red",PerkRankType.VIP,Color.RED),
    BLACK("Black",PerkRankType.VIP,Color.BLACK),
    WHITE("White",PerkRankType.VIP,Color.WHITE),
    RAINBOW("Rainbow",PerkRankType.HOLY,Color.GRAY);

    private final String name;
    private PerkRankType perkRankType;
    private Color color;

}
