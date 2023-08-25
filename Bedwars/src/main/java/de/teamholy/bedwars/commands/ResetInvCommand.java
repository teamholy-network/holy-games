package de.teamholy.bedwars.commands;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.enums.GameState;
import de.teamholy.bedwars.model.PlayerEntry;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class ResetInvCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;

        if (!Bedwars.isRushMode()) {
            player.sendMessage(Bedwars.getInstance().getPrefix() + "This command is only allowed in RushBW!");
            return false;
        }

        if (Bedwars.getInstance().getGameState() != GameState.LOBBY) {
            player.sendMessage(Bedwars.getInstance().getPrefix() + "This command is only allowed in the §2Lobby §7state");
            return false;
        }

        PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId());
        playerEntry.createInv();

        player.sendMessage(Bedwars.getInstance().getPrefix() + "§aYour shop inventory was reset");
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 2f, 2f);

        return false;
    }
}
