package de.teamholy.bedwars.commands;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.enums.GameState;
import de.teamholy.bedwars.model.MapEntry;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class ForcemapCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (!player.hasPermission("teamholy.forcemap")) {
            player.sendMessage(Bedwars.getInstance().getPrefix() + "You need §dVIP §7or higher to forcemap a game!");
            return false;
        }

        if (Bedwars.getInstance().getForceMap() != null) {
            player.sendMessage(Bedwars.getInstance().getPrefix() + "The map was already forced!");
            return false;
        }

        if (Bedwars.getInstance().getGameState() != GameState.LOBBY) {
            player.sendMessage(Bedwars.getInstance().getPrefix() + "The game is not in the lobby phase!");
            return false;
        }

        Inventory inventory = new Inventory("§8» §6Forcemap",9 * 2);
        for (int i = 0; i < 9 * 2; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }
        int i1 = 0;
        for (MapEntry mapEntry : Bedwars.getInstance().getCacheHandler().getMapEntries().values()) {
            inventory.setItem(new ItemBuilder(mapEntry.getMaterial(),1,(byte) mapEntry.getMaterialSubId()).setName("§8» §6" + mapEntry.getName()).build(),i1,event -> {
                player.playSound(player.getPlayer().getLocation(), Sound.CLICK, 1.0F, 100.0F);


                for (Player all : Bukkit.getOnlinePlayers()) {
                    all.sendMessage("§8§m------------------------------");
                    all.sendMessage("          §f§lFORCEMAP            ");
                    all.sendMessage("§7The map was forced to §6" + mapEntry.getName() + "§7!");
                    all.sendMessage("§8§m------------------------------");
                    all.playSound(all.getLocation(),Sound.ANVIL_BREAK,1f,1f);
                    all.closeInventory();
                }
                Bedwars.getInstance().setForceMap(mapEntry);
                Bedwars.getInstance().updateMotd();
            });
            i1++;
        }
        player.openInventory(inventory.getInventory());
        return false;
    }
}
