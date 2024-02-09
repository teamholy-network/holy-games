package de.teamholy.bridge.map;

import com.boydti.fawe.object.schematic.Schematic;
import de.teamholy.bridge.util.ItemBuilder;
import de.teamholy.core.bukkit.perks.PerkRankType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.io.File;

/* copyright by Yassino */

@Getter @AllArgsConstructor
public class BridgeMapSkin {

    private File schematic;
    private BridgeMapType bridgeMapType;

    private String name;
    private String description;

    private ItemBuilder item;

    private long price;
    @Nullable
    private String specialText;
    @Nullable
    private PerkRankType rankType;
    private boolean isDefault;

    public boolean isBuyable() {
        return rankType == null && specialText == null;
    }

    public boolean isSpecial() {
        return specialText != null;
    }

    public boolean isRank() {
        return rankType != null;
    }
}
