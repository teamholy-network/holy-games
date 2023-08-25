package de.teamholy.api.bukkit.config;

import de.teamholy.api.BukkitHolyAPI;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class ChatTabConfig {

    private File file = new File("plugins/API/config.yml");
    public FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

    public ChatTabConfig() throws IOException {
        configuration.options().copyDefaults(true);
        getConfiguration().options().header("Deaktivere Chat & tab prefix.");
        configuration.addDefault("ChatPrefix",true);
        configuration.addDefault("TabPrefix",true);
        BukkitHolyAPI.getInstance().setChatPrefix(configuration.getBoolean("ChatPrefix"));
        BukkitHolyAPI.getInstance().setTabPrefix(configuration.getBoolean("TabPrefix"));
        configuration.save(file);
    }

}
