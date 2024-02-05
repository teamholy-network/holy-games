package de.teamholy.lobby.commands;

import de.teamholy.core.bukkit.utils.ItemBuilder;
import net.minecraft.server.v1_8_R3.EntityItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("*")) {
            sender.sendMessage("§cYou don't have the permission to execute this command.");
            return true;
        }

        EntityItem entity = dropItem(((Player)sender).getLocation(), new ItemBuilder(Material.DIAMOND).setName("§e§lTest").build());



        Bukkit.broadcastMessage((entity.isAlive() ? "JA" : "NEIN") + " - " + (entity.isInvisible() ? "JA" : "NEIN"));
        Bukkit.broadcastMessage(entity.getName() + " - " + entity.getItemStack().getItem().getName());
        Bukkit.broadcastMessage(entity.getBukkitEntity().getLocation().toString());

        return true;
    }

    public EntityItem dropItem(Location loc, ItemStack item) {
        if (loc.getChunk().getEntities().length > 64 * 4) return null;
        EntityItem entity = new EntityItem(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ(), CraftItemStack.asNMSCopy(item));
        entity.pickupDelay = 10;
        entity.motX = 0.0D;
        entity.motY = 0.0D;
        entity.motZ = 0.0D;
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(entity);
        return entity;
    }

}
