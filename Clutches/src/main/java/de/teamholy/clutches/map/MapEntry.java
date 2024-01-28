package de.teamholy.clutches.map;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.arena.ArenaEntry;
import de.teamholy.clutches.arena.ArenaType;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class MapEntry {

    private String name;
    private World world;
    private ItemBuilder itemBuilder;
    private Location spawn, npc;
    private List<ArenaType> notSupported;

    private HashMap<Integer, ArenaEntry> arenaEntryHashMap = new HashMap<>();

    @SneakyThrows
    public MapEntry(String name, ItemBuilder itemBuilder, List<ArenaType> notSupported) {
        this.name = name;
        this.itemBuilder = itemBuilder;
        this.notSupported = notSupported;
        String world = "CLUTCHES-" + name;


        final int[] spawn = {0};
        for (int i = 0; i < 20; i++) {


            SlimeWorld slimeWorld = Clutches.getInstance().getSlimePlugin()
                    .loadWorld(Clutches.getInstance().getSlimeLoader(),world,true,new SlimePropertyMap())
                    .clone(name + "-" + i,Clutches.getInstance().getSlimePlugin().getLoader("file"));

            Clutches.getInstance().getSlimePlugin().generateWorld(slimeWorld);

            int finalI = i;
            Bukkit.getScheduler().runTaskLater(Clutches.getInstance(),() -> {
                Location location = new Location(Bukkit.getWorld(name + "-" + finalI), 0.5,74,0.5);
                ArenaEntry arenaEntry = new ArenaEntry(spawn[0],this, location );


                arenaEntryHashMap.put(finalI,arenaEntry);
                spawn[0] =  spawn[0] +250;
            },20);
        }
    }



    public ArenaEntry getFreeArena() {
        List<ArenaEntry> arenaEntryList = arenaEntryHashMap.values().stream().filter(arenaEntry -> !arenaEntry.isUsed()).filter(arenaEntry -> arenaEntry.getArenaPlayers().isEmpty()).toList();
        if (arenaEntryList.isEmpty()) {
            return null;
        } else {
            return arenaEntryList.get(0);
        }
    }
}
