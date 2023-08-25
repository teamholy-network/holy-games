package de.teamholy.clutches.arena;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ArenaType {
    REDUCE("Reduce"),
    CLUTCH("Clutch"),
    EXPERIMENTAL("Multi Reduce"),
    DIAGONAL_CLUTCH("Diagonal Clutch");

    private String name;
}
