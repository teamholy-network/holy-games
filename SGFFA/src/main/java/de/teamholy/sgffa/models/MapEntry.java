package de.teamholy.sgffa.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.List;

/**
 * Represents a map entry for SGFFA.
 * Copyright by Yassino
 */
@Getter
@Setter
public class MapEntry {

    private String mapName;
    private List<Location> spawns;
    private int deathHeight;

    public MapEntry(String mapName, List<Location> spawns, int deathHeight) {
        this.mapName = mapName;
        this.spawns = spawns;
        this.deathHeight = deathHeight;
    }
}
