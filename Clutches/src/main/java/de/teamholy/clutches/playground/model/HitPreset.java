package de.teamholy.clutches.playground.model;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter @NoArgsConstructor
public class HitPreset implements Cloneable {

    private UUID uuid = UUID.randomUUID();

    private String name = "new preset";
    private Icon icon = Icon.STICK;
    private Map<Integer, Hit> hitMap = Maps.newConcurrentMap();
    private int used = 0;
    private long created = System.currentTimeMillis(), lastEdit = System.currentTimeMillis();
    private Origin origin = Origin.CREATED;
    private boolean shared = false;

    public HitPreset deepCopy() {
        HitPreset copy = new HitPreset();
        copy.uuid = UUID.randomUUID();
        copy.name = this.name;
        copy.icon = this.icon;
        copy.hitMap = new ConcurrentHashMap<>();
        copy.hitMap.putAll(this.hitMap);
        copy.used = this.used;
        copy.created = this.created;
        copy.lastEdit = this.lastEdit;
        copy.origin = this.origin;
        copy.shared = this.shared;
        return copy;
    }

    public enum Origin { OTHER, CREATED, IMPORTED }

    @AllArgsConstructor @Getter
    public enum Icon {
        STICK(Material.STICK,0),
        COOBLE(Material.COBBLESTONE,0),
        STONE(Material.STONE,0),
        OAK_PLANK(Material.WOOD,0),
        SANDSTONE(Material.SANDSTONE,0),
        REDSANDSTONE(Material.RED_SANDSTONE,0),
        WOOD_STAIRS(Material.WOOD_STAIRS,0),
        WOOD_SWORD(Material.WOOD_SWORD,0),
        WOOD_PICKAXE(Material.WOOD_PICKAXE,0),
        RAW_FISH(Material.RAW_FISH,0),
        CLOWN_FISH(Material.RAW_FISH,2),
        WOOD_SLAB(Material.WOOD_STEP,0),
        STONE_SLAB(Material.STEP,3),
        IRON_SWORD(Material.IRON_SWORD,0),
        DIAMOND_SWORD(Material.DIAMOND_SWORD,0),
        DIAMOND_PICKAXE(Material.DIAMOND_PICKAXE,0),
        IRON_PICKAXE(Material.IRON_PICKAXE,0),
        DIAMOND(Material.DIAMOND,0),
        IRON(Material.IRON_INGOT,0),
        GOLD(Material.GOLD_INGOT,0),
        HOE(Material.GOLD_HOE,0);



        Material material;
        int subId;
    }

}
