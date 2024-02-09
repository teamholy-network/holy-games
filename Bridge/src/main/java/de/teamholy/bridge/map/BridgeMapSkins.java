package de.teamholy.bridge.map;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.io.File;

/* copyright by Yassino */
@Getter @AllArgsConstructor
public enum BridgeMapSkins {

    TREE_SHORT(new BridgeMapSkin(new File(pathToSchematic() + "/Tree-Short.schematic"),
            BridgeMapType.SHORT,
            "Tree",
            "Default Teamholy bridge map",
            new ItemBuilder(Material.SAPLING),
            0,null,null,true)),

    TREE_DIAGONAL(new BridgeMapSkin(new File(pathToSchematic() + "/Tree-Diagonal.schematic"),
            BridgeMapType.DIAGONAL,"Tree","Default Teamholy bridge map",
            new ItemBuilder(Material.SAPLING),
            0,null,null,true)),

    TREE_LONG(new BridgeMapSkin(
            new File(pathToSchematic() + "/Tree-Long.schematic"),
            BridgeMapType.LONG,"Tree","Default Teamholy bridge map",
            new ItemBuilder(Material.SAPLING),
            0,null,null,true));

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
