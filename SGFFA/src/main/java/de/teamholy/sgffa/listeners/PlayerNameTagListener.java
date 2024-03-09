package de.teamholy.sgffa.listeners;

import de.teamholy.core.bukkit.manager.PlayerCacheManager;
import de.teamholy.sgffa.SGFFA;
import de.teamholy.sgffa.models.PlayerEntry;
import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.entities.clanplayer.ClanPlayerProfile;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bukkit.BukkitCore;
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

        PlayerEntry playerEntry = SGFFA.getInstance().getCacheHandler().getPlayerEntryHashMap().get(player.getUniqueId());


        // Get default values from HolyPlayer
        int sortId = playerRank.getSortId();
        String prefix = playerRank.getTabPrefix();
        String suffix = "";

        // Get PlayerProfiles
        ClanPlayerProfile clanPlayerProfile = BukkitCore.getAPI().getClanPlayerService().getRedisCache().get(player.getUniqueId());

        // Check and add clan-tag as suffix if exists
        if (clanPlayerProfile != null) {
            Clan clan = BukkitCore.getAPI().getClanManager().getClanById(clanPlayerProfile.getClanId());
            if (clan != null) {
                suffix = " §8[" + clan.getColor() + clan.getTag() + "§8]";
            }
        }

        // Fake PLAYER rank if we got a nicked player
        if (MarkupAPI.isNicked(player)) {
            sortId = PlayerRank.PLAYER.getSortId();
            prefix = PlayerRank.PLAYER.getTabPrefix();
            suffix = "";
        }
        if (playerEntry != null && playerEntry.getTeamEntry() != null) {
            suffix = suffix + " §7§o" + playerEntry.getTeamEntry().getTag();
            event.setDisplaySuffix(" §7§o" + playerEntry.getTeamEntry().getTag());
        }


        if(player.getName().equalsIgnoreCase("Koboo")) {
            prefix = "§8[§5Koboo§8] §7";
        }

        // Set the values into the event
        event.setSortId(sortId);
        event.setPrefix(prefix);
        event.setSuffix(suffix);
    }
}
