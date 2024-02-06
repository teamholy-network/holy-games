package de.teamholy.bridge.command;

import com.google.common.collect.Lists;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.map.management.BridgeMapManagement;
import de.teamholy.bridge.player.management.PlayerManagement;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import net.minecraft.server.v1_8_R3.EntityItem;
import org.bukkit.*;
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

        switch (args[0].toLowerCase()) {
            case "list": {
                if (bridgeMapManagement.getLoader().getMaps().isEmpty()) {
                    player.sendMessage(Bridge.PREFIX + "§7There are no maps.");
                    return true;
                }

                player.sendMessage("§7§m-------------------§r §6Bridge Maps §7§m-------------------");
                bridgeMapManagement.getLoader().getMaps().forEach(bridgeMap -> player.sendMessage(" §7- §e" + bridgeMap.getName() + " §8- §7" + bridgeMap.getTitle()));
                player.sendMessage("§7§m-----------------------------------------------------");


                return true;
            }

            case "deletestats": {
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


            case "debug": {

                // create a new world and teleport player to it
                Bukkit.createWorld(new WorldCreator("debug").generateStructures(false));
                player.teleport(Bukkit.getWorld("debug").getSpawnLocation());
                player.sendMessage("success");

                return false;
            }

            case "stats": {
                var bridgePlayer = bridgePlayerManager.getBridgePlayer(player);
                player.sendMessage("§7§m-------------------§r §6Bridge Stats §7§m-------------------");
                player.sendMessage("§7Wins: §e" + bridgePlayer.getWins());
                player.sendMessage("§7Blocks placed: §e" + bridgePlayer.getPlacedBlocks());
                for (var mapTypes : BridgeMapType.values()) {
                    player.sendMessage("§7" + mapTypes.getName() + " best time: §e" + bridgePlayerManager.checkBestTimeString(bridgePlayer.getGlobalBestTime(mapTypes)) + " §8| §6average time§8: §e" + bridgePlayerManager.checkBestTimeString(bridgePlayerManager.getAverageTime(bridgePlayer, mapTypes)));
                }
                player.sendMessage("§7§m-----------------------------------------------------");


                EntityItem entity = bridgePlayerManager.dropItem(player.getLocation(), new ItemBuilder(Material.DIAMOND).setName("§e§lTest").build());


                Bukkit.broadcastMessage((entity.isAlive() ? "JA" : "NEIN") + " - " + (entity.isInvisible() ? "JA" : "NEIN"));
                Bukkit.broadcastMessage(entity.getName() + " - " + entity.getItemStack().getItem().getName());
                // broadcast location of entity
                Bukkit.broadcastMessage(entity.getBukkitEntity().getLocation().toString());

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
        player.sendMessage(" §7- §e/bridge list §8- §7List all available maps.");
        player.sendMessage(" §7- §e/bridge stats §8- §7Show your stats.");
        player.sendMessage(" §7- §e/bridge deletestats §8- §7Delete your stats.");
        player.sendMessage("§7§m-----------------------------------------------------");
    }
}
