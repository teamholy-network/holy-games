package de.teamholy.bedwars.task;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.commands.NPCShopCommand;
import de.teamholy.bedwars.enums.GameState;
import de.teamholy.bedwars.model.PlayerEntry;
import de.teamholy.bedwars.model.TeamEntry;
import de.dytanic.cloudnet.ext.bridge.bukkit.BukkitCloudNetHelper;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/* copyright by Yassino */
@Getter @Setter
public class LobbyTask {

    private int task;
    public static int count;
    private float xp = 1.0F;
    private int minPlayers = Bedwars.getInstance().getMinPlayers();

    public void sendActionBar(Player p, String nachricht) {
        CraftPlayer cp = (CraftPlayer) p;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + nachricht + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        cp.getHandle().playerConnection.sendPacket(ppoc);
    }

    public void startLobby() {
        count = 60;
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bedwars.getInstance(),() -> {
            if (Bedwars.getInstance().getMaps().size() == 0) {
                Bukkit.getOnlinePlayers().forEach(player -> sendActionBar(player,Bedwars.getInstance().getPrefix() + "No map has been set up yet /setup"));
                return;
            }
            if (Bukkit.getOnlinePlayers().size() < minPlayers) {
                count = 60;
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.setLevel(60);
                    player.setExp(xp);
                    if (minPlayers - Bukkit.getOnlinePlayers().size() == 1) {
                        sendActionBar(player,Bedwars.getInstance().getPrefix() + "There is still §6one §7player missing to start");
                    } else {
                        sendActionBar(player,Bedwars.getInstance().getPrefix() + "There is still §6" + (minPlayers - Bukkit.getOnlinePlayers().size()) + " §7players missing to start");
                    }
                });
            } else {
                if (count >= 1 && count <= 5) {
                    xp = 0.016666668F * count;
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.setLevel(count);
                        player.setExp(xp);
                        player.playSound(player.getLocation(), Sound.NOTE_BASS,3f,3f);
                    });
                } else if (count == 0){
                    startGame();
                } else {
                    xp = 0.016666668F * count;
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.setLevel(count);
                        player.setExp(xp);
                    });
                }
                count--;
            }
        },0,20);
    }

    public void startGame() {
        Bedwars.getInstance().setGameState(GameState.INGAME);
        stopLobby();
        Bedwars.getInstance().setGameStartedSince(System.currentTimeMillis());


        if (Bedwars.getInstance().getForceMap() == null) {
            Bedwars.getInstance().setMapEntry(Bedwars.getInstance().getCacheHandler().getMapEntries().get(Bedwars.getInstance().getInventoryHandler().getMapVotingInventory().getHighestValue()));
        } else {
            Bedwars.getInstance().setMapEntry(Bedwars.getInstance().getForceMap());
        }

        String map = Bedwars.getInstance().getMapEntry().getName();



        String[] shopSkinNames1 = {"iamSlowly", "2sa" , "sturmliebender" , "reatyy","derNOZE","AltsRIP","stacho"};
        ArrayList<String> shopSkinNames = new ArrayList(Arrays.asList(shopSkinNames1));

        for (TeamEntry teamEntry : Bedwars.getInstance().getCacheHandler().getTeamEntries()) {
            teamEntry.setSpawn(getConfigLocation(map + ".spawn." + teamEntry.getName()));
            teamEntry.setShop(getConfigLocation(map + ".shop." + teamEntry.getName()));
            teamEntry.setBed(getConfigLocation(map + ".bed." + teamEntry.getName()));


            if (!NPCShopCommand.NPCSHOP) {
                Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(), () -> {
                    ArmorStand armorStand = teamEntry.getShop().getWorld().spawn(teamEntry.getShop(),ArmorStand.class);
                    armorStand.setCustomName("§6§lShop");
                    armorStand.setCustomNameVisible(true);
                    armorStand.setVisible(false);
                    armorStand.setHelmet(new ItemBuilder(Material.SKULL_ITEM,1,(byte) 3).setSkullOwner(shopSkinNames.get(new Random().nextInt(shopSkinNames.size()))).build());
                    armorStand.setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE,1,(byte) 0).setLeatherColor(teamEntry.getColor()).build());
                    armorStand.setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS,1,(byte) 0).setLeatherColor(teamEntry.getColor()).build());
                    armorStand.setBoots(new ItemBuilder(Material.LEATHER_BOOTS,1,(byte) 0).setLeatherColor(teamEntry.getColor()).build());
                }, 2);
            }


        }

        for (PlayerEntry playerEntry : Bedwars.getInstance().getCacheHandler().getPlayerEntries().values()) {

            if (NPCShopCommand.NPCSHOP) {
                playerEntry.setNpcShops();
            }

            if (!playerEntry.hasTeam()) {
                playerEntry.getTeamForPlayer1();
            }

/*            ArrayList<TeamEntry> teamWithPlayers = new ArrayList<>();
            for (TeamEntry teamEntry : Bedwars.getInstance().getCacheHandler().getTeamEntries()) {
                if (!teamEntry.getPlayers().isEmpty()) {
                    teamWithPlayers.add(teamEntry);
                }
            }

            if (teamWithPlayers.size() == 1) {
                playerEntry.getPlayer().sendMessage(Bedwars.getInstance().getPrefix()+"the teams have been redistributed");
                playerEntry.removePlayerFromTeam();
                playerEntry.getTeamForPlayer2();
            }*/

            playerEntry.getPlayer().sendMessage("§8§m------------------------------");
            playerEntry.getPlayer().sendMessage(Bedwars.getInstance().getPrefix() + "§7Team §8» " + playerEntry.getTeamEntry().getColorCode() + playerEntry.getTeamEntry().getName());
            playerEntry.getPlayer().sendMessage(Bedwars.getInstance().getPrefix() + "§7Map §8» §6" + map);
            playerEntry.getPlayer().sendMessage(Bedwars.getInstance().getPrefix() + "§7Gold §8» " + ((Bedwars.getInstance().getInventoryHandler().getGoldVotingInventory().isGold()) ? "§a✔" : "§c✘"));
            playerEntry.getPlayer().sendMessage("§8§m------------------------------");
            playerEntry.clearPlayer();
            playerEntry.getPlayer().teleport(playerEntry.getTeamEntry().getSpawn());
            playerEntry.getPlayer().playSound(playerEntry.getPlayer().getLocation(),Sound.ANVIL_BREAK,50f,50f);
            playerEntry.getPlayer().sendTitle("§6§lTeamholy.de",(Bedwars.isRushMode() ? "§bRushBW" : "§6Bedwars"));
            BukkitCore.getInstance().getStatsManager().addStat(Bedwars.MODE.toString(),"played_games",playerEntry.getPlayer().getUniqueId());
            Bedwars.getInstance().getIngamePlayers().add(playerEntry);
            Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(), playerEntry::setScorebord,1);
        }

        Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(),() -> Bedwars.getInstance().updateNameTags(),1);
        Double death = Bedwars.getInstance().getYamlConfiguration().getDouble(map + ".death.Y");

        AtomicInteger i = new AtomicInteger();

        int sched = 0;
        int finalSched = sched;

        sched = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bedwars.getInstance(),() -> {
            Bedwars.getInstance().getCacheHandler().getPlayerEntries().values().forEach(holyPlayer -> {
                if (holyPlayer.getScoreboardAPI() != null) {
                    if (Bedwars.getInstance().getGameState() == GameState.INGAME) {
                        sendActionBar(holyPlayer.getPlayer(),"§7Gold §8» " + ((Bedwars.getInstance().getInventoryHandler().getGoldVotingInventory().isGold()) ? "§a✔" : "§c✘") + " §8︳ §7Map §8» §6" + Bedwars.getInstance().getMapEntry().getName());
                        holyPlayer.getPlayer().getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName("§6§lTeamholy.de §8- §7" + formatSeconds(i.get()));
                    } else {
                        holyPlayer.getPlayer().getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName("§6§lTeamholy.de");
                        Bukkit.getScheduler().cancelTask(finalSched);
                    }
                }
            });
            i.getAndIncrement();
        },0,20);

        int sched1 = 0;
        int finalSched1 = sched;

        sched1 = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bedwars.getInstance(),() ->{
            for (PlayerEntry playerEntry : Bedwars.getInstance().getCacheHandler().getPlayerEntries().values()) {
                if (Bedwars.getInstance().getIngamePlayers().contains(playerEntry)) {
                    if (playerEntry.getPlayer().getLocation().getBlockY() < death) {
                        if (Bedwars.getInstance().getGameState() == GameState.INGAME) {
                            playerEntry.getPlayer().damage(1111);
                        }
                    }
                }
            }

            if (Bedwars.getInstance().getGameState() == GameState.END) Bukkit.getScheduler().cancelTask(finalSched1);

        },1,1);

        for (TeamEntry teamEntry : Bedwars.getInstance().getCacheHandler().getTeamEntries()) {
            teamEntry.getPlayers().forEach(player -> {
                teamEntry.getAllPlayers().add(Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId()));
            });

            if (teamEntry.getPlayers().isEmpty()) {
                teamEntry.setHasBed(false);
            } else {
                teamEntry.setHasBed(true);
            }
        }

        Bedwars.getInstance().getMapEntry().startSpawner();
        Bedwars.getInstance().updateMotd();
        BukkitCloudNetHelper.setMaxPlayers(100);
        BukkitCloudNetHelper.changeToIngame(true);

    }


    public Location getConfigLocation(String path) {
            YamlConfiguration cfg = Bedwars.getInstance().getYamlConfiguration();
            World w = Bukkit.getWorld(cfg.getString(path + ".World"));
            double x = cfg.getDouble(path + ".X");
            double y = cfg.getDouble(path + ".Y");
            double z = cfg.getDouble(path + ".Z");
            float yaw = (float) cfg.getDouble(path + ".Yaw");
            float pitch = (float) cfg.getDouble(path + ".Pitch");
            return new Location(w, x, y, z, yaw, pitch);
    }

    public void stopLobby() { Bukkit.getScheduler().cancelTask(task);}

    public String formatSeconds(int seconds)
    {
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}
