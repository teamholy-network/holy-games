package de.teamholy.bridge.map.skin;

import com.boydti.fawe.object.schematic.Schematic;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.core.bukkit.perks.PerkRankType;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

/* copyright by Yassino */

@Getter @AllArgsConstructor
public class BridgeMapSkin {

    private int id;

    private File schematic;
    private BridgeMapType bridgeMapType;

    private String name;
    private List<String> description;

    private ItemBuilder item;

    private long price;
    @Nullable
    private String specialText;
    @Nullable
    private PerkRankType rankType;
    private boolean isDefault;

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
