package de.teamholy.mlgrush;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.mlgrush.commands.SpawnCMD;
import de.teamholy.mlgrush.enums.GameType;
import de.teamholy.mlgrush.game.GameEntry;
import de.teamholy.mlgrush.game.GameEntryHandler;
import de.teamholy.mlgrush.game.GameState;
import de.teamholy.mlgrush.manager.InventoryManager;
import de.teamholy.mlgrush.manager.RegionManager;
import de.teamholy.mlgrush.manager.holograms.QueueHandler;
import de.teamholy.mlgrush.map.MapEntry;
import de.teamholy.mlgrush.map.MapEntryHandler;
import de.teamholy.mlgrush.maptemplate.MapTemplateEntry;
import de.teamholy.mlgrush.maptemplate.MapTemplateEntryHandler;
import de.teamholy.mlgrush.player.PlayerEntry;
import de.teamholy.mlgrush.player.PlayerEntryHandler;
import de.teamholy.mlgrush.player.PlayerState;
import de.teamholy.mlgrush.utils.LocationManager;
import de.teamholy.mlgrush.utils.PlayerUtils;
import com.google.common.reflect.ClassPath;
import de.teamholy.mlgrush.commands.QuitCMD;
import de.teamholy.mlgrush.commands.SetupCMD;
import de.teamholy.mlgrush.commands.SpectateCommand;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Getter @Setter
public class MLGRush extends JavaPlugin {

    @Getter
    public static MLGRush instance;
    private MapEntryHandler mapEntryHandler;
    private MapTemplateEntryHandler mapTemplateEntryHandler;
    private LocationManager locationManager;
    private PlayerEntryHandler playerEntryHandler;
    private GameEntryHandler gameEntryHandler;
    private PlayerUtils playerUtils;
    private InventoryManager inventoryManager;
    private Location lobby;
    private QueueHandler queueHandler;


    private String prefix = "§6MLGRush §8× §7";
    private ArrayList<String> templates = new ArrayList<>();
    private ArrayList<String> maps = new ArrayList<>();

    private File cfgfFile = new File("plugins//MLGRush//ChefConfig.yml");
    private YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(cfgfFile);

    @Override
    public void onEnable() {
        registerClasses();
        registerTemplates();
        getCommand("setup").setExecutor(new SetupCMD());
        getCommand("quit").setExecutor(new QuitCMD());
        getCommand("spawn").setExecutor(new SpawnCMD());
        getCommand("spectate").setExecutor(new SpectateCommand());

        registerListener("de.teamholy.mlgrush.listener");
        startIngameCounter();
        startIngameKiller();


        lobby = BukkitHolyAPI.getInstance().getLocationManager().getConfigLocation("lobby");
        for (World world : Bukkit.getWorlds()) {
            world.setMonsterSpawnLimit(0);
            world.setTicksPerMonsterSpawns(8888888);
            world.setTime(0);
            world.setGameRuleValue("doDaylightCycle","false");
            world.setGameRuleValue("doMobSpawning","false");
            for (Entity ent : Bukkit.getWorld(world.getName()).getEntities()) {
                if (ent instanceof Animals)
                    ent.remove();
                if (ent instanceof Monster)
                    ent.remove();
            }
        }
    }




    private void registerClasses() {
        instance = this;
        mapEntryHandler = new MapEntryHandler();
        mapTemplateEntryHandler = new MapTemplateEntryHandler();
        locationManager = new LocationManager();
        playerEntryHandler = new PlayerEntryHandler();
        gameEntryHandler = new GameEntryHandler();
        playerUtils = new PlayerUtils();
        inventoryManager = new InventoryManager();
        queueHandler = new QueueHandler(this);
    }

