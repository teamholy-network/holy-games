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
import de.skydb.updater.BukkitUpdaterAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

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
        new BukkitUpdaterAPI(this, "9dffacd9-2bc3-4601-ba95-672bd682d21c", "")
                .setHibernat(true)
                .setOnlyempty(true)
                .setOnlyrestart(true);
        cacheHandler = new CacheHandler();
        itemHandler = new ItemHandler();
        teamingHandler = new TeamingHandler(this);
        registerListener("de.teamholy.sgffa.listeners");
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
                final Object obj = Class.forName(info.getName(), true, classLoader).getDeclaredConstructor()
                        .newInstance();
                if (obj instanceof Listener) {
                    this.getServer().getPluginManager().registerEvents((Listener) obj, this);
                    this.getLogger().info("Registered " + obj.getClass().getName());
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void registerMapsAndConfig() {
        file = new File("plugins/SGFFA/locations.yml");
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        List<String> mapStrings = yamlConfiguration.getStringList("Maps");
        for (String map : mapStrings) {
            int death = getYamlConfiguration().contains(map + ".high.Y")
                    ? getYamlConfiguration().getInt(map + ".high.Y")
                    : -1000;
            List<Location> locationList = Optional.ofNullable(getYamlConfiguration().getList(map + ".spawns"))
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(Location.class::isInstance)
                    .map(Location.class::cast)
                    .collect(Collectors.toList());
            getCacheHandler().getMapEntryHashMap().put(map, new MapEntry(map, locationList, death));
        }
        if (mapStrings.size() != 0)
            activeMapEntry = getCacheHandler().getMapEntryHashMap().get(mapStrings.get(new Random().nextInt(mapStrings.size())));
    }

}
