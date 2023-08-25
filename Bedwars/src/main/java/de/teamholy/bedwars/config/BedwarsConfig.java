package de.teamholy.bedwars.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/* copyright by Yassino */
@Getter
public class BedwarsConfig {


    private File file = new File("plugins/Bedwars/Config.yml");
    public FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

    public BedwarsConfig() {
        configuration.options().copyDefaults(true);
        getConfiguration().options().header("2x1, 4x2, 8x1 <- Bedwars varianten");
        configuration.addDefault("GameVariant","2x1");
        configuration.addDefault("PlayersToStart",2);
        configuration.addDefault("RushMode",false);
        try {
            configuration.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


}
