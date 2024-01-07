package de.teamholy.api.bukkit.listeners;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.interfaces.ILabyMod;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.api.entities.clanplayer.ClanPlayerProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bukkit.BukkitCore;
import eu.koboo.markup.MarkupAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/* copyright by Yassino */
public class PlayerJoinListener implements Listener, ILabyMod {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(null);

        PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService().getRedisCache().get(player.getUniqueId());
        ClanPlayerProfile clanPlayerProfile = BukkitCore.getAPI().getClanPlayerService().getRedisCache().get(player.getUniqueId());
        BukkitHolyAPI.getInstance().getBukkitCacheHandler().getHolyPlayerHashMap().put(player.getUniqueId(),PlayerRank.valueOf(playerProfile.getRank()));

        if (clanPlayerProfile != null) {
            BukkitCore.getAPI().getExecutor().execute(() -> BukkitHolyAPI
                    .getInstance()
                    .getBukkitCacheHandler()
                    .getClanPlayerHashMap()
                    .put(player.getUniqueId(),BukkitCore.getAPI().getClanManager().getClanById(clanPlayerProfile.getClanId())));

        }


        /*
        Labymod
         */
        setMiddleClickActions(player);
        sendServerBanner(player,"https://media.discordapp.net/attachments/1071527216845963404/1071530957850562640/teamHoly_Banner.png");
        updateBalanceDisplay(player,EnumBalanceType.CASH,true, (int) playerProfile.getCoins());
        sendDiscordHook(player);
        sendCurrentPlayingGameMode(player, true, "§6§lTeamHoly §8» §a" + Wrapper.getInstance().getServiceId().getName());



        if (!BukkitCore.getInstance().getGroup().toLowerCase().contains("lobby")) {
            if (playerProfile.isAutoNick()) {
                Bukkit.getScheduler().runTaskLater(BukkitHolyAPI.getInstance(),() -> player.chat("/nick"),1);
            } else {
                Bukkit.getScheduler().runTaskLater(BukkitHolyAPI.getInstance(),() -> MarkupAPI.updateNameTag(player),7);
            }
        } else {
            Bukkit.getScheduler().runTaskLater(BukkitHolyAPI.getInstance(),() -> MarkupAPI.updateNameTag(player),7);
        }

    }

    private void setMiddleClickActions( Player player ) {
        JsonArray array = new JsonArray();

        JsonObject entry;
        entry = new JsonObject();
        entry.addProperty( "displayName", "§6§lTEAM§f§lHOLY §finvite to §5party" );
        entry.addProperty( "type", EnumActionType.RUN_COMMAND.name() );
        entry.addProperty( "value", "party invite {name}" );
        array.add(entry);

        entry = new JsonObject();
        entry.addProperty( "displayName", "§6§lTEAM§f§lHOLY §4report §fplayer" );
        entry.addProperty( "type", EnumActionType.SUGGEST_COMMAND.name() );
        entry.addProperty( "value", "report {name} ID" );
        array.add(entry);

        entry = new JsonObject();
        entry.addProperty( "displayName", "§6§lTEAM§f§lHOLY §fadd as §6friend" );
        entry.addProperty( "type", EnumActionType.RUN_COMMAND.name() );
        entry.addProperty( "value", "friend add {name}" );
        array.add(entry);

        entry = new JsonObject();
        entry.addProperty( "displayName", "§6§lTEAM§f§lHOLY §fshow §cstats" );
        entry.addProperty( "type", EnumActionType.RUN_COMMAND.name() );
        entry.addProperty( "value", "stats {name}" );
        array.add(entry);


        entry = new JsonObject();
        entry.addProperty( "displayName", "§6§lTEAM§f§lHOLY §fshow §cstats §fon §6website" );
        entry.addProperty( "type", EnumActionType.OPEN_BROWSER.name() );
        entry.addProperty( "value", "https://teamholy.de/stats/?player={name}" );
        array.add(entry);

        ILabyMod.sendLMCMessage( player, "user_menu_actions", array );
    }

    public void sendServerBanner(Player player, String imageUrl) {
        JsonObject object = new JsonObject();
        object.addProperty("url", imageUrl); // Url of the image
        ILabyMod.sendLMCMessage(player, "server_banner", object);
    }

    public static void updateBalanceDisplay( Player player, EnumBalanceType type, boolean visible, int balance ) {
        JsonObject economyObject = new JsonObject();
        JsonObject cashObject = new JsonObject();

        cashObject.addProperty( "visible", visible );

        cashObject.addProperty( "balance", balance );

        cashObject.addProperty( "icon", "https://i.imgur.com/VwOPXTe.png" );



        economyObject.add(type.getKey(), cashObject);

        ILabyMod.sendLMCMessage( player, "economy", economyObject );
    }

    public enum EnumBalanceType {
        CASH("cash"),
        BANK("bank");

        private final String key;

        EnumBalanceType(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }


    enum EnumActionType {
        NONE,
        CLIPBOARD,
        RUN_COMMAND,
        SUGGEST_COMMAND,
        OPEN_BROWSER
    }


    private void sendDiscordHook(Player player) {
        JsonObject obj = new JsonObject();
        obj.addProperty("hasGame", "true");
        obj.addProperty("game_mode", Wrapper.getInstance().getServiceId().getName());
        obj.addProperty("game_startTime", System.currentTimeMillis());
        obj.addProperty("game_endTime", 0L);
        ILabyMod.sendLMCMessage(player, "discord_rpc", obj);
    }

}
