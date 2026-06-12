package de.teamholy.bedwars.listeners;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.enums.GameState;
import de.teamholy.bedwars.model.PlayerEntry;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.manager.PlayerCacheManager;
import de.teamholy.core.bukkit.perks.model.Perk;
import eu.koboo.markup.MarkupAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/* copyright by Yassino */
public class PlayerChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(event.getPlayer().getUniqueId());
        // Vorher NPE bei fehlendem Cache-Eintrag (vom Event-Bus geschluckt, Handler brach ab) — gleiches Ergebnis per Early-Return
        if (playerEntry == null) return;
        Player player = playerEntry.getPlayer();
        String message = event.getMessage().replace("%","%%");
        if (Bedwars.getInstance().getGameState() != GameState.INGAME) {

            if(MarkupAPI.isNicked(player)) {
                event.setFormat(PlayerRank.PLAYER.getChatPrefix() + player.getDisplayName() + " §8» §7" +  message);
                return;
            }

            PlayerCacheManager.CachedBukkitPlayer cachedBukkitPlayer = BukkitCore.getInstance().getPlayerCacheManager().getCachedPlayers().get(player.getUniqueId());
            PlayerRank playerRank = cachedBukkitPlayer.getRank();
            Perk perk = BukkitCore.getInstance().getPerkManager().getPerkHashMap().get(cachedBukkitPlayer.getPerkPlayerProfile().getChatPerk());
            if (perk == null) {
                event.setCancelled(true);
                return;
            }

            String[] color = perk.getName().split("-");
            event.setFormat(playerRank.getChatPrefix() + event.getPlayer().getName() + " §8» §" + color[0] + message);
        } else {
            event.setCancelled(true);
            if (Bedwars.getInstance().getIngamePlayers().contains(playerEntry)) {
                String[] split = Bedwars.getInstance().getMode().split("x");
                if (split[1].equals("1")) {
                    Bukkit.getOnlinePlayers().forEach(player1 -> player1.sendMessage("§f§lGLOBAL " + playerEntry.getTeamEntry().getColorCode() + player.getDisplayName() + " §8» §7" + message));
                } else {
                    if (message.startsWith("@a") || message.startsWith("@all")) {
                        String[] messages = message.split(" ");
                        if (messages.length == 1) {
                            event.setCancelled(true);
                        } else {
                            Bukkit.getOnlinePlayers().forEach(player1 -> player1.sendMessage("§f§lGLOBAL "+playerEntry.getTeamEntry().getColorCode() + player.getDisplayName() + " §8»§7" + message.replace(messages[0],"")));
                        }
                    } else {
                        for (Player teamPlayers : playerEntry.getTeamEntry().getPlayers()) {
                            teamPlayers.sendMessage("§f§lTEAM "+playerEntry.getTeamEntry().getColorCode() + player.getDisplayName() + " §8» §7" + message);
                        }
                    }
                }
            } else {
                player.sendMessage(Bedwars.getInstance().getPrefix() + "You cannot chat as spectator!");
            }
        }
    }

}
