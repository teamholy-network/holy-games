package de.teamholy.lobby.listeners;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.npc.NPCBuilder;
import de.teamholy.lobby.Lobby;
import de.teamholy.lobby.lobbyplayer.LobbyPlayer;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;

/* copyright by Yassino */
public class PlayerJoinQuitListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)

    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (Lobby.getInstance().isPremiumLobby() && !player.hasPermission("teamholy.perk.premium")) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST,"§cshop.teamholy.de");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodLevel(20);
        player.sendTitle("","§c§kN§c §oNEW §cgamemode §a§lPlayground§c §c§kd");

        final LobbyPlayer[] lobbyPlayer = new LobbyPlayer[1];
        Bukkit.getScheduler().runTaskLater(Lobby.getInstance(),() -> {
            lobbyPlayer[0]  = new LobbyPlayer(player);
            player.teleport(BukkitHolyAPI.getInstance().getLocationManager().getLocation("lobby"));
            Lobby.getInstance().getLobbyPlayerEntryHandler().put(player.getUniqueId(), lobbyPlayer[0]);
            for (PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
            player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setLeatherColor(Color.fromBGR(
                    lobbyPlayer[0].getPlayerRank().getBlue(), lobbyPlayer[0].getPlayerRank().getGreen(), lobbyPlayer[0].getPlayerRank().getRed()
            )).build());
            player.playSound(player.getLocation(),Sound.VILLAGER_YES,50f,50f);
            lobbyPlayer[0].createNPC("§6§lMLGRush", UUID.fromString("1cff8006-9714-4231-997c-7b37a69dfff0"),BukkitHolyAPI.getInstance().getLocationManager().getLocation("mlgrush"));
            lobbyPlayer[0].createNPC("§b§lClutches", UUID.fromString("878d7127-9a83-4480-86e4-43cdde07b11a"),BukkitHolyAPI.getInstance().getLocationManager().getLocation("clutches"));
            lobbyPlayer[0].createNPC("§e§lKnockbackFFA", UUID.fromString("2552774a-1364-4407-b7e3-d2f66d93605c"),BukkitHolyAPI.getInstance().getLocationManager().getLocation("kbffa"));
            lobbyPlayer[0].createNPC("§a§lSGFFA", UUID.fromString("bef28b0b-cb18-412c-98ec-2c6b33ac3933"),BukkitHolyAPI.getInstance().getLocationManager().getLocation("sgffa"));

            new NPCBuilder("bedwars","§c§lBedwars&Rush", UUID.fromString("dfdb6c61-4060-4d8c-af20-1b21c4fc65a4"),50,10,true,true,BukkitHolyAPI.getInstance().getLocationManager().getLocation("bedwars")).build(player);

            new NPCBuilder("bedwars_spawn","§c§lBedwars", UUID.fromString("6d40f495-d796-4244-9f45-964cdd7e685a"),50,10,true,true,BukkitHolyAPI.getInstance().getLocationManager().getLocation("bw_spawn_bw")).build(player);
            new NPCBuilder("rush_spawn","§c§lRushBW", UUID.fromString("7414ffe4-6355-4877-8103-1ff6e0432e61"),50,10,true,true,BukkitHolyAPI.getInstance().getLocationManager().getLocation("bw_spawn_rbw")).build(player);

            new NPCBuilder("namemc","§f§lNameMC", UUID.fromString("982239a5-582a-483f-ac1f-2289b8dccf20"),50,10,true,false,BukkitHolyAPI.getInstance().getLocationManager().getLocation("namemc_npc")).addHolo("Vote on","§6https://teamholy.de/vote","To receive §eCoins!").build(player);
            new NPCBuilder("bw_spec","§6§lSpectate", UUID.fromString("cac3bd6a-b55f-4bfd-8c2b-537dc06375a6"),50,10,true,false,BukkitHolyAPI.getInstance().getLocationManager().getLocation("bw_spawn_spec")).build(player);




            player.teleport(BukkitHolyAPI.getInstance().getLocationManager().getLocation("lobby"));
        },1);
        Bukkit.getScheduler().runTaskLater(Lobby.getInstance(),() -> new NPCBuilder("labymod","§6§lWebsite", UUID.fromString("eecc3c44-eaaf-48fe-af23-3af762578446"),50,10,true,false,BukkitHolyAPI.getInstance().getLocationManager().getLocation("labymod_npc"))
                .addHolo("§6Link §7yourself with the website","§7to get §e500 coins").build(player),10);
    }



    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        LobbyPlayer lobbyPlayer = Lobby.getInstance().getLobbyPlayerEntryHandler().remove(player.getUniqueId());
    }

}