    private void registerTemplates() {
        templates = (ArrayList) yamlConfiguration.getStringList("Templates");
        templates.forEach(templates -> getMapTemplateEntryHandler().put(templates,new MapTemplateEntry(templates, Material.valueOf(yamlConfiguration.getString(templates + ".material")))));
        maps = (ArrayList) yamlConfiguration.getStringList("Maps");
        maps.forEach(map -> {
            getMapEntryHandler().put(map,new MapEntry(map));
            String[] mapSplit = map.split("-");
            MapEntry mapEntry = getMapEntryHandler().get(map);
            mapEntry.setDeathhight(yamlConfiguration.getDouble(map + ".deathhight"));
            mapEntry.setMapTemplate(getMapTemplateEntryHandler().get(mapSplit[0]));
            mapEntry.setBed1(getLocationManager().getConfigLocation(map+ ".bed1",yamlConfiguration));
            mapEntry.setBed2(getLocationManager().getConfigLocation(map+ ".bed2",yamlConfiguration));
            mapEntry.setSpawn1(getLocationManager().getConfigLocation(map+ ".spawn1",yamlConfiguration));
            mapEntry.setSpawn2(getLocationManager().getConfigLocation(map+ ".spawn2",yamlConfiguration));
            mapEntry.setRegion1(getLocationManager().getConfigLocation(map+ ".region1",yamlConfiguration));
            mapEntry.setRegion2(getLocationManager().getConfigLocation(map+ ".region2",yamlConfiguration));
            mapEntry.setRegionManager(new RegionManager(mapEntry.getRegion1(),mapEntry.getRegion2()));
            mapEntry.setGameType(GameType.TWOxONE);
            Location bed3 = getLocationManager().getConfigLocation(map+ ".bed3",yamlConfiguration);
            if (bed3 != null) {
                mapEntry.setGameType(GameType.FOURxONE);
                mapEntry.setBed3(bed3);
                mapEntry.setBed4(getLocationManager().getConfigLocation(map+ ".bed4",yamlConfiguration));
                mapEntry.setSpawn3(getLocationManager().getConfigLocation(map+ ".spawn3",yamlConfiguration));
                mapEntry.setSpawn4(getLocationManager().getConfigLocation(map+ ".spawn4",yamlConfiguration));
            }
            mapEntry.getMapTemplate().getFreeTemplatesCount().add(mapEntry);
            mapEntry.getMapTemplate().getTemplatesCount().add(mapEntry);
        });
    }


    private void startIngameKiller() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (PlayerEntry playerEntry : getPlayerEntryHandler().values()) {
                if (playerEntry.getGameEntry() != null) {
                    if (playerEntry.getPlayerState() == PlayerState.SPECTATE || (playerEntry.getPlayerState() == PlayerState.INGAME && playerEntry.getGameEntry().getGameState() == GameState.INGAME)) {
                        if (!playerEntry.getGameEntry().getMapEntry().getRegionManager().isInRegion(playerEntry.getPlayer().getLocation(), false)) {
                            playerEntry.getGameEntry().teleportToSpawn(playerEntry);
                        }
                    }
                    if (playerEntry.getGameEntry().getGameState() == GameState.INGAME && playerEntry.getPlayerState() == PlayerState.INGAME) {
                        if (playerEntry.getGameEntry().getMapEntry().getDeathhight() > playerEntry.getPlayer().getLocation().getY()) {
                            playerEntry.killPlayer();
                        }
                    }
                } else {
                    if (playerEntry.getPlayerState() == PlayerState.LOBBY) {
                        if (playerEntry.getPlayer().getLocation().getY() < 0) {
                            playerEntry.getPlayer().teleport(lobby);
                        }
                    }
                }
            }
        },0,5);
    }

    private void startIngameCounter() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            MLGRush.getInstance().getInventoryManager().updateSpectator();
            for (GameEntry gameEntry : getGameEntryHandler().values()) {
                if (gameEntry.getGameState() == GameState.INGAME) {
                    gameEntry.setTimeSinceStart(gameEntry.getTimeSinceStart() + 1);

                    for (PlayerEntry playerEntry : gameEntry.getPlayersInArena()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        AtomicInteger i = new AtomicInteger();
                        gameEntry.getPlayersPlaying().forEach(playerEntry1 -> {
                            i.getAndIncrement();
                            if (gameEntry.getPlayersPlaying().size() == 2 && i.get() == 2) {
                                stringBuilder.append(BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(playerEntry1.getPlayer().getUniqueId()) + playerEntry1.getPlayer().getName() + " §e" + playerEntry1.getIngamePlayer().getBeds());
                            } else if (gameEntry.getPlayersPlaying().size() == 4 && i.get() == 4) {
                                stringBuilder.append(BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(playerEntry1.getPlayer().getUniqueId()) + playerEntry1.getPlayer().getName() + " §e" + playerEntry1.getIngamePlayer().getBeds());
                            } else {
                                stringBuilder.append(BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(playerEntry1.getPlayer().getUniqueId()) + playerEntry1.getPlayer().getName() + " §e" + playerEntry1.getIngamePlayer().getBeds() + " §8§l︳ ");
                            }
                        });
                        playerEntry.updateScoreBoard();
                        playerUtils.sendActionBar(playerEntry.getPlayer(),stringBuilder.toString());
                    }
                    if (gameEntry.getTimeSinceStart() > 3600) {
                        gameEntry.finishGame(false);
                    }
                }
            }
        },20,20);
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

    @Override
    public void onDisable() {
        yamlConfiguration.set("Maps",maps);
        yamlConfiguration.set("Templates",templates);
        for (GameEntry gameEntry : getGameEntryHandler().values()) {
            gameEntry.finishGame(true);
        }
        for (PlayerEntry playerEntry : getPlayerEntryHandler().values()) {
            playerEntry.saveData();
        }

        // test
    }


}
