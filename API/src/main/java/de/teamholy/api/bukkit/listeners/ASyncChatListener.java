package de.teamholy.api.bukkit.listeners;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.Perk;
import eu.koboo.markup.MarkupAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/* copyright by Yassino */
public class ASyncChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (BukkitHolyAPI.getInstance().isChatPrefix()) {
            String message = event.getMessage().replace("%","%%");
            Player player = event.getPlayer();

            if(MarkupAPI.isNicked(player)) {
                event.setFormat(PlayerRank.PLAYER.getChatPrefix() + player.getDisplayName() + " §8» §7" +  message);
                return;
            }
            PlayerRank playerRank = BukkitHolyAPI.getInstance().getBukkitCacheHandler().getHolyPlayerHashMap().get(player.getUniqueId());
            Perk perk = BukkitCore.getInstance().getPerkCache().getPerkHashMap().get(BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().get(player.getUniqueId()).getChatPerk());
            if (perk == null) {
                event.setCancelled(true);
                return;
            }
            String[] color = perk.getName().split("-");
            event.setFormat(playerRank.getChatPrefix() + event.getPlayer().getName() + " §8» §" + color[0] + message);
        }
    }

}
