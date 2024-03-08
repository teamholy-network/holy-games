package de.teamholy.clutches.commands;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.player.PlayerEntry;
import de.teamholy.clutches.player.PlayerState;
import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        PlayerEntry playerEntry = Clutches.getInstance().getPlayerEntryHandler().get(player.getUniqueId());
        if (playerEntry.getPlayerState() == PlayerState.LOBBY) {
            player.teleport(BukkitCore.getInstance().getLocationManager().getLocation("lobby"));
        }
        return false;
    }
}
