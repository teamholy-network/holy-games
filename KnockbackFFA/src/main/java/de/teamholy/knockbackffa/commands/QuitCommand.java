package de.teamholy.knockbackffa.commands;

import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.enums.PlayerState;
import de.teamholy.knockbackffa.listeners.EntityDamageByEntityListener;
import de.teamholy.knockbackffa.models.DamagedPlayer;
import de.teamholy.knockbackffa.models.PlayerEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class QuitCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(player.getUniqueId());
        if (playerEntry.getPlayerState() == PlayerState.INGAME) {
            if (EntityDamageByEntityListener.COMBATLOG.containsKey(playerEntry.getPlayer().getUniqueId())) {
                DamagedPlayer damagedPlayer = EntityDamageByEntityListener.COMBATLOG.get(playerEntry.getPlayer().getUniqueId());
                if (damagedPlayer.getHitTime() > System.currentTimeMillis()) {
                    player.sendMessage("§cYou can't leave the map while in fight §7(" + ((damagedPlayer.getHitTime() - System.currentTimeMillis()) / 1000 ) + "§cs§7)");
                    return false;
                }
            }
            playerEntry.leaveGame();
        } else {
            player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "You are not ingame!");
        }
        return false;
    }
}
