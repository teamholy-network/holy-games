package de.teamholy.clutches;


import de.teamholy.clutches.arena.ArenaType;
import de.teamholy.clutches.commands.SpawnCMD;
import de.teamholy.clutches.commands.VanishCommand;
import de.teamholy.clutches.map.MapEntry;
import de.teamholy.clutches.map.MapEntryHandler;
import de.teamholy.clutches.player.PlayerEntry;
import de.teamholy.clutches.player.PlayerEntryHandler;
import de.teamholy.clutches.player.PlayerState;
import de.teamholy.clutches.playground.commands.PlaygroundCommand;
import de.teamholy.clutches.playground.PlaygroundManager;
import de.teamholy.clutches.playground.commands.PresentedPresetCommand;
import de.teamholy.clutches.task.ClutchTask;
import de.teamholy.clutches.utils.PlayerUtils;
import com.google.common.reflect.ClassPath;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import de.teamholy.clutches.commands.QuitCommand;
import de.teamholy.clutches.npcskin.NPCSkin;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.npc.models.SkinEntry;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

@Getter
@Setter
public class Clutches extends JavaPlugin {

    public static final String PREFIX = "§bClutches §8× §7";
    @Getter
    private static Clutches instance;
    private PlayerEntryHandler playerEntryHandler = new PlayerEntryHandler();
    private PlayerUtils playerUtils;
    private MapEntryHandler mapEntryHandler = new MapEntryHandler();
    private HologramManager hologramManager;
    private PlaygroundManager playgroundManager;

    private SlimePlugin slimePlugin;
    private SlimeLoader slimeLoader;


    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;

        slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        slimeLoader = slimePlugin.getLoader("mongodb");


        mapEntryHandler.put("line", new MapEntry("Line",  new ItemBuilder(Material.WOOD, 1, (byte) 0).setName("§8» §6Line"),
            List.of(ArenaType.DIAGONAL_CLUTCH)));
        mapEntryHandler.put("wood", new MapEntry("Wood", new ItemBuilder(Material.LOG, 1, (byte) 0).setName("§8» §6Wood"), null));
        mapEntryHandler.put("rainbow", new MapEntry("Rainbow",  new ItemBuilder(Material.GLASS, 1, (byte) 1).setName("§8» §6Rainbow"), null));
        mapEntryHandler.put("island", new MapEntry("Island", new ItemBuilder(Material.GRASS, 1, (byte) 0).setName("§8» §6Island"),Arrays.asList(ArenaType.EXPERIMENTAL,ArenaType.DIAGONAL_CLUTCH)));
        mapEntryHandler.put("cube", new MapEntry("Cube", new ItemBuilder(Material.STONE, 1, (byte) 0).setName("§8» §6Cube"), null));
        mapEntryHandler.put("mushroom", new MapEntry("Mushroom", new ItemBuilder(Material.BROWN_MUSHROOM, 1, (byte) 0).setName("§8» §6Mushroom"), null));
        mapEntryHandler.put("diagonal", new MapEntry("Diagonal", new ItemBuilder(Material.STICK, 1, (byte) 0).setName("§8» §6Diagonal §8(§fQuadratHose§8)"), null));
        registerListener("de.teamholy.clutches.listeners");
        getCommand("spawn").setExecutor(new SpawnCMD());
        getCommand("quit").setExecutor(new QuitCommand());
        getCommand("playworld").setExecutor(new PlaygroundCommand());
        getCommand("playgroundpreset").setExecutor(new PresentedPresetCommand());
        getCommand("vanish").setExecutor(new VanishCommand());
        hologramManager = new HologramManager();
        playgroundManager = new PlaygroundManager(this);
        new ClutchTask();

        for (NPCSkin value : NPCSkin.values()) {
            SkinEntry temp = new SkinEntry();
            temp.setUuid(value.getUuid());
            temp.fetch(skinEntry -> {
                BukkitCore.getInstance().getNpcService().getSkinEntryHashMap().put(value.getUuid(),skinEntry);
                System.out.println(value.getName() + " wurde gecached");
            });
        }



        for (World world : Bukkit.getWorlds()) {
            world.setMonsterSpawnLimit(0);
            world.setTicksPerMonsterSpawns(8888888);
            world.setTime(0);
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            for (Entity ent : world.getEntities()) {
                if (ent instanceof Animals || ent instanceof Monster) {
                    ent.remove();
                }
            }
        }
        startMoveListener();
    }



    private void registerListener(final String path) {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            for (final ClassPath.ClassInfo info : ClassPath.from(classLoader).getTopLevelClasses(path)) {
                final Object obj = Class.forName(info.getName(), true, classLoader).newInstance();
                if (obj instanceof Listener) {
                    this.getServer().getPluginManager().registerEvents((Listener) obj, this);
                }
            }
        } catch (IOException | InstantiationException | IllegalAccessException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }


    private void startMoveListener() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (PlayerEntry playerEntry : getPlayerEntryHandler().values()) {
                Player player = playerEntry.getPlayer();
                if (playerEntry.getPlayerState() == PlayerState.LOBBY && playerEntry.getPlayer().getLocation().getBlockY() < 30) {
                    player.teleport(BukkitCore.getInstance().getLocationManager().getLocation("lobby"));
                } else if (playerEntry.getPlayerState() == PlayerState.INGAME && player.getLocation().distance(playerEntry.getArenaEntry().getNpc()) > 400) {
                    player.teleport(playerEntry.getArenaEntry().getPlayerSpawn());
                    player.playSound(player.getLocation(),Sound.ENDERMAN_TELEPORT,5,5);
                    player.sendMessage(PREFIX + "You got out of map!");
                } else if (playerEntry.getPlayerState() == PlayerState.PLAYGROUND && player.getLocation().getBlockY() < playerEntry.getPlaygroundPlayer().getPlaygroundWorld().getDeathHeight()) {
                    player.setVelocity(new Vector(0,0,0));
                    if (playerEntry.getPlaygroundPlayer().getPrivateWorld().isPresent()) {
                        playerEntry.getPlaygroundPlayer().getPrivateWorld().get().teleportPlayerToRandomSpawn(player);
                    } else {
                        player.teleport(playerEntry.getPlaygroundPlayer().getPlaygroundWorld().getSpawns().get(new Random().nextInt(playerEntry.getPlaygroundPlayer().getPlaygroundWorld().getSpawns().size())));
                    }
                    player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 50, 1);
                    playerEntry.getPlaygroundPlayer().getPlayerTask().stopIfActive();
                }
            }
        }, 10, 5);
    }

}
