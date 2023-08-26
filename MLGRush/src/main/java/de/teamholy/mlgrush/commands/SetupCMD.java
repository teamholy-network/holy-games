package de.teamholy.mlgrush.commands;

import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.enums.GameType;
import de.teamholy.mlgrush.map.MapEntry;
import de.teamholy.mlgrush.maptemplate.MapTemplateEntry;
import de.teamholy.mlgrush.maptemplate.MapTemplateSetup;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player)commandSender;
            if (!player.hasPermission("command.setup"))
                return false;
            if (args.length == 2) {
                switch (args[1].toLowerCase()) {
                    case "addmap":
                        if (!MLGRush.getInstance().getTemplates().contains(args[0])) {
                            player.sendMessage(MLGRush.getInstance().getPrefix() + "Die Gruppe §6" + args[0] + " §7gibt es nicht. /setup");
                            return false;
                        }
                        new MapTemplateSetup(player,new MapEntry(args[0] + "-" + (MLGRush.getInstance().getMapTemplateEntryHandler().get(args[0]).getTemplatesCount().size() +1)), GameType.TWOxONE);
                        break;
                    case "addmap4x1":
                        if (!MLGRush.getInstance().getTemplates().contains(args[0])) {
                            player.sendMessage(MLGRush.getInstance().getPrefix() + "Die Gruppe §6" + args[0] + " §7gibt es nicht. /setup");
                            return false;
                        }
                        new MapTemplateSetup(player,new MapEntry(args[0] + "-" + (MLGRush.getInstance().getMapTemplateEntryHandler().get(args[0]).getTemplatesCount().size() +1)),GameType.FOURxONE);
                        break;
                    case "tp":
                        if (!MLGRush.getInstance().getMaps().contains(args[0])) {
                            player.sendMessage(MLGRush.getInstance().getPrefix() + "Die Arena §6" + args[0] + " §7gibt es nicht.");
                            return false;
                        }
                        player.teleport(MLGRush.getInstance().getMapEntryHandler().get(args[0]).getSpawn1());
                        break;
                    case "create":
                        if (MLGRush.getInstance().getTemplates().contains(args[0])) {
                            player.sendMessage(MLGRush.getInstance().getPrefix() + "Die Gruppe §6" + args[0] + "§7 gibt es bereits");
                            return false;
                        }
                        MLGRush.getInstance().getTemplates().add(args[0]);
                        MLGRush.getInstance().getYamlConfiguration().set("Templates", MLGRush.getInstance().getTemplates());
                        player.sendMessage(MLGRush.getInstance().getPrefix() + "Die Gruppe §6" + args[0] + " §7wurde erstellt!");
                        MLGRush.getInstance().getYamlConfiguration().set(args[0]+".material", player.getItemInHand().getType().toString());
                        MLGRush.getInstance().getMapTemplateEntryHandler().put(args[0],new MapTemplateEntry(args[0],player.getItemInHand().getType()));
                        new MapTemplateSetup(player,new MapEntry(args[0] + "-1"),GameType.TWOxONE);
                        break;
                    case "create4x1":
                        if (MLGRush.getInstance().getTemplates().contains(args[0])) {
                            player.sendMessage(MLGRush.getInstance().getPrefix() + "Die Gruppe §6" + args[0] + "§7 gibt es bereits");
                            return false;
                        }
                        MLGRush.getInstance().getTemplates().add(args[0]);
                        MLGRush.getInstance().getYamlConfiguration().set("Templates", MLGRush.getInstance().getTemplates());
                        player.sendMessage(MLGRush.getInstance().getPrefix() + "Die Gruppe §6" + args[0] + " §7wurde erstellt!");
                        MLGRush.getInstance().getYamlConfiguration().set(args[0]+".material", player.getItemInHand().getType().toString());
                        MLGRush.getInstance().getMapTemplateEntryHandler().put(args[0],new MapTemplateEntry(args[0],player.getItemInHand().getType()));
                        new MapTemplateSetup(player,new MapEntry(args[0] + "-1"), GameType.FOURxONE);
                        break;
                }
            } else if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "info":
                        player.sendMessage(MLGRush.getInstance().getPrefix() + " MLGRush Gruppen");
                        for (MapTemplateEntry mapTemplateEntry : MLGRush.getInstance().getMapTemplateEntryHandler().values()) {
                            player.sendMessage(MLGRush.getInstance().getPrefix() + " -> " + mapTemplateEntry.getName());
                        }
                        player.sendMessage(" ");
                        player.sendMessage(MLGRush.getInstance().getPrefix() + " MLGRush Maps");
                        player.sendMessage(MLGRush.getInstance().getMapEntryHandler().get("test-1").getMapId());
                        for (MapEntry mapEntry : MLGRush.getInstance().getMapEntryHandler().values()) {
                            player.sendMessage(MLGRush.getInstance().getPrefix() + " -> " + mapEntry.getMapId() + " | " + mapEntry.getGameType());
                        }
                        break;
                }
            } else {
                player.sendMessage(MLGRush.getInstance().getPrefix() + "/Setup (Gruppe) create - Material in die Hand nehmen");
                player.sendMessage(MLGRush.getInstance().getPrefix() + "/Setup (Gruppe) create4x1 - Material in die Hand nehmen");
                player.sendMessage(MLGRush.getInstance().getPrefix() + "/Setup (Gruppe) addmap");
                player.sendMessage(MLGRush.getInstance().getPrefix() + "/Setup (Gruppe) addmap4x1");
                player.sendMessage(MLGRush.getInstance().getPrefix() + "/Setup (Arena) tp");
                player.sendMessage(MLGRush.getInstance().getPrefix() + "/Setup info");
            }
        }
        return false;
    }
}
