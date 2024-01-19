package dev.charon.bridge.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.charon.bridge.custom.CustomBlock;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class GsonBlockAdapter extends TypeAdapter<CustomBlock> {

    @Override
    public void write(JsonWriter out, CustomBlock value) throws IOException {
        out.value(value.getMaterial().name() + ";" +
                value.getLocation().getWorld().getName() + ";" +
                value.getLocation().getX() + ";" +
                value.getLocation().getY() + ";" +
                value.getLocation().getZ() + ";" +
                value.getLocation().getYaw() + ";" +
                value.getLocation().getPitch());
    }

    @Override
    public CustomBlock read(JsonReader in) throws IOException {
        String location = in.nextString();
        String[] split = location.split(";");
        return new CustomBlock(new Location(Bukkit.getWorld(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3]),
                Double.parseDouble(split[4]),
                Float.parseFloat(split[5]),
                Float.parseFloat(split[6])), Material.valueOf(split[0]));
    }
}
