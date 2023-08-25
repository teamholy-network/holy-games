package de.teamholy.clutches.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HitType {
    NONE("§7NONE", 0),
    EASY("§aEASY", 1),
    MEDIUM("§6MEDIUM", 1.5),
    HARD("§cHARD", 2);

    private String string;
    private double knockback;
}
