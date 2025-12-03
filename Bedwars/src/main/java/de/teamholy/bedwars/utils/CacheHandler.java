package de.teamholy.bedwars.utils;

import de.teamholy.bedwars.model.MapEntry;
import de.teamholy.bedwars.model.PlayerEntry;
import de.teamholy.bedwars.model.TeamEntry;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/* copyright by Yassino */
@Getter
public class CacheHandler {
    private final HashMap<UUID, PlayerEntry>  playerEntries = new HashMap<>();
    private final ArrayList<TeamEntry>  teamEntries = new ArrayList<>();
    private final HashMap<String, MapEntry>  mapEntries = new HashMap<>();

    public TeamEntry getTeamByName(String name) {

        for (TeamEntry teamEntry : teamEntries) {
            if (teamEntry.getName().equalsIgnoreCase(name)) {
                return teamEntry;
            }
        }
        return null;
    }
}
