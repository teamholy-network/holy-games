package de.teamholy.knockbackffa.handlers;

import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.models.PlayerEntry;
import de.teamholy.knockbackffa.models.TeamEntry;
import eu.koboo.markup.MarkupAPI;

import java.util.Locale;

/* copyright by Yassino */
public class TeamingHandler {

    private String prefix = "§bTeaming §8× §7";


    private KnockbackFFA instance;

    public TeamingHandler(KnockbackFFA instance) {
        this.instance = instance;
    }

    public TeamEntry getTeamByPlayerEntry(PlayerEntry playerEntry) {
        for (TeamEntry value : instance.getCacheHandler().getTeamEntryHashMap().values()) {
            if (value.getPlayerEntries().contains(playerEntry)) {
                return value;
            }
        }
        return null;
    }

    public boolean isInTeam(PlayerEntry playerEntry) {
        if (getTeamByPlayerEntry(playerEntry) != null) return true;
        return false;
    }

    public boolean isLeader(PlayerEntry playerEntry) {
        for (TeamEntry value : instance.getCacheHandler().getTeamEntryHashMap().values()) {
            if (value.getPlayerEntries().get(0).equals(playerEntry)) {
                return true;
            }
        }
        return false;
    }

    public TeamEntry createTeam(String tag, PlayerEntry playerEntry) {
        TeamEntry teamEntry = new TeamEntry(tag);
        teamEntry.getPlayerEntries().add(playerEntry);
        playerEntry.setTeamEntry(teamEntry);
        playerEntry.updateTeamScore();
        MarkupAPI.updateNameTag(playerEntry.getPlayer());
        KnockbackFFA.getInstance().getCacheHandler().getTeamEntryHashMap().put(tag.toLowerCase(Locale.ROOT),teamEntry);
        playerEntry.getPlayer().sendMessage(prefix + "You created the team §b" + tag);
        return teamEntry;
    }

    public void leaveFromTeam(PlayerEntry playerEntry) {
        TeamEntry teamEntry = getTeamByPlayerEntry(playerEntry);
        if (teamEntry.getPlayerEntries().get(0) == playerEntry) {
            teamEntry.getPlayerEntries().forEach(playerEntry1 -> {
                playerEntry1.setTeamEntry(null);
                playerEntry1.updateTeamScore();
                MarkupAPI.updateNameTag(playerEntry.getPlayer());
                playerEntry1.getPlayer().sendMessage(prefix + "The team was deleted because the leader left");
            });
            KnockbackFFA.getInstance().getCacheHandler().getTeamEntryHashMap().remove(teamEntry.getTag().toLowerCase(Locale.ROOT));
        } else {
            playerEntry.setTeamEntry(null);
            playerEntry.updateTeamScore();
            teamEntry.getPlayerEntries().remove(playerEntry);
            MarkupAPI.updateNameTag(playerEntry.getPlayer());
            sendMessageToTeam(teamEntry,prefix + BukkitCore.getInstance().getPlayerColor(playerEntry.getPlayer().getUniqueId(), true) + playerEntry.getPlayer().getName() + " §cleft the team");
        }
    }

    public void kickFromTeam(PlayerEntry playerEntry) {
        sendMessageToTeam(playerEntry.getTeamEntry(),prefix + "§c" + playerEntry.getPlayer().getName() + " was kicked");
        playerEntry.getTeamEntry().getPlayerEntries().remove(playerEntry);
        playerEntry.setTeamEntry(null);
        playerEntry.updateTeamScore();
        MarkupAPI.updateNameTag(playerEntry.getPlayer());
    }

    public void joinTeam(PlayerEntry playerEntry, TeamEntry teamEntry) {
        teamEntry.getInvites().remove(playerEntry);
        teamEntry.getPlayerEntries().add(playerEntry);
        playerEntry.setTeamEntry(teamEntry);
        playerEntry.updateTeamScore();
        MarkupAPI.updateNameTag(playerEntry.getPlayer());
        sendMessageToTeam(teamEntry,prefix + "§a" + playerEntry.getPlayer().getName() + " joined the team");
    }

    public void sendMessageToTeam(TeamEntry teamEntry, String message) {
        teamEntry.getPlayerEntries().forEach(playerEntry -> playerEntry.getPlayer().sendMessage(message));
    }


}
