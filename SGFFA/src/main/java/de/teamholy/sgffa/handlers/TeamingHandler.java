package de.teamholy.sgffa.handlers;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.sgffa.SGFFA;
import de.teamholy.sgffa.models.PlayerEntry;
import de.teamholy.sgffa.models.TeamEntry;
import eu.koboo.markup.MarkupAPI;

import java.util.Locale;

/* copyright by Yassino */
public class TeamingHandler {

    private String prefix = "§bTeaming §8× §7";


    private SGFFA instance;

    public TeamingHandler(SGFFA instance) {
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
        SGFFA.getInstance().getCacheHandler().getTeamEntryHashMap().put(tag.toLowerCase(Locale.ROOT),teamEntry);
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
            SGFFA.getInstance().getCacheHandler().getTeamEntryHashMap().remove(teamEntry.getTag().toLowerCase(Locale.ROOT));
        } else {
            playerEntry.setTeamEntry(null);
            playerEntry.updateTeamScore();
            teamEntry.getPlayerEntries().remove(playerEntry);
            MarkupAPI.updateNameTag(playerEntry.getPlayer());
            sendMessageToTeam(teamEntry,prefix + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(playerEntry.getPlayer().getUniqueId()) + playerEntry.getPlayer().getName() + " §cleft the team");
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
