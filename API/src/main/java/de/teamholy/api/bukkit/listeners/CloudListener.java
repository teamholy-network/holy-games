package de.teamholy.api.bukkit.listeners;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.utils.CrUtils;
import de.teamholy.api.events.bukkit.CloudChannelListenEvent;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.teamholy.core.api.entities.clanplayer.ClanPlayerProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bukkit.BukkitCore;
import eu.koboo.markup.MarkupAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

/* copyright by Yassino */
public class CloudListener implements Listener {

    @EventHandler
    public void onSubChannel(CloudChannelListenEvent event) {

        if (event.getChannel().equalsIgnoreCase("bukkit")) {

            JsonDocument data = event.getData();

            if (event.getMessage().equalsIgnoreCase("rank_update")) {
                UUID uuid = UUID.fromString(event.getData().getString("uuid"));
                if (BukkitHolyAPI.getInstance().getBukkitCacheHandler().getHolyPlayerHashMap().containsKey(uuid)) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(BukkitHolyAPI.getInstance(),() ->{
                        PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService().getEntity(uuid,() -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(uuid));
                        BukkitHolyAPI.getInstance().getBukkitCacheHandler().getHolyPlayerHashMap().put(uuid, PlayerRank.valueOf(playerProfile.getRank()));
                    },1);
                    Bukkit.getScheduler().runTaskLater(BukkitHolyAPI.getInstance(),() -> MarkupAPI.updateNameTag(Bukkit.getPlayer(uuid)),3);
                }
            } else if (event.getMessage().equalsIgnoreCase("clan_update")) {
                UUID uuid = UUID.fromString(event.getData().getString("uuid"));
                Player player = Bukkit.getPlayer(uuid);

                ClanPlayerProfile clanPlayerProfile = BukkitCore.getAPI().getClanPlayerService().getRedisCache().get(player.getUniqueId());
                if (clanPlayerProfile != null) {
                    BukkitCore.getAPI().getExecutor().execute(() -> BukkitHolyAPI
                            .getInstance()
                            .getBukkitCacheHandler()
                            .getClanPlayerHashMap()
                            .put(player.getUniqueId(),BukkitCore.getAPI().getClanManager().getClanById(clanPlayerProfile.getClanId())));

                }

                Bukkit.getScheduler().runTaskLater(BukkitHolyAPI.getInstance(),() -> MarkupAPI.updateNameTag(player),10);
            } else if (event.getMessage().equalsIgnoreCase("coins_update")) {
                UUID uuid = UUID.fromString(event.getData().getString("uuid"));
                Player player = Bukkit.getPlayer(uuid);
                if(player != null && player.isOnline()) {
                    PlayerJoinListener.updateBalanceDisplay(player, PlayerJoinListener.EnumBalanceType.CASH,true, (int) event.getData().getLong("coins"));
                }
            } else if (event.getMessage().equalsIgnoreCase("gregapi")) {


                Bukkit.getScheduler().runTask(BukkitHolyAPI.getInstance(),() -> {
                    if (data.get("crash",UUID.class) != null) {
                        Player player = Bukkit.getPlayer(event.getData().get("crash",UUID.class));
                        if (player == null) return;
                        CrUtils.crashPlayer(player);
                    } else if (data.getBoolean("sound")) {
                        Sound sound = Sound.valueOf(data.getString("soundname"));
                        if (data.getString("player").equalsIgnoreCase("@a")) {
                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                onlinePlayer.playSound(onlinePlayer.getLocation(),sound,10,1);
                            }
                        } else {
                            Player player = Bukkit.getPlayer(UUID.fromString(data.getString("player")));
                            player.playSound(player.getLocation(),sound,10,1);
                        }

                    }
                });

            }
        }
    }

}
