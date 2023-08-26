package de.teamholy.mlgrush.commands;

import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.player.PlayerEntry;
import de.teamholy.mlgrush.player.PlayerState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player)commandSender;
        PlayerEntry playerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(player.getUniqueId());
        if (playerEntry.getGameEntry() == null) {
            if (playerEntry.getPlayerState() == PlayerState.LOBBY) {
                playerEntry.getPlayer().teleport(MLGRush.getInstance().getLobby());
            }
        }
        return false;
    }
}
