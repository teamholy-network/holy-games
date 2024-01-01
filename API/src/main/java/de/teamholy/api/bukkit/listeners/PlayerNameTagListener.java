package de.teamholy.api.bukkit.listeners;

import de.teamholy.api.BukkitHolyAPI;
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

        if (!BukkitHolyAPI.getInstance().isTabPrefix()) return;

        Player player = event.getPlayer();
        PlayerRank playerRank = BukkitHolyAPI.getInstance().getBukkitCacheHandler().getHolyPlayerHashMap().get(player.getUniqueId());

        if (playerRank == null) return;

        // Get default values from HolyPlayer
        int sortId = playerRank.getSortId();
        String prefix = playerRank.getTabPrefix();
        String suffix = "";

        // Get PlayerProfiles
        Clan clan = BukkitHolyAPI.getInstance().getBukkitCacheHandler().getClanPlayerHashMap().get(player.getUniqueId());

        // Check and add clan-tag as suffix if exists
        if (clan != null) {
            suffix = " §8[" + clan.getColor() + clan.getTag() + "§8]";
        }

        // Fake PLAYER rank if we got a nicked player
        if (MarkupAPI.isNicked(player)) {
            sortId = PlayerRank.PLAYER.getSortId();
            prefix = PlayerRank.PLAYER.getTabPrefix();
            suffix = "";
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
