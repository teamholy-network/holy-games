package de.teamholy.knockbackffa.models;

import de.teamholy.knockbackffa.KnockbackFFA;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a map entry for Knockback FFA.
 * Copyright by Yassino
 */
@Getter
public class MapEntry {
    private final String mapName;
    private final Location spawn;
    private final Double spawnHeight;
    private final Double deathHeight;
    private final List<Player> players;
    private Sign sign;

    public MapEntry(String mapName, Location spawn, Double spawnHeight, Double deathHeight, Sign sign) {
        this.mapName = mapName;
        this.spawn = spawn;
        this.spawnHeight = spawnHeight;
        this.deathHeight = deathHeight;
        this.players = new ArrayList<>();
        this.sign = sign;
    }

    /**
     * Sets the sign for this map and saves it to configuration.
     *
     * @param sign the sign to set
     */
    public void setSign(Sign sign) {
        this.sign = sign;
        KnockbackFFA.getInstance().getYamlConfiguration().set(mapName + ".sign", sign.getLocation());
        try {
            KnockbackFFA.getInstance().getYamlConfiguration().save(KnockbackFFA.getInstance().getConfigFile());
        } catch (IOException exception) {
            KnockbackFFA.getInstance().getLogger().warning("Failed to save sign location: " + exception.getMessage());
        }
    }

    /**
     * Updates the sign with current map information.
     */
    public void updateSign() {
        Block signBlock = sign.getBlock();
        sign.setLine(0, "§0× §6§lKBFFA §0×");
        sign.setLine(1, "   " + mapName + "   ");
        sign.setLine(2, "   Players » " + players.size() + "   ");
        sign.setLine(3, "§0× §6§lTeamHoly §0×");
        sign.update();
    }
}
