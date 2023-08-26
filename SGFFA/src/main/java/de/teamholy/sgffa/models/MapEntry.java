package de.teamholy.sgffa.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;

/* copyright by Yassino */
@Getter @Setter
public class MapEntry {

    private String mapName;
    private ArrayList<Location> spawns;
    private int deathHight;

    public MapEntry(String mapName, ArrayList spawns, int deathHight) {
        this.mapName = mapName;
        this.spawns = spawns;
        this.deathHight = deathHight;
    }
}
