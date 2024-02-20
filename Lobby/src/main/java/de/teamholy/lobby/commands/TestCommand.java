package de.teamholy.lobby.commands;

import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import de.teamholy.lobby.Lobby;
import net.minecraft.server.v1_8_R3.EntityItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("*")) {
            sender.sendMessage("§cYou don't have the permission to execute this command.");
            return true;
        }

        sender.sendMessage(Lobby.getInstance().getCloudCacheHandler().getServerInfos().size() + " servers");
        for (Gamemodes gamemodes : Gamemodes.values()) {
            for (StatsType value : StatsType.values()) {
                sender.sendMessage(Lobby.getInstance().getLeaderboardInventory().topEntries.get(gamemodes).get(value).size() + " " + value.name() + " - " + gamemodes.name());
            }
        }
        sender.sendMessage(Lobby.getInstance().getBedwarsSpectateInventory().getGameHashMap().size() + " games spec");
        sender.sendMessage(Lobby.getInstance().getLobbyPlayerEntryHandler().size() + " players");
        sender.sendMessage(Lobby.getInstance().getHologramHandler().getHolograms().size() + " holograms");
        return true;
    }


}
