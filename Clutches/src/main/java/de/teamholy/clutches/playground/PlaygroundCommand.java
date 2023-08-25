package de.teamholy.clutches.playground;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.playground.model.PlaygroundWorld;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/* copyright by Yassino */
public class PlaygroundCommand implements CommandExecutor, Listener {



    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (!player.hasPermission("teamholy.setup")) return false;
        PlaygroundManager playgroundManager = Clutches.getInstance().getPlaygroundManager();


        if (strings.length == 1 && strings[0].equalsIgnoreCase("info")) {
            int i = playgroundManager.getPlaygroundWorlds().size();
            for (PlaygroundWorld playgroundWorld : playgroundManager.getPlaygroundWorlds()) {
                player.sendMessage(Clutches.PREFIX + " §8-> §6" + playgroundWorld.getName());
                player.sendMessage(Clutches.PREFIX + " §8spawns§7: §c" + playgroundWorld.getSpawns().size());
                player.sendMessage(Clutches.PREFIX + " §8death height§7: §c" + playgroundWorld.getDeathHeight());
                player.sendMessage(Clutches.PREFIX + " §8materialAndId§7: §c" + playgroundWorld.getMaterialAndSubId());
                player.sendMessage(" ");
            }
            player.sendMessage(Clutches.PREFIX + " There are currently §6" + i + " §7playworlds");
        } else if (strings.length == 2){
            String type = strings[0].toLowerCase();
            String mapName = strings[1];
            PlaygroundWorld playgroundWorld = new PlaygroundWorld();
            switch (type) {
                case "create":
                    if (playgroundManager.getPlaygroundWorlds().stream().anyMatch(temp -> temp.getName().equalsIgnoreCase(mapName))) {
                        player.sendMessage("§cMap already there");
                        return false;
                    }

                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(Clutches.PREFIX + "Bitte leg ein Item in die hand");
                        return false;
                    }

                    String materialAndSub = player.getItemInHand().getType().toString() + ";" + player.getItemInHand().getDurability();
                    playgroundWorld.setMaterialAndSubId(materialAndSub);
                    playgroundWorld.setName(mapName);
                    playgroundManager.getPlaygroundWorlds().add(playgroundWorld);

                    player.sendMessage("§aCreated map!");
                    break;
                case "addspawn":

                    playgroundWorld = playgroundManager.getPlaygroundWorlds().stream().filter(temp -> temp.getName().equalsIgnoreCase(mapName)).findFirst().orElse(null);

                    if (playgroundWorld == null) {
                        player.sendMessage("§cMap does not exist");
                        return false;
                    }

                    playgroundWorld.getSpawns().add(player.getLocation());
                    player.sendMessage("§aAdded spawn!");
                    break;
                case "deathheight":

                    playgroundWorld = playgroundManager.getPlaygroundWorlds().stream().filter(temp -> temp.getName().equalsIgnoreCase(mapName)).findFirst().orElse(null);

                    if (playgroundWorld == null) {
                        player.sendMessage("§cMap does not exist");
                        return false;
                    }

                    playgroundWorld.setDeathHeight(player.getLocation().getBlockY());
                    player.sendMessage("§aAdded death height!");
                    break;
                case "save":
                    playgroundWorld = playgroundManager.getPlaygroundWorlds().stream().filter(temp -> temp.getName().equalsIgnoreCase(mapName)).findFirst().orElse(null);

                    if (playgroundWorld == null) {
                        player.sendMessage("§cMap does not exist");
                        return false;
                    }

                    YamlConfiguration yamlConfiguration = playgroundManager.getYamlConfiguration();

                    List<String> mapNames = yamlConfiguration.getStringList("maps");
                    if (mapNames == null) mapNames = new ArrayList<>();

                    mapNames.add(mapName);
                    yamlConfiguration.set("maps",mapNames);
                    yamlConfiguration.set(mapName + ".materialAndSubId" , playgroundWorld.getMaterialAndSubId());
                    yamlConfiguration.set(mapName + ".deathHeight" , playgroundWorld.getDeathHeight());
                    yamlConfiguration.set(mapName + ".spawns" , playgroundWorld.getSpawns());

                    yamlConfiguration.save(playgroundManager.getCfgfFile());
                    player.sendMessage("§aAdded map §b"+ mapName);
                    break;
            }
        } else {
            player.sendMessage(Clutches.PREFIX + "/playworld create (name) - item in hand");
            player.sendMessage(Clutches.PREFIX + "/playworld addspawn (name)");
            player.sendMessage(Clutches.PREFIX + "/playworld deathheight (name)");
            player.sendMessage(Clutches.PREFIX + "/playworld save (name)");
            player.sendMessage(Clutches.PREFIX + "/playworld info");
        }

        return false;
    }
}
