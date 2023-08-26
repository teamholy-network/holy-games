package de.teamholy.mlgrush.commands;

import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.game.GameState;
import de.teamholy.mlgrush.player.PlayerEntry;
import de.teamholy.mlgrush.player.PlayerState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuitCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player)commandSender;
        PlayerEntry playerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(player.getUniqueId());
        if (playerEntry.getGameEntry() != null) {
            if (playerEntry.getGameEntry().getGameState() == GameState.INGAME && playerEntry.getPlayerState() == PlayerState.INGAME) {
                playerEntry.getGameEntry().finishGame(false);
            }
        }
        return false;
    }
}
