package de.teamholy.mlgrush.maptemplate;

import de.teamholy.mlgrush.map.MapEntry;
import de.teamholy.mlgrush.map.MapState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Comparator;

@Getter @Setter
public class MapTemplateEntry {

    private String name;
    private Material material;
    private ArrayList<MapEntry> templatesCount;
    private ArrayList<MapEntry> freeTemplatesCount;


    public MapTemplateEntry(String name, Material material) {
        this.name = name;
        this.material = material;
        this.templatesCount = new ArrayList<MapEntry>();
        this.freeTemplatesCount = new ArrayList<MapEntry>();
    }

    public MapEntry getFreeArena() {
        freeTemplatesCount.sort(Comparator.comparingInt(MapEntry::getId));
        for (MapEntry mapEntry : freeTemplatesCount) {
            if (!(mapEntry.getMapState() == MapState.INUSE)) {
                return freeTemplatesCount.get(0);
            }
        }
        return null;
    }

}
