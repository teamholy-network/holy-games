package de.teamholy.mlgrush.map;

import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.enums.GameType;
import de.teamholy.mlgrush.manager.RegionManager;
import de.teamholy.mlgrush.maptemplate.MapTemplateEntry;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

/**
 * Represents a map entry for MLGRush game mode.
 */
@Getter
@Setter
public class MapEntry {

    private String mapId;
    private MapTemplateEntry mapTemplate;
    private RegionManager regionManager;
    private int id;

    private Location region1;
    private Location region2;
    private Location spawn1;
    private Location spawn2;

    private Location spawn3;
    private Location spawn4;

    private Location bed1;
    private Location bed2;

    private Location bed3;
    private Location bed4;
    private Double deathHeight;

    private MapState mapState;
    private GameType gameType;


    public MapEntry(String mapId) {
        this.mapId = mapId;
        String[] ids = mapId.split("-");
        id = Integer.parseInt(ids[1]);
        mapState = MapState.NOUSE;
    }

    /**
     * Saves the map configuration to the config file.
     */
    public void saveMapInConfig() {
        MLGRush.getInstance().getYamlConfiguration().set(mapId + ".deathHeight", deathHeight);
        MLGRush.getInstance().getLocationManager().createConfigLocation(spawn1, mapId + ".spawn1", MLGRush.getInstance().getConfigFile(), MLGRush.getInstance().getYamlConfiguration());
        MLGRush.getInstance().getLocationManager().createConfigLocation(spawn2, mapId + ".spawn2", MLGRush.getInstance().getConfigFile(), MLGRush.getInstance().getYamlConfiguration());
        MLGRush.getInstance().getLocationManager().createConfigLocation(bed1, mapId + ".bed1", MLGRush.getInstance().getConfigFile(), MLGRush.getInstance().getYamlConfiguration());
        MLGRush.getInstance().getLocationManager().createConfigLocation(bed2, mapId + ".bed2", MLGRush.getInstance().getConfigFile(), MLGRush.getInstance().getYamlConfiguration());
        MLGRush.getInstance().getLocationManager().createConfigLocation(region1, mapId + ".region1", MLGRush.getInstance().getConfigFile(), MLGRush.getInstance().getYamlConfiguration());
        MLGRush.getInstance().getLocationManager().createConfigLocation(region2, mapId + ".region2", MLGRush.getInstance().getConfigFile(), MLGRush.getInstance().getYamlConfiguration());
        if (bed3 != null) {
            MLGRush.getInstance().getLocationManager().createConfigLocation(spawn3, mapId + ".spawn3", MLGRush.getInstance().getConfigFile(), MLGRush.getInstance().getYamlConfiguration());
            MLGRush.getInstance().getLocationManager().createConfigLocation(spawn4, mapId + ".spawn4", MLGRush.getInstance().getConfigFile(), MLGRush.getInstance().getYamlConfiguration());
            MLGRush.getInstance().getLocationManager().createConfigLocation(bed3, mapId + ".bed3", MLGRush.getInstance().getConfigFile(), MLGRush.getInstance().getYamlConfiguration());
            MLGRush.getInstance().getLocationManager().createConfigLocation(bed4, mapId + ".bed4", MLGRush.getInstance().getConfigFile(), MLGRush.getInstance().getYamlConfiguration());
        }
    }
}
