package de.teamholy.knockbackffa.listeners;

import de.teamholy.core.api.utility.TrophieLeague;
import de.teamholy.core.bukkit.manager.PlayerCacheManager;
import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.models.PlayerEntry;
import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.replay.api.ReplayAPI;
import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.events.PlayerNameTagEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerNameTagListener implements Listener {


    @EventHandler
    public void onNameTag(PlayerNameTagEvent event) {

        Player player = event.getPlayer();


        PlayerCacheManager.CachedBukkitPlayer playerCache = BukkitCore.getInstance().getPlayerCacheManager().getCachedPlayers().get(player.getUniqueId());
        if (playerCache == null) return;

        PlayerRank playerRank = playerCache.getRank();

        if (playerRank == null) return;

        PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(player.getUniqueId());



        int sortId = playerRank.getSortId();
        int playerElo = 1000;
        if (playerEntry != null) playerElo = playerEntry.getAlltimeTrophies();
        String prefix = playerRank.getTabPrefix();
        StringBuilder suffixBuilder = new StringBuilder();
        StringBuilder displaySuffixBuilder = new StringBuilder();


        if (MarkupAPI.isNicked(player)) {
            sortId = PlayerRank.PLAYER.getSortId();
            prefix = PlayerRank.PLAYER.getTabPrefix();

            suffixBuilder.append(" §8[").append("§7N").append("§8]");
        } else if (player.getName().equalsIgnoreCase("Koboo")) {
            prefix = "§8[§5Koboo§8] §7";
        } else {
            Clan clan = BukkitCore.getInstance().getPlayerCacheManager().getCachedPlayers().get(player.getUniqueId()).getClan();
            if (playerEntry != null && playerEntry.getTeamEntry() != null) {
                suffixBuilder.append(" §7§o").append(playerEntry.getTeamEntry().getTag());
                displaySuffixBuilder.append(" §7§o").append(playerEntry.getTeamEntry().getTag());
            } else if (clan != null) {
                suffixBuilder.append(" §8[").append(clan.getColor()).append(clan.getTag()).append("§8]");
            }

            suffixBuilder.append(" §8[").append(TrophieLeague.getEloRank(playerElo).getShortName()).append("§8]");
        }


        ReplayAPI.getInstance().addNameTagDataToAllRecordings(player.getName(), prefix, suffixBuilder.toString(), prefix, displaySuffixBuilder.toString());
        event.setSortId(sortId);
        event.setPrefix(prefix);
        event.setSuffix(suffixBuilder.toString());
        event.setDisplaySuffix(displaySuffixBuilder.toString());
    }


}
