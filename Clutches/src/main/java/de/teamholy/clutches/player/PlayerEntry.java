package de.teamholy.clutches.player;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.npc.models.NPCEntry;
import de.teamholy.api.bukkit.npc.models.SkinEntry;
import de.teamholy.api.bukkit.utils.InventoryUtils;
import de.teamholy.api.bukkit.utils.scoreboard.ScoreboardAPI;
import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.arena.ArenaEntry;
import de.teamholy.clutches.arena.ArenaType;
import de.teamholy.clutches.enums.HitType;
import de.teamholy.clutches.enums.Items;
import de.teamholy.clutches.map.MapEntry;
import de.teamholy.clutches.npcskin.NPCSkin;
import de.teamholy.clutches.playground.model.PlaygroundPlayer;
import de.teamholy.clutches.utils.PlayerUtils;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.PerkType;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class PlayerEntry {

    private Player player;
    private ScoreboardAPI scoreboardAPI;
    private PlayerState playerState;

    private ArenaEntry arenaEntry;
    private ArenaType arenaType;


    private ArenaEntry spectateArena;
    private ArrayList<Location> blocks;
    private long cooldown = System.currentTimeMillis();
    private org.bukkit.inventory.Inventory inventory;

    private HitType npcHit = HitType.EASY, firstHit = HitType.EASY, secondHit = HitType.NONE, thirdHit = HitType.NONE, fourthHit = HitType.NONE;

    private int delay = 3;
    private int npcAirHits = 0;
    private double multiReduceNpcDistance = 3;

    private NPCSkin npcSkin = NPCSkin.IAMSLOWLY;

    private boolean isPause, isSecondRound;
    private long attackCooldown;

    public AtomicInteger pre = new AtomicInteger(6);
    public AtomicInteger countdown = new AtomicInteger(delay + 1);
    public AtomicInteger clutchCount = new AtomicInteger(0);
    public AtomicInteger npcAirHit = new AtomicInteger(npcAirHits);

    private PlaygroundPlayer playgroundPlayer;

    public PlayerEntry(Player player) {
        this.player = player;
        playerState = PlayerState.LOBBY;
        isPause = false;
        attackCooldown = System.currentTimeMillis();
        this.scoreboardAPI = new ScoreboardAPI();
        scoreboardAPI.createScoreboard(player, "§b");

        createInv();
        GameProfile statsProfile = BukkitCore.getAPI().getGameService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()));
        playgroundPlayer = new PlaygroundPlayer(this,statsProfile);
        if (!statsProfile.exists(Gamemodes.CLUTCHES.toString())) {
            statsProfile.setStat(Gamemodes.CLUTCHES.toString(), StatsType.ALLTIME,"lol",0);


            statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"invsort",InventoryUtils.inventoryToString(inventory));

            statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"delay", String.valueOf(delay));
            statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"npcSkin",String.valueOf(npcSkin.getId()));
            statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"npcAirHits", String.valueOf(npcAirHits));
            statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"multiReduceNpcDistance",String.valueOf(multiReduceNpcDistance));

            statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"firstHit",HitType.EASY.name());
            statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"secondHit",HitType.NONE.name());
            statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"thirdHit",HitType.NONE.name());
            statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"fourthHit",HitType.NONE.name());
            statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"npcHit",HitType.EASY.name());

            BukkitCore.getAPI().getGameService().saveEntity(statsProfile, true, true);

        } else {



            String inventory = statsProfile.getSetting(Gamemodes.CLUTCHES.toString(),"invsort");
            if (inventory.isEmpty()) {
                createInv();
            } else {
                setInventory(InventoryUtils.inventoryFromString(inventory));
            }


            delay = Integer.parseInt(statsProfile.getSetting(Gamemodes.CLUTCHES.toString(),"delay"));
            npcAirHits = Integer.parseInt(statsProfile.getSetting(Gamemodes.CLUTCHES.toString(),"npcAirHits"));
            multiReduceNpcDistance = Double.parseDouble(statsProfile.getSetting(Gamemodes.CLUTCHES.toString(),"multiReduceNpcDistance"));

            firstHit = HitType.valueOf(statsProfile.getSetting(Gamemodes.CLUTCHES.toString(),"firstHit"));
            secondHit = HitType.valueOf(statsProfile.getSetting(Gamemodes.CLUTCHES.toString(),"secondHit"));
            thirdHit = HitType.valueOf(statsProfile.getSetting(Gamemodes.CLUTCHES.toString(),"thirdHit"));
            fourthHit= HitType.valueOf(statsProfile.getSetting(Gamemodes.CLUTCHES.toString(),"fourthHit"));
            npcHit = HitType.valueOf(statsProfile.getSetting(Gamemodes.CLUTCHES.toString(),"npcHit"));
            npcSkin = NPCSkin.getNPCSkinFromId(Integer.parseInt(statsProfile.getSetting(Gamemodes.CLUTCHES.toString(),"npcSkin")));

        }

        blocks = new ArrayList<>();
    }

    public void openSpectator() {
        Inventory inventory = new Inventory("§8» §6Spectate", 54);
        int i = 0;
        for (PlayerEntry playerEntry : Clutches.getInstance().getPlayerEntryHandler().values()) {
            if (playerEntry.getPlayerState() == PlayerState.INGAME) {
                ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setSkullOwner(playerEntry.player.getName())
                        .setName("§8» " + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(playerEntry.getPlayer().getUniqueId()) + playerEntry.getPlayer().getName());
                item.setLore("§7Type §8» §6" + playerEntry.getArenaType().getName()
                        , "§7Map §8» §6" + playerEntry.getArenaEntry().getMapEntry().getName()
                );
                inventory.setItem(item.build(), i, event -> {
                    if (playerEntry.getPlayerState() == PlayerState.INGAME) {
                        spectatePlayer(playerEntry);
                    }
                });
                i++;
            }
        }
        player.openInventory(inventory.getInventory());
    }


    private void spectatePlayer(PlayerEntry playerEntry) {
        setPlayerState(PlayerState.SPECTATE);
        player.getInventory().clear();
        playerEntry.player.hidePlayer(player);
        player.spigot().setCollidesWithEntities(false);
        setSpectateArena(playerEntry.getArenaEntry());
        getSpectateArena().getArenaPlayers().add(this);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 15));
        player.getInventory().setItem(4, new ItemBuilder(Material.MAGMA_CREAM).setName("§8» §cLeave").build());

        player.teleport(getSpectateArena().getPlayerSpawn());

        if (spectateArena.getArenaPlayers().get(0).arenaType == ArenaType.REDUCE) {
            NPCEntry npcEntry = new NPCEntry("§6§lTeamholy.de", spectateArena.getArenaPlayers().get(0).npcSkin.getUuid(), getSpectateArena().getNpc(), 100, 20, true,false).setPlayer(playerEntry.player);
            npcEntry.setHeldItem(new ItemBuilder(Material.STICK, 1, (byte) 0).setEnchantments(Enchantment.KNOCKBACK, 1).build());
            npcEntry.update();
            BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()).getNpcs().put("reduceNPC", npcEntry);
        } else if (spectateArena.getArenaPlayers().get(0).arenaType == ArenaType.EXPERIMENTAL) {
            for (int j = 0; j < 10; j++) {
                Location location = new Location(getSpectateArena().getNpc().getWorld(), getSpectateArena().getNpc().getX() + (j * spectateArena.getArenaPlayers().get(0).getMultiReduceNpcDistance()), getSpectateArena().getNpc().getY(), getSpectateArena().getNpc().getZ());
                NPCEntry npcEntry = new NPCEntry("§6§lTeamholy.de", spectateArena.getArenaPlayers().get(0).npcSkin.getUuid(), location, 100, 20, true,false).setPlayer(playerEntry.player);
                npcEntry.setHeldItem(new ItemBuilder(Material.STICK, 1, (byte) 0).setEnchantments(Enchantment.KNOCKBACK, 1).build());
                npcEntry.update();
                BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()).getNpcs().put("reduceNPC " + j, npcEntry);
            }
        }
    }

    public void leaveSpectator() {
        if (playerState == PlayerState.SPECTATE) {
            getSpectateArena().getArenaPlayers().remove(this);
            setSpectateArena(null);


            player.setFlying(false);
            player.setAllowFlight(false);
            player.spigot().setCollidesWithEntities(true);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.showPlayer(getPlayer());
            }
            setPlayerState(PlayerState.LOBBY);
            setItemsSpawn();
            setScoreboard();

            player.teleport(BukkitHolyAPI.getInstance().getLocationManager().getLocation("lobby"));
            if (BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()) != null) {
                BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()).getNpcs().remove("reduceNPC");
                for (int i = 0; i < 10; i++) {
                    BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()).getNpcs().remove("reduceNPC " + i);
                }
            }
        }
    }

    public Vector getVelocity(Vector blick, double speed) {
        double y = 0;
        if (arenaType == ArenaType.REDUCE) {
            // 0.3533
            y = 0.3533;
        } else if (arenaType == ArenaType.CLUTCH || arenaType == ArenaType.DIAGONAL_CLUTCH) {
            y = 0.3733;
        } else if (arenaType == ArenaType.EXPERIMENTAL) {
            y = 0.3633;
        }
        double multiplier = Math.sqrt((speed * speed) / (blick.getX() * blick.getX() + y * y + blick.getZ() * blick.getZ()));
        return blick.multiply(multiplier).setY(y);
    }

    public void openMapInventory(ArenaType arenaType) {
        if (playerState != PlayerState.LOBBY || getArenaEntry() != null) return;
        Inventory inventory = new Inventory("§8» §6Map selection", 9);

        int i = 0;
        for (MapEntry mapEntry : Clutches.getInstance().getMapEntryHandler().values()) {
            if (mapEntry.getNotSupported() == null || !mapEntry.getNotSupported().contains(arenaType)) {
                int size = (int) mapEntry.getArenaEntryHashMap().values().stream().filter(arenaEntry1 -> !arenaEntry1.isUsed()).count();
                if (size != 0) {
                    inventory.setItem(mapEntry.getItemBuilder().setAmount(size).build(), i, event -> {


                        ArenaEntry arenaEntry = mapEntry.getFreeArena();
                        setArenaEntry(arenaEntry);
                        if (arenaEntry == null) return;
                        arenaEntry.setUsed(true);
                        getArenaEntry().getArenaPlayers().add(this);
                        this.arenaType = arenaType;


                        player.teleport(getArenaEntry().getPlayerSpawn());
                        setPlayerState(PlayerState.INGAME);
                        Clutches.getInstance().getHologramManager().updateHolograms();

                        if (arenaType == ArenaType.REDUCE) {


                            NPCEntry npcEntry = new NPCEntry("§6§lTeamholy.de", npcSkin.getUuid(), getArenaEntry().getNpc(), 100, 20, true,false).setPlayer(player);
                            npcEntry.setHeldItem(new ItemBuilder(Material.STICK, 1, (byte) 0).setEnchantments(Enchantment.KNOCKBACK, 1).build());
                            npcEntry.update();
                            BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()).getNpcs().put("reduceNPC", npcEntry);


                        } else if (arenaType == ArenaType.EXPERIMENTAL) {


                            for (int j = 0; j < 10; j++) {
                                Location location = new Location(getArenaEntry().getNpc().getWorld(), getArenaEntry().getNpc().getX() + (j * getMultiReduceNpcDistance()), getArenaEntry().getNpc().getY(), getArenaEntry().getNpc().getZ());
                                NPCEntry npcEntry = new NPCEntry("§6§lTeamholy.de", npcSkin.getUuid(), location, 100, 20, true,false).setPlayer(player);
                                npcEntry.setHeldItem(new ItemBuilder(Material.STICK, 1, (byte) 0).setEnchantments(Enchantment.KNOCKBACK, 1).build());
                                npcEntry.update();
                                BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()).getNpcs().put("reduceNPC " + j, npcEntry);
                            }
                        }

                        player.sendMessage(Clutches.PREFIX + "Use /quit to leave the game");
                        setIngameItems();
                        setScoreboard();
                    });

                    i++;
                }
            }
        }
        player.openInventory(inventory.getInventory());
    }


    public void createInv() {
        inventory = Bukkit.createInventory(null, 9, "inv");
        for (Items items : Items.values()) {
            inventory.setItem(items.getSlot(), items.getItemStack());
        }
    }

    public void performSpawn() {
        resetClutch();
        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodLevel(20);
        player.setHealth(20);
        setItemsSpawn();
    }

    public void resetMap() {
        player.teleport(arenaEntry.getPlayerSpawn());
        blocks.forEach(blocks -> blocks.getBlock().setType(Material.AIR));
        blocks.clear();
        setIngameItems();
        setSecondRound(true);
        resetClutch();
    }

    public void resetClutch() {
        getPre().set(6);
        getCountdown().set(getDelay() + 1);
        getClutchCount().set(0);
    }

    public void checkQuit() {
        if (playerState != PlayerState.INGAME) return;
        resetMap();
        setSecondRound(false);
        arenaEntry.setUsed(false);
        PlayerUtils.sendBar(player, "");
        arenaEntry.getArenaPlayers().forEach(playerEntry -> {
            playerEntry.performSpawn();
            playerEntry.setPlayerState(PlayerState.LOBBY);
            playerEntry.getPlayer().setLevel(0);
            playerEntry.getPlayer().setExp(0);
            playerEntry.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
            playerEntry.getPlayer().setAllowFlight(false);
            playerEntry.getPlayer().setFlying(false);
            playerEntry.getPlayer().spigot().setCollidesWithEntities(true);
            playerEntry.getPlayer().teleport(BukkitHolyAPI.getInstance().getLocationManager().getLocation("lobby"));
            if (BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(playerEntry.getPlayer().getUniqueId()) != null) {
                BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(playerEntry.getPlayer().getUniqueId()).getNpcs().remove("reduceNPC");
                for (int i = 0; i < 10; i++) {
                    BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(playerEntry.getPlayer().getUniqueId()).getNpcs().remove("reduceNPC " + i);
                }
            }
            playerEntry.setScoreboard();
            playerEntry.setSpectateArena(null);
        });
        arenaEntry.getArenaPlayers().clear();
        setArenaEntry(null);
        setArenaType(null);
        Clutches.getInstance().getHologramManager().updateHolograms();
    }

    public void setIngameItems() {
        player.getInventory().clear();
        int slot = 0;
        if (!(BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.STICK) == null)) {
            for (ItemStack itemStack : inventory.getContents()) {
                if (itemStack != null && itemStack.getType() != null) {
                    if (itemStack.getType() == Material.STICK) {
                        if (arenaType == ArenaType.REDUCE || arenaType == ArenaType.EXPERIMENTAL) {
                            player.getInventory().setItem(slot, BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.STICK).setUnbreakable().setEnchantments(Enchantment.KNOCKBACK, 1).build());
                        } else {

                            player.getInventory().setItem(slot, BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.BLOCK).setAmount(64).build());

                        }
                    } else if (itemStack.getType() == Material.SANDSTONE) {

                        player.getInventory().setItem(slot, BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.BLOCK).setAmount(64).build());

                    } else {
                        this.player.getInventory().setItem(slot, itemStack);
                    }
                }
                slot++;
            }
        } else {
            System.out.println(player.getName() + " items sind null");
            createInv();
            player.kickPlayer("§cError! §eplease rejoin");
        }
    }

    private int getHitInt(HitType hitType) {
        if (hitType == HitType.NONE) {
            return 7;
        } else if (hitType == HitType.EASY) {
            return 5;
        } else if (hitType == HitType.MEDIUM) {
            return 1;
        } else if (hitType == HitType.HARD) {
            return 14;
        }
        return 0;
    }

    public void openIngameSettings() {

        Inventory inventory = new Inventory("§8» §6Settings", 27);


        for (int i = 0; i < 27; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        inventory.setItem(new ItemBuilder(Material.WATCH, delay, (byte) 0).setName("§8» §6Delay").setLore("§7Currently selected §8» §d" + delay, "§crightclick §7-1", "§aleftclick §7+1").build(), 10, event -> {

            if (event.getClick().isRightClick()) {
                if (getDelay() <= 2) {
                    return;
                } else {
                    setDelay(getDelay() - 1);
                    openIngameSettings();
                    player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
                }
            } else if (event.getClick().isLeftClick()) {
                if (getDelay() >= 12) {
                    return;
                } else {
                    setDelay(getDelay() + 1);
                    openIngameSettings();
                    player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
                }
            }

        });


        inventory.setItem(new ItemBuilder(Material.STICK, npcAirHits, (byte) 0).setName("§8» §6Air NPC hit").setLore("§7ignores the hit range &" , "§7hits you §b" + npcAirHits + " §7times in the air", "§crightclick §7-1", "§aleftclick §7+1").build(), 4, event -> {

            if (event.getClick().isRightClick()) {
                if (getNpcAirHits() <= 0) {
                    return;
                } else {
                    setNpcAirHits(getNpcAirHits() - 1);
                    openIngameSettings();
                    player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
                }
            } else if (event.getClick().isLeftClick()) {
                if (getNpcAirHits() >= 64) {
                    return;
                } else {
                    setNpcAirHits(getNpcAirHits() + 1);
                    openIngameSettings();
                    player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
                }
            }

            getNpcAirHit().set(getNpcAirHits());

        });

        DecimalFormat df = new DecimalFormat("0.0");
        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM,1, (byte) 3).setName("§8» §6Multireduce NPC distance").setLore("§7every npc has a §b" + df.format(multiReduceNpcDistance) + " §7blocks distance", "§crightclick §7-0.1", "§aleftclick §7+0.1").build(), 21, event -> {

            if (event.getClick().isRightClick()) {
                if (getMultiReduceNpcDistance() <= 1) {
                    return;
                } else {
                    setMultiReduceNpcDistance(getMultiReduceNpcDistance() - 0.1);
                    openIngameSettings();
                    player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
                }
            } else if (event.getClick().isLeftClick()) {
                if (getMultiReduceNpcDistance() >= 7) {
                    return;
                } else {
                    setMultiReduceNpcDistance(getMultiReduceNpcDistance() + 0.1);
                    openIngameSettings();
                    player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
                }
            }

        });

        inventory.setItem(new ItemBuilder(Material.STAINED_GLASS, 1, (byte) getHitInt(npcHit)).setName("§8» §6Reduce-NPC hit").setLore("§7Currently selected §8» §d" + npcHit.getString()).build(), 3, event -> {
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
            if (npcHit == HitType.EASY) {
                npcHit = HitType.MEDIUM;
            } else if (npcHit == HitType.MEDIUM) {
                npcHit = HitType.HARD;
            } else if (npcHit == HitType.HARD) {
                npcHit = HitType.EASY;
            }
            openIngameSettings();
            updateScoreboard();
        });


        inventory.setItem(new ItemBuilder(Material.STAINED_GLASS, 1, (byte) getHitInt(firstHit)).setName("§8» §6First hit").setLore("§7Currently selected §8» §d" + firstHit.getString()).build(), 12, event -> {
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
            if (firstHit == HitType.EASY) {
                firstHit = HitType.MEDIUM;
            } else if (firstHit == HitType.MEDIUM) {
                firstHit = HitType.HARD;
            } else if (firstHit == HitType.HARD) {
                firstHit = HitType.EASY;
            }
            openIngameSettings();
            updateScoreboard();
        });

        inventory.setItem(new ItemBuilder(Material.STAINED_GLASS, 1, (byte) getHitInt(secondHit)).setName("§8» §6Second hit").setLore("§7Currently selected §8» §d" + secondHit.getString()).build(), 13, event -> {
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
            if (secondHit == HitType.NONE) {
                secondHit = HitType.EASY;
            } else if (secondHit == HitType.EASY) {
                secondHit = HitType.MEDIUM;
            } else if (secondHit == HitType.MEDIUM) {
                secondHit = HitType.HARD;
            } else if (secondHit == HitType.HARD) {
                secondHit = HitType.NONE;
            }
            openIngameSettings();
            updateScoreboard();
        });


        inventory.setItem(new ItemBuilder(Material.STAINED_GLASS, 1, (byte) getHitInt(thirdHit)).setName("§8» §6Third hit").setLore("§7Currently selected §8» §d" + thirdHit.getString()).build(), 14, event -> {
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
            if (thirdHit == HitType.NONE) {
                thirdHit = HitType.EASY;
            } else if (thirdHit == HitType.EASY) {
                thirdHit = HitType.MEDIUM;
            } else if (thirdHit == HitType.MEDIUM) {
                thirdHit = HitType.HARD;
            } else if (thirdHit == HitType.HARD) {
                thirdHit = HitType.NONE;
            }
            openIngameSettings();
            updateScoreboard();
        });


        inventory.setItem(new ItemBuilder(Material.STAINED_GLASS, 1, (byte) getHitInt(fourthHit)).setName("§8» §6Fourth hit").setLore("§7Currently selected §8» §d" + fourthHit.getString()).build(), 15, event -> {
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
            if (fourthHit == HitType.NONE) {
                fourthHit = HitType.EASY;
            } else if (fourthHit == HitType.EASY) {
                fourthHit = HitType.MEDIUM;
            } else if (fourthHit == HitType.MEDIUM) {
                fourthHit = HitType.HARD;
            } else if (fourthHit == HitType.HARD) {
                fourthHit = HitType.NONE;
            }
            openIngameSettings();
            updateScoreboard();
        });


        if (arenaEntry != null)
            setPause(true);


        player.openInventory(inventory.getInventory());
    }

    public void openSettings() {

        Inventory inventory = new Inventory("§8» §6Settings", 9);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        inventory.setItem(new ItemBuilder(Material.DIAMOND, 1, (byte) 0).setName("§8» §6Perks").build(), 5, event -> {
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
            BukkitCore.getInstance().getPerkManager().openMainPerkInventory(player);
        });

        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setName("§8» §6NPC Skin").build(), 3, event -> {

            Inventory npcSkinInventory = new Inventory("§8» §6NPC Skins", 18);

            for (int i = 0; i < 18; i++) {
                npcSkinInventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
            }

            int i = 0;
            for(NPCSkin npcSkin : NPCSkin.values()) {
                ItemBuilder itemBuilder = new ItemBuilder(Material.SKULL_ITEM,1, (byte) 3).setName("§8» §6" + npcSkin.getName());
                SkinEntry skinEntry = BukkitHolyAPI.getInstance().getBukkitCacheHandler().getSkinEntryHashMap().get(npcSkin.getUuid());
                itemBuilder.setSkullMeta(skinEntry.getValue(),skinEntry.getSignature());

                if (npcSkin == this.npcSkin) {
                    itemBuilder.setLore("§2selected");
                    itemBuilder.setEnchantments(Enchantment.DURABILITY,1);
                    itemBuilder.setAttributs();
                }

                npcSkinInventory.setItem(itemBuilder.build(),i,event1 -> {
                    player.closeInventory();
                    setNpcSkin(npcSkin);
                    player.sendMessage(Clutches.PREFIX + "selected §6" + npcSkin.getName());
                });
                i++;
            }

            player.openInventory(npcSkinInventory.getInventory());

        });

        inventory.setItem(new ItemBuilder(Material.REDSTONE_COMPARATOR, 1, (byte) 0).setName("§8» §6Ingame settings").build(), 1, event -> {
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
            openIngameSettings();
        });

        inventory.setItem(new ItemBuilder(Material.ARMOR_STAND, 1, (byte) 0).setName("§8» §6Inventory Sort").build(), 7, event -> {
            org.bukkit.inventory.Inventory sortInv = Bukkit.createInventory(null, 9, "§8» §6Inventory Sort");
            sortInv.setContents(getInventory().getContents());
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 100.0F);
            player.openInventory(sortInv);
            player.getInventory().clear();
        });
        player.openInventory(inventory.getInventory());
    }

    public void saveData() {
        GameProfile statsProfile = BukkitCore.getAPI().getGameService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()));


        statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"invsort",InventoryUtils.inventoryToString(inventory));
        statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"delay",String.valueOf(delay));
        statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"npcAirHits", String.valueOf(npcAirHits));
        statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"multiReduceNpcDistance",String.valueOf(multiReduceNpcDistance));

        statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"firstHit",firstHit.toString());
        statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"secondHit",secondHit.toString());
        statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"thirdHit",thirdHit.toString());
        statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"fourthHit",fourthHit.toString());
        statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"npcHit",npcHit.toString());

        statsProfile.setSetting(Gamemodes.CLUTCHES.toString(),"npcSkin",String.valueOf(npcSkin.getId()));

        BukkitCore.getAPI().getGameService().saveEntity(statsProfile, true, true);
    }

    public void setItemsSpawn() {
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.getInventory().setItem(2, new ItemBuilder(Material.EYE_OF_ENDER).setName("§8» §6Spectate §8(§7rightclick§8)").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.REDSTONE_COMPARATOR).setName("§8» §6Settings §8(§7rightclick§8)").build());
        player.getInventory().setItem(6, new ItemBuilder(Material.SLIME_BALL).setName("§8» §6Leave §8(§7rightclick§8)").build());
    }

    public void updateScoreboard() {
        if (arenaEntry != null) {
            if (arenaType == ArenaType.REDUCE || arenaType == ArenaType.EXPERIMENTAL) {
                scoreboardAPI.updateLine(3, " §7NPC hit§8: §b" + npcHit.getString());
            } else {
                scoreboardAPI.updateLine(6, " §7First hit§8: §b" + firstHit.getString());
                scoreboardAPI.updateLine(5, " §7Second hit§8: §b" + secondHit.getString());
                scoreboardAPI.updateLine(4, " §7Third hit§8: §b" + thirdHit.getString());
                scoreboardAPI.updateLine(3, " §7Fourth hit§8: §b" + fourthHit.getString());
            }
        }
    }

    public void setScoreboard() {
        scoreboardAPI.clearScoreboard();
        if (playerState == PlayerState.LOBBY) {
            scoreboardAPI.setLine(8, " §8§m--------------- ");
            scoreboardAPI.setLine(7, "§7");
            scoreboardAPI.setLine(6, " §ftrain your");
            scoreboardAPI.setLine(5, " §fclutche skills");
            scoreboardAPI.setLine(4, " §fto become the");
            scoreboardAPI.setLine(3, " §cbest §freducer!");
            scoreboardAPI.setLine(2, "§5");
            scoreboardAPI.setLine(1, " §8§m--------------- ");
            scoreboardAPI.setLine(0,"§o"+Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName());
        } else if (playerState == PlayerState.SPECTATE) {
            scoreboardAPI.setLine(5, " §8§m--------------- ");
            scoreboardAPI.setLine(4,"§2");
            scoreboardAPI.setLine(3, " §7Arena§8: §b" + arenaEntry.getMapEntry().getName());
            scoreboardAPI.setLine(2, "§5");
            scoreboardAPI.setLine(1, " §8§m--------------- ");
            scoreboardAPI.setLine(0,"§o"+Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName());
        } else {
            if (arenaType == ArenaType.REDUCE || arenaType == ArenaType.EXPERIMENTAL) {

                scoreboardAPI.setLine(7, " §8§m--------------- ");
                scoreboardAPI.setLine(6,"§1");
                scoreboardAPI.setLine(5, " §7Arena§8: §b" + arenaEntry.getMapEntry().getName());
                scoreboardAPI.setLine(4, "§2");
                scoreboardAPI.setLine(3, " §7NPC hit§8: §b" + npcHit.getString());
                scoreboardAPI.setLine(2, "§3");
                scoreboardAPI.setLine(1, " §8§m--------------- ");
                scoreboardAPI.setLine(0,"§o"+Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName());

            } else {



                scoreboardAPI.setLine(11, " §8§m--------------- ");
                scoreboardAPI.setLine(10,"§1");
                scoreboardAPI.setLine(9, " §7Arena§8: §b" + arenaEntry.getMapEntry().getName());
                scoreboardAPI.setLine(8, " §7Mode§8: §b" + arenaType.getName());
                scoreboardAPI.setLine(7, "§2");
                scoreboardAPI.setLine(6, " §7First hit§8: §b" + firstHit.getString());
                scoreboardAPI.setLine(5, " §7Second hit§8: §b" + secondHit.getString());
                scoreboardAPI.setLine(4, " §7Third hit§8: §b" + thirdHit.getString());
                scoreboardAPI.setLine(3, " §7Fourth hit§8: §b" + fourthHit.getString());
                scoreboardAPI.setLine(2, "§3");
                scoreboardAPI.setLine(1, " §8§m--------------- ");
                scoreboardAPI.setLine(0,"§o"+Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName());
            }
        }
        scoreboardAPI.build();
    }

}
