package de.teamholy.bedwars;

import de.teamholy.bedwars.commands.*;
import de.teamholy.bedwars.handlers.InventoryHandler;
import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.bedwars.config.BedwarsConfig;
import de.teamholy.bedwars.enums.GameState;
import de.teamholy.bedwars.inventoriers.ShopInventory;
import de.teamholy.bedwars.model.MapEntry;
import de.teamholy.bedwars.model.PlayerEntry;
import de.teamholy.bedwars.model.TeamEntry;
import de.teamholy.bedwars.task.LobbyTask;
import de.teamholy.bedwars.utils.CacheHandler;
import com.google.common.reflect.ClassPath;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import de.dytanic.cloudnet.ext.bridge.bukkit.BukkitCloudNetHelper;
import de.teamholy.core.api.utility.Gamemodes;
import eu.koboo.markup.MarkupAPI;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

/* copyright by Yassino */
@Getter
@Setter
public class Bedwars extends JavaPlugin {

    @Getter
    private static Bedwars instance;
    public static Gamemodes MODE = Gamemodes.BEDWARS;

    private GameState gameState;
    private String prefix = "§6Bedwars §8× §7";
    private ArrayList<String> maps;
    private ArrayList<PlayerEntry> ingamePlayers;
    private ArrayList<PlayerEntry> spectatePlayers;
    private ArrayList<Location> placedBlocks;
    private int maxPlayers;
    private int minPlayers;
    private String mode;

    private MapEntry mapEntry;

    private MapEntry forceMap;

    private YamlConfiguration yamlConfiguration;
    private File file;
    private CacheHandler cacheHandler;
    private BedwarsConfig bedwarsConfig;
    private InventoryHandler inventoryHandler;

    private SlimePlugin slimePlugin;
    private SlimeLoader slimeLoader;

    private long gameStartedSince;


    private LobbyTask lobbyTask;

    public ArrayList<UUID> shopUuids;

