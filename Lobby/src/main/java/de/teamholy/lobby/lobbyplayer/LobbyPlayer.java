package de.teamholy.lobby.lobbyplayer;

import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.bukkit.manager.PlayerCacheManager;
import de.teamholy.core.bukkit.npc.models.NPCEntry;
import de.teamholy.core.bukkit.perks.enums.PerkType;
import de.teamholy.core.bukkit.utils.ScoreboardAPI;
import de.teamholy.lobby.Lobby;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.entities.friend.FriendProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.utility.PartyInviteAllowance;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/* copyright by Yassino */
@Getter
@Setter
public class LobbyPlayer {

    private Player player;
    private ScoreboardAPI scoreboardAPI;
    private PlayerRank playerRank;

    public boolean isInArena;
    public String onlineTimeString, clanNameString;
    private Long cooldown = System.currentTimeMillis();
    private boolean fly = false, collectedNameMCReward = false, collectedLabyModReward = false;

    private FriendEntry friendEntry;

    private GameProfile gameProfile;


    public LobbyPlayer(Player player, PlayerCacheManager.CachedBukkitPlayer cachedBukkitPlayer) {
        this.player = player;
        scoreboardAPI = new ScoreboardAPI();
        scoreboardAPI.createScoreboard(player, "§6");
        player.getInventory().clear();

        playerRank = cachedBukkitPlayer.getRank();
        setScoreboard();

        //setLabyModSubtitle();

        friendEntry = new FriendEntry(player);
        setInventory();

        BukkitCore.getAPI().getGameService().getEntityAsync(player.getUniqueId(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()), this::setGameProfile);
    }

    public void executeBungeeCommand(String command) {
        BukkitCore.getAPI().getCloudManager().sendCloudMessage("command", "command", JsonDocument.newDocument("uuid", player.getUniqueId()).append("command", command));
    }

    public void createNPC(String name, UUID uuid, Location location) {
        BukkitCore.getInstance().getPlayerCacheManager().getCachedPlayers().get(player.getUniqueId()).getNpcPlayer().getNpcs().put(ChatColor.stripColor(name),
                new NPCEntry(name, uuid, location, 100, 10, true, true).setPlayer(player));
    }

    private String getOnlineTimeFormated(Long millis) {
        Long hours = Long.valueOf(millis / 3600000L);
        Long minT = Long.valueOf(millis - hours.longValue() * 3600000L);
        Long min = Long.valueOf(minT.longValue() / 60000L);
        return "§6" + hours + "h " + min + "m";
    }

