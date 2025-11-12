/*
   Coded by Yassine Svensko | Yassino using IntelliJ
   (c) 2020, All rights reserved
*/

package de.teamholy.mlgrush.utils;

import de.teamholy.mlgrush.MLGRush;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for managing locations in configuration files.
 */
public class LocationManager {
    
    /**
     * Saves a location to the configuration file.
     *
     * @param loc  the location to save
     * @param path the configuration path
     * @param file the configuration file
     * @param cfg  the YAML configuration
     */
    public void createConfigLocation(Location loc, String path, File file, YamlConfiguration cfg) {
        cfg.set(path + ".World", loc.getWorld().getName());
        cfg.set(path + ".X", loc.getX());
        cfg.set(path + ".Y", loc.getY());
        cfg.set(path + ".Z", loc.getZ());
        cfg.set(path + ".Yaw", loc.getYaw());
        cfg.set(path + ".Pitch", loc.getPitch());
        try {
            cfg.save(file);
        } catch (IOException e) {
            MLGRush.getInstance().getLogger().severe("Failed to save location: " + e.getMessage());
        }
    }

    /**
     * Retrieves a location from the configuration file.
     *
     * @param path the configuration path
     * @param cfg  the YAML configuration
     * @return the location, or null if not found
     */
    public Location getConfigLocation(String path, YamlConfiguration cfg) {
        if (MLGRush.getInstance().getYamlConfiguration().get(path) != null) {
            World w = Bukkit.getWorld(cfg.getString(path + ".World"));
            double x = cfg.getDouble(path + ".X");
            double y = cfg.getDouble(path + ".Y");
            double z = cfg.getDouble(path + ".Z");
            float yaw = (float) cfg.getDouble(path + ".Yaw");
            float pitch = (float) cfg.getDouble(path + ".Pitch");

            return new Location(w, x, y, z, yaw, pitch);
        }
        return null;
    }
}
