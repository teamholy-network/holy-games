package de.teamholy.sgffa;


import de.teamholy.sgffa.commands.SetupCommand;
import de.teamholy.sgffa.commands.TeamingCommand;
import de.teamholy.sgffa.commands.VanishCommand;
import de.teamholy.sgffa.handlers.CacheHandler;
import de.teamholy.sgffa.handlers.ItemHandler;
import de.teamholy.sgffa.handlers.TeamingHandler;
import de.teamholy.sgffa.models.MapEntry;
import de.teamholy.sgffa.tasks.MapChangeTask;
import com.google.common.reflect.ClassPath;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* copyright by Yassino */
@Getter
public class SGFFA extends JavaPlugin {

    @Getter
    private static SGFFA instance;
    public static String PREFIX = "§aSGFFA §8× §7";
    @Setter
    private MapEntry activeMapEntry;

    private CacheHandler cacheHandler;
    private ItemHandler itemHandler;

    private TeamingHandler teamingHandler;

    private File file;
    private YamlConfiguration yamlConfiguration;

    private final ArrayList<Location> clickedChests = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        cacheHandler = new CacheHandler();
        itemHandler = new ItemHandler();
        teamingHandler = new TeamingHandler(this);
        registerListener("codes.yassino.sgffa.listeners");
        registerMapsAndConfig();

        getCommand("setup").setExecutor(new SetupCommand());
        getCommand("vanish").setExecutor(new VanishCommand());
        getCommand("teaming").setExecutor(new TeamingCommand());

        new MapChangeTask();
    }

    private void registerListener(final String path) {
        try {
            final ClassLoader classLoader = this.getClass().getClassLoader();
            for (final ClassPath.ClassInfo info : ClassPath.from(classLoader).getTopLevelClasses(path)) {
                final Object obj = Class.forName(info.getName(), true, classLoader).newInstance();
                if (obj instanceof Listener) {
                    this.getServer().getPluginManager().registerEvents((Listener)obj, this);
                    this.getLogger().info("Registered " + obj.getClass().getName());
                }
            }
        }
        catch (Exception ignored) {}
    }

    private void registerMapsAndConfig() {
        file = new File("plugins/SGFFA/locations.yml");
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        List<String> mapStrings = yamlConfiguration.getStringList("Maps");
        for (String map : mapStrings) {
            int death = -1000;
            if (getYamlConfiguration().contains( map+ ".high.Y"))  death = getYamlConfiguration().getInt( map+ ".high.Y");
            getCacheHandler().getMapEntryHashMap().put(map, new MapEntry(map, new ArrayList(getYamlConfiguration().getList(map + ".spawns")),death));
        }
        if (mapStrings.size() != 0)
        activeMapEntry = getCacheHandler().getMapEntryHashMap().get(mapStrings.get(new Random().nextInt(mapStrings.size())));
    }

}
