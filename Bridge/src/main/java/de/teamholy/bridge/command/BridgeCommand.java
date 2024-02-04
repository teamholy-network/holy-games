package de.teamholy.bridge.command;

import com.google.common.collect.Lists;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.map.management.BridgeMapManagement;
import de.teamholy.bridge.player.management.PlayerManagement;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
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
        PlayerManagement bridgePlayerManager = Bridge.getInstance().getPlayerManagement();

        switch (args[0]) {
            case "list": {
                if (bridgeMapManagement.getLoader().getMaps().isEmpty()) {
                    player.sendMessage(Bridge.PREFIX + "§7There are no maps.");
                    return true;
                }

                player.sendMessage("§7§m-------------------§r §6Bridge Maps §7§m-------------------");
                bridgeMapManagement.getLoader().getMaps().forEach(bridgeMap -> player.sendMessage(" §7- §e" + bridgeMap.getName() + " §8- §7" + bridgeMap.getTitle()));
                player.sendMessage("§7§m-----------------------------------------------------");
            }

            case "deleteStats": {
                if (!player.hasPermission("*")) return false;

                var bridgePlayer = bridgePlayerManager.getBridgePlayer(player);
                bridgePlayer.setWins(0);
                for (var mapTypes : BridgeMapType.values()) {
                    bridgePlayer.setGlobalBestTime(mapTypes, 0);
                    bridgePlayer.setLocalBestTime(mapTypes, 0);

                    bridgePlayer.getBestTimes().put(mapTypes, Lists.newArrayList());
                }
                bridgePlayer.setPlacedBlocks(0);


                player.kickPlayer("§cYour stats have been reset.");
                return false;
            }

            case "stats": {
                var bridgePlayer = bridgePlayerManager.getBridgePlayer(player);
                player.sendMessage("§7§m-------------------§r §6Bridge Stats §7§m-------------------");
                player.sendMessage("§7Wins: §e" + bridgePlayer.getWins());
                player.sendMessage("§7Blocks placed: §e" + bridgePlayer.getPlacedBlocks());
                for (var mapTypes : BridgeMapType.values()) {
                    player.sendMessage("§7" + mapTypes.getName() + " best time: §e" + bridgePlayerManager.checkBestTime(bridgePlayer.getGlobalBestTime(mapTypes)) + " §8| §6average time§8: §e" + bridgePlayerManager.checkBestTime(bridgePlayerManager.getAverageTime(bridgePlayer, mapTypes)));
                }
                player.sendMessage("§7§m-----------------------------------------------------");

                player.getLocation().getWorld().dropItem(player.getLocation(), new ItemBuilder(Material.DIAMOND).setName("§6Stats").setLore("§7Wins: §e" + bridgePlayer.getWins(), "§7Blocks placed: §e" + bridgePlayer.getPlacedBlocks()).build());

                return false;
            }


            default: {
                player.sendMessage("§cUnknown command. Use /bridge help for help.");
            }

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
