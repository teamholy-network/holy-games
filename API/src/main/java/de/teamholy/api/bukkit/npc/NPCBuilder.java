package de.teamholy.api.bukkit.npc;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.npc.models.NPCEntry;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/* copyright by Yassino */
public class NPCBuilder {

    private String name, displayName;
    private UUID skin;
    private int maxSeeRange, maxTargetRange;
    private boolean looker, kickBack;
    private Location location;
    private final List<String> holoLines = new ArrayList<>();

    public NPCBuilder(String name, String displayName, UUID skin, int maxSeeRange, int maxTargetRange, boolean looker, boolean kickBack, Location location) {
        this.name = name;
        this.displayName = displayName;
        this.skin = skin;
        this.maxSeeRange = maxSeeRange;
        this.maxTargetRange = maxTargetRange;
        this.looker = looker;
        this.kickBack = kickBack;
        this.location = location;
    }

    public NPCBuilder addHolo(String... lines) {
        holoLines.addAll(Arrays.asList(lines));
        return this;
    }

    public void build(Player player) {
        if (BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()) != null) {
            BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()).getNpcs().put(name,
                    new NPCEntry(displayName, skin, location, maxSeeRange, maxTargetRange, looker, kickBack).setPlayer(player).addHolo(holoLines));

        }
      }
}
