package de.teamholy.lobby;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class HalloweenEffectsRunnable extends BukkitRunnable {

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            world.setTime(18000);
            world.setStorm(true);

            if (Math.random() < 0.7) {
                world.setThundering(true);
                world.setThunderDuration(100);
            } else {
                world.setThundering(false);
            }
        }
    }
}
