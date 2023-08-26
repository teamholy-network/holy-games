package de.teamholy.api;

import de.teamholy.api.bukkit.commands.*;
import de.teamholy.api.bukkit.npc.tasks.UpdateLookTask;
import de.teamholy.api.cloud.BukkitCloudUtil;
import de.teamholy.api.bukkit.config.ChatTabConfig;
import de.teamholy.api.bukkit.listeners.TabCompleteListener;
import de.teamholy.api.bukkit.npc.listeners.PlayerJoinQuitListener;
import de.teamholy.api.bukkit.npc.listeners.PlayerMoveListener;
import de.teamholy.api.bukkit.npc.listeners.ProtocolLibListener;
import de.teamholy.api.bukkit.utils.LocationManager;
import de.teamholy.api.cache.BukkitCacheHandler;
import de.teamholy.api.manager.StatsManager;
import de.teamholy.api.repositories.NPCSkinRepository;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.reflect.ClassPath;
import de.teamholy.core.bukkit.BukkitCore;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.management.ManagementFactory;

/* copyright by Yassino */
@Getter
@Setter
public class BukkitHolyAPI extends JavaPlugin {
    @Getter
    private static BukkitHolyAPI instance;
    private String prefix = "§6Teamholy §8× §7";
    private int maxMoveDistance = 20;

    private BukkitCacheHandler bukkitCacheHandler;
    private BukkitCloudUtil bukkitCloudUtil;

    private LocationManager locationManager;
    private StatsManager statsManager;

    private NPCSkinRepository npcSkinRepository;

    private File cfgFile = new File("plugins//API//locations.yml");
    private YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(cfgFile);

    private ChatTabConfig chatTabConfig;
    private ProtocolManager protocolManager;

    private boolean chatPrefix;
    private boolean tabPrefix;

    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;
        registerListener("de.teamholy.api.bukkit.listeners");
        bukkitCacheHandler = new BukkitCacheHandler();
        locationManager = new LocationManager();
        bukkitCloudUtil = new BukkitCloudUtil(this);
        chatTabConfig = new ChatTabConfig();
        statsManager = new StatsManager();
        npcSkinRepository = BukkitCore.getAPI().getMongoManager().create(NPCSkinRepository.class);
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new TabCompleteListener(this, PacketType.Play.Client.TAB_COMPLETE));
        registerNpcAPI();
        getCommand("location").setExecutor(new LocationCommand());
        getCommand("chatclear").setExecutor(new ChatclearCommand());
        getCommand("gc").setExecutor(new GcCommand());
        getCommand("tpblock").setExecutor(new TpBlockCommand());
        getCommand("sudo").setExecutor(new SudoCommand());
        getCommand("bcommand").setExecutor(new BungeeCommandCommand());
        getCommand("gamemode").setExecutor(new GamemodeCommand());

        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (World world : Bukkit.getWorlds()) {
                world.setMonsterSpawnLimit(0);
                world.setTicksPerMonsterSpawns(8888888);
                world.setTime(1000);
                world.setDifficulty(Difficulty.EASY);
                world.setGameRuleValue("doDaylightCycle", "false");
                world.setGameRuleValue("doMobSpawning", "false");
                for (Entity ent : Bukkit.getWorld(world.getName()).getEntities()) {
                    if (ent instanceof Animals)
                        ent.remove();
                    if (ent instanceof Monster)
                        ent.remove();
                }
            }
        }, 200);


        String fallback = new File("/proc/self").getCanonicalFile().getName();
        String pid = getProcessId(fallback);
        System.out.println("PID: " + pid);
        npcSkinRepository.asyncFindAll().thenAccept(skinEntries -> skinEntries.forEach(skinEntry -> {
            getBukkitCacheHandler().getSkinEntryHashMap().put(skinEntry.getUuid(),skinEntry);
        }));
    }


    private void registerNpcAPI() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);
        protocolManager.addPacketListener(new ProtocolLibListener());
        new UpdateLookTask();
    }

    private void registerListener(final String path) {
        try {
            final ClassLoader classLoader = this.getClass().getClassLoader();
            for (final ClassPath.ClassInfo info : ClassPath.from(classLoader).getTopLevelClasses(path)) {
                final Object obj = Class.forName(info.getName(), true, classLoader).newInstance();
                if (obj instanceof Listener) {
                    this.getServer().getPluginManager().registerEvents((Listener) obj, this);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static String getProcessId(final String fallback) {
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf('@');

        if (index < 1) {
            return fallback;
        }

        try {
            return Long.toString(Long.parseLong(jvmName.substring(0, index)));
        } catch (NumberFormatException e) {
        }
        return fallback;
    }
}
