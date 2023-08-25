package de.teamholy.knockbackffa.commands;

import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.enums.PlayerState;
import de.teamholy.knockbackffa.models.PlayerEntry;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/* copyright by Yassino */
public class VanishCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (!player.hasPermission("teamholy.team")) return false;
        PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(player.getUniqueId());


        if (playerEntry.getPlayerState() == PlayerState.SPECTATE) {
            player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "Du bist nun nicht mehr im Vanish");
            player.setAllowFlight(false);
            player.setFlying(false);
            playerEntry.performSpawn();
            player.removePotionEffect(PotionEffectType.INVISIBILITY);

            Bukkit.getScheduler().runTaskLater(KnockbackFFA.getInstance(),() -> {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (!all.hasPermission("teamholy.team")) {
                        all.showPlayer(player);
                    }
                }
            },5);

        } else {
            if (playerEntry.getPlayerState() == PlayerState.INGAME) playerEntry.leaveGame();
            playerEntry.setPlayerState(PlayerState.SPECTATE);
            player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "Du bist nun im Vanish");
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (!all.hasPermission("teamholy.team")) {
                    all.hidePlayer(player);
                }
            }
            player.setAllowFlight(true);
            player.setFlying(true);
            player.getInventory().clear();
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,99999,2));
            player.getInventory().setItem(2,new ItemBuilder(Material.COMPASS).setName("§8» §6Vanish Menü").build());
            player.getInventory().setItem(6,new ItemBuilder(Material.SLIME_BALL).setName("§8» §6Leave Vanish").build());
        }

        return false;
    }
}
