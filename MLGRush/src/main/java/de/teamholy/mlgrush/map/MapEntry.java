package de.teamholy.mlgrush.map;

import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.enums.GameType;
import de.teamholy.mlgrush.manager.RegionManager;
import de.teamholy.mlgrush.maptemplate.MapTemplateEntry;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter @Setter
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
    private Double deathhight;

    private MapState mapState;
    private GameType gameType;


    public MapEntry(String mapId) {
        this.mapId = mapId;
        String[] ids = mapId.split("-");
        id = Integer.parseInt(ids[1]);
        mapState = MapState.NOUSE;
    }

    public void saveMapInConfig() {
        MLGRush.instance.getYamlConfiguration().set(mapId + ".deathhight",deathhight);
        MLGRush.getInstance().getLocationManager().createConfigLocaiton(spawn1,mapId+".spawn1",MLGRush.getInstance().getCfgfFile(),MLGRush.getInstance().getYamlConfiguration());
        MLGRush.getInstance().getLocationManager().createConfigLocaiton(spawn2,mapId+".spawn2",MLGRush.getInstance().getCfgfFile(),MLGRush.getInstance().getYamlConfiguration());
        MLGRush.getInstance().getLocationManager().createConfigLocaiton(bed1,mapId+".bed1",MLGRush.getInstance().getCfgfFile(),MLGRush.getInstance().getYamlConfiguration());
        MLGRush.getInstance().getLocationManager().createConfigLocaiton(bed2,mapId+".bed2",MLGRush.getInstance().getCfgfFile(),MLGRush.getInstance().getYamlConfiguration());
        MLGRush.getInstance().getLocationManager().createConfigLocaiton(region1,mapId+".region1",MLGRush.getInstance().getCfgfFile(),MLGRush.getInstance().getYamlConfiguration());
        MLGRush.getInstance().getLocationManager().createConfigLocaiton(region2,mapId+".region2",MLGRush.getInstance().getCfgfFile(),MLGRush.getInstance().getYamlConfiguration());
        if (bed3 != null) {
            MLGRush.getInstance().getLocationManager().createConfigLocaiton(spawn3,mapId+".spawn3",MLGRush.getInstance().getCfgfFile(),MLGRush.getInstance().getYamlConfiguration());
            MLGRush.getInstance().getLocationManager().createConfigLocaiton(spawn4,mapId+".spawn4",MLGRush.getInstance().getCfgfFile(),MLGRush.getInstance().getYamlConfiguration());
            MLGRush.getInstance().getLocationManager().createConfigLocaiton(bed3,mapId+".bed3",MLGRush.getInstance().getCfgfFile(),MLGRush.getInstance().getYamlConfiguration());
            MLGRush.getInstance().getLocationManager().createConfigLocaiton(bed4,mapId+".bed4",MLGRush.getInstance().getCfgfFile(),MLGRush.getInstance().getYamlConfiguration());
        }
    }
}
