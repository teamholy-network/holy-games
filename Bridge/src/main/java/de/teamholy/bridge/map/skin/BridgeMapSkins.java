package de.teamholy.bridge.map.skin;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.core.bukkit.perks.enums.PerkRankType;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/* copyright by Yassino */
@Getter @AllArgsConstructor
public enum BridgeMapSkins {

    TREE(new BridgeMapSkin(0,new File(pathToSchematic() + "/Tree-%type%.schematic"),

            "Tree", List.of("§7Default §6Teamholy §7bridge map"),
            new ItemBuilder(Material.SAPLING),
            0,null,null,true, BridgeMapType.SHORT, BridgeMapType.LONG, BridgeMapType.DIAGONAL)),

    ROCKS(new BridgeMapSkin(1,new File(pathToSchematic() + "/Rocks-%type%.schematic"),
            "Rocks", List.of("§7Default §6Teamholy §7bridge map"),
            new ItemBuilder(Material.STONE),
            0,null, PerkRankType.PREMIUM,false, BridgeMapType.SHORT, BridgeMapType.LONG, BridgeMapType.DIAGONAL)),

    Cypherpunk(new BridgeMapSkin(5550,new File(pathToSchematic() + "/Cypherpunk-%type%.schematic"),
            "Cypherpunk", List.of("§7Welcome to the §6new §bworld"),
            new ItemBuilder(Material.ANVIL),
            300,null, null,false, BridgeMapType.SHORT, BridgeMapType.LONG, BridgeMapType.DIAGONAL)),

    CUBES(new BridgeMapSkin(5551,new File(pathToSchematic() + "/Cubes-%type%.schematic"),
            "Cubes", List.of("§7Uhhh some §6cubes §7i guess?"),
            new ItemBuilder(Material.HARD_CLAY,1,3),
            0,null, PerkRankType.PREMIUM,false, BridgeMapType.SHORT, BridgeMapType.LONG, BridgeMapType.DIAGONAL)),

    YASSINO(new BridgeMapSkin(5552,new File(pathToSchematic() + "/Yassino-%type%.schematic"),
            "§4Yassino", Arrays.asList("§7The §agreat §4Admin" , "§7of this §agreat §6network"),
            new ItemBuilder(Material.SKULL_ITEM,1,3).setSkullMeta(
                    getYassinoSkinTexture()
                    ,""),
            400,null, null,false, BridgeMapType.SHORT, BridgeMapType.LONG, BridgeMapType.DIAGONAL)),

    CHARONXYZ(new BridgeMapSkin(5553,new File(pathToSchematic() + "/charonxyz-%type%.schematic"),
            "§bcharonxyz",
            Arrays.asList("§7The §agreat §bDeveloper" , "§7of this §agreat §6gamemode"),
            new ItemBuilder(Material.SKULL_ITEM,1,3).setSkullMeta(
                    getCharonxyzSkinTexture(),""),
            400,null, null,false, BridgeMapType.SHORT, BridgeMapType.LONG, BridgeMapType.DIAGONAL)),
    ;

    private final BridgeMapSkin bridgeMapSkin;

    @Getter
    private static final String charonxyzSkinTexture = "ewogICJ0aW1lc3RhbXAiIDogMTcwNzY3MzIyNDI5NywKICAicHJvZmlsZUlkIiA6ICJkMDhjYmJkNzVlNjU0M2UxYWQ1ODZlZDkw" +
            "YTU2MDgxOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJjaGFyb254eXoiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1" +
            "cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDU5MWVmMDY0M2NmMTEzMGFmYmE5MWZlZjY4ZTQxOT" +
            "I5ZjFkMzdjYjllMDc5YWU4MWU0N2M2ZTZlZTFlMmVmZSIKICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0MGMwZT" +
            "AzZGQyNGExMWIxNWE4YjMzYzJhN2U5ZTMyYWJiMjA1MWIyNDgxZDBiYTdkZWZkNjM1Y2E3YTkzMyIKICAgIH0KICB9Cn0="
            , yassinoSkinTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcL2YxZjliZjFmZmQ1Nm" +
            "JhMzBmMjZhMGYwZmM2MzY4ZjBhYzRlMzZjMTFlNmE4MmQ4NDRiZGMzNWVlNTM2YzE0ZjUifX19";

    private static String pathToSchematic() {
        return Bridge.getInstance().getDataFolder().getAbsolutePath() + "/schematics";
    }

    public static BridgeMapSkin getDefaultSkin(BridgeMapType bridgeMapType) {
        for (BridgeMapSkins value : values()) {
            for (BridgeMapType mapType : value.getBridgeMapSkin().getBridgeMapTypes()) {
                if (mapType == bridgeMapType && value.getBridgeMapSkin().isDefault()) {
                    return value.getBridgeMapSkin();
                }
            }
        }
        return null;
    }

    public static BridgeMapSkin getById(int id) {
        for (BridgeMapSkins value : values()) {
            if (value.getBridgeMapSkin().getId() == id) {
                return value.getBridgeMapSkin();
            }
        }
        return TREE.getBridgeMapSkin();
    }


}
