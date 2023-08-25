package de.teamholy.bedwars.commands;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class NPCShopCommand implements CommandExecutor {

    public static boolean NPCSHOP = true;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (!player.hasPermission("teamholy.team")) return false;
        if (Bedwars.getInstance().getGameState() != GameState.LOBBY) {
            player.sendMessage(Bedwars.getInstance().getPrefix() + "You can only change the shop in the lobby phase!");
            return false;
        }

        NPCSHOP = !NPCSHOP;

        for (Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(Bedwars.getInstance().getPrefix() + "The shop was set to §6" + (NPCSHOP ? "NPC" : "ArmorStand"));
            all.playSound(all.getLocation(), Sound.ANVIL_BREAK,1,1);
        }

        return false;
    }
}
