package de.teamholy.clutches.playground;

import com.google.common.collect.Lists;
import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.playground.model.Hit;
import de.teamholy.clutches.playground.model.HitPreset;
import de.teamholy.clutches.playground.model.PlaygroundWorld;
import de.teamholy.clutches.playground.task.ArmorColorRainbowTask;
import de.teamholy.core.bukkit.BukkitCore;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/* copyright by Yassino */
@Getter
public class PlaygroundManager {

    private final List<PlaygroundWorld> playgroundWorlds = new ArrayList<>();
    private List<HitPreset> presentedHits = new ArrayList<>();
    private final File cfgfFile = new File(Clutches.getInstance().getDataFolder(), "playground.yml");
    private final YamlConfiguration yamlConfiguration;


    private final ArmorColorRainbowTask armorColorRainbowTask;
    private final EditInventories editInventories;
    private final PlaygroundRepository playgroundRepository;

    public PlaygroundManager(Clutches instance) {
        yamlConfiguration = YamlConfiguration.loadConfiguration(cfgfFile);
        armorColorRainbowTask = new ArmorColorRainbowTask(instance);
        editInventories = new EditInventories();


        playgroundRepository = BukkitCore.getAPI().getMongoManager().create(PlaygroundRepository.class);
        instance.getServer().getPluginManager().registerEvents(new PlaygroundListener(), instance);
        System.out.println(yamlConfiguration.getStringList("maps"));
        presentedHits = loadPresetsFromConfig();

        if (yamlConfiguration.contains("maps")) {

            yamlConfiguration.getStringList("maps").forEach(map -> {
                PlaygroundWorld playgroundWorld = new PlaygroundWorld();
                playgroundWorld.setName(map);
                playgroundWorld.setMaterialAndSubId(yamlConfiguration.getString(map + ".materialAndSubId"));
                playgroundWorld.setDeathHeight(yamlConfiguration.getInt(map + ".deathHeight"));
                playgroundWorld.setSpawns(new ArrayList(yamlConfiguration.getList(map + ".spawns")));
                try {
                    playgroundWorld.setSlimeWorld(Clutches.getInstance().getSlimePlugin()
                            .loadWorld(Clutches.getInstance().getSlimeLoader(),"PLAYGROUND-" + map,true,new SlimePropertyMap()));
                } catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException |
                         WorldInUseException e) {
                    throw new RuntimeException(e);
                }
                playgroundWorlds.add(playgroundWorld);
            });
        }
    }

    public List<HitPreset> loadPresetsFromConfig() {
        if (yamlConfiguration.getMapList("presets") == null) return Lists.newArrayList();
        List<Map<?, ?>> presetsList = yamlConfiguration.getMapList("presets");
        List<HitPreset> presets = new ArrayList<>();

        for (Map<?, ?> presetMap : presetsList) {
            HitPreset preset = new HitPreset();
            preset.setUuid(UUID.fromString((String) presetMap.get("uuid")));
            preset.setName((String) presetMap.get("name"));
            preset.setIcon(HitPreset.Icon.valueOf((String) presetMap.get("icon")));
            preset.setHitMap(loadHitMapFromConfig(presetMap.get("hitMap")));
            preset.setUsed((Integer) presetMap.get("used"));
            preset.setCreated((Long) presetMap.get("created"));
            preset.setLastEdit((Long) presetMap.get("lastEdit"));
            preset.setOrigin(HitPreset.Origin.valueOf((String) presetMap.get("origin")));
            preset.setShared((Boolean) presetMap.get("shared"));

            presets.add(preset);
        }

        return presets;
    }

    private Map<Integer, Hit> loadHitMapFromConfig(Object hitMapObj) {
        if (hitMapObj instanceof List) {
            List<Map<String, Object>> hitMapList = (List<Map<String, Object>>) hitMapObj;
            Map<Integer, Hit> hitMap = new HashMap<>();

            for (Map<String, Object> hitMapEntry : hitMapList) {
                int slot = (Integer) hitMapEntry.get("slot");
                double xKnock = (Double) hitMapEntry.get("xKnock");
                double yKnock = (Double) hitMapEntry.get("yKnock");
                Hit.Icon icon = Hit.Icon.valueOf((String) hitMapEntry.get("icon"));
                Hit.DiagonalDirection diagonalDirection = Hit.DiagonalDirection.valueOf((String) hitMapEntry.get("diagonalDirection"));

                Hit hit = new Hit();
                hit.setYknock(yKnock);
                hit.setXknock(xKnock);
                hit.setIcon(icon);
                hit.setDiagonalDirection(diagonalDirection);
                hitMap.put(slot, hit);
            }

            return hitMap;
        }

        return null;

    }

}
