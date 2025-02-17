package de.teamholy.clutches.commands;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.player.PlayerEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class VanishCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        if (!player.hasPermission("teamholy.team")) return false;

        PlayerEntry playerEntry = Clutches.getInstance().getPlayerEntryHandler().get(player.getUniqueId());
        playerEntry.vanishPlayer();
        return false;
    }
}
