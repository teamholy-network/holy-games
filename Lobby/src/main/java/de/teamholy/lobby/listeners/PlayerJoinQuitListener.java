package de.teamholy.lobby.listeners;

import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.event.CachedPlayerJoinEvent;
import de.teamholy.core.bukkit.npc.NPCBuilder;
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
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

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
    public void onJoin(CachedPlayerJoinEvent event) {
        Player player = event.getCachedBukkitPlayer().getPlayer();
        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodLevel(20);
        player.sendTitle("","§c§kN§c §oNEW §cgamemode §e§lBridge§c §c§kd");

        final LobbyPlayer[] lobbyPlayer = new LobbyPlayer[1];
        Bukkit.getScheduler().runTaskLater(Lobby.getInstance(),() -> {
            lobbyPlayer[0]  = new LobbyPlayer(player, event.getCachedBukkitPlayer());
            //player.teleport();
            Lobby.getInstance().getLobbyPlayerEntryHandler().put(player.getUniqueId(), lobbyPlayer[0]);
            for (PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
            player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setLeatherColor(Color.fromBGR(
                    lobbyPlayer[0].getPlayerRank().getBlue(), lobbyPlayer[0].getPlayerRank().getGreen(), lobbyPlayer[0].getPlayerRank().getRed()
            )).build());
            player.playSound(player.getLocation(),Sound.VILLAGER_YES,50f,50f);
           // player.teleport(BukkitCore.getInstance().getLocationManager().getLocation("lobby"));
        },1);
        Bukkit.getScheduler().runTaskLater(Lobby.getInstance(),() -> {

            new NPCBuilder("mlgrush","§6§lMLGRush", UUID.fromString("03c55754-08fc-4a12-a451-e517c89a3f91"),50,10,true,true,BukkitCore.getInstance().getLocationManager().getLocation("mlgrush")).build(player);
            new NPCBuilder("clutches","§b§lClutches", UUID.fromString("1dd0cc8f-5271-4d49-b774-16dc36877017"),50,10,true,true,BukkitCore.getInstance().getLocationManager().getLocation("clutches")).build(player);
            new NPCBuilder("knockbackffa","§e§lKnockbackFFA", UUID.fromString("2646aecf-ddcc-4f3a-bedd-3b2c8d386350"),50,10,true,true,BukkitCore.getInstance().getLocationManager().getLocation("kbffa")).build(player);
            new NPCBuilder("sgffa","§a§lSGFFA", UUID.fromString("fa44c187-80dd-4171-bb5a-2e694c4c8b4f"),50,10,true,true,BukkitCore.getInstance().getLocationManager().getLocation("sgffa")).build(player);
            new NPCBuilder("bridge","§e§lBridge", UUID.fromString("d08cbbd7-5e65-43e1-ad58-6ed90a560818"),50,10,true,true,BukkitCore.getInstance().getLocationManager().getLocation("bridge")).build(player);

            new NPCBuilder("bedwars","§c§lBedwars&Rush", UUID.fromString("6d40f495-d796-4244-9f45-964cdd7e685a"),50,10,true,true,BukkitCore.getInstance().getLocationManager().getLocation("bedwars")).build(player);

            new NPCBuilder("bedwars_spawn","§c§lBedwars", UUID.fromString("fa44c187-80dd-4171-bb5a-2e694c4c8b4f"),50,10,true,true,BukkitCore.getInstance().getLocationManager().getLocation("bw_spawn_bw")).build(player);
            new NPCBuilder("rush_spawn","§c§lRushBW", UUID.fromString("7414ffe4-6355-4877-8103-1ff6e0432e61"),50,10,true,true,BukkitCore.getInstance().getLocationManager().getLocation("bw_spawn_rbw")).build(player);

            new NPCBuilder("namemc","§f§lNameMC", UUID.fromString("982239a5-582a-483f-ac1f-2289b8dccf20"),50,10,true,false,BukkitCore.getInstance().getLocationManager().getLocation("namemc_npc")).addHolo("Vote on","§6https://teamholy.de/vote","To receive §eCoins!").build(player);
            new NPCBuilder("bw_spec","§6§lSpectate", UUID.fromString("cac3bd6a-b55f-4bfd-8c2b-537dc06375a6"),50,10,true,false,BukkitCore.getInstance().getLocationManager().getLocation("bw_spawn_spec")).build(player);

            new NPCBuilder("labymod","§6§lWebsite", UUID.fromString("eecc3c44-eaaf-48fe-af23-3af762578446"),50,10,true,false,BukkitCore.getInstance().getLocationManager().getLocation("labymod_npc"))
                    .addHolo("§6Link §7yourself with the website","§7to get §e500 coins").build(player);

        },10);
    }

    @EventHandler
    public void onSpawnLoc(PlayerSpawnLocationEvent event) {
        event.setSpawnLocation(BukkitCore.getInstance().getLocationManager().getLocation("lobby"));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
       Lobby.getInstance().getLobbyPlayerEntryHandler().remove(player.getUniqueId());
    }

}
