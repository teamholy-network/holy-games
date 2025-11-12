package de.teamholy.knockbackffa;

import com.google.common.reflect.ClassPath;
import de.skydb.updater.BukkitUpdaterAPI;
import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.EffectManager;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import de.teamholy.core.bukkit.utils.TopHolo;
import de.teamholy.knockbackffa.commands.QuitCommand;
import de.teamholy.knockbackffa.commands.SetupCommand;
import de.teamholy.knockbackffa.commands.TeamingCommand;
import de.teamholy.knockbackffa.commands.VanishCommand;
import de.teamholy.knockbackffa.enums.PlayerState;
import de.teamholy.knockbackffa.handlers.CacheHandler;
import de.teamholy.knockbackffa.handlers.PerkInventoriesHandler;
import de.teamholy.knockbackffa.handlers.TeamingHandler;
import de.teamholy.knockbackffa.managers.ActiveEnderPearlManager;
import de.teamholy.knockbackffa.models.MapEntry;
import de.teamholy.knockbackffa.models.PlayerEntry;
import de.teamholy.knockbackffa.tasks.ArmorColorRainbowTask;
import de.teamholy.knockbackffa.utils.PlayerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

/**
 * Main plugin class for Knockback FFA game mode.
 * Copyright by Yassino
 */
@Getter
@Setter
public class KnockbackFFA extends JavaPlugin {

    private static KnockbackFFA instance;
    private String prefix = "§eKnockbackFFA §8× §7";
    private CacheHandler cacheHandler;
    private PlayerUtils playerUtils;
    private final File configFile = new File("plugins/KnockbackFFA/locations.yml");
    private YamlConfiguration yamlConfiguration;
    private EffectManager effectManager;
    private PerkInventoriesHandler perkInventoriesHandler;
    private TeamingHandler teamingHandler;

    @Override
    public void onEnable() {
        initializePlugin();
        initializeConfiguration();
        registerCommands();
        registerListener("de.teamholy.knockbackffa.listeners");
        registerMaps();
        startTasks();
    }

    /**
     * Initialize plugin instance and core services.
     */
    private void initializePlugin() {
        instance = this;
        new BukkitUpdaterAPI(this, "37153192-6ff5-46f5-9899-8d33a67f3797", "")
                .setHibernat(true)
                .setOnlyempty(true)
                .setOnlyrestart(true);
    }

    /**
     * Initialize configuration and managers.
     */
    private void initializeConfiguration() {
        yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);
        effectManager = new EffectManager(EffectLib.instance());
        cacheHandler = new CacheHandler();
        playerUtils = new PlayerUtils();
        teamingHandler = new TeamingHandler(this);
        perkInventoriesHandler = new PerkInventoriesHandler();
    }

    /**
     * Register commands with their executors.
     */
    private void registerCommands() {
        getCommand("setup").setExecutor(new SetupCommand());
        getCommand("quit").setExecutor(new QuitCommand());
        getCommand("vanish").setExecutor(new VanishCommand());
        getCommand("teaming").setExecutor(new TeamingCommand());
    }

    /**
     * Start scheduled tasks and holograms.
     */
    private void startTasks() {
        startMoveListener();
        new TopHolo(BukkitCore.getInstance().getLocationManager().getLocation("topHolo"),
                Gamemodes.KNOCKBACKFFA,
                new ItemBuilder(Material.SANDSTONE).build());
        new ArmorColorRainbowTask(this);
    }

    /**
     * Starts the move listener task that monitors player positions.
     */
    private void startMoveListener() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerUtils.sendActionBar(player, prefix + "§f§lmax 3 players per team (/teaming)");
            PlayerEntry playerEntry = getCacheHandler().getPlayerEntrys().get(player.getUniqueId());
            if (playerEntry != null) {
                if (playerEntry.getPlayerState() == PlayerState.INGAME) {
                    if (player.getLocation().getBlockY() < playerEntry.getActiveMap().getSpawnHeight() && player.getInventory().contains(Material.MAGMA_CREAM) && player.getGameMode() == GameMode.SURVIVAL) {
                        player.closeInventory();
                        playerEntry.setIngameItems();
                    } else if (player.getLocation().getBlockY() < playerEntry.getActiveMap().getDeathHeight() && player.getGameMode() == GameMode.SURVIVAL && !player.getInventory().contains(Material.MAGMA_CREAM) && player.getHealth() > 0.00D && !player.isDead() && !ActiveEnderPearlManager.hasActive(player.getUniqueId())) {
                        player.damage(1234);
                    }
                } else if (playerEntry.getPlayerState() == PlayerState.LOBBY && player.getGameMode() == GameMode.SURVIVAL) {
                    if (player.getLocation().getBlockY() < 10) {
                        player.teleport(BukkitCore.getInstance().getLocationManager().getLocation("lobby"));
                    }
                }
            }
        }), 0, 10);
    }

    /**
     * Registers all maps from configuration.
     */
    private void registerMaps() {
        for (String map : SetupCommand.MAPS) {
            Sign sign;
            try {
                sign = (Sign) ((Location) yamlConfiguration.get(map + ".sign")).getBlock().getState();
            } catch (Exception e) {
                sign = null;
                getLogger().log(Level.WARNING, "Map " + map + " doesn't have a sign! Use /setup");
            }
            cacheHandler.getMapEntrys().put(map,
                    new MapEntry(map,
                            (Location) yamlConfiguration.get(map + ".spawn"),
                            yamlConfiguration.getDouble(map + ".high.Y"),
                            yamlConfiguration.getDouble(map + ".death.Y"),
                            sign
                    ));
        }
    }

    /**
     * Registers event listeners from the specified package.
     *
     * @param path the package path containing listeners
     */
    private void registerListener(final String path) {
        try {
            final ClassLoader classLoader = this.getClass().getClassLoader();
            for (final ClassPath.ClassInfo info : ClassPath.from(classLoader).getTopLevelClasses(path)) {
                final Object obj = Class.forName(info.getName(), true, classLoader).getDeclaredConstructor().newInstance();
                if (obj instanceof Listener) {
                    this.getServer().getPluginManager().registerEvents((Listener) obj, this);
                    this.getLogger().info("Registered " + obj.getClass().getName());
                }
            }
        } catch (Exception e) {
            getLogger().warning("Failed to register listener: " + e.getMessage());
        }
    }

    /**
     * Gets the plugin instance.
     *
     * @return the KnockbackFFA plugin instance
     */
    public static KnockbackFFA getInstance() {
        return instance;
    }
}
