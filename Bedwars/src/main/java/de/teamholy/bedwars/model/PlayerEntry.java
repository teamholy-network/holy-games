package de.teamholy.bedwars.model;

import de.teamholy.api.manager.StatsManager;
import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.enums.GameState;
import de.teamholy.bedwars.enums.RushBWShopItems;
import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.npc.NPCBuilder;
import de.teamholy.api.bukkit.utils.InventoryUtils;
import de.teamholy.api.bukkit.utils.scoreboard.ScoreboardAPI;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.ext.bridge.bukkit.BukkitCloudNetHelper;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.api.utility.TrophieLeague;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import eu.koboo.markup.MarkupAPI;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/* copyright by Yassino */
@Getter
@Setter
public class PlayerEntry {

    private Player player;
    private TeamEntry teamEntry;
    private MapEntry votedMap;
    private ScoreboardAPI scoreboardAPI;

    private boolean dead;

    private Inventory shopInventory;

    private int beds = 0, kills = 0;

    private int alltimeTrophies = 1000;
    private int gameTrophies = 0;

    public PlayerEntry(Player player) {
        this.player = player;
        GameProfile statsProfile = BukkitCore.getAPI().getGameService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()));


        if (Bedwars.isRushMode()) createInv();
        if (!statsProfile.exists(Bedwars.MODE.toString())) {
            for (StatsType time : StatsType.values()) {
                for (Gamemodes.StatKey statKey : Bedwars.MODE.getStatKeys()) statsProfile.setStat(Bedwars.MODE.toString(), time, statKey.getName(), statKey.getDefaultValue());
            }

            if (Bedwars.isRushMode()) {
                statsProfile.setSetting(Gamemodes.RUSHBW.toString(), "invsort", InventoryUtils.inventoryToString(shopInventory));
            }

            BukkitCore.getAPI().getGameService().saveEntity(statsProfile,true,true);
        } else {
            alltimeTrophies = (int) statsProfile.getStat(Bedwars.MODE.toString(),StatsType.ALLTIME,"trophies");
            if (Bedwars.isRushMode()) {
                try {
                    String inventory = statsProfile.getSetting(Gamemodes.RUSHBW.toString(), "invsort");
                    if (inventory.isEmpty()) {
                        createInv();
                    } else {
                        setShopInventory(InventoryUtils.inventoryFromString(inventory));
                    }
                } catch (Exception ignored) {
                }
            }
        }


        scoreboardAPI = new ScoreboardAPI().createScoreboard(player, "§6");
        setScorebord();
    }

    public void createInv() {
        shopInventory = Bukkit.createInventory(null, 9 * 3, "inv");
        for (RushBWShopItems items : RushBWShopItems.values()) {
            shopInventory.setItem(items.getSlot(), items.getItemStack());
        }
    }


    public void updateTeamScoreboard() {
        if (teamEntry != null) {
            scoreboardAPI.updateLine(8, " §7Team§8: " + teamEntry.getColorCode() + teamEntry.getName());
        } else {
            scoreboardAPI.updateLine(8, " §7Team§8: §6-/-");
        }
    }

    public void setScorebord() {
        scoreboardAPI.clearScoreboard();
        if (Bedwars.getInstance().getGameState() == GameState.LOBBY) {
            BukkitCore.getAPI().getGameService().getEntityAsync(player.getUniqueId(),() -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()),gameProfile -> {
                scoreboardAPI.setLine(11, " §8§m--------------- ");
                scoreboardAPI.setLine(10, " §7§o" + (Bedwars.isRushMode() ? "rush bedwars" : "bedwars"));
                scoreboardAPI.setLine(9, "§6");
                scoreboardAPI.setLine(8, " §7Team§8: §6-/-");
                scoreboardAPI.setLine(7, " §7Mode§8: §6" + Bedwars.getInstance().getMode());
                scoreboardAPI.setLine(6, "§1");
                scoreboardAPI.setLine(5, " §cALLTIME");
                scoreboardAPI.setLine(4," §7Rank§8: §6#" + BukkitCore.getAPI().getRankingManager().getRankFromUUID(Bedwars.MODE,StatsType.ALLTIME,player.getUniqueId()));
                int elo = (int) gameProfile.getStat(Bedwars.MODE.toString(),StatsType.ALLTIME,"trophies");
                scoreboardAPI.setLine(3," §7Trophies§8: §6" + elo + " " + TrophieLeague.getEloRank(elo).getShortName());
                scoreboardAPI.setLine(2, "§3");
                scoreboardAPI.setLine(1, " §8§m--------------- ");
                scoreboardAPI.setLine(0, "§o" + Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName());
                Bedwars.getInstance().getServer().getScheduler().runTask(Bedwars.getInstance(),() -> scoreboardAPI.build());
            });
        } else if (Bedwars.getInstance().getGameState() == GameState.INGAME) {
            int i = 3;
            ArrayList<TeamEntry> teamEntries = new ArrayList<>();
            teamEntries.addAll(Bedwars.getInstance().getCacheHandler().getTeamEntries().stream().filter(team -> !team.isHasBed()).collect(Collectors.toList()));
            teamEntries.addAll(Bedwars.getInstance().getCacheHandler().getTeamEntries().stream().filter(TeamEntry::isHasBed).collect(Collectors.toList()));
            for (TeamEntry teamEntry : teamEntries) {
                scoreboardAPI.setLine(i, " " + (teamEntry.isHasBed() ? "§c❤ " : "§7❤ ") + teamEntry.getColorCode() + teamEntry.getName() + " §8(" + (teamEntry.getPlayers().size() == 0 ? "§c" : "§6") + teamEntry.getPlayers().size() + "§8)");
                i++;
            }
            scoreboardAPI.setLine(i + 4, " §8§m--------------- ");
            scoreboardAPI.setLine(i + 3, "§8");
            scoreboardAPI.setLine(i, "§7");
            scoreboardAPI.setLine(i + 2, " §7Beds§8: §6" + getBeds());
            scoreboardAPI.setLine(i + 1, " §7Kills§8: §6" + getKills());
            scoreboardAPI.setLine(2, "§7");
            scoreboardAPI.setLine(1, " §8§m--------------- ");
            scoreboardAPI.setLine(0, "§o" + Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName());
            scoreboardAPI.build();
        }
    }

    public void updateScoreboard() {
        int i = 3;
        ArrayList<TeamEntry> teamEntries = new ArrayList<>();
        teamEntries.addAll(Bedwars.getInstance().getCacheHandler().getTeamEntries().stream().filter(team -> !team.isHasBed()).collect(Collectors.toList()));
        teamEntries.addAll(Bedwars.getInstance().getCacheHandler().getTeamEntries().stream().filter(TeamEntry::isHasBed).collect(Collectors.toList()));
        for (TeamEntry teamEntry : teamEntries) {
            scoreboardAPI.updateLine(i, " " + (teamEntry.isHasBed() ? "§c❤ " : "§7❤ ") + teamEntry.getColorCode() + teamEntry.getName() + " §8(" + (teamEntry.getPlayers().size() == 0 ? "§c" : "§6") + teamEntry.getPlayers().size() + "§8)");
            i++;
        }

        scoreboardAPI.updateLine(i + 2, " §7Beds§8: §6" + getBeds());
        scoreboardAPI.updateLine(i + 1, " §7Kills§8: §6" + getKills());
    }

    public void performSpawn() {
        clearPlayer();
        player.teleport(BukkitHolyAPI.getInstance().getLocationManager().getLocation("lobby"));
        setLobbyItems();
    }

    public void setLobbyItems() {
        if (Bedwars.isRushMode()) {
            player.getInventory().setItem(1, new ItemBuilder(Material.DIAMOND).setName("§8» §6Perks §8(§7rightclick§8)").build());
            player.getInventory().setItem(3, new ItemBuilder(Material.BED).setName("§8» §6Team selector §8(§7rightclick§8)").build());
            player.getInventory().setItem(5, new ItemBuilder(Material.ARMOR_STAND).setName("§8» §6Sort shop inventory §8(§7rightclick§8)").build());
            player.getInventory().setItem(7, new ItemBuilder(Material.PAPER).setName("§8» §6Map voting §8(§7rightclick§8)").build());
        } else {
            player.getInventory().setItem(2, new ItemBuilder(Material.DIAMOND).setName("§8» §6Perks §8(§7rightclick§8)").build());
            player.getInventory().setItem(4, new ItemBuilder(Material.BED).setName("§8» §6Team Selector §8(§7rightclick§8)").build());
            player.getInventory().setItem(6, new ItemBuilder(Material.PAPER).setName("§8» §6Votings §8(§7rightclick§8)").build());
        }
    }

    public void removePlayerFromTeam() {
        if (teamEntry != null) {
            teamEntry.removePlayer(player);
            Bedwars.getInstance().getInventoryHandler().getTeamSelectInventory().updateInventory();
        }
    }

    public void addPlayerTeam(InventoryClickEvent event) {
        if (event.getInventory().getName().toLowerCase().contains("team")) {
            if (event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) return;
            String item = event.getCurrentItem().getItemMeta().getDisplayName().replace("» ", "");
            TeamEntry teamEntry = Bedwars.getInstance().getCacheHandler().getTeamByName(ChatColor.stripColor(item));
            if (teamEntry.getPlayers().size() == teamEntry.getSize()) {
                player.sendMessage(Bedwars.getInstance().getPrefix() + "The team is already full");
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 2f, 2f);
                return;
            }
            if (getTeamEntry() == teamEntry) {
                player.sendMessage(Bedwars.getInstance().getPrefix() + "You are already in the team!");
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 2f, 2f);
                return;
            }
            removePlayerFromTeam();
            teamEntry.addPlayer(player);
            setTeamEntry(teamEntry);
            player.sendMessage(Bedwars.getInstance().getPrefix() + "You are now in team " + teamEntry.getColorCode() + teamEntry.getName());
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 2f, 2f);
            Bedwars.getInstance().getInventoryHandler().getTeamSelectInventory().updateInventory();
            updateTeamScoreboard();
        }
    }

    public void setTeamManuell(TeamEntry teamEntry) {
        removePlayerFromTeam();
        teamEntry.addPlayer(player);
        setTeamEntry(teamEntry);
        player.sendMessage(Bedwars.getInstance().getPrefix() + "You are now in team " + teamEntry.getColorCode() + teamEntry.getName());
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 2f, 2f);
        Bedwars.getInstance().getInventoryHandler().getTeamSelectInventory().updateInventory();
        updateTeamScoreboard();
    }

    public void checkTeams(Player killer) {
        if (!Bedwars.getInstance().getIngamePlayers().contains(this)) {
            removePlayerFromTeam();
            setDead(true);
            BukkitHolyAPI.getInstance().getStatsManager().addStat(Bedwars.MODE.toString(), "deaths", player.getUniqueId());
            if (killer != null) {
                BukkitCore.getAPI().getCoinManager().addCoins(killer.getUniqueId(), 10, true);

                PlayerEntry killerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(killer.getUniqueId());


                int difference = getAlltimeTrophies() - killerEntry.getAlltimeTrophies();

                int killerTrophies = BukkitHolyAPI.getInstance().getStatsManager().handleTrophie(killer.getUniqueId(),Bedwars.MODE.toString(), StatsManager.TrophieAdjustType.PLUS,
                        TrophieLeague.calculateRange(difference,3,6));

                int playerTrophies = BukkitHolyAPI.getInstance().getStatsManager().handleTrophie(player.getUniqueId(),Bedwars.MODE.toString(), StatsManager.TrophieAdjustType.MINUS,
                        TrophieLeague.calculateRange(difference,3,6));


                killer.getPlayer().sendTitle("","§a+" + killerTrophies + " §6trophies");
                player.getPlayer().sendTitle("","§c-" + playerTrophies + " §6trophies");


                killerEntry.setKills(killerEntry.getKills() + 1);
                killerEntry.updateScoreboard();

                BukkitHolyAPI.getInstance().getStatsManager().addStat(Bedwars.MODE.toString(), "kills", killer.getUniqueId());
                killer.playSound(killer.getLocation(), Sound.LEVEL_UP, 1, 1);
            } else {
                int playerTrophies = BukkitHolyAPI.getInstance().getStatsManager().handleTrophie(player.getUniqueId(),Bedwars.MODE.toString(), StatsManager.TrophieAdjustType.MINUS, 7);

                player.getPlayer().sendTitle("","§c-" + playerTrophies + " §6trophies");
            }
            if (teamEntry.getPlayers().size() == 0) {
                teamEntry.setHasBed(false);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId());
                    playerEntry. updateScoreboard();
                    player.sendMessage(Bedwars.getInstance().getPrefix() + "The " + teamEntry.getColorCode() + teamEntry.getName() + " §7team is eliminated");
                }
            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (teamEntry.getPlayers().size() < 1) {
                        player.sendMessage(Bedwars.getInstance().getPrefix() + "one player is still alive in the " + teamEntry.getColorCode() + teamEntry.getName() + " §7team");
                    } else {
                        player.sendMessage(Bedwars.getInstance().getPrefix() + teamEntry.getPlayers().size() + " players are still alive in the " + teamEntry.getColorCode() + teamEntry.getName() + " §7team");
                    }
                }
            }
        }
        Bedwars.getInstance().updateMotd();

    }

    public void saveData() {
        GameProfile statsProfile = BukkitCore.getAPI().getGameService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()));
        statsProfile.setSetting(Gamemodes.RUSHBW.toString(), "invsort", InventoryUtils.inventoryToString(shopInventory));
        BukkitCore.getAPI().getGameService().saveEntity(statsProfile, true, true);
    }

    public void setSpectator() {
        if (Bedwars.getInstance().getIngamePlayers().contains(Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId())))
            Bedwars.getInstance().getIngamePlayers().remove(Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId()));
        if (!Bedwars.getInstance().getSpectatePlayers().contains(Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId())))
            Bedwars.getInstance().getSpectatePlayers().add(Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(player.getUniqueId()));
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.spigot().setCollidesWithEntities(false);
        player.getInventory().clear();
        player.getInventory().setItem(8, new ItemBuilder(Material.SLIME_BALL).setName("§8» §6Leave §8(§7rightclick§8)").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.PAPER).setName("§8» §6Next match §8(§7rightclick§8)").build());
        player.getInventory().setItem(0, new ItemBuilder(Material.COMPASS).setName("§8» §6Spectate §8(§7rightclick§8)").build());
        removePlayerFromTeam();
        player.teleport(BukkitHolyAPI.getInstance().getLocationManager().getLocation("lobby"));

        Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(), () -> {
            for (PotionEffect effect : player.getActivePotionEffects())
                player.removePotionEffect(effect.getType());
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 9999));
        }, 5L);

        for (PlayerEntry all : Bedwars.getInstance().getCacheHandler().getPlayerEntries().values()) {
            all.getPlayer().hidePlayer(player);
        }

        for (PlayerEntry spectator : Bedwars.getInstance().getSpectatePlayers())
            player.showPlayer(spectator.getPlayer());


        Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(), () -> MarkupAPI.updateNameTag(player), 1);
    }

    public void checkWin() {
        TeamEntry winner;
        ArrayList list = new ArrayList<TeamEntry>();
        for (TeamEntry teamEntry : Bedwars.getInstance().getCacheHandler().getTeamEntries()) {
            if (!teamEntry.getPlayers().isEmpty()) {
                list.add(teamEntry);
            }
        }
        if (list.size() == 1) {
            winner = (TeamEntry) list.get(0);
            File dir = new File("plugins/Bedwars/songs/");
            File[] files = dir.listFiles();
            Song song = NBSDecoder.parse(files[new Random().nextInt(files.length)]);
            RadioSongPlayer radioSongPlayer = new RadioSongPlayer(song);
            BukkitCloudNetHelper.setMotd("END");
            BukkitCloudNetHelper.setState("END");
            Bedwars.getInstance().setGameState(GameState.END);
            Bedwars.getInstance().updateMotd();
            Bedwars.getInstance().getMapEntry().stopSpawner();


            for (Player a : Bukkit.getOnlinePlayers()) {
                a.setHealth(20);
                a.setMaxHealth(20);
                a.setFoodLevel(20);
                a.setAllowFlight(false);
                a.setFlying(false);
                a.getInventory().setArmorContents(null);
                a.sendTitle("§7Winner §8» ", winner.getColorCode() + "Team " + winner.getName());

                for (PlayerEntry spectator : Bedwars.getInstance().getSpectatePlayers()) {
                    a.showPlayer(spectator.getPlayer());
                }

                for (PotionEffect effect : a.getActivePotionEffects()) {
                    a.removePotionEffect(effect.getType());
                }

                Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(), () -> {
                    a.getInventory().clear();
                    a.sendMessage(" ");
                    a.sendMessage(Bedwars.getInstance().getPrefix() + "The team " + winner.getColorCode() + winner.getName() + " §7has won!");
                    a.sendMessage(Bedwars.getInstance().getPrefix() + "The server is restarting in 10 seconds");
                    a.sendMessage(" ");
                    a.teleport(BukkitHolyAPI.getInstance().getLocationManager().getLocation("lobby"));
                    a.getInventory().setItem(8, new ItemBuilder(Material.SLIME_BALL).setName("§8» §6Leave §8(§7rightclick§8)").build());
                    a.getInventory().setItem(4, new ItemBuilder(Material.PAPER).setName("§8» §6Next match §8(§7rightclick§8)").build());
                    a.setFoodLevel(20);
                }, 1);

                radioSongPlayer.addPlayer(a);
            }

            winner.getPlayers().forEach(winners -> {
                BukkitCore.getAPI().getCoinManager().addCoins(winners.getUniqueId(), 50, true);
                BukkitHolyAPI.getInstance().getStatsManager().addStat(Bedwars.MODE.toString(), "won_games", winners.getUniqueId());
                BukkitHolyAPI.getInstance().getStatsManager().handleTrophie(winners.getUniqueId(), Bedwars.MODE.toString(), StatsManager.TrophieAdjustType.PLUS, 20);
                winners.sendTitle("","§a+10 §6trophies");
            });

            Bedwars.getInstance().updateNameTags();


            radioSongPlayer.setPlaying(true);
            AtomicInteger i = new AtomicInteger(10);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(all -> sendActionBar(all, Bedwars.getInstance().getPrefix() + "The server restarts in §6" + i.get() + " §7seconds!"));
                    if (i.get() == 0) {

                        Bedwars.getInstance().getCacheHandler().getPlayerEntries().forEach((uuid, playerEntry) -> {
                            playerEntry.quickJoin();
                        });

                        radioSongPlayer.setPlaying(false);
                        Bukkit.shutdown();
                        Bedwars.getInstance().bootrap();
                        cancel();
                    }
                    i.getAndDecrement();
                }
            }.runTaskTimer(Bedwars.getInstance(), 0, 20);
        }

        if (list.isEmpty()) {
            AtomicInteger i = new AtomicInteger(10);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(all -> sendActionBar(all, Bedwars.getInstance().getPrefix() + "The server restarts in §6" + i.get() + " §7seconds!"));
                    if (i.get() == 0) {

                        Bedwars.getInstance().getCacheHandler().getPlayerEntries().forEach((uuid, playerEntry) -> {
                            playerEntry.quickJoin();
                        });

                        Bukkit.shutdown();
                        Bedwars.getInstance().bootrap();
                        cancel();
                    }
                    i.getAndDecrement();
                }
            }.runTaskTimer(Bedwars.getInstance(), 0, 20);
        }


    }

    public boolean hasTeam() {
        for (TeamEntry teamEntry : Bedwars.getInstance().getCacheHandler().getTeamEntries()) {
            if (teamEntry.getPlayers().contains(player)) {
                return true;
            }
        }
        return false;
    }


    public void getTeamForPlayer1() {
        for (TeamEntry teamEntry : Bedwars.getInstance().getCacheHandler().getTeamEntries()) {
            if (!teamEntry.getPlayers().isEmpty() && teamEntry.getPlayers().size() != teamEntry.getSize()) {
                teamEntry.addPlayer(player);
                setTeamEntry(teamEntry);
                teamEntry.addPlayer(player);
                return;
            }
        }
        for (TeamEntry teamEntry : Bedwars.getInstance().getCacheHandler().getTeamEntries()) {
            if (teamEntry.getPlayers().size() < teamEntry.getSize()) {
                teamEntry.addPlayer(player);
                setTeamEntry(teamEntry);
                teamEntry.addPlayer(player);
                return;
            }
        }
    }

    public void getTeamForPlayer2() {
        for (TeamEntry teamEntry : Bedwars.getInstance().getCacheHandler().getTeamEntries()) {
            if (teamEntry.getPlayers().isEmpty()) {
                teamEntry.addPlayer(player);
                setTeamEntry(teamEntry);
                teamEntry.addPlayer(player);
                return;
            }
        }
        for (TeamEntry teamEntry : Bedwars.getInstance().getCacheHandler().getTeamEntries()) {
            if (teamEntry.getPlayers().size() < teamEntry.getSize()) {
                teamEntry.addPlayer(player);
                setTeamEntry(teamEntry);
                teamEntry.addPlayer(player);
                return;
            }
        }
    }

    public void clearPlayer() {
        player.setLevel(0);
        player.setExp(0);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
        Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(), () -> {
            for (PlayerEntry all : Bedwars.getInstance().getCacheHandler().getPlayerEntries().values()) {
                all.getPlayer().showPlayer(player);
            }
        }, 5);
        player.spigot().setCollidesWithEntities(true);
    }


    public Material getMaterial(String material) {
        if (material.toLowerCase().equals("iron")) {
            return Material.IRON_INGOT;
        } else if (material.toLowerCase().equals("bronze")) {
            return Material.CLAY_BRICK;
        } else if (material.toLowerCase().equals("gold")) {
            return Material.GOLD_INGOT;
        }
        return null;
    }


    public void sendActionBar(Player p, String nachricht) {
        CraftPlayer cp = (CraftPlayer) p;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + nachricht + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        cp.getHandle().playerConnection.sendPacket(ppoc);
    }


    public void quickJoin() {
        String group = Wrapper.getInstance().getCurrentServiceInfoSnapshot().getConfiguration().getGroups()[0];

        List<ServiceInfoSnapshot> servers = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices().stream()
                .filter(info -> info.getConfiguration().getGroups()[0].equals(group))
                .filter(serviceInfoSnapshot -> !serviceInfoSnapshot.getProperty(BridgeServiceProperty.IS_IN_GAME).get())
                .filter(serviceInfoSnapshot -> serviceInfoSnapshot.getProperty(BridgeServiceProperty.STATE).isPresent())
                .filter(serviceInfoSnapshot -> serviceInfoSnapshot.getProperty(BridgeServiceProperty.MOTD).isPresent())
                .filter(serviceInfoSnapshot -> serviceInfoSnapshot.getProperty(BridgeServiceProperty.STATE).get().equalsIgnoreCase("LOBBY"))
                .filter(serviceInfoSnapshot -> serviceInfoSnapshot.getProperty(BridgeServiceProperty.EXTRA).isPresent())
                .filter(serviceInfoSnapshot -> serviceInfoSnapshot.getProperty(BridgeServiceProperty.EXTRA).get().equalsIgnoreCase("1"))
                .filter(serviceInfoSnapshot -> !serviceInfoSnapshot.getProperty(BridgeServiceProperty.IS_FULL).get())
                .sorted(Comparator.comparingInt(info -> info.getProperty(BridgeServiceProperty.ONLINE_COUNT).get()))
                .collect(Collectors.toList());
        Collections.reverse(servers);
        try {
            ServiceInfoSnapshot service = servers.get(0);
            BukkitHolyAPI.getInstance().getBukkitCloudUtil().getPlayerManager().getPlayerExecutor(player.getUniqueId()).connect(service.getName());
        } catch (IndexOutOfBoundsException e) {
            player.kickPlayer(Bedwars.getInstance().getPrefix() + "Could not find a §c" + group + " §7server");
        }
    }

    public void setNpcShops() {
        BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()).getNpcs().clear();
        int i = 0;
        for (TeamEntry teamEntry : Bedwars.getInstance().getCacheHandler().getTeamEntries()) {
            i++;
            Location location = new Location(teamEntry.getShop().getWorld(), teamEntry.getShop().getX(), teamEntry.getShop().getBlockY(), teamEntry.getShop().getZ(), teamEntry.getShop().getYaw(), teamEntry.getShop().getPitch());
            new NPCBuilder(String.valueOf(i), "§6§lSHOP", teamEntry.getShopNPCUuid(), 100, 100, false, false, location).build(player);
        }
    }


}
