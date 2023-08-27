package de.teamholy.clutches.playground;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.playground.model.HitPreset;
import de.teamholy.clutches.playground.model.PlaygroundWorld;
import de.teamholy.clutches.playground.task.ArmorColorRainbowTask;
import de.teamholy.clutches.playground.task.CountdownItemTask;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* copyright by Yassino */
@Getter
public class PlaygroundManager {

    private final List<PlaygroundWorld> playgroundWorlds = new ArrayList<>();
    private final List<HitPreset> presentedHits = new ArrayList<>();
    private final File cfgfFile = new File(Clutches.getInstance().getDataFolder(),"playgroundWorlds.yml");
    private final YamlConfiguration yamlConfiguration;

    private final ArmorColorRainbowTask armorColorRainbowTask;

    public PlaygroundManager(Clutches instance) {
        yamlConfiguration = YamlConfiguration.loadConfiguration(cfgfFile);
        armorColorRainbowTask = new ArmorColorRainbowTask(instance);
        instance.getServer().getPluginManager().registerEvents(new PlaygroundListener(),instance);
        instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance,new CountdownItemTask(),10,10);
        System.out.println(yamlConfiguration.getStringList("maps"));
        if (yamlConfiguration.contains("maps")) {

            yamlConfiguration.getStringList("maps").forEach(map -> {
                PlaygroundWorld playgroundWorld = new PlaygroundWorld();
                playgroundWorld.setName(map);
                playgroundWorld.setMaterialAndSubId(yamlConfiguration.getString(map + ".materialAndSubId"));
                playgroundWorld.setDeathHeight(yamlConfiguration.getInt(map + ".deathHeight"));
                playgroundWorld.setSpawns(new ArrayList(yamlConfiguration.getList(map + ".spawns")));
                playgroundWorlds.add(playgroundWorld);
            });
        }
    }

}
