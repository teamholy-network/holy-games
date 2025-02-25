package de.teamholy.sgffa.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/* copyright by Yassino */
@Getter @Setter
public class MapEntry {

    private String mapName;
    private List<Location> spawns;
    private int deathHight;

    public MapEntry(String mapName, List<Location> spawns, int deathHight) {
        this.mapName = mapName;
        this.spawns = spawns;
        this.deathHight = deathHight;
    }
}
