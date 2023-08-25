package de.teamholy.bedwars.commands;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class StartCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        if (!player.hasPermission("teamholy.start")) {
            player.sendMessage(Bedwars.getInstance().getPrefix() + "You need §dVIP §7or higher to start a game!");
            return false;
        }

        if (Bedwars.getInstance().getGameState() != GameState.LOBBY) {
            player.sendMessage(Bedwars.getInstance().getPrefix() + "The Match is already ingame!");
            return false;
        }

        if (Bukkit.getOnlinePlayers().size() >= Bedwars.getInstance().getMinPlayers()) {
            Bedwars.getInstance().getLobbyTask().startGame();
            player.sendMessage(Bedwars.getInstance().getPrefix() + "you started the game!");
        } else {
            player.sendMessage(Bedwars.getInstance().getPrefix() + "there are not enough players online to start the game!");
        }


        return false;
    }
}
