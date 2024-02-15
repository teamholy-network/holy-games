package de.teamholy.bridge.command;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.player.service.PlayerService;
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
public class SpectateCommand implements CommandExecutor {

    private final PlayerService playerService = Bridge.getInstance().getPlayerService();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            return false;
        }

        if (strings.length == 0) {
            player.sendMessage("§c/spectate <player>");
            return true;
        }

        var target = player.getServer().getPlayer(strings[0]);
        if (target == null) {
            player.sendMessage("§cThe player is not online.");
            return true;
        }

        if (target == player) {
            player.sendMessage("§cYou can't spectate yourself.");
            return true;
        }

        playerService.startSpectating(player, target);
        return false;
    }
}
