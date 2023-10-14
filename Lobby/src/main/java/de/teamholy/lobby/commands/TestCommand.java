package de.teamholy.lobby.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("*")) {
            sender.sendMessage("§cYou don't have the permission to execute this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /test <speed>");
            return true;
        }

        String args1 = args[1];

        if (args1.equalsIgnoreCase("licht")) {
            World world = sender.getServer().getWorlds().get(0);
            Player player = (Player) sender;
            world.strikeLightning(player.getLocation());
            return true;
        }





        int speed;
        try {
            speed = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid speed. I need a number");
            return true;
        }

        World world = sender.getServer().getWorlds().get(0);
        world.setStorm(true);
        world.setThundering(false);
        world.setTime(13000);
        return true;
    }

    private void transe(World world, int speed) {
        new BukkitRunnable() {
            int time = 0;
            final int targetTime = 13000;

            @Override
            public void run() {
                if (time >= targetTime) {
                    this.cancel();
                    return;
                }
                world.setTime(time);
                time += speed;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("Lobby"), 0L, 10L);
    }
}
