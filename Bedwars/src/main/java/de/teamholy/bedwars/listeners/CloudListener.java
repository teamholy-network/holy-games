package de.teamholy.bedwars.listeners;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.enums.GameState;
import de.teamholy.bedwars.model.PlayerEntry;
import de.teamholy.bedwars.model.TeamEntry;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.bukkit.event.CloudChannelListenEvent;
import eu.koboo.markup.MarkupAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/* copyright by Yassino */
public class CloudListener implements Listener {
    @EventHandler
    public void onSubChannel(CloudChannelListenEvent event) {
        if (event.getChannel().equalsIgnoreCase(Wrapper.getInstance().getServiceConfiguration().getGroups()[0])) {
            if (event.getMessage().equalsIgnoreCase("autoteam")) {
                Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(),() -> {
                    if (Bedwars.getInstance().getGameState() != GameState.LOBBY) return;
                    ArrayList<String> arrayList = new ArrayList<String>(event.getData().get("players", List.class));
                    ArrayList<PlayerEntry> playerEntries = new ArrayList<>();

                    for (String stringUUid : arrayList) {
                        UUID uuid1 = UUID.fromString(stringUUid);
                        PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(uuid1);
                        if (playerEntry != null) {
                            playerEntries.add(playerEntry);
                        }
                    }

                    for (TeamEntry teamEntry : Bedwars.getInstance().getCacheHandler().getTeamEntries()) {
                        if (teamEntry.getPlayers().isEmpty() && !(arrayList.size() > teamEntry.getSize())) {
                            playerEntries.forEach(playerEntry -> {
                                playerEntry.setTeamManuell(teamEntry);
                                playerEntry.getPlayer().sendMessage(Bedwars.getInstance().getPrefix() + "The party was sorted automatically in team " + teamEntry.getColorCode() + teamEntry.getName());
                                playerEntry.getPlayer().sendTitle("§5Party" , "§7Team §8» " + playerEntry.getTeamEntry().getColorCode() + playerEntry.getTeamEntry().getName());
                            });
                            return;
                        }
                    }

                    playerEntries.forEach(playerEntry ->{
                        playerEntry.getPlayer().sendMessage(Bedwars.getInstance().getPrefix() + "Could not find a Team for your party");
                        playerEntry.getPlayer().sendTitle("§5Party" , "§cNo team");
                    });



                },5);
            }
        } else if (event.getChannel().equalsIgnoreCase("bukkit")) {
            if (event.getMessage().equalsIgnoreCase("clan_update")) {
                if (Bedwars.getInstance().getGameState() != GameState.LOBBY) return;
                UUID uuid = UUID.fromString(event.getData().getString("uuid"));
                Player player = Bukkit.getPlayer(uuid);
                if(player != null && player.isOnline()) {
                    MarkupAPI.updateNameTag(player);
                }
            }
        }
    }
}
