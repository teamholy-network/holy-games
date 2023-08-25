package de.teamholy.knockbackffa.models;

import de.teamholy.knockbackffa.KnockbackFFA;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;

/* copyright by Yassino */
@Getter
public class MapEntry {
    private String mapName;
    private Location spawn;
    private Double spawnHight;
    private Double deathHight;
    private ArrayList<Player> players;
    private Sign sign;

    public MapEntry(String mapName, Location spawn, Double spawnHight, Double deathHight, Sign sign) {
        this.mapName = mapName;
        this.spawn = spawn;
        this.spawnHight = spawnHight;
        this.deathHight = deathHight;
        players = new ArrayList<>();
        this.sign = sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
        KnockbackFFA.getInstance().getYamlConfiguration().set(mapName + ".sign",sign.getLocation());
        try {
            KnockbackFFA.getInstance().getYamlConfiguration().save(KnockbackFFA.getInstance().getCfgfFile());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void updateSign() {
        Block signBlock = sign.getBlock();
        sign.setLine(0,"§0× §6§lKBFFA §0×");
        sign.setLine(1, "   " + mapName + "   ");
        sign.setLine(2, "   Players » " + players.size() + "   ");
        sign.setLine(3,"§0× §6§lTeamHoly §0×");
        sign.update();
    }
}
