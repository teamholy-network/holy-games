package de.teamholy.knockbackffa.commands;

import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.handlers.TeamingHandler;
import de.teamholy.knockbackffa.models.PlayerEntry;
import de.teamholy.knockbackffa.models.TeamEntry;
import eu.koboo.markup.MarkupAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

/* copyright by Yassino */
public class TeamingCommand implements CommandExecutor {

    private String prefix = "§bTeaming §8× §7";
    private final TeamingHandler teamingHandler = KnockbackFFA.getInstance().getTeamingHandler();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(player.getUniqueId());

        if (args.length > 1 && args[0] != null && args[0].equalsIgnoreCase("chat")) {

            if (!teamingHandler.isInTeam(playerEntry)) {
                player.sendMessage(prefix + "You are not in a team!");
                return false;
            }

            StringBuilder sb = new StringBuilder();
            for (int amount = 1; amount < args.length; amount++) {
                sb.append(args[amount]).append(" ");
            }

            TeamEntry teamEntry = playerEntry.getTeamEntry();
            teamingHandler.sendMessageToTeam(teamEntry,prefix + BukkitCore.getInstance().getPlayerColor(player.getUniqueId(), true) + player.getName() + " §8» §7" + sb);
        } else if (args.length == 0) {
            sendHelp(player);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("leave")) {
                if (!teamingHandler.isInTeam(playerEntry)) {
                    player.sendMessage(prefix + "You are not in a team!");
                    return false;
                }

                teamingHandler.leaveFromTeam(playerEntry);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                if (teamingHandler.isInTeam(playerEntry)) {
                    player.sendMessage(prefix + "You are already in a team!");
                    return false;
                }

                String tag = args[1];
                if (tag.length() > 5 || tag.length() < 2) {
                    player.sendMessage(prefix + "not allowed to be longer than 5 and shorter than 2");
                    return false;
                }

                if (KnockbackFFA.getInstance().getCacheHandler().getTeamEntryHashMap().containsKey(tag.toLowerCase(Locale.ROOT))) {
                    player.sendMessage(prefix + "§cThis team already exists");
                    return false;
                }

                teamingHandler.createTeam(tag,playerEntry);
            } else if (args[0].equalsIgnoreCase("invite")) {
                if (!teamingHandler.isInTeam(playerEntry)) {
                    player.sendMessage(prefix + "You are not in a team!");
                    return false;
                }

                if (!teamingHandler.isLeader(playerEntry)) {
                    player.sendMessage(prefix + "You are not the team leader!");
                    return false;
                }

                PlayerEntry targetEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(BukkitCore.getAPI().getUuidManager().getUUID(args[1]));
                if (targetEntry == null) {
                    player.sendMessage(prefix + "The player is not online!");
                    return false;
                }

                if (MarkupAPI.isNicked(targetEntry.getPlayer())) {
                    player.sendMessage(prefix + "The player is not online!");
                    return false;
                }

                if (teamingHandler.isLeader(targetEntry)) {
                    player.sendMessage(prefix + "The player is already in a team!");
                    return false;
                }

                if (playerEntry.getTeamEntry().getInvites().contains(targetEntry)) {
                    player.sendMessage(prefix + "The player was already invited!");
                    return false;
                }

                if (playerEntry.getTeamEntry().getPlayerEntries().size() == 3) {
                    player.sendMessage(prefix + "The team is full!");
                    return false;
                }

                playerEntry.getTeamEntry().getInvites().add(targetEntry);
                player.sendMessage(prefix + "You invited " + BukkitCore.getInstance().getPlayerColor(targetEntry.getPlayer().getUniqueId(), true) + targetEntry.getPlayer().getName() + " §7into your team!");
                targetEntry.getPlayer().sendMessage(prefix + "You were invited by the team §b" + playerEntry.getTeamEntry().getTag() + " §7(§b/teaming§7)");
            } else if (args[0].equalsIgnoreCase("kick")) {
                if (!teamingHandler.isInTeam(playerEntry)) {
                    player.sendMessage(prefix + "You are not in a team!");
                    return false;
                }

                if (!teamingHandler.isLeader(playerEntry)) {
                    player.sendMessage(prefix + "You are not the team leader!");
                    return false;
                }

                PlayerEntry targetEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(BukkitCore.getAPI().getUuidManager().getUUID(args[1]));
                if (targetEntry == null) {
                    player.sendMessage(prefix + "The player is not online!");
                    return false;
                }

                if (targetEntry.getTeamEntry() == null || targetEntry.getTeamEntry() != playerEntry.getTeamEntry()) {
                    player.sendMessage(prefix + "The player is not in your team!");
                    return false;
                }

                teamingHandler.kickFromTeam(targetEntry);
            } else if (args[0].equalsIgnoreCase("accept")) {
                if (teamingHandler.isInTeam(playerEntry)) {
                    player.sendMessage(prefix + "You are already in a team!");
                    return false;
                }

                TeamEntry teamEntry = KnockbackFFA.getInstance().getCacheHandler().getTeamEntryHashMap().get(args[1].toLowerCase(Locale.ROOT));
                if (teamEntry == null) {
                    player.sendMessage(prefix + "This team does not exist!");
                    return false;
                }

                if (!teamEntry.getInvites().contains(playerEntry)) {
                    player.sendMessage(prefix + "You were not invited into the team!");
                    return false;
                }

                if (teamEntry.getPlayerEntries().size() == 3) {
                    player.sendMessage(prefix + "The team is already full");
                    return false;
                }

                teamingHandler.joinTeam(playerEntry,teamEntry);

            } else if (args[0].equalsIgnoreCase("deny")) {
                TeamEntry teamEntry = KnockbackFFA.getInstance().getCacheHandler().getTeamEntryHashMap().get(args[1].toLowerCase(Locale.ROOT));
                if (teamEntry == null) {
                    player.sendMessage(prefix + "This team does not exist!");
                    return false;
                }

                if (!teamEntry.getInvites().contains(playerEntry)) {
                    player.sendMessage(prefix + "You were not invited into the team!");
                    return false;
                }

                if (teamEntry.getPlayerEntries().size() == 3) {
                    player.sendMessage(prefix + "The team is already full");
                    return false;
                }

                teamEntry.getInvites().remove(playerEntry);
                teamingHandler.sendMessageToTeam(teamEntry,prefix + "§c" + playerEntry.getPlayer().getName() + " has declined the team invite!");
            }
        }
        return false;
    }

    private void sendHelp(Player player) {
        player.sendMessage(prefix + "§cYou can only team upto 3 players!");
        player.sendMessage(prefix + "/teaming create (tag)");
        player.sendMessage(prefix + "/teaming invite (player)");
        player.sendMessage(prefix + "/teaming accept (tag)");
        player.sendMessage(prefix + "/teaming deny (tag)");
        player.sendMessage(prefix + "/teaming kick (player)");
        player.sendMessage(prefix + "/teaming chat (message)");
        player.sendMessage(prefix + "/teaming leave");
    }
}
