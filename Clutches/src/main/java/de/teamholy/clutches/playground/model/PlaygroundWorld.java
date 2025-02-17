package de.teamholy.clutches.playground.model;

import com.grinderwolf.swm.api.world.SlimeWorld;
import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.player.PlayerState;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;

/* copyright by Yassino */
@Setter @Getter
public class PlaygroundWorld {

    private SlimeWorld slimeWorld;
    private String name;
    private String materialAndSubId;
    private int deathHeight;
    private ArrayList<Location> spawns = new ArrayList<>();

    public ItemBuilder asItemBuilder() {
        String[] typeAndId = materialAndSubId.split(";");
        Material material = Material.getMaterial(typeAndId[0]);
        int id = Integer.parseInt(typeAndId[1]);
        return new ItemBuilder(material,
                (int) Clutches.getInstance().getPlayerEntryHandler().values()
                        .stream().filter(playerEntry -> playerEntry.getPlayerState() == PlayerState.PLAYGROUND)
                        .filter(playerEntry -> playerEntry.getPlaygroundPlayer().getPlaygroundWorld() == this).count()
                , (byte) id).setName("§8» §6" + name);
    }

}
