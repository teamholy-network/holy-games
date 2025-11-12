package de.teamholy.mlgrush.maptemplate;

import de.teamholy.mlgrush.map.MapEntry;
import de.teamholy.mlgrush.map.MapState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a map template entry for MLGRush.
 */
@Getter
@Setter
public class MapTemplateEntry {

    private String name;
    private Material material;
    private List<MapEntry> templatesCount;
    private List<MapEntry> freeTemplatesCount;

    public MapTemplateEntry(String name, Material material) {
        this.name = name;
        this.material = material;
        this.templatesCount = new ArrayList<>();
        this.freeTemplatesCount = new ArrayList<>();
    }

    /**
     * Gets a free arena from the available templates.
     *
     * @return the first free MapEntry, or null if none available
     */
    public MapEntry getFreeArena() {
        freeTemplatesCount.sort(Comparator.comparingInt(MapEntry::getId));
        for (MapEntry mapEntry : freeTemplatesCount) {
            if (mapEntry.getMapState() != MapState.INUSE) {
                return freeTemplatesCount.get(0);
            }
        }
        return null;
    }
}
