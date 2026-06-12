package de.teamholy.clutches.playground.model;


import com.google.common.collect.Lists;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldAlreadyExistsException;
import com.grinderwolf.swm.api.world.SlimeWorld;
import de.teamholy.clutches.Clutches;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Getter
public class PrivateWorld {

    public static List<PrivateWorld> LOADED_WORLDS = Lists.newArrayList();

    private final UUID uuid = UUID.randomUUID();
    private SlimeWorld slimeWorld;
    private final PlaygroundWorld playgroundWorld;
    private final List<UUID> allowedPlayers = Lists.newArrayList();

    public PrivateWorld(PlaygroundWorld playgroundWorld) {
        this.playgroundWorld = playgroundWorld;
    }

    public void unloadWorld() {
        // Zuerst austragen: Schlug deleteWorld fehl, blieb die Welt vorher dauerhaft in LOADED_WORLDS (Memory-Leak)
        LOADED_WORLDS.remove(this);
        try {
            Clutches.getInstance().getSlimeLoader().deleteWorld(slimeWorld.getName());
        } catch (UnknownWorldException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadWorld(Player player) {
        try {
            slimeWorld = playgroundWorld.getSlimeWorld().clone("PRIVATE-" + uuid + "-" + playgroundWorld.getName(), Clutches.getInstance().getSlimePlugin().getLoader("file"));
            Clutches.getInstance().getSlimePlugin().generateWorld(slimeWorld);
            LOADED_WORLDS.add(this);


            while (Bukkit.getWorld("PRIVATE-" + uuid + "-" + playgroundWorld.getName()) != null) {
                teleportPlayerToRandomSpawn(player);
                break;
            }

        } catch (WorldAlreadyExistsException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void teleportPlayerToRandomSpawn(Player player) {
        List<Location> spawns = playgroundWorld.getSpawns();
        if (!spawns.isEmpty()) {
            Location randomSpawn = spawns.get(new Random().nextInt(spawns.size()));
            player.teleport(new Location(Bukkit.getWorld("PRIVATE-" + uuid + "-" + playgroundWorld.getName()), randomSpawn.getX(), randomSpawn.getY(), randomSpawn.getZ()));
        }
    }
}
