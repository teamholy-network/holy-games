package de.teamholy.sgffa.commands;

import de.teamholy.sgffa.SGFFA;
import lombok.SneakyThrows;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;

/* copyright by Yassino */
public class SetupCommand implements CommandExecutor {
    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player player) {
          if (!player.hasPermission("command.setup")) {
                return false;
            }

            if (!(args.length == 2)) {
                player.sendMessage("§7§m--------------------");
                player.sendMessage(SGFFA.PREFIX + "/setup [Map] finish");
                player.sendMessage(SGFFA.PREFIX + "/setup [Map] addspawn");
                player.sendMessage(SGFFA.PREFIX + "/setup [Map] death");
                player.sendMessage("§7§m--------------------");
            } else {
                switch (args[1].toLowerCase()) {
                    case "addspawn":
                        player.sendMessage(SGFFA.PREFIX + "Du hast einen Spawn für die Map §a" + args[0] + " §7hinugefügt");
                        ArrayList spawn = (ArrayList) SGFFA.getInstance().getYamlConfiguration().getList(args[0]+".spawns");
                        if (spawn == null)
                            spawn = new ArrayList();
                        spawn.add(player.getLocation());
                        SGFFA.getInstance().getYamlConfiguration().set(args[0]+".spawns",spawn);
                        SGFFA.getInstance().getYamlConfiguration().save(SGFFA.getInstance().getFile());
                        break;
                    case "death":
                        SGFFA.getInstance().getYamlConfiguration().set(args[0] + ".high.Y", player.getLocation().getBlockY());
                        try {
                            SGFFA.getInstance().getYamlConfiguration().save(SGFFA.getInstance().getFile());
                            player.sendMessage(SGFFA.PREFIX + "Die todes höhe für §a" + args[0] + " §7wurde gesetzt");
                        } catch (IOException e) {
                            player.sendMessage(SGFFA.PREFIX + "Fehler beim Speichern der ChatTabConfig");
                        }
                        break;
                    case "finish":
                        if (SGFFA.getInstance().getCacheHandler().getMapEntryHashMap().containsKey(args[0].toLowerCase())) {
                            player.sendMessage(SGFFA.PREFIX + "Diese Map gibt es bereits!");
                        } else {
                            SGFFA.getInstance().getYamlConfiguration().set("Maps", SGFFA.getInstance().getCacheHandler().getMapEntryHashMap().values());
                            try {
                                SGFFA.getInstance().getYamlConfiguration().save(SGFFA.getInstance().getFile());
                                player.sendMessage(SGFFA.PREFIX + "Die Map §a" + args[0] + " §7wurde erstellt");
                            } catch (IOException e) {
                                player.sendMessage(SGFFA.PREFIX + "Fehler beim Speichern der Config");
                            }
                        }
                        break;
                }
            }
        }
        return false;
    }
}