    public void updateOnlineTime() {

        BukkitCore.getAPI().getPlayerService().getEntityAsync(player.getUniqueId(), () -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId()), playerProfile -> {
            setOnlineTimeString(getOnlineTimeFormated(playerProfile.getOnlineTime()));
            scoreboardAPI.updateLine(3, " §7Playtime§8: §6" + getOnlineTimeString());
        });
    }

    public void setLabyModSubtitle() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Lobby.getInstance(), () -> {
            for (LobbyPlayer all : Lobby.getInstance().getLobbyPlayerEntryHandler().values()) {
                //    setSubtitle(all.getPlayer(), player.getUniqueId(), "§7Clan §8» " + getClanNameString() + " §8︳ §7Onlinetime §8» §a" + getOnlineTimeString());
                //    setSubtitle(player, all.getPlayer().getUniqueId(), "§7Clan §8» " + all.getClanNameString() + " §8︳ §7Onlinetime §8» §a" + all.getOnlineTimeString());
            }
        }, 5);
    }


    public void updateClanTagScore() {

        BukkitCore.getAPI().getClanPlayerService().getEntityAsync(player.getUniqueId(), () -> BukkitCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()), playerProfile -> {
            if (playerProfile != null) {
                Clan clan = BukkitCore.getAPI().getClanManager().getClanById(playerProfile.getClanId());
                clanNameString = clan.getColor() + clan.getName();
            } else {
                clanNameString = "§cno clan";
            }
            scoreboardAPI.updateLine(5, " §7Clan§8: " + clanNameString);

        });
    }

    public void updateCoinsScore() {
        BukkitCore.getAPI().getCoinManager().getCoinsAsync(player.getUniqueId(), coins -> scoreboardAPI.updateLine(4, " §7Coins§8: §6" + BukkitCore.getAPI().getCoinManager().formatInteger(coins)));
    }

    public void updateRankScore() {
        scoreboardAPI.updateLine(7, " §7Rank§8: " + playerRank.getColorCode() + playerRank.getName());
    }


    public void setScoreboard() {
        scoreboardAPI.setLine(9, " §8§m--------------- ");
        scoreboardAPI.setLine(8, "§7");
        scoreboardAPI.setLine(7, " §7Rank§8: " + playerRank.getColorCode() + playerRank.getName());
        scoreboardAPI.setLine(6, "§2");

        CountDownLatch latch = new CountDownLatch(2);

        BukkitCore.getAPI().getClanPlayerService().getEntityAsync(player.getUniqueId(), () -> BukkitCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()), playerProfile -> {
            if (playerProfile != null) {
                Clan clan = BukkitCore.getAPI().getClanManager().getClanById(playerProfile.getClanId());
                clanNameString = clan.getColor() + clan.getName();
            } else {
                clanNameString = "§cno clan";
            }
            scoreboardAPI.setLine(5, " §7Clan§8: " + clanNameString);
            latch.countDown();
        });

        BukkitCore.getAPI().getPlayerService().getEntityAsync(player.getUniqueId(), () -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId()), playerProfile -> {
            setOnlineTimeString(getOnlineTimeFormated(playerProfile.getOnlineTime()));
            scoreboardAPI.setLine(4, " §7Coins§8: §6" + BukkitCore.getAPI().getCoinManager().formatInteger(playerProfile.getCoins()));
            scoreboardAPI.setLine(3, " §7Playtime§8: §6" + getOnlineTimeString());
            latch.countDown();
        });

        scoreboardAPI.setLine(2, "§5");
        scoreboardAPI.setLine(1, " §8§m--------------- ");
        scoreboardAPI.setLine(0, "§o" + Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName());

        Bukkit.getScheduler().runTaskAsynchronously(Lobby.getInstance(), () -> {
            try {
                latch.await();
                Bukkit.getScheduler().runTask(Lobby.getInstance(), scoreboardAPI::build);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void openGameSubInventory(String group, Material material) {


        String error = "§cthere is currently no §6" + group + " §cserver available§4!";
        AtomicInteger i = new AtomicInteger();
        List<ServiceInfoSnapshot> gameServices = Lobby.getInstance().getCloudCacheHandler().getServerInfos().values().stream()
                .filter(info -> info.getConfiguration().getGroups()[0].equals(group))
                .sorted(Comparator.comparingInt(info -> info.getServiceId().getTaskServiceId())).collect(Collectors.toList());

        try {
            if (gameServices.isEmpty()) {
                player.sendMessage(Lobby.getInstance().getPrefix() + error);
            } else if (gameServices.size() == 1) {
                BukkitCore.getAPI().getCloudManager().getPlayerManager().getPlayerExecutor(player.getUniqueId()).connect(gameServices.get(0).getName());
            } else {
                Inventory inventory = new Inventory("§8» §6" + group, 9);

                for (int j = 0; j < 9; j++) {
                    inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), j);
                }
                gameServices.forEach(gameService -> {
                    int onlinecount = (gameService.getProperty(BridgeServiceProperty.ONLINE_COUNT).isPresent() ? gameService.getProperty(BridgeServiceProperty.ONLINE_COUNT).get() : 0);
                    inventory.setItem(new ItemBuilder(material, Math.min(64, onlinecount)).setLore("§7Players §8× §6" + onlinecount).setName("§8» §6" + gameService.getName()).build(), i.get(), (event) -> {
                        BukkitCore.getAPI().getCloudManager().getPlayerManager().getPlayerExecutor(player.getUniqueId()).connect(gameService.getName());
                    });
                    i.getAndIncrement();
                });

                player.openInventory(inventory.getInventory());
            }
        } catch (IndexOutOfBoundsException e) {
            player.sendMessage(Lobby.getInstance().getPrefix() + error);
        }
    }

    public void sendPlayerToGroup(String group) {
        List<ServiceInfoSnapshot> collect = Lobby.getInstance().getCloudCacheHandler().getServerInfos().values().stream()
                .filter(info -> info.getConfiguration().getGroups()[0].equals(group))
                .filter(serviceInfoSnapshot -> !serviceInfoSnapshot.getProperty(BridgeServiceProperty.IS_IN_GAME).get())
                .filter(serviceInfoSnapshot -> serviceInfoSnapshot.getProperty(BridgeServiceProperty.STATE).isPresent())
                .filter(serviceInfoSnapshot -> serviceInfoSnapshot.getProperty(BridgeServiceProperty.MOTD).isPresent())
                .filter(serviceInfoSnapshot -> serviceInfoSnapshot.getProperty(BridgeServiceProperty.STATE).get().equalsIgnoreCase("LOBBY"))
                .filter(serviceInfoSnapshot -> serviceInfoSnapshot.getProperty(BridgeServiceProperty.EXTRA).isPresent())
                .filter(serviceInfoSnapshot -> serviceInfoSnapshot.getProperty(BridgeServiceProperty.EXTRA).get().equalsIgnoreCase("1"))
                .filter(serviceInfoSnapshot -> !serviceInfoSnapshot.getProperty(BridgeServiceProperty.IS_FULL).get())
                .sorted(Comparator.comparingInt(info -> info.getProperty(BridgeServiceProperty.ONLINE_COUNT).get())).collect(Collectors.toList());
        Collections.reverse(collect);
        ServiceInfoSnapshot service = collect.get(0);
        if (service == null) {
            player.sendMessage(Lobby.getInstance().getPrefix() + "Could not find a §c" + group + " §7server");
        } else {
            BukkitCore.getAPI().getCloudManager().getPlayerManager().getPlayerExecutor(player.getUniqueId()).connect(service.getName());
        }
    }


    public void openLobbySwitcher() {
        Inventory inventory = new Inventory("§8» §6Lobby Switcher", 9);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        AtomicInteger i = new AtomicInteger();
        List<ServiceInfoSnapshot> gameServices = Lobby.getInstance().getCloudCacheHandler().getServerInfos().values().stream().filter(info -> info.getConfiguration().getGroups()[0].equals("Lobby")).sorted(Comparator.comparingInt(info -> info.getServiceId().getTaskServiceId())).collect(Collectors.toList());
        List<ServiceInfoSnapshot> gameServices1 = Lobby.getInstance().getCloudCacheHandler().getServerInfos().values().stream().filter(info -> info.getConfiguration().getGroups()[0].equals("PremiumLobby")).sorted(Comparator.comparingInt(info -> info.getServiceId().getTaskServiceId())).collect(Collectors.toList());

        gameServices1.forEach(gameService -> {
            int onlinecount = (gameService.getProperty(BridgeServiceProperty.ONLINE_COUNT).isPresent() ? gameService.getProperty(BridgeServiceProperty.ONLINE_COUNT).get() : 0);
            if (gameService.getName().equalsIgnoreCase(Wrapper.getInstance().getCurrentServiceInfoSnapshot().getConfiguration().getGroups()[0])) {
                inventory.setItem(new ItemBuilder(Material.GLOWSTONE_DUST, Math.min(64, onlinecount)).setEnchantments(Enchantment.KNOCKBACK, 1).setAttributs().setLore("§7Players §8× §6" + onlinecount, "§cYou are currently on this lobby").setName("§8» §6" + gameService.getName()).build(), i.get());
            } else {
                inventory.setItem(new ItemBuilder(Material.GLOWSTONE_DUST, Math.min(64, onlinecount)).setLore("§7You need §6Premium §7or above to join", ("§7Players §8× §6" + onlinecount)).setName("§8» §6" + gameService.getName()).build(), i.get(), event -> {
                    if (player.hasPermission("teamholy.fulljoin")) {
                        BukkitCore.getAPI().getCloudManager().getPlayerManager().getPlayerExecutor(player.getUniqueId()).connect(gameService.getName());
                    }
                });
            }
            i.getAndIncrement();
        });

        gameServices.forEach(gameService -> {
            int onlinecount = (gameService.getProperty(BridgeServiceProperty.ONLINE_COUNT).isPresent() ? gameService.getProperty(BridgeServiceProperty.ONLINE_COUNT).get() : 0);
            ;
            if (gameService.getName().equalsIgnoreCase(Wrapper.getInstance().getCurrentServiceInfoSnapshot().getConfiguration().getGroups()[0])) {
                inventory.setItem(new ItemBuilder(Material.SUGAR, Math.min(64, onlinecount)).setEnchantments(Enchantment.KNOCKBACK, 1).setAttributs().setLore("§7Players §8× §6" + onlinecount, "§cYou are currently on this lobby").setName("§8» §6" + gameService.getName()).build(), i.get());
            } else {
                inventory.setItem(new ItemBuilder(Material.SUGAR, Math.min(64, onlinecount)).setLore("§7Players §8× §6" + onlinecount).setName("§8» §6" + gameService.getName()).build(), i.get(), (event) -> BukkitCore.getAPI().getCloudManager().getPlayerManager().getPlayerExecutor(player.getUniqueId()).connect(gameService.getName()));
            }
            i.getAndIncrement();
        });


        player.openInventory(inventory.getInventory());
    }

    public void openGamesInventory() {
        Inventory inventory = new Inventory("§8» §6Games", 9 * 4);

        for (int i = 0; i < 9 * 4; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        inventory.setItem(new ItemBuilder(Material.SLIME_BALL, Math.min(64, (Lobby.getInstance().getCloudCacheHandler().getOnlineCount("Lobby") + Lobby.getInstance().getCloudCacheHandler().getOnlineCount("PremiumLobby")))).setName("§8» §6Spawn").build(), 10, event -> player.teleport(BukkitCore.getInstance().getLocationManager().getLocation("lobby")));


        inventory.setItem(new ItemBuilder(Material.STICK, Math.min(64, Lobby.getInstance().getCloudCacheHandler().getOnlineCount("MLGRush")))
                .setName("§8» §6MLGRush")
                .setLore(" "
                        , " §7Fight against an opponent in a 1v1 or 1v1v1v1 mode! "
                        , " §7Try to destroy their bed and knock them down. "
                        , " §7It's also great for practicing, clutching, and improving your skills."
                        , " "
                        , " §fMultiplayer "
                        , " §7Currently playing§8: §6" + Lobby.getInstance().getCloudCacheHandler().getOnlineCount("MLGRush") + " §7players"
                        , " "
                        , "§8» §7Click to §6§nconnect"
                )
                .build(), 21, (event) -> openGameSubInventory("MLGRush", Material.STICK));
        inventory.setItem(new ItemBuilder(Material.RED_SANDSTONE, Math.min(64, Lobby.getInstance().getCloudCacheHandler().getOnlineCount("Clutches")))
                .setName("§8» §6Clutches §8x §6Reduce §8/ §aPlayground")
                .setLore(""
                        , " §7The perfect mode for practicing your clutching skills §7§lalone§7! "
                        , " §7Use our ReduceBot, which behaves just like a real player, and "
                        , " §7it offers modes like reduce, clutch, diagonal-clutch, and multi-reduce."
                        , " "
                        , " §fSingleplayer "
                        , " §7Currently playing§8: §6" + Lobby.getInstance().getCloudCacheHandler().getOnlineCount("Clutches") + " §7players"
                        , " "
                        , "§8» §7Click to §6§nconnect"
                )
                .build(), 13, (event) -> openGameSubInventory("Clutches", Material.RED_SANDSTONE));
        inventory.setItem(new ItemBuilder(Material.SANDSTONE, Math.min(64, Lobby.getInstance().getCloudCacheHandler().getOnlineCount("KnockbackFFA")))
                .setName("§8» §6KnockbackFFA")
                .setLore(" "
                        , " §7Fight against other players on small platforms! "
                        , " §7You can practice your PvP and bow skills "
                        , " §7with different maps & perks. "
                        , " "
                        , " §fMultiplayer "
                        , " §7Currently playing§8: §6" + Lobby.getInstance().getCloudCacheHandler().getOnlineCount("KnockbackFFA") + " §7players"
                        , " "
                        , "§8» §7Click to §6§nconnect"
                )
                .build(), 14, (event) -> openGameSubInventory("KnockbackFFA", Material.SANDSTONE));


        inventory.setItem(new ItemBuilder(Material.BED, Math.min(64, Lobby.getInstance().getBedwarsServerInventory().getBedwarsPlayers() + Lobby.getInstance().getBedwarsServerInventory().getRushBWPlayers()))
                .setName("§8» §6Bedwars §7& §cRushBW")
                .setLore(" "
                        , " §7The most intense PvP game, combining "
                        , " §7all game modes at once: Bedwars, German-style."
                        , " §7Break the enemy's bed and knock them down "
                        , " §7in 2x1, 4x2, and 8x1 variants. "
                        , " §7Introducing §cRushBW§8: §7a faster Bedwars mode for "
                        , " §7the best German mouse abuse experience "
                        , " "
                        , " §fMultiplayer "
                        , " §7Currently playing§8: §6" + (Lobby.getInstance().getBedwarsServerInventory().getBedwarsPlayers() + Lobby.getInstance().getBedwarsServerInventory().getRushBWPlayers()) + " §7players"
                        , " "
                        , "§8» §7Click to §6§nteleport"
                )
                .build(), 15, event -> player.teleport(BukkitCore.getInstance().getLocationManager().getLocation("bw_spawn")));
        inventory.setItem(new ItemBuilder(Material.IRON_SWORD, Math.min(64, Lobby.getInstance().getCloudCacheHandler().getOnlineCount("SGFFA")))
                .setName("§8» §6SGFFA")
                .setLore(" "
                        , " §7The all-time favorite Survival Games, but with a little "
                        , " §7twist: we combined Survival Games and Free-For-All in one mode "
                        , " §7Try to think fast and get out of difficult situations. "
                        , " "
                        , " §fMultiplayer "
                        , " §7Currently playing§8: §6" + Lobby.getInstance().getCloudCacheHandler().getOnlineCount("SGFFA") + " §7players"
                        , " "
                        , "§8» §7Click to §6§nconnect"
                )
                .build(), 16, (event) -> openGameSubInventory("SGFFA", Material.IRON_SWORD));

        inventory.setItem(new ItemBuilder(Material.IRON_PICKAXE, Math.min(64, Lobby.getInstance().getCloudCacheHandler().getOnlineCount("Bridge")))
                .setName("§8» §6Bridge §8/ §6Fastbuilders")
                .setLore(" "
                        , " §7Practice your building skills "
                        , " §7by fast building a §ebridge §7to the end island. "
                        , " §7Best way to practice your §atelly§8, §emoonwalk§8, §cgodbridge§8.... "
                        , " "
                        , " §fSingleplayer "
                        , " §7Currently playing§8: §6" + Lobby.getInstance().getCloudCacheHandler().getOnlineCount("Bridge") + " §7players"
                        , " "
                        , "§8» §7Click to §6§nconnect"
                )
                .build(), 12, (event) -> openGameSubInventory("Bridge", Material.IRON_PICKAXE));

        inventory.setItem(new ItemBuilder(Material.GRASS, Math.min(64, Lobby.getInstance().getCloudCacheHandler().getOnlineCount("SkyWarsFFA")))
                .setName("§8» §6SkywarsFFA §c§lNEW")
                .setLore(" "
                        , " §7Practice your combat skills "
                        , " §7by fighting against other players on "
                        , " §7skywars environment islands. "
                        , " "
                        , " §fMultiplayer "
                        , " §7Currently playing§8: §6" + Lobby.getInstance().getCloudCacheHandler().getOnlineCount("SkyWarsFFA") + " §7players"
                        , " "
                        , "§8» §7Click to §6§nconnect"
                )
                .build(), 22, (event) -> openGameSubInventory("SkyWarsFFA", Material.GRASS));


        player.openInventory(inventory.getInventory());
    }

    public void openSettings() {

        Inventory inventory = new Inventory("§8» §6Settings", 9 * 3);
        for (int i = 0; i < 9 * 3; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        ItemBuilder itemBuilder = new ItemBuilder(Material.FIREWORK, 1).setName("§8» §6Allow Party invites").setAttributs();
        ItemBuilder requests = new ItemBuilder(Material.BOOK, 1).setName("§8» §6Allow Friend requests").setLore("§7currently §cdeactivated").setAttributs();
        ItemBuilder jump = new ItemBuilder(Material.ENDER_PEARL, 1).setName("§8» §6Allow Friend jump").setLore("§7currently §cdeactivated").setAttributs();

        FriendProfile friendProfile = BukkitCore.getAPI().getFriendService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getFriendService().getRepository().findFirstById(player.getUniqueId()));
        PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId()));

        setPartyLore(itemBuilder, friendProfile.getPartyInviteAllowance());
        if (friendProfile.getPartyInviteAllowance() == PartyInviteAllowance.EVERYONE || friendProfile.getPartyInviteAllowance() == PartyInviteAllowance.ONLY_FRIENDS) {
            itemBuilder.setEnchantments(Enchantment.KNOCKBACK, 1);
        }

        changeActivatedLore(jump, friendProfile.isAllowFriendJump());
        changeActivatedLore(requests, friendProfile.isAllowFriendRequests());

        AtomicBoolean friendUpdate = new AtomicBoolean(false);
        AtomicBoolean nickUpdate = new AtomicBoolean(false);

        inventory.setItem(new ItemBuilder(Material.DIAMOND, 1).setName("§8» §6Perks").build(), 22, event1 -> BukkitCore.getInstance().getPerkManager().openMainPerkInventory(player));
        inventory.setItem(requests.build(), 11, event1 -> {
            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2F, 2F);
            friendProfile.setAllowFriendRequests(!friendProfile.isAllowFriendRequests());

            changeActivatedLore(requests, friendProfile.isAllowFriendRequests());
            inventory.setItem(requests.build(), 11);

            friendUpdate.set(true);
        });

        inventory.setItem(jump.build(), 10, event1 -> {
            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2F, 2F);
            friendProfile.setAllowFriendJump(!friendProfile.isAllowFriendJump());

            changeActivatedLore(jump, friendProfile.isAllowFriendJump());
            inventory.setItem(jump.build(), 10);

            friendUpdate.set(true);
        });

        inventory.setItem(itemBuilder.build(), 15, event1 -> {
            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2F, 2F);
            PartyInviteAllowance partyInviteAllowance = null;
            if (friendProfile.getPartyInviteAllowance() == PartyInviteAllowance.EVERYONE) {
                partyInviteAllowance = PartyInviteAllowance.ONLY_FRIENDS;
            } else if (friendProfile.getPartyInviteAllowance() == PartyInviteAllowance.ONLY_FRIENDS) {
                partyInviteAllowance = PartyInviteAllowance.NONE;
            } else if (friendProfile.getPartyInviteAllowance() == PartyInviteAllowance.NONE) {
                partyInviteAllowance = PartyInviteAllowance.EVERYONE;
            }
            friendProfile.setPartyInviteAllowance(partyInviteAllowance);
            setPartyLore(itemBuilder, friendProfile.getPartyInviteAllowance());
            if (friendProfile.getPartyInviteAllowance() == PartyInviteAllowance.EVERYONE || friendProfile.getPartyInviteAllowance() == PartyInviteAllowance.ONLY_FRIENDS) {
                itemBuilder.setEnchantments(Enchantment.KNOCKBACK, 1);
            } else {
                ItemMeta itemMeta = itemBuilder.itemStack.getItemMeta();
                itemMeta.removeEnchant(Enchantment.KNOCKBACK);
                itemBuilder.itemStack.setItemMeta(itemMeta);
            }

            inventory.setItem(itemBuilder.build(), 15);
            friendUpdate.set(true);
        });

        inventory.setOnClose(inventoryCloseEvent -> {
            if (nickUpdate.get()) {
                BukkitCore.getAPI().getPlayerService().saveEntity(playerProfile, true, true);
            }

            if (friendUpdate.get()) {
                BukkitCore.getAPI().getFriendService().saveEntity(friendProfile, true, true);
            }
        });

        ItemBuilder nick = new ItemBuilder(Material.NAME_TAG).setName("§8» §6Autonick").setAttributs();

        if (!player.hasPermission("markuapi.nick")) {
            nick.setLore("§cYou need atleast the §dVIP §crank!");
        }

        changeActivatedLore(nick, playerProfile.isAutoNick());

        inventory.setItem(nick.build(), 16, event -> {

            if (!player.hasPermission("markupapi.nick")) {
                player.sendMessage(Lobby.getInstance().getPrefix() + "§cYou need at least the §dVIP §crank to nick yourself!");
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 50);
                player.closeInventory();
                return;
            }

            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2F, 2F);


            if (playerProfile.isAutoNick()) {
                playerProfile.setAutoNick(false);
            } else {
                playerProfile.setAutoNick(true);
            }

            changeActivatedLore(nick, playerProfile.isAutoNick());
            inventory.setItem(nick.build(), 16);

            nickUpdate.set(true);
        });

        inventory.setItem(new ItemBuilder(Material.LAVA_BUCKET, 1).setName("§8» §6Statsreset").build(), 4, event1 -> Lobby.getInstance().getStatsResetHandler().openStatsReset(player));

        player.openInventory(inventory.getInventory());
    }

    private void changeActivatedLore(ItemBuilder itemBuilder, boolean activated) {
        if (activated) {
            itemBuilder.setLore("§7currently §aactivated");
            itemBuilder.setEnchantments(Enchantment.KNOCKBACK, 1);
        } else {
            itemBuilder.setLore("§7currently §cdeactivated");
            ItemMeta itemMeta = itemBuilder.itemStack.getItemMeta();
            itemMeta.removeEnchant(Enchantment.KNOCKBACK);
            itemBuilder.itemStack.setItemMeta(itemMeta);
        }
    }


    private void setPartyLore(de.teamholy.core.bukkit.utils.ItemBuilder itemBuilder, PartyInviteAllowance partyInviteAllowance) {


        List<String> collection = Arrays.stream(PartyInviteAllowance.values())
                .map(value -> (partyInviteAllowance == value) ? "§a" + value.toString().replace("_", " ").toLowerCase(Locale.ROOT) : "§7" + value.toString().replace("_", " ").toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());

        Collections.reverse(collection);

        itemBuilder.setLore(collection);
    }


    public void setInventory() {

        player.getInventory().clear();


        ItemBuilder perk = BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.BLOCK);
        if (perk != null) {
            player.getInventory().setItem(4, perk.setAmount(64).setName("§8» §6Blocks §8(§7rightclick§8)").build());
        }

        player.getInventory().setItem(0, new ItemBuilder(Material.COMPASS, 1).setName("§8» §6Games §8(§7rightclick§8)").build());
        player.getInventory().setItem(7, new ItemBuilder(Material.REDSTONE_COMPARATOR, 1).setName("§8» §6Settings §8(§7rightclick§8)").build());
        player.getInventory().setItem(1, new ItemBuilder(Material.NETHER_STAR, 1).setName("§8» §6Lobby Switcher §8(§7rightclick§8)").build());
        player.getInventory().setItem(8, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setSkullOwner(player.getName()).setName("§8» §6Friends §8(§7rightclick§8)").build());
    }
}