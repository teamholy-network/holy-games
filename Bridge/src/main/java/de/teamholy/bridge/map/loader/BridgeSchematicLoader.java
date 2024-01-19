package de.teamholy.bridge.map.loader;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.BridgePlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    private final int x;
    private final Location location;

    public BridgeSchematicLoader(String schematicName) {
        this.schematicName = schematicName;

        int mineNumber = Bridge.getInstance().getBridgeSchematicIndex().getIndex();
        int distance = BridgeMapType.mapType("Normal").getDistanceBetweenMaps();
        x = mineNumber * distance;
        location = new Location(Bukkit.getWorld("world"), x, 100, 0, 180.0f, 0.5f);
        mineNumber++;

        Bridge.getInstance().getBridgeSchematicIndex().setIndex(mineNumber);
        Bridge.getInstance().saveBridgeSchematicIndex();
    }

    public void loadSchematic(Player player) {
        File schematicFile = new File(Bridge.getInstance().getDataFolder() + "/schematics", schematicName + ".schematic");

        ClipboardFormat format = ClipboardFormat.findByFile(schematicFile);
        if (format == null) {
            player.sendMessage(Bridge.PREFIX + "§cThe Map §e" + schematicName + " §cdoes not exist!");
            return;
        }

        player.sendMessage(Bridge.PREFIX + "§aLoading Map...");

        try {
            var reader = format.getReader(new FileInputStream(schematicFile));
            var bukkitWorld = new BukkitWorld(player.getWorld());
            var worldData = bukkitWorld.getWorldData();

            Vector vector = Vector.toBlockPoint(x, location.getBlockY(), location.getBlockZ());

            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(bukkitWorld, -1);
            Clipboard clipboard = reader.read(worldData);

            Operation operation = new ClipboardHolder(clipboard, worldData)
                    .createPaste(editSession, worldData)
                    .to(vector)
                    .ignoreAirBlocks(true)
                    .build();

            try {
                Operations.complete(operation);
                player.sendMessage(Bridge.PREFIX + "§aMap Loaded! §eTeleporting...");
            } catch (WorldEditException ex) {
                ex.printStackTrace();
            }

            Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> teleportPlayer(player), 2L);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void teleportPlayer(Player player) {
        player.teleport(location);
      //  bridgePlayer.setState(BridgePlayer.PlayerState.INGAME);
    }
}
