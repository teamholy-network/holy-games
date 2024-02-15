package de.teamholy.bridge.map.skin;

import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.core.bukkit.perks.PerkRankType;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

/* copyright by Yassino */

@Getter
@FieldDefaults(makeFinal = true)
public class BridgeMapSkin {

    private int id;

    private File schematic;


    private String name;
    private List<String> description;

    private ItemBuilder item;

    private long price;
    @Nullable
    private String specialText;
    @Nullable
    private PerkRankType rankType;
    private boolean isDefault;

    private BridgeMapType[] bridgeMapTypes;


    public BridgeMapSkin(int id, File schematic, String name, List<String> description, ItemBuilder item, long price, @Nullable String specialText, @Nullable PerkRankType rankType, boolean isDefault,
                         BridgeMapType... bridgeMapTypes) {
        this.id = id;
        this.schematic = schematic;
        this.name = name;
        this.description = description;
        this.item = item;
        this.price = price;
        this.specialText = specialText;
        this.rankType = rankType;
        this.isDefault = isDefault;
        this.bridgeMapTypes = bridgeMapTypes;
    }

    public boolean isBuyable() {
        if (isDefault) return false;
        return rankType == null && specialText == null;
    }

    public boolean isSpecial() {
        return specialText != null;
    }

    public boolean isRank() {
        return rankType != null;
    }
}
