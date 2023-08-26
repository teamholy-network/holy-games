package de.teamholy.mlgrush.commands;

import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.player.PlayerEntry;
import de.teamholy.mlgrush.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class SpectateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        if (!player.hasPermission("teamholy.team")) return false;
        PlayerEntry playerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(player.getUniqueId());
        if (playerEntry.getPlayerState() != PlayerState.LOBBY) return false;
        if (args.length != 1) {
            player.sendMessage(MLGRush.getInstance().getPrefix() + "§7/spec (player)" );
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(MLGRush.getInstance().getPrefix() + "§cPlayer is not online!");
                return false;
            }
            PlayerEntry specPlayerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(target.getUniqueId());
            if (specPlayerEntry.getPlayerState()!= PlayerState.INGAME) {
                player.sendMessage(MLGRush.getInstance().getPrefix() + "Player is not ingame!");
                return false;
            }

            playerEntry.setSpectator(specPlayerEntry.getGameEntry());
        }
        return false;
    }
}
