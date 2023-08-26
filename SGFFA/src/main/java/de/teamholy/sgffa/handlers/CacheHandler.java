package de.teamholy.sgffa.handlers;

import de.teamholy.sgffa.models.MapEntry;
import de.teamholy.sgffa.models.PlayerEntry;
import de.teamholy.sgffa.models.TeamEntry;
import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

/* copyright by Yassino */
@Getter
public class CacheHandler {

    private final HashMap<String, MapEntry> mapEntryHashMap = new HashMap<>();
    private final HashMap<UUID, PlayerEntry> playerEntryHashMap = new HashMap<>();
    private HashMap<String, TeamEntry> teamEntryHashMap = new HashMap<>();

}
