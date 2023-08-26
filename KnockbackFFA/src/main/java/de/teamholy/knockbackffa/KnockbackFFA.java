package de.teamholy.knockbackffa;

import de.teamholy.knockbackffa.commands.QuitCommand;
import de.teamholy.knockbackffa.commands.SetupCommand;
import de.teamholy.knockbackffa.commands.TeamingCommand;
import de.teamholy.knockbackffa.commands.VanishCommand;
import de.teamholy.knockbackffa.enums.PlayerState;
import de.teamholy.knockbackffa.handlers.CacheHandler;
import de.teamholy.knockbackffa.handlers.PerkInventoriesHandler;
import de.teamholy.knockbackffa.handlers.TeamingHandler;
import de.teamholy.knockbackffa.models.MapEntry;
import de.teamholy.knockbackffa.models.PlayerEntry;
import de.teamholy.knockbackffa.utils.PlayerUtils;
import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.knockbackffa.tasks.ArmorColorRainbowTask;
import com.google.common.reflect.ClassPath;
import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.EffectManager;
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

/* copyright by Yassino */
@Getter @Setter
public class KnockbackFFA extends JavaPlugin {

    @Getter
    private static KnockbackFFA instance;
    private String prefix = "§eKnockbackFFA §8× §7";
    private CacheHandler cacheHandler;
    private PlayerUtils playerUtils;
    private File cfgfFile = new File("plugins//KnockbackFFA//locations.yml");
    private YamlConfiguration yamlConfiguration;
    private EffectManager effectManager;
    private PerkInventoriesHandler perkInventoriesHandler;
    private TeamingHandler teamingHandler;

    @Override
    public void onEnable() {
        instance = this;
        yamlConfiguration = YamlConfiguration.loadConfiguration(cfgfFile);
        effectManager = new EffectManager(EffectLib.instance());
        cacheHandler = new CacheHandler();
        playerUtils = new PlayerUtils();
        teamingHandler = new TeamingHandler(this);
        perkInventoriesHandler = new PerkInventoriesHandler();
        getCommand("setup").setExecutor(new SetupCommand());
        getCommand("quit").setExecutor(new QuitCommand());
        getCommand("vanish").setExecutor(new VanishCommand());
        getCommand("teaming").setExecutor(new TeamingCommand());
        registerListener("de.teamholy.knockbackffa.listeners");
        registerMaps();
        startMoveListener();
        new ArmorColorRainbowTask(this);
    }

    private void startMoveListener() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerUtils.sendActionBar(player,prefix + "§f§lmax 3 player per team (/teaming)");
            PlayerEntry playerEntry = getCacheHandler().getPlayerEntrys().get(player.getUniqueId());
            if (playerEntry != null) {
                if (playerEntry.getPlayerState() == PlayerState.INGAME) {
                    if (player.getLocation().getBlockY() < playerEntry.getActiveMap().getSpawnHight() && player.getInventory().contains(Material.MAGMA_CREAM))  {
                        player.closeInventory();
                        playerEntry.setIngameItems();
                    } else if (player.getLocation().getBlockY() < playerEntry.getActiveMap().getDeathHight() && player.getGameMode() == GameMode.SURVIVAL && !player.getInventory().contains(Material.MAGMA_CREAM) && player.getHealth() > 0.00D && !player.isDead()) {
                        player.damage(1234);
                    }
                } else if (playerEntry.getPlayerState() == PlayerState.LOBBY) {
                    if (player.getLocation().getBlockY() < 10) {
                        player.teleport(BukkitHolyAPI.getInstance().getLocationManager().getLocation("lobby"));
                    }
                }
            }
        }),0,10);
    }

    private void registerMaps() {
        for (String map : SetupCommand.MAPS) {
            Sign sign;
            try {
                sign = (Sign) ((Location) yamlConfiguration.get(map + ".sign")).getBlock().getState();

            } catch (Exception e) {
                sign = null;

                getLogger().log(Level.WARNING,"Map " + map + " dont have a sign! /setup");
            }
            cacheHandler.getMapEntrys().put(map,
                    new MapEntry(map
                    ,(Location)yamlConfiguration.get(map + ".spawn")
                            ,yamlConfiguration.getDouble(map + ".high.Y"),
                            yamlConfiguration.getDouble(map + ".death.Y"),
                            sign
                    ));
        }
    }

    private void registerListener(final String path) {
        try {
            final ClassLoader classLoader = this.getClass().getClassLoader();
            for (final ClassPath.ClassInfo info : ClassPath.from(classLoader).getTopLevelClasses(path)) {
                final Object obj = Class.forName(info.getName(), true, classLoader).newInstance();
                if (obj instanceof Listener) {
                    this.getServer().getPluginManager().registerEvents((Listener) obj, this);
                    this.getLogger().info("Registered " + obj.getClass().getName());
                }
            }
        } catch (Exception ignored) {
        }
    }

}
