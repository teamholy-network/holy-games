package de.teamholy.bridge.command;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.management.BridgeMapManagement;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class BridgeCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player))
            return false;

        if (!player.hasPermission("bridge.setup")) {
            player.sendMessage("§cYou don't have the permission to execute this command.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(player);
            return true;
        }

        BridgeMapManagement bridgeMapManagement = Bridge.getInstance().getMapManagement();

        if (args[0].equalsIgnoreCase("list")) {

            if (bridgeMapManagement.getLoader().getMaps().isEmpty()) {
                player.sendMessage(Bridge.PREFIX + "§7There are no maps.");
                return true;
            }

            player.sendMessage("§7§m-------------------§r §6Bridge Maps §7§m-------------------");
            bridgeMapManagement.getLoader().getMaps().forEach(bridgeMap -> player.sendMessage(" §7- §e" + bridgeMap.getName() + " §8- §7" + bridgeMap.getTitle()));
            player.sendMessage("§7§m-----------------------------------------------------");
        } else {
            sendHelp(player);
        }

        return false;
    }

    private void sendHelp(Player player) {
        player.sendMessage("§7§m-------------------§r §6Bridge Help §7§m-------------------");
        player.sendMessage("§7/bridge help §8- §7Shows this help page.");
        player.sendMessage("§7/bridge create <name> <title> §8- §7Creates a new bridge map.");
        player.sendMessage("§7/bridge sethigh <name> §8- §7Sets the spawn location of the map.");
        player.sendMessage("§7/bridge setbottom <name> §8- §7Sets the spawn location of the map.");
        player.sendMessage("§7/bridge setspawn <name> §8- §7Sets the spawn location of the map.");
        player.sendMessage("§7/bridge setmiddle <name> §8- §7Sets the middle location of the map.");
        player.sendMessage("§7/bridge setendstart <name> §8- §7Sets the start location of the map end.");
        player.sendMessage("§7/bridge setendend <name> §8- §7Sets the end location of the map end.");
        player.sendMessage("§7/bridge setendhigh <name> §8- §7Sets the high location of the map end.");
        player.sendMessage("§7/bridge setitem <name> §8- §7Sets the item of the map.");
        player.sendMessage("§7/bridge list §8- §7Lists all maps.");
        player.sendMessage("§7/bridge save <name> §8- §7Saves the map.");
        player.sendMessage("§7/bridge delete <name> §8- §7Deletes a map.");
        player.sendMessage("§7§m------------------------------------------------");
    }
}
