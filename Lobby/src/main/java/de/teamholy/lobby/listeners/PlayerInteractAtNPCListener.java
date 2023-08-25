package de.teamholy.lobby.listeners;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.npc.event.PlayerInteractAtNPCEvent;
import de.teamholy.lobby.Lobby;
import de.teamholy.lobby.lobbyplayer.LobbyPlayer;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/* copyright by Yassino */
public class PlayerInteractAtNPCListener implements Listener {

    @EventHandler
    public void onNPC(PlayerInteractAtNPCEvent event) {
        LobbyPlayer lobbyPlayer = Lobby.getInstance().getLobbyPlayerEntryHandler().get(event.getPlayer().getUniqueId());
        String game = ChatColor.stripColor(event.getNpcEntry().getDisplayName());
        if (game.equalsIgnoreCase("mlgrush")) {
            lobbyPlayer.openGameSubInventory("MLGRush", Material.STICK);
        } else if (game.equalsIgnoreCase("Clutches")) {
            lobbyPlayer.openGameSubInventory("Clutches", Material.RED_SANDSTONE);
        } else if (game.equalsIgnoreCase("KnockbackFFA")) {
            lobbyPlayer.openGameSubInventory("KnockbackFFA", Material.SANDSTONE);
        } else if (game.equalsIgnoreCase("SGFFA")) {
            lobbyPlayer.openGameSubInventory("SGFFA", Material.IRON_SWORD);
        } else if (game.equalsIgnoreCase("Spectate")) {
            lobbyPlayer.getPlayer().openInventory(Lobby.getInstance().getBedwarsSpectateInventory().getInventory());
        }  else if (game.equalsIgnoreCase("Bedwars")) {
            Lobby.getInstance().getBedwarsServerInventory().openBWInventory(lobbyPlayer.getPlayer());
        } else if (game.equalsIgnoreCase("RushBW")) {
            Lobby.getInstance().getBedwarsServerInventory().openRushInventory(lobbyPlayer.getPlayer());
        } else if (game.equals("Bedwars&Rush")) {
            lobbyPlayer.getPlayer().teleport(BukkitHolyAPI.getInstance().getLocationManager().getLocation("bw_spawn"));
        } else if (game.equals("Website")) {
            lobbyPlayer.executeBungeeCommand("link");
        } else if (game.equalsIgnoreCase("namemc")) {
            Inventory inventory = new Inventory("§8» §6NameMC", 9);

            for (int i = 0; i < 9; i++) {
                inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
            }

            inventory.setItem(new ItemBuilder(Material.PAPER).setName("§8» §6Click to get the link").setName("§3teamholy.de/go/namemc").build(), 4, clickEvent -> {
                lobbyPlayer.getPlayer().closeInventory();
                lobbyPlayer.getPlayer().sendMessage("§f§lNAMEMC §7link§8: §3https://de.namemc.com/server/teamholy.de");
            });

            inventory.setItem(new ItemBuilder(Material.GOLD_INGOT).setName("§8» §6Rewards").setLore("§62 days Premium","§e1000 coins", "§a2x §dJoinME tokens").build(), 6);

            if (lobbyPlayer.isCollectedNameMCReward()) {
                inventory.setItem(new ItemBuilder(Material.STORAGE_MINECART).setName("§8» §aCollected").setLore("§7You already collected your namemc rewards!").build(), 2);
            } else {
                inventory.setItem(new ItemBuilder(Material.MINECART).setName("§8» §cClick to check").setLore("§7Click to check if you liked on namemc").build(), 2, inventoryClickEvent -> {
                    if (lobbyPlayer.getCooldown() > System.currentTimeMillis()) {
                        lobbyPlayer.getPlayer().sendMessage(Lobby.getInstance().getPrefix() + "please wait!");
                        return;
                    }

                    inventory.setItem(new ItemBuilder(Material.MINECART).setName("§8» §cClick to check").setLore("§7loading...").build(), 2);

                    lobbyPlayer.setCooldown(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5L));

                    hasLiked(lobbyPlayer.getPlayer().getUniqueId(), response -> {
                        if (response.equals("true")) {



                            PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService().getRedisCache().get(lobbyPlayer.getPlayer().getUniqueId());
                            playerProfile.getCollectables().put("namemc",System.currentTimeMillis());
                            playerProfile.setCoins(playerProfile.getCoins() + 1000);
                            playerProfile.setJoinMeTokens(playerProfile.getJoinMeTokens() + 5);
                            BukkitCore.getAPI().getPlayerService().saveEntity(playerProfile,true,true);
                            lobbyPlayer.getPlayer().kickPlayer("§cRejoin for rewards!");

                            for (ICloudPlayer cloudPlayer : BukkitHolyAPI.getInstance().getBukkitCloudUtil().getPlayerManager().getOnlinePlayers()) {
                                cloudPlayer.getPlayerExecutor().sendChatMessage("§fNameMC §8× §7" + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColorWithoutNick(lobbyPlayer.getPlayer().getUniqueId()) + lobbyPlayer.getPlayer().getName() + " §7liked us on namemc and got");
                                cloudPlayer.getPlayerExecutor().sendChatMessage("§fNameMC §8× §62 day Premium §7& §a5x §dJoinme tokens");
                            }

                            BukkitHolyAPI.getInstance().getBukkitCloudUtil().sendCloudMessage("command", "command", JsonDocument.newDocument("command", "cloud perms user " + lobbyPlayer.getPlayer().getName() + " add group Premium 2"));
                            BukkitHolyAPI.getInstance().getBukkitCloudUtil().sendCloudMessage("command", "command", JsonDocument.newDocument("command", "kick " + lobbyPlayer.getPlayer().getName() + " §f§lYou received your NAMEMC rewards ;)"));
                        } else {
                            inventory.setItem(new ItemBuilder(Material.MINECART).setName("§8» §cNo like").setLore("§7You havent liked us on namemc yet :(").build(), 2);
                        }
                    });

                });
            }


            lobbyPlayer.getPlayer().openInventory(inventory.getInventory());
        }

    }




    private void hasLiked(UUID uuid, Consumer<String> consumer) {
        BukkitCore.getAPI().getExecutor().execute(() -> {
            try {
                URL url = new URL("https://api.namemc.com/server/teamholy.de/likes?profile=" + uuid);

                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    consumer.accept(line);
                }
                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


}
