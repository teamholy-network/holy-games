package de.teamholy.api.bukkit.commands;

import de.teamholy.api.BukkitHolyAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class LocationCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!player.hasPermission("teamholy.location"))
                return false;
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("set")) {
                    player.sendMessage(BukkitHolyAPI.getInstance().getPrefix() + "location " + args[1] + " set");
                    BukkitHolyAPI.getInstance().getLocationManager().addLocation(args[1],player.getLocation());
                }
            } else {
                player.sendMessage(BukkitHolyAPI.getInstance().getPrefix() + "/location set (name)");
            }
        }
        return false;
    }
}
