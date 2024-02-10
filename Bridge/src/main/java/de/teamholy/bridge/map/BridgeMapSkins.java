package de.teamholy.bridge.map;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.util.ItemBuilder;
import de.teamholy.core.bukkit.perks.PerkRankType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.io.File;
import java.util.Arrays;

/* copyright by Yassino */
@Getter @AllArgsConstructor
public enum BridgeMapSkins {

    TREE_SHORT(new BridgeMapSkin(0,new File(pathToSchematic() + "/Tree-Short.schematic"),
            BridgeMapType.SHORT,
            "Tree", Arrays.asList("§7Default §6Teamholy §7bridge map"),
            new ItemBuilder(Material.SAPLING),
            0,null,null,true)),

    TREE_DIAGONAL(new BridgeMapSkin(0,new File(pathToSchematic() + "/Tree-Diagonal.schematic"),
            BridgeMapType.DIAGONAL,
            "Tree",Arrays.asList("§7Default §6Teamholy §7bridge map"),
            new ItemBuilder(Material.SAPLING),
            0,null,null,true)),

    TREE_LONG(new BridgeMapSkin(0,
            new File(pathToSchematic() + "/Tree-Long.schematic"),
            BridgeMapType.LONG,
            "Tree",Arrays.asList("§7Default §6Teamholy §7bridge map"),
            new ItemBuilder(Material.SAPLING),
            0,null,null,true)),


    ROCKS_SHORT(new BridgeMapSkin(1,new File(pathToSchematic() + "/Rocks-Short.schematic"),
    BridgeMapType.SHORT,
            "Rocks", Arrays.asList("§7Default §6Teamholy §7bridge map"),
            new ItemBuilder(Material.STONE),
            0,null, PerkRankType.PREMIUM,false)),


    ROCKS_LONG(new BridgeMapSkin(1,new File(pathToSchematic() + "/Rocks-Long.schematic"),
            BridgeMapType.LONG,
            "Rocks", Arrays.asList("§7Default §6Teamholy §7bridge map"),
            new ItemBuilder(Material.STONE),
            0,null,PerkRankType.PREMIUM,false)),

    ROCKS_DIAGONAL(new BridgeMapSkin(1,new File(pathToSchematic() + "/Rocks-Diagonal.schematic"),
            BridgeMapType.DIAGONAL,
            "Rocks", Arrays.asList("§7Default §6Teamholy §7bridge map"),
            new ItemBuilder(Material.STONE),
            0,null,PerkRankType.PREMIUM,false)),


    Cypherpunk_SHORT(new BridgeMapSkin(5550,new File(pathToSchematic() + "/Cypherpunk-Short.schematic"),
    BridgeMapType.SHORT,
            "Cypherpunk", Arrays.asList("§7Welcome to the §6new §bworld"),
            new ItemBuilder(Material.ANVIL),
            300,null, null,false)),

    Cypherpunk_LONG(new BridgeMapSkin(5550,new File(pathToSchematic() + "/Cypherpunk-Long.schematic"),
    BridgeMapType.LONG,
            "Cypherpunk", Arrays.asList("§7Welcome to the §6new §bworld"),
            new ItemBuilder(Material.ANVIL),
            300,null, null,false)),

    Cypherpunk_DIAGONAL(new BridgeMapSkin(5550,new File(pathToSchematic() + "/Cypherpunk-Diagonal.schematic"),
    BridgeMapType.DIAGONAL,
            "Cypherpunk", Arrays.asList("§7Welcome to the §6new §bworld"),
            new ItemBuilder(Material.ANVIL),
            300,null, null,false));

    private BridgeMapSkin bridgeMapSkin;

    private static String pathToSchematic() {
        return Bridge.getInstance().getDataFolder().getAbsolutePath() + "/schematics";
    }

    public static BridgeMapSkin getDefaultSkin(BridgeMapType bridgeMapType) {
        for (BridgeMapSkins value : values()) {
            if (value.getBridgeMapSkin().getBridgeMapType() == bridgeMapType && value.getBridgeMapSkin().isDefault()) {
                return value.getBridgeMapSkin();
            }
        }
        return null;
    }

}
