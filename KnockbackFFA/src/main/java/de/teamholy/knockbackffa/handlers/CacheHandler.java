package de.teamholy.knockbackffa.handlers;

import de.teamholy.knockbackffa.models.MapEntry;
import de.teamholy.knockbackffa.models.PlayerEntry;
import de.teamholy.knockbackffa.models.TeamEntry;
import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

/* copyright by Yassino */
@Getter
public class CacheHandler {
    private final HashMap<String, MapEntry> mapEntrys = new HashMap<>();
    private final HashMap<UUID, PlayerEntry> playerEntrys = new HashMap<>();
    private final HashMap<String, TeamEntry> teamEntryHashMap = new HashMap<>();
}
