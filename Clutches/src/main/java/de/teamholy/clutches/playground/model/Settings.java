package de.teamholy.clutches.playground.model;

import com.google.common.collect.Lists;
import de.teamholy.api.bukkit.utils.InventoryUtils;
import de.teamholy.clutches.playground.enums.ArmorColor;
import de.teamholy.clutches.playground.enums.CountdownLocation;
import de.teamholy.clutches.playground.enums.PlaygroundItems;
import eu.koboo.en2do.repository.entity.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/* copyright by Yassino */
@Getter
@Setter
@NoArgsConstructor
public class Settings {

    @Id
    private UUID uuid;

    private boolean pvpEnabled = false;
    private boolean adjustDirection = true;
    private int countdown = 3;
    private String inventoryString = InventoryUtils.inventoryToString(PlaygroundItems.newInventory());
    private CountdownLocation countdownLocation = CountdownLocation.TITLE;
    private ArmorColor armorColor = ArmorColor.GREY;
    private List<HitPreset> hitPresetMap = Lists.newLinkedList();

}