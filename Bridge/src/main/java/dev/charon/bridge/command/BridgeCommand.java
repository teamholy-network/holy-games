package dev.charon.bridge.command;

import dev.charon.bridge.Bridge;
import dev.charon.bridge.map.BridgeMap;
import dev.charon.bridge.map.management.BridgeMapManagement;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class BridgeCommand implements CommandExecutor {


    private Location highLeft, bottomRight, endHighLeft, endBottomRight;

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

        if (args.length > 1) {
            String name = args[1];

            if (args[0].equalsIgnoreCase("create")) {

                String[] split = Arrays.copyOfRange(args, 2, args.length);
                String message = String.join(" ", split).replace("&", "§");

                ItemStack itemStack = player.getItemInHand();
                if (itemStack.getType() == Material.AIR) {
                    player.sendMessage(Bridge.PREFIX + "§cYou need to hold an item in your hand.");
                    return true;
                }

                if (highLeft == null || bottomRight == null) {
                    player.sendMessage(Bridge.PREFIX + "§cYou need to set the high and bottom location of the map.");
                    return true;
                }

                if (bridgeMapManagement.getMap(name) != null) {
                    player.sendMessage(Bridge.PREFIX + "§cA map with this name already exists.");
                    return true;
                }

                bridgeMapManagement.create(highLeft, bottomRight, endHighLeft, endBottomRight, player.getLocation(), name, message, itemStack);
                player.sendMessage(Bridge.PREFIX + "§7You've created a new bridge map with name §e" + name + "§7 and title §e" + message);
            } else if (args[0].equalsIgnoreCase("sethigh")) {
                highLeft = player.getLocation();
                player.sendMessage(Bridge.PREFIX + "§7You've set the high location of the map §e" + name + "§7.");
            } else if (args[0].equalsIgnoreCase("setbottom")) {
                bottomRight = player.getLocation();
                player.sendMessage(Bridge.PREFIX + "§7You've set the bottom location of the map §e" + name + "§7.");
            } else if (args[0].equalsIgnoreCase("setspawn")) {
                BridgeMap bridgeMap = bridgeMapManagement.getMap(name);
                bridgeMap.getMapPosition().setStart(player.getLocation());
                bridgeMapManagement.getLoader().save(bridgeMap);
                player.sendMessage(Bridge.PREFIX + "§7You've set the spawn location of the map §e" + name + "§7.");
            } else if (args[0].equalsIgnoreCase("setmiddle")) {
                BridgeMap bridgeMap = bridgeMapManagement.getMap(name);
                bridgeMap.getMapPosition().setMiddle(player.getLocation());
                bridgeMapManagement.getLoader().save(bridgeMap);
                player.sendMessage(Bridge.PREFIX + "§7You've set the middle location of the map §e" + name + "§7.");
            } else if (args[0].equalsIgnoreCase("setendstart")) {
                BridgeMap bridgeMap = bridgeMapManagement.getMap(name);
                bridgeMap.getMapPosition().setEndStart(player.getLocation());
                bridgeMapManagement.getLoader().save(bridgeMap);
                player.sendMessage(Bridge.PREFIX + "§7You've set the end location of the map §e" + name + "§7.");
            } else if (args[0].equalsIgnoreCase("setendend")) {
                endBottomRight = player.getLocation();
                player.sendMessage(Bridge.PREFIX + "§7You've set the bottom end location of the map §e" + name + "§7.");
            } else if (args[0].equalsIgnoreCase("setendhigh")) {
                endHighLeft = player.getLocation();
                player.sendMessage(Bridge.PREFIX + "§7You've set the high end location of the map §e" + name + "§7.");
            } else if (args[0].equalsIgnoreCase("setitem")) {
                BridgeMap bridgeMap = bridgeMapManagement.getMap(name);
                bridgeMap.setMaterialName(player.getItemInHand().getType().name());
                bridgeMapManagement.getLoader().save(bridgeMap);
                player.sendMessage(Bridge.PREFIX + "§7You've set the item of the map §e" + name + "§7.");
            } else if (args[0].equalsIgnoreCase("save")) {
                bridgeMapManagement.getLoader().save(bridgeMapManagement.getMap(name));
                player.sendMessage(Bridge.PREFIX + "§7You've saved the map §e" + name + "§7.");
            } else if (args[0].equalsIgnoreCase("delete")) {
                BridgeMap bridgeMap = bridgeMapManagement.getMap(name);
                bridgeMapManagement.getLoader().getMaps().remove(bridgeMap);
                bridgeMapManagement.getLoader().delete(bridgeMap);
                player.sendMessage(Bridge.PREFIX + "§7You've deleted the map §e" + name + "§7.");
            } else {
                sendHelp(player);
            }
        } else if (args[0].equalsIgnoreCase("list")) {

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
