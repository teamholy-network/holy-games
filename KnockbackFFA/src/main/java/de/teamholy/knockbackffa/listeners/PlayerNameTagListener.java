package de.teamholy.knockbackffa.listeners;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.api.utility.TrophieLeague;
import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.models.PlayerEntry;
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
        PlayerRank playerRank = BukkitHolyAPI.getInstance().getBukkitCacheHandler().getHolyPlayerHashMap().get(player.getUniqueId());
        PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(player.getUniqueId());

        if (playerRank == null) return;

        GameProfile gameProfile = playerEntry.getGameProfileCache().get(player.getUniqueId());


        // Get default values from HolyPlayer
        int sortId = playerRank.getSortId();
        int playersElo = (int) gameProfile.getStat(Gamemodes.KNOCKBACKFFA.toString(), StatsType.ALLTIME, "trophies");
        String prefix = playerRank.getTabPrefix();
        StringBuilder suffixBuilder = new StringBuilder();
        StringBuilder displaySuffixBuilder = new StringBuilder();


        if (MarkupAPI.isNicked(player)) {
            sortId = PlayerRank.PLAYER.getSortId();
            prefix = PlayerRank.PLAYER.getTabPrefix();

            suffixBuilder.append(" §8[").append("§7N").append("§8]"); // Did this so we hide the real elo from players
        } else if (player.getName().equalsIgnoreCase("Koboo")) {
            prefix = "§8[§5Koboo§8] §7";
        } else {
            ClanPlayerProfile clanPlayerProfile = BukkitCore.getAPI().getClanPlayerService().getRedisCache().get(player.getUniqueId());
            if (playerEntry.getTeamEntry() != null) {
                suffixBuilder.append(" §7§o").append(playerEntry.getTeamEntry().getTag());
                displaySuffixBuilder.append(" §7§o").append(playerEntry.getTeamEntry().getTag());
            } else if (clanPlayerProfile != null) {
                Clan clan = BukkitCore.getAPI().getClanManager().getClanById(clanPlayerProfile.getClanId());
                if (clan != null) {
                    suffixBuilder.append(" §8[").append(clan.getColor()).append(clan.getTag()).append("§8]");
                }
            }

            suffixBuilder.append(" §8[").append(TrophieLeague.getEloRank(playersElo).getShortName()).append("§8]");
        }


        event.setSortId(sortId);
        event.setPrefix(prefix);
        event.setSuffix(suffixBuilder.toString());
        event.setDisplaySuffix(displaySuffixBuilder.toString());
    }


}
