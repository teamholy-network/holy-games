package de.teamholy.bedwars.commands;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.model.TeamEntry;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/* copyright by Yassino */
public class SetupCommand implements CommandExecutor {
    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String s, String[] args) {
        if (commandSender instanceof Player player) {
          YamlConfiguration cfg = Bedwars.getInstance().getYamlConfiguration();
            if (!player.hasPermission("system.setup")) {
                return false;
            }
            switch (args.length) {
                case 2:
                    if (args[1].equalsIgnoreCase("addmap")) {
                        if (player.getItemInHand().getType() == Material.AIR) {
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Bitte leg ein Item in die hand für das voting");
                            return false;
                        } else {
                            String materialAndSub = player.getItemInHand().getType().toString() + ";" + player.getItemInHand().getDurability();
                            Bedwars.getInstance().getMaps().add(args[0]);
                            cfg.set("Maps", Bedwars.getInstance().getMaps());
                            cfg.set(args[0] + ".material", materialAndSub);
                            cfg.save(Bedwars.getInstance().getFile());
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Die Map §c" + args[0] + " §7wurde erstellt");
                        }
                    } else if (args[1].equalsIgnoreCase("setdeath")) {
                        if (!Bedwars.getInstance().getMaps().contains(args[0])) {
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Diese Map gibt es nicht");
                            return false;
                        }
                        cfg.set(args[0] + ".death.Y", player.getLocation().getY());
                        cfg.save(Bedwars.getInstance().getFile());
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "Die todes höhe für §c" + args[0] + " §7wurde gesetzt");
                        setArmorStand(player, player.getLocation(), "§6§lDEATH");
                    } else if (args[1].equalsIgnoreCase("check")) {
                        if (!Bedwars.getInstance().getMaps().contains(args[0])) {
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Diese Map gibt es nicht");
                            return false;
                        }
                        player.sendMessage(" ");
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "Setup Info Über die Map §8» §c" + args[0]);
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "Death high §8» " + (isExists(args[0] + ".death.Y") ? "§a✔" : "§c✗"));
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "Bronze-Spawner anzahl §8» §c" + (isExists(args[0] + ".bronze-spawner") ? cfg.getList(args[0] + ".bronze-spawner").size() : "0"));
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "Eisen-Spawner anzahl §8» §c" + (isExists(args[0] + ".eisen-spawner") ? cfg.getList(args[0] + ".eisen-spawner").size() : "0"));
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "Gold-Spawner anzahl §8» §c" + (isExists(args[0] + ".gold-spawner") ? cfg.getList(args[0] + ".gold-spawner").size() : "0"));
                        for (TeamEntry teamEntry : Bedwars.getInstance().getCacheHandler().getTeamEntries()) {
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Bed von " + teamEntry.getColorCode() + teamEntry.getName() + " §8» " + (isExists(args[0] + ".bed." + teamEntry.getName() + ".Y") ? "§a✔" : "§c✗"));
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Spawn von " + teamEntry.getColorCode() + teamEntry.getName() + " §8» " + (isExists(args[0] + ".spawn." + teamEntry.getName() + ".Y") ? "§a✔”" : "§c✗"));
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Shop von " + teamEntry.getColorCode() + teamEntry.getName() + " §8» " + (isExists(args[0] + ".shop." + teamEntry.getName() + ".Y") ? "§a✔”" : "§c✗"));
                            System.out.println(teamEntry.getName());
                        }
                        player.sendMessage(" ");
                    } else {
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "/setup (map) addmap §7§lVOTING ITEM IN HAND");
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "/setup (map) check");
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "/setup (map) setdeath");
                    }
                    break;
                case 3:
                    if (args[1].equalsIgnoreCase("setspawn")) {
                        TeamEntry teamEntry = Bedwars.getInstance().getCacheHandler().getTeamByName(args[2]);
                        if (teamEntry == null) {
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Dieses Team gibt es nicht");
                            return false;
                        }
                        if (!Bedwars.getInstance().getMaps().contains(args[0])) {
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Diese Map gibt es nicht");
                            return false;
                        }
                        createConfigLocation(player.getLocation(), args[0] + ".spawn." + teamEntry.getName());
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "Der Spawn für " + teamEntry.getColorCode() + teamEntry.getName() + " §7auf der Map §c" + args[0] + " §7wurde gesetzt");
                        setArmorStand(player, player.getLocation(), teamEntry.getColorCode() + teamEntry.getName() + " §8» §7Spawn");
                    } else if (args[1].equalsIgnoreCase("setshop")) {
                        TeamEntry teamEntry = Bedwars.getInstance().getCacheHandler().getTeamByName(args[2]);
                        if (teamEntry == null) {
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Dieses Team gibt es nicht");
                            return false;
                        }
                        if (!Bedwars.getInstance().getMaps().contains(args[0])) {
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Diese Map gibt es nicht");
                            return false;
                        }
                        createConfigLocation(player.getLocation(), args[0] + ".shop." + teamEntry.getName());
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "Der Shop für " + teamEntry.getColorCode() + teamEntry.getName() + " §7auf der Map §c" + args[0] + " §7wurde gesetzt");
                        setArmorStand(player, player.getLocation(), teamEntry.getColorCode() + teamEntry.getName() + " §8» §7Shop");
                    } else if (args[1].equalsIgnoreCase("addspawner")) {
                        if (!Bedwars.getInstance().getMaps().contains(args[0])) {
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Diese Map gibt es nicht");
                            return false;
                        }
                        if (args[2].equalsIgnoreCase("bronze")) {
                            try {
                                ArrayList list = (ArrayList) cfg.get(args[0] + ".bronze-spawner");
                                list.add(player.getLocation());
                                cfg.set(args[0] + ".bronze-spawner", list);
                            } catch (Exception e) {
                                ArrayList list = new ArrayList<Location>();
                                list.add(player.getLocation());
                                cfg.set(args[0] + ".bronze-spawner", list);
                            }
                            cfg.save(Bedwars.getInstance().getFile());
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Der Bronze-Spawner auf der Map §c" + args[0] + " §7wurde gesetzt");
                            setArmorStand(player, player.getLocation(), "§cBronze §8» §7Spawn");
                        } else if (args[2].equalsIgnoreCase("eisen")) {
                            try {
                                ArrayList list = (ArrayList) cfg.get(args[0] + ".eisen-spawner");
                                list.add(player.getLocation());
                                cfg.set(args[0] + ".eisen-spawner", list);
                            } catch (Exception e) {
                                ArrayList list = new ArrayList<Location>();
                                list.add(player.getLocation());
                                cfg.set(args[0] + ".eisen-spawner", list);
                            }
                            cfg.save(Bedwars.getInstance().getFile());
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Der Eisen-Spawner auf der Map §c" + args[0] + " §7wurde gesetzt");
                            setArmorStand(player, player.getLocation(), "§fEisen §8» §7Spawn");
                        } else if (args[2].equalsIgnoreCase("gold")) {
                            try {
                                ArrayList list = (ArrayList) cfg.get(args[0] + ".gold-spawner");
                                list.add(player.getLocation());
                                cfg.set(args[0] + ".gold-spawner", list);
                            } catch (Exception e) {
                                ArrayList list = new ArrayList<Location>();
                                list.add(player.getLocation());
                                cfg.set(args[0] + ".gold-spawner", list);
                            }
                            cfg.save(Bedwars.getInstance().getFile());
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Der Gold-Spawner auf der Map §c" + args[0] + " §7wurde gesetzt");
                            setArmorStand(player, player.getLocation(), "§6Gold §8» §7Spawn");
                        }
                    } else if (args[1].equalsIgnoreCase("setbed")) {
                        TeamEntry teamEntry = Bedwars.getInstance().getCacheHandler().getTeamByName(args[2]);
                        if (teamEntry == null) {
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Dieses Team gibt es nicht");
                            return false;
                        }
                        if (!Bedwars.getInstance().getMaps().contains(args[0])) {
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Diese Map gibt es nicht");
                            return false;
                        }

                        if (player.getLocation().getBlock().getType() == Material.BED || player.getLocation().getBlock().getType() == Material.BED_BLOCK) {
                            createConfigLocation(player.getLocation(), args[0] + ".bed." + teamEntry.getName());
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Du hast das Bett für Team " + teamEntry.getColorCode() + teamEntry.getName() + " §7auf der Map §c" + args[0] + " §7gesetzt");
                            setArmorStand(player, player.getLocation(), teamEntry.getColorCode() + teamEntry.getName() + " §8» §7Bed");
                        } else {
                            player.sendMessage(Bedwars.getInstance().getPrefix() + "Bitte stelle dich auf ein Bett");
                        }

                    } else {
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "/setup (map) setSpawn (team)");
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "/setup (map) setShop (team)");
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "/setup (map) setbed (team)");
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "/setup (map) addSpawner (bronze/eisen/gold)");
                    }
                    break;
                default:
                    sendHelp(player);
                    break;
            }
        }
        return false;
    }

    private boolean isExists(String name) {
        return (Bedwars.getInstance().getYamlConfiguration().getString(name) != null);
    }

    public void createConfigLocation(Location location, String path) {
        File file = Bedwars.getInstance().getFile();
        YamlConfiguration cfg = Bedwars.getInstance().getYamlConfiguration();
        Location loc = new Location(location.getWorld(), location.getBlockX() + 0.5D, location.getBlockY() + 0.5D, location.getBlockZ() + 0.5D, location.getYaw(), location.getPitch());
        cfg.set(path + ".World", loc.getWorld().getName());
        cfg.set(path + ".X", loc.getX());
        cfg.set(path + ".Y", loc.getY());
        cfg.set(path + ".Z", loc.getZ());
        cfg.set(path + ".Yaw", loc.getYaw());
        cfg.set(path + ".Pich", loc.getPitch());
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setArmorStand(Player player, Location loc, String name) {
        WorldServer s = ((CraftWorld) player.getWorld()).getHandle();
        EntityArmorStand stand = new EntityArmorStand(s);

        stand.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        stand.setCustomName(name);
        stand.setCustomNameVisible(true);
        stand.setGravity(false);
        stand.setSmall(true);


        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(stand);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }


    private void sendHelp(Player player) {
        player.sendMessage(" ");
        player.sendMessage(Bedwars.getInstance().getPrefix() + "/setup (map) addmap");
        player.sendMessage(Bedwars.getInstance().getPrefix() + "/setup (map) check");
        player.sendMessage(Bedwars.getInstance().getPrefix() + "/setup (map) setdeath");
        player.sendMessage(Bedwars.getInstance().getPrefix() + "/setup (map) setSpawn (team)");
        player.sendMessage(Bedwars.getInstance().getPrefix() + "/setup (map) setShop (team)");
        player.sendMessage(Bedwars.getInstance().getPrefix() + "/setup (map) addSpawner (Bronze/Eisen/Gold)");
        player.sendMessage(Bedwars.getInstance().getPrefix() + "/setup (map) setBed (team)");
        player.sendMessage(" ");
    }
}
