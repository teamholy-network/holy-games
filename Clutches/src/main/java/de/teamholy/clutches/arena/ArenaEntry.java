package de.teamholy.clutches.arena;

import de.teamholy.clutches.map.MapEntry;
import de.teamholy.clutches.player.PlayerEntry;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;

@Setter
@Getter
public class ArenaEntry {



    int id;
    private Location npc;
    private double death;
    private MapEntry mapEntry;
    private World world;
    private ArrayList<PlayerEntry> arenaPlayers;
    private boolean isUsed = false;

    public ArenaEntry(int id, MapEntry mapEntry, Location location) {
        this.mapEntry = mapEntry;
        arenaPlayers = new ArrayList<>();


        this.id = id;
        npc = location;
        world = location.getWorld();


        death = 67;

    }


    public Location getPlayerSpawn() {
        Location temp = null;
        if (arenaPlayers.get(0).getArenaType() == ArenaType.DIAGONAL_CLUTCH) return getPlayerSpawnDiagonal(npc,mapEntry.getName());
        switch (mapEntry.getName().toLowerCase()) {
            case "cube":
                temp = new Location(world, npc.getX() , npc.getBlockY(), npc.getZ() + 1);
                break;
            case "wood":
                temp = new Location(world, npc.getX(), npc.getBlockY(), npc.getZ() + 1);
                break;
            case "line":
                temp = new Location(world, npc.getX() , npc.getBlockY(), npc.getZ() + 3);
                break;
            case "rainbow":
                temp = new Location(world, npc.getX() , npc.getBlockY(), npc.getZ() + 1);
                break;
            case "island":
                temp = new Location(world, npc.getX(), npc.getBlockY(), npc.getZ() + 1);
                break;

            case "mushroom":
                temp = new Location(world, npc.getX(), npc.getBlockY(), npc.getZ() + 1);
                break;
        }
        temp.setYaw(180);
        return temp;
    }

    public Location getPlayerSpawnDiagonal(Location location,String name) {
        Location temp = null;
        if (arenaPlayers.get(0).getArenaType() == ArenaType.DIAGONAL_CLUTCH)
            switch (name.toLowerCase()) {
                case "cube":
                    temp = new Location(world, location.getX() - 1 , location.getBlockY(), location.getZ() + 1);
                    break;
                case "wood":
                    temp = new Location(world, location.getX() - 1, location.getBlockY(), location.getZ() + 1);
                    break;
                case "rainbow":
                    temp = new Location(world, location.getX() -1, location.getBlockY(), location.getZ() + 1);
                    break;
                case "mushroom":
                    temp = new Location(world, location.getX() -1, location.getBlockY(), location.getZ() + 1);
                    break;
            }
        temp.setYaw(-135F);
        return temp;
    }

}