    @SneakyThrows
    @Override
    public void onEnable() {

        shopUuids = new ArrayList<>();
        shopUuids.add(UUID.fromString("6d40f495-d796-4244-9f45-964cdd7e685a"));
        shopUuids.add(UUID.fromString("7f835fbc-0b45-4def-b5b2-d3bdfd3b23f8"));
        shopUuids.add(UUID.fromString("878d7127-9a83-4480-86e4-43cdde07b11a"));
        shopUuids.add(UUID.fromString("4d5dfbba-02d0-4070-b4b8-fc233ece8725"));
        shopUuids.add(UUID.fromString("8dd5c08c-1fcd-4280-90dc-b9a73a25451f"));
        shopUuids.add(UUID.fromString("2552774a-1364-4407-b7e3-d2f66d93605c"));
        shopUuids.add(UUID.fromString("b7d0add0-9173-4584-aec6-59d3487c8f4c"));
        shopUuids.add(UUID.fromString("1dd0cc8f-5271-4d49-b774-16dc36877017"));
        shopUuids.add(UUID.fromString("2646aecf-ddcc-4f3a-bedd-3b2c8d386350"));
        shopUuids.add(UUID.fromString("3c192f66-9c9f-4b5e-837d-a60cfdd6f96f"));
        shopUuids.add(UUID.fromString("03c55754-08fc-4a12-a451-e517c89a3f91"));
        shopUuids.add(UUID.fromString("7414ffe4-6355-4877-8103-1ff6e0432e61"));
        shopUuids.add(UUID.fromString("fa44c187-80dd-4171-bb5a-2e694c4c8b4f"));
        shopUuids.add(UUID.fromString("9f71b37d-d66a-4475-ba58-573c7e21b527"));

        Collections.shuffle(shopUuids);


        slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        slimeLoader = slimePlugin.getLoader("mongodb");

        getCommand("setup").setExecutor(new SetupCommand());
        getCommand("start").setExecutor(new StartCommand());
        getCommand("forcemap").setExecutor(new ForcemapCommand());
        getCommand("npcshop").setExecutor(new NPCShopCommand());
        getCommand("resetinv").setExecutor(new ResetInvCommand());
        instance = this;
        file = new File("plugins/Bedwars/locations.yml");



        new File("plugins/Bedwars/songs/").mkdirs();

        yamlConfiguration = YamlConfiguration.loadConfiguration(file);



        Bukkit.getScheduler().scheduleSyncRepeatingTask(Bedwars.getInstance(), () -> {
            if (getGameState() != GameState.INGAME) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if ((BukkitHolyAPI.getInstance().getLocationManager().getLocation("lobby").getBlockY() - 50) > player.getLocation().getBlockY() && player.getLocation().getWorld().getName().equalsIgnoreCase(BukkitHolyAPI.getInstance().getLocationManager().getLocation("lobby").getWorld().getName())) {
                        player.teleport(BukkitHolyAPI.getInstance().getLocationManager().getLocation("lobby"));
                    }
                });
            }
        }, 20, 20);
        registerListener("codes.yassino.bedwars.listeners");
        Bukkit.getPluginManager().registerEvents(new ShopInventory(), this);



        bootrap();



    }

    public void bootrap() {



        bedwarsConfig = new BedwarsConfig();
        cacheHandler = new CacheHandler();
        inventoryHandler = new InventoryHandler();

        maps = (ArrayList<String>) getYamlConfiguration().getStringList("Maps");

        initMAps();

        initMode();

        forceMap = null;
        mapEntry = null;

        lobbyTask = new LobbyTask();
        lobbyTask.startLobby();

        inventoryHandler.getTeamSelectInventory().updateInventory();
        inventoryHandler.getGoldVotingInventory().updateInventory();
        inventoryHandler.getMapVotingInventory().updateInventory();

        gameState = GameState.LOBBY;

        placedBlocks = new ArrayList<>();
        ingamePlayers = new ArrayList<>();
        spectatePlayers = new ArrayList<>();

        BukkitCloudNetHelper.setState("LOBBY");
        BukkitCloudNetHelper.setMaxPlayers(getMaxPlayers());
        updateMotd();

    }

    private void initMAps() {
        for (String mapName : getMaps()) {
            String[] typeAndId = yamlConfiguration.getString(mapName + ".material").split(";");
            Material material = Material.getMaterial(typeAndId[0]);
            int id = Integer.parseInt(typeAndId[1]);
            if (yamlConfiguration.getString(mapName + ".spawn.Red.World") == null) {
                System.out.println(mapName + " ----------");
            }
            getCacheHandler().getMapEntries().put(mapName, new MapEntry(material, id, mapName, yamlConfiguration.getString(mapName + ".spawn.Red.World")));
        }
    }

    public void updateNameTags() {
        MarkupAPI.updateNameTags();
    }

    public void updateData() {
        Bukkit.getScheduler().runTaskLater(this, this::updateMotd,2);
    }

    public void updateMotd() {
        if (gameState == GameState.LOBBY) {
            if (forceMap == null) {
                BukkitCloudNetHelper.setMotd(Bukkit.getOnlinePlayers().size() + ";" + getMaxPlayers() + ";null;null;null");
            } else {
                BukkitCloudNetHelper.setMotd(Bukkit.getOnlinePlayers().size() + ";" + getMaxPlayers() + ";" + forceMap.getName() + ";" + forceMap.getMaterial() + ";" + forceMap.getMaterialSubId());
            }
            BukkitCloudNetHelper.setExtra("1");
        } else if (gameState == GameState.INGAME) {
            BukkitCloudNetHelper.setMotd(mapEntry.getName() + ";" + getMapEntry().getMaterial() + ";" + getMapEntry().getMaterialSubId());

            JsonObject object = new JsonObject();
            object.addProperty("rushbw", isRushMode());
            object.addProperty("variante",mode);
            object.addProperty("ingame",getIngamePlayers().size());
            object.addProperty("start",gameStartedSince);
            Bedwars.getInstance().getCacheHandler().getTeamEntries().forEach(teamEntry -> {

                JsonArray playerObjects = new JsonArray();
                teamEntry.getAllPlayers().forEach(player -> {
                    JsonObject playerObject = new JsonObject();
                    playerObject.addProperty("kills",player.getKills());
                    playerObject.addProperty("beds",player.getBeds());
                    playerObject.addProperty("dead",player.isDead());

                    playerObject.addProperty("nicked",MarkupAPI.isNicked(player.getPlayer()));
                    playerObject.addProperty("uuid", String.valueOf(player.getPlayer().getUniqueId()));
                    playerObject.addProperty("name", player.getPlayer().getName());

                    playerObjects.add(playerObject);
                });


                JsonObject teamObj = new JsonObject();
                teamObj.add("players",playerObjects);
                teamObj.addProperty("hasBed",teamEntry.isHasBed());
                teamObj.addProperty("colorCode",teamEntry.getColorCode());

                object.add(teamEntry.getName(),teamObj);
            });
            BukkitCloudNetHelper.setExtra(new Gson().toJson(object));
        } else if (Bedwars.getInstance().getGameState() == GameState.END) {
            BukkitCloudNetHelper.setExtra("1");
        }
        BukkitCloudNetHelper.updateServiceInfo();
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

    public static boolean isRushMode() {
        return (MODE == Gamemodes.RUSHBW);
    }

    private void initMode() {
        gameState = GameState.LOBBY;
        if (!Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
            getServer().getConsoleSender().sendMessage("§l§cBEDWARS §7Diese Plugin benötigt die NoteBlockAPI");
            getServer().getConsoleSender().sendMessage("§l§cBEDWARS §7https://github.com/koca2000/NoteBlockAPI");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (getBedwarsConfig().getConfiguration().getBoolean("RushMode")) {
            MODE = Gamemodes.RUSHBW;
            prefix = "§6RushBW §8× §7";
        }
        mode = getBedwarsConfig().getConfiguration().getString("GameVariant");
        minPlayers = getBedwarsConfig().getConfiguration().getInt("PlayersToStart");
        if (!(mode.equals("2x1") || mode.equals("4x2") || mode.equals("8x1") || mode.equals("8x2") || mode.equals("4x4"))) {
            getServer().getPluginManager().disablePlugin(this);
            getServer().getConsoleSender().sendMessage("§l§cBEDWARS §7" + "Bitte Ãndere die Variante in §c2x1§8/§c4x2§8/§c8x1/§c8x2§8/§c4x4§7!");
        } else {
            getCacheHandler().getTeamEntries().clear();
            getServer().getConsoleSender().sendMessage("§l§cBEDWARS" + "§RushMode §8: §b" + isRushMode());
            getServer().getConsoleSender().sendMessage("§l§cBEDWARS" + "§7Spiel-Variante §8: §b" + mode);
            getServer().getConsoleSender().sendMessage("§l§cBEDWARS" + " §7Benoetigten-Spieler-zum-starten §8: §b" + minPlayers);
            switch (mode) {
                case "2x1":
                    maxPlayers = 2;
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Red", "§c", Color.RED, 1, "6fb290a13df88267ea5f5fcf796b6157ff64ccee5cd39d469724591babeed1f6", 1));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Blue", "§9", Color.BLUE, 1, "e08f40f53f1c27b36b5721c1474d6da695c7d4e0d6d0ffb343b9a56ca3c515fe", 2));
                    break;
                case "4x2":
                    maxPlayers = 8;
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Red", "§c", Color.RED, 2, "6fb290a13df88267ea5f5fcf796b6157ff64ccee5cd39d469724591babeed1f6", 1));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Blue", "§9", Color.BLUE, 2, "e08f40f53f1c27b36b5721c1474d6da695c7d4e0d6d0ffb343b9a56ca3c515fe", 2));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Yellow", "§e", Color.YELLOW, 2, "27799f092895c1ff1c12512719ff2629fd3f1cee6d932aa9873acd906b64d", 3));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Green", "§a", Color.LIME, 2, "c6e2985a6fe844fee7bb8c861787a8bb46411c1133df1726b87d25d121081da", 4));
                    break;
                case "8x2":
                    maxPlayers = 16;
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Red", "§c", Color.RED, 2, "6fb290a13df88267ea5f5fcf796b6157ff64ccee5cd39d469724591babeed1f6", 1));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Blue", "§9", Color.BLUE, 2, "e08f40f53f1c27b36b5721c1474d6da695c7d4e0d6d0ffb343b9a56ca3c515fe", 2));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Yellow", "§e", Color.YELLOW, 2, "27799f092895c1ff1c12512719ff2629fd3f1cee6d932aa9873acd906b64d", 3));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Green", "§a", Color.LIME, 2, "c6e2985a6fe844fee7bb8c861787a8bb46411c1133df1726b87d25d121081da", 4));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Black", "§0", Color.BLACK, 2, "f68a7aacb174a96b40efc3e3c8e63862c5f4fdd19417d75c70c4ba7d8acc65e", 5));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("White", "§f", Color.WHITE, 2, "1ad1b267ca42ff1a3d186cad2dc6fe87df58b8b223fb68887d29387bd4ea24e", 6));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Orange", "§6", Color.ORANGE, 2, "605ba45f53731769be2be182f13bf28de91c446f2a3422d88adbc849a316d6", 7));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Pink", "§d", Color.PURPLE, 2, "fa3fdd94456f426cfda48fe240541a8fa617d53e1b62d1c0e6fadbb21f8f213", 8));
                    break;
                case "8x1":
                    maxPlayers = 8;
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Red", "§c", Color.RED, 1, "6fb290a13df88267ea5f5fcf796b6157ff64ccee5cd39d469724591babeed1f6", 1));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Blue", "§9", Color.BLUE, 1, "e08f40f53f1c27b36b5721c1474d6da695c7d4e0d6d0ffb343b9a56ca3c515fe", 2));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Yellow", "§e", Color.YELLOW, 1, "27799f092895c1ff1c12512719ff2629fd3f1cee6d932aa9873acd906b64d", 3));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Green", "§a", Color.LIME, 1, "c6e2985a6fe844fee7bb8c861787a8bb46411c1133df1726b87d25d121081da", 4));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Black", "§0", Color.BLACK, 1, "f68a7aacb174a96b40efc3e3c8e63862c5f4fdd19417d75c70c4ba7d8acc65e", 5));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("White", "§f", Color.WHITE, 1, "1ad1b267ca42ff1a3d186cad2dc6fe87df58b8b223fb68887d29387bd4ea24e", 6));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Orange", "§6", Color.ORANGE, 1, "605ba45f53731769be2be182f13bf28de91c446f2a3422d88adbc849a316d6", 7));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Pink", "§d", Color.PURPLE, 1, "fa3fdd94456f426cfda48fe240541a8fa617d53e1b62d1c0e6fadbb21f8f213", 8));
                    break;
                case "4x4":
                    maxPlayers = 16;
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Red", "§c", Color.RED, 4, "6fb290a13df88267ea5f5fcf796b6157ff64ccee5cd39d469724591babeed1f6", 1));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Blue", "§9", Color.BLUE, 4, "e08f40f53f1c27b36b5721c1474d6da695c7d4e0d6d0ffb343b9a56ca3c515fe", 2));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Yellow", "§e", Color.YELLOW, 4, "27799f092895c1ff1c12512719ff2629fd3f1cee6d932aa9873acd906b64d", 3));
                    getCacheHandler().getTeamEntries().add(new TeamEntry("Green", "§a", Color.LIME, 4, "c6e2985a6fe844fee7bb8c861787a8bb46411c1133df1726b87d25d121081da", 4));
                    break;
            }
        }
    }


}
