package de.teamholy.api.cache;

import de.teamholy.api.bukkit.npc.models.NPCPlayer;
import de.teamholy.api.bukkit.npc.models.SkinEntry;
import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.utility.PlayerRank;
import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

/* copyright by Yassino */
@Getter
public class BukkitCacheHandler {
    private final HashMap<UUID, PlayerRank> holyPlayerHashMap;
    private final HashMap<UUID, NPCPlayer> npcPlayerHashMap;
    private final HashMap<UUID, SkinEntry> skinEntryHashMap;
    private final HashMap<UUID, String> countryCodes;
    private final HashMap<UUID, Clan> clanPlayerHashMap;


    public BukkitCacheHandler() {
        this.npcPlayerHashMap = new HashMap<>();
        this.holyPlayerHashMap = new HashMap<>();
        this.skinEntryHashMap = new HashMap<>();
        this.countryCodes = new HashMap<>();
        this.clanPlayerHashMap = new HashMap<>();
    }
}
