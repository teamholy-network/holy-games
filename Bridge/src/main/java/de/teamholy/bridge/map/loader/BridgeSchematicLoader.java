package de.teamholy.bridge.map.loader;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import de.teamholy.bridge.Bridge;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class BridgeSchematicLoader {

    private final String schematicName;
    private final int index;

    public BridgeSchematicLoader(String schematicName, int index) {
        this.schematicName = schematicName;
        this.index = index;
    }

    public void loadSchematic(Player player, int x, int z) {
        File schematicFile = new File(Bridge.getInstance().getDataFolder() + "/schematics", schematicName + ".schematic");

        ClipboardFormat format = ClipboardFormat.findByFile(schematicFile);
        if (format == null) {
            player.sendMessage(Bridge.PREFIX + "§cThe Map §e" + schematicName + " §cdoes not exist!");
            return;
        }

        player.sendMessage(Bridge.PREFIX + "Loading Map...");

        try {
            var reader = format.getReader(new FileInputStream(schematicFile));

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
