package de.teamholy.bridge.map;

import de.teamholy.bridge.Bridge;
import de.teamholy.core.bukkit.perks.PerkRankType;
import de.teamholy.core.bukkit.utils.ItemBuilder;
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
            300,null, null,false)),

    CUBES_SHORT(new BridgeMapSkin(5551,new File(pathToSchematic() + "/Cubes-Short.schematic"),
    BridgeMapType.SHORT,
            "Cubes", Arrays.asList("§7Uhhh some §6cubes §7i guess?"),
            new ItemBuilder(Material.HARD_CLAY,1,3),
            0,null, PerkRankType.PREMIUM,false)),
    CUBES_LONG(new BridgeMapSkin(5551,new File(pathToSchematic() + "/Cubes-Long.schematic"),
    BridgeMapType.LONG,
            "Cubes", Arrays.asList("§7Uhhh some §6cubes §7i guess?"),
            new ItemBuilder(Material.HARD_CLAY,1,3),
            0,null, PerkRankType.PREMIUM,false)),
    CUBES_DIAGONAL(new BridgeMapSkin(5551,new File(pathToSchematic() + "/Cubes-Diagonal.schematic"),
    BridgeMapType.DIAGONAL,
            "Cubes", Arrays.asList("§7Uhhh some §6cubes §7i guess?"),
            new ItemBuilder(Material.HARD_CLAY,1,3),
            0,null, PerkRankType.PREMIUM,false)),

    YASSINO_SHORT(new BridgeMapSkin(5552,new File(pathToSchematic() + "/Yassino-Short.schematic"),
    BridgeMapType.SHORT,
            "§4Yassino", Arrays.asList("§7The §agreat §4Admin" , "§7of this §agreat §6network"),
            new ItemBuilder(Material.SKULL_ITEM,1,3).setSkullMeta(
                    getYassinoSkinTexture()
                    ,""),
            400,null, null,false)),

    YASSINO_LONG(new BridgeMapSkin(5552,new File(pathToSchematic() + "/Yassino-Long.schematic"),
            BridgeMapType.LONG,
            "§4Yassino", Arrays.asList("§7The §agreat §4Admin" , "§7of this §agreat §6network"),
            new ItemBuilder(Material.SKULL_ITEM,1,3).setSkullMeta(
                    getYassinoSkinTexture()
                    ,""),
            400,null, null,false)),

    YASSINO_DIAGONAL(new BridgeMapSkin(5552,new File(pathToSchematic() + "/Yassino-Diagonal.schematic"),
            BridgeMapType.DIAGONAL,
            "§4Yassino", Arrays.asList("§7The §agreat §4Admin" , "§7of this §agreat §6network"),
            new ItemBuilder(Material.SKULL_ITEM,1,3).setSkullMeta(
                    getYassinoSkinTexture()
                    ,""),
            400,null, null,false)),

    CHARONXYZ_LONG(new BridgeMapSkin(5553,new File(pathToSchematic() + "/charonxyz-Long.schematic"),
            BridgeMapType.LONG,
            "§bcharonxyz",
            Arrays.asList("§7The §agreat §bDeveloper" , "§7of this §agreat §6gamemode"),
            new ItemBuilder(Material.SKULL_ITEM,1,3).setSkullMeta(
                    getCharonxyzSkinTexture(),""),
            400,null, null,false)),
    CHARONXYZ_DIAGONAL(new BridgeMapSkin(5553,new File(pathToSchematic() + "/charonxyz-Diagonal.schematic"),
    BridgeMapType.DIAGONAL,
            "§bcharonxyz",
            Arrays.asList("§7The §agreat §bDeveloper" , "§7of this §agreat §6gamemode"),
            new ItemBuilder(Material.SKULL_ITEM,1,3).setSkullMeta(
                    getCharonxyzSkinTexture() ,""),
            400,null, null,false)),
    CHARONXYZ_SHORT(new BridgeMapSkin(5553,
            new File(pathToSchematic() + "/charonxyz-Short.schematic"),
    BridgeMapType.SHORT,
            "§bcharonxyz",
            Arrays.asList("§7The §agreat §bDeveloper" , "§7of this §agreat §6gamemode"),
            new ItemBuilder(Material.SKULL_ITEM,1,3).setSkullMeta(
                    getCharonxyzSkinTexture(),""),
            400,null, null,false));

    private final BridgeMapSkin bridgeMapSkin;

    @Getter
    private static final String charonxyzSkinTexture = "ewogICJ0aW1lc3RhbXAiIDogMTcwNzY3MzIyNDI5NywKICAicHJvZmlsZUlkIiA6ICJkMDhjYmJkNzVlNjU0M2UxYWQ1ODZlZDkw" +
            "YTU2MDgxOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJjaGFyb254eXoiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1" +
            "cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDU5MWVmMDY0M2NmMTEzMGFmYmE5MWZlZjY4ZTQxOT" +
            "I5ZjFkMzdjYjllMDc5YWU4MWU0N2M2ZTZlZTFlMmVmZSIKICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0MGMwZTAzZGQyNGExMWIxNWE4YjMzYzJhN2U5ZTMyYWJiMjA1MWIyNDgxZDBiYTdkZWZkNjM1Y2E3YTkzMyIKICAgIH0KICB9Cn0="
            , yassinoSkinTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcL2YxZjliZjFmZmQ1Nm" +
            "JhMzBmMjZhMGYwZmM2MzY4ZjBhYzRlMzZjMTFlNmE4MmQ4NDRiZGMzNWVlNTM2YzE0ZjUifX19";

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
