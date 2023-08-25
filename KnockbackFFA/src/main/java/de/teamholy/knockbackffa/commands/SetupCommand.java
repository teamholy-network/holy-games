package de.teamholy.knockbackffa.commands;

import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.models.MapEntry;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* copyright by Yassino */
public class SetupCommand implements CommandExecutor, Listener {

    public static final List<String> MAPS = new ArrayList<>(KnockbackFFA.getInstance().getYamlConfiguration().getStringList("Maps"));
    private String currentMap;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!player.hasPermission("command.setup")) {
                return false;
            }
            if (!(args.length == 2)) {
                player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "/setup (Map) setspawn");
                player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "/setup (Map) sethight");
                player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "/setup (Map) setdeath");
                player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "/setup (Map) setsign");
                player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "/setup (Map) finish");
            } else {
                switch (args[1].toLowerCase()) {
                    case "setdeath":
                        KnockbackFFA.getInstance().getYamlConfiguration().set(args[0] + ".death.Y", player.getLocation().getY());
                        try {
                            KnockbackFFA.getInstance().getYamlConfiguration().save(KnockbackFFA.getInstance().getCfgfFile());
                            player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "Die todes höhe für §e" + args[0] + " §7wurde gesetzt");
                        } catch (IOException e) {
                            player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "Fehler beim Speichern der ChatTabConfig");
                        }
                        break;
                    case "sethight":
                        KnockbackFFA.getInstance().getYamlConfiguration().set(args[0] + ".high.Y", player.getLocation().getY());
                        try {
                            KnockbackFFA.getInstance().getYamlConfiguration().save(KnockbackFFA.getInstance().getCfgfFile());
                            player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "Die Spawn höhe für §e" + args[0] + " §7wurde gesetzt");
                        } catch (IOException e) {
                            player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "Fehler beim Speichern der ChatTabConfig");
                        }
                        break;
                    case "setspawn":
                        KnockbackFFA.getInstance().getYamlConfiguration().set(args[0] + ".spawn", player.getLocation());
                        player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "Der Spawn für §e" + args[0] + " §7wurde gesetzt");
                        break;
                    case "setsign":
                        currentMap = args[0];
                        player.sendMessage(KnockbackFFA.getInstance().getPrefix() + " click on the sign for the map §e" + args[0]);
                        break;
                    case "finish":
                        if (MAPS.contains(args[0].toLowerCase())) {
                            player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "Diese Map gibt es bereits!");
                        } else {
                            MAPS.add(args[0]);
                            KnockbackFFA.getInstance().getYamlConfiguration().set("Maps", MAPS);
                            try {
                                KnockbackFFA.getInstance().getYamlConfiguration().save(KnockbackFFA.getInstance().getCfgfFile());
                                KnockbackFFA.getInstance().getCacheHandler().getMapEntrys().put(args[0],
                                        new MapEntry(args[0]
                                                ,(Location)KnockbackFFA.getInstance().getYamlConfiguration().get(args[0] + "spawn")
                                                ,KnockbackFFA.getInstance().getYamlConfiguration().getDouble(args[0] + ".high.Y"),
                                                KnockbackFFA.getInstance().getYamlConfiguration().getDouble(args[0] + ".death.Y"),
                                                null
                                        ));
                                player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "Die Map §e" + args[0] + " §7wurde eingerichtet");
                            } catch (IOException e) {
                                player.sendMessage(KnockbackFFA.getInstance().getPrefix() + "Fehler beim Speichern der ChatTabConfig");
                            }
                        }
                        break;
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (currentMap == null) return;
        Player player = (Player)event.getPlayer();
        int clickedBlock = event.getClickedBlock().getTypeId();
        if(clickedBlock==63 | clickedBlock==68) {
            Sign a = (Sign)event.getClickedBlock();
            player.sendMessage("yes thats a sign");
            KnockbackFFA.getInstance().getYamlConfiguration().set(currentMap + ".sign",a.getLocation());
            try {
                KnockbackFFA.getInstance().getYamlConfiguration().save(KnockbackFFA.getInstance().getCfgfFile());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            currentMap = null;
        }

    }
}
