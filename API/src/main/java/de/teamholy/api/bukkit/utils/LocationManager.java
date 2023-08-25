package de.teamholy.api.bukkit.utils;

import de.teamholy.api.BukkitHolyAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/* copyright by Yassino */
@Getter @Setter
public class LocationManager {

    private HashMap<String, Location> locations = new HashMap<>();


    public void addLocation(String name, Location location) {
        locations.put(name,location);
        createConfigLocation(location,name);
    }

    public Location getLocation(String location) {
        if (getConfigLocation(location) != null) {
            locations.put(location,getConfigLocation(location));
            return locations.get(location);
        }
        return null;
    }


    public void createConfigLocation(Location loc, String path) {
        File file = BukkitHolyAPI.getInstance().getCfgFile();
        YamlConfiguration cfg = BukkitHolyAPI.getInstance().getYamlConfiguration();
        cfg.set(path + ".World", loc.getWorld().getName());
        cfg.set(path + ".X", loc.getX());
        cfg.set(path + ".Y", loc.getY());
        cfg.set(path + ".Z", loc.getZ());
        cfg.set(path + ".Yaw", loc.getYaw());
        cfg.set(path + ".Pich", loc.getPitch());
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location getConfigLocation(String path) {
        if (BukkitHolyAPI.getInstance().getYamlConfiguration().get(path) != null) {
            YamlConfiguration cfg = BukkitHolyAPI.getInstance().getYamlConfiguration();
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
