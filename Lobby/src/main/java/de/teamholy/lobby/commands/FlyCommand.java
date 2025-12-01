package de.teamholy.lobby.commands;


import de.teamholy.lobby.Lobby;
import de.teamholy.lobby.lobbyplayer.LobbyPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class FlyCommand implements CommandExecutor {

    private String prefix = "§6Fly §8× §7";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (!player.hasPermission("teamholy.perk.vip")) {
            player.sendMessage(prefix + "You need at least the §dVIP §7rank.");
            return false;
        }

        LobbyPlayer lobbyPlayer = Lobby.getInstance().getLobbyPlayerEntryHandler().get(player.getUniqueId());

        if (lobbyPlayer.isInArena()) {
            player.sendMessage(prefix + "You cannot use fly in the arena");
            return false;
        }

        if (lobbyPlayer.isFly()) {
            lobbyPlayer.setFly(false);
            player.sendMessage(prefix + "§cYou disabled fly");
            player.setAllowFlight(false);
            player.setFlying(false);
        } else {
            lobbyPlayer.setFly(true);
            player.sendMessage(prefix + "§aYou enabled fly");
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        return false;
    }
}
