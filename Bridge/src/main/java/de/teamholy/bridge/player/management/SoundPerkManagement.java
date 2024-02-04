package de.teamholy.bridge.player.management;

import com.google.common.collect.Lists;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.settings.Settings;
import de.teamholy.bridge.player.settings.sounds.BridgeSong;
import de.teamholy.bridge.player.settings.sounds.BridgeSound;
import de.teamholy.bridge.player.settings.sounds.BridgeSoundType;
import de.teamholy.bridge.player.settings.sounds.BridgeSounds;
import de.teamholy.bridge.util.ItemBuilder;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.utility.Pagifier;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.PerkManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class SoundPerkManagement {

    public static final int MAX_SOUNDS_PER_PAGE = 21;
    private final int radioSongPlayerDestroyDelay = 20 * 5;
    private final PlayerManagement playerManagement = Bridge.getInstance().getPlayerManagement();

    public void playSoundPerk(BridgePlayer bridgePlayer, boolean win, boolean record) {
        Player bukkitPlayer = bridgePlayer.getPlayer();
        if (bukkitPlayer == null) {
            return;
        }

        var sound = bridgePlayer.getSettings().getCurrentSound();

        if (sound == null) {
            return;
        }

        Settings.BridgeSoundEventType eventType = bridgePlayer.getSettings().getSoundEvents().get(sound);
        if (eventType == null) return;


        switch (eventType) {
            case WIN -> {
                if (!win) {
                    return;
                }
                playSound(bukkitPlayer, sound);
            }
            case DEATH -> {
                if (win) {
                    return;
                }
                playSound(bukkitPlayer, sound);
            }

            case NEW_RECORD -> {
                if (!win && !record || win && !record) {
                    return;
                }

                playSound(bukkitPlayer, sound);
            }
        }
    }

    private void playSound(Player player, BridgeSound sound) {
        if (sound.getSoundType() == BridgeSoundType.SONG) {
            BridgeSong bridgeSong = Bridge.getInstance().getSongManager().getSong(sound.getName().split("_")[1].toLowerCase());
            Song song = NBSDecoder.parse(bridgeSong.getFile());

            RadioSongPlayer radioSongPlayer = new RadioSongPlayer(song);
            radioSongPlayer.addPlayer(player);
            radioSongPlayer.setPlaying(true);

            Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), radioSongPlayer::destroy, radioSongPlayerDestroyDelay);
        } else {
            player.playSound(player.getLocation(), sound.getBukkitSound(), sound.getVolume(), sound.getPitch());
        }
    }

    public void buyPerk(BridgePlayer bridgePlayer, BridgeSound bridgeSound) {
        PerkPlayerProfile perkProfile = bridgePlayer.getPerkPlayerProfile();

        PlayerProfile playerProfile = BukkitCore.getInstance().getCoreAPI().getPlayerService().getEntity(bridgePlayer.getUuid(), () -> BukkitCore.getInstance().getCoreAPI().getPlayerService().getRepository().findFirstById(bridgePlayer.getUuid()));

        if (playerProfile == null) {
            return;
        }

        if (perkProfile.getOwnedPerks().contains(bridgeSound.getPerkId())) {
            bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "§cYou already own this perk!");
            return;
        }

        if (playerProfile.getCoins() < bridgeSound.getPrice()) {
            bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "§cYou don't have enough coins to buy this perk!");
            return;
        }

        playerProfile.setCoins(playerProfile.getCoins() - bridgeSound.getPrice());
        perkProfile.getOwnedPerks().add(bridgeSound.getPerkId());
        BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().put(bridgePlayer.getUuid(), perkProfile);
        BukkitCore.getAPI().getPerkPlayerService().saveEntity(perkProfile, true, true);
        BukkitCore.getAPI().getPlayerService().saveEntity(playerProfile, true, true);

        bridgePlayer.getSettings().getSounds().add(bridgeSound);

        Player bukkitPlayer = bridgePlayer.getPlayer();
        if (bukkitPlayer != null) {
            switch (bridgeSound.getSoundType()) {
                case SONG ->
                        bridgePlayer.getSettings().getSoundEvents().putIfAbsent(bridgeSound, Settings.BridgeSoundEventType.NEW_RECORD); // default value
                case SAD ->
                        bridgePlayer.getSettings().getSoundEvents().putIfAbsent(bridgeSound, Settings.BridgeSoundEventType.DEATH); // default value
                case HAPPY ->
                        bridgePlayer.getSettings().getSoundEvents().putIfAbsent(bridgeSound, Settings.BridgeSoundEventType.WIN); // default value
                default -> throw new IllegalStateException("Unexpected value: " + bridgeSound.getSoundType());
            }

            bukkitPlayer.sendMessage(Bridge.PREFIX + "§7You successfully bought the §e" + bridgeSound.getDisplayName() + " §7perk for §e" + bridgeSound.getPrice() + " §6coins!");
            bukkitPlayer.sendMessage(Bridge.PREFIX + "§7Automatically selected the perk!");
            bridgePlayer.getSettings().setCurrentSound(bridgeSound);
            bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.LEVEL_UP, 2.0F, 2.0F);
        }
    }


    public de.teamholy.core.bukkit.utils.Inventory openSoundInventory(BridgePlayer bridgePlayer, BridgeSoundType bridgeSoundType, PerkManager.SortOptionPerk sortOptionPerk, PerkManager.SortOptionPlayer sortOptionPlayer, int page) {
        var size = 4; // 4 rows by default

        List<BridgeSound> sounds = Arrays.stream(BridgeSounds.values())
                .map(BridgeSounds::getBridgeSound)
                .filter(sound ->
                        switch (bridgeSoundType) {
                            case SONG -> sound.getSoundType() == BridgeSoundType.SONG;
                            case SAD -> sound.getSoundType() == BridgeSoundType.SAD;
                            case HAPPY -> sound.getSoundType() == BridgeSoundType.HAPPY;
                            default -> true;
                        }
                )
                .filter(sound ->
                        switch (sortOptionPlayer) {
                            case OWNED -> (sound.isBuyable() && bridgePlayer.getSettings().getSounds().contains(sound)
                                    || (!sound.isBuyable() && bridgePlayer.getPlayer().hasPermission(sound.getPerkRankType().getPermission()))
                                    || (sound.isSpecial() && bridgePlayer.getSettings().getSounds().contains(sound))
                            );
                            case UNOWNED ->
                                    (sound.isBuyable() && !bridgePlayer.getSettings().getSounds().contains(sound)
                                            || (!sound.isBuyable() && !bridgePlayer.getPlayer().hasPermission(sound.getPerkRankType().getPermission()))
                                            || (sound.isSpecial() && !bridgePlayer.getSettings().getSounds().contains(sound)));
                            default -> true;
                        }
                )
                .filter(sound -> {
                    switch (sortOptionPerk) {
                        case COINS -> {
                            return sound.isBuyable();
                        }
                        case RANK -> {
                            return !sound.isBuyable();
                        }
                        case SPECIAL -> {
                            return sound.isSpecial();
                        }
                        default -> {
                            return true;
                        }
                    }
                })
                .sorted((sound1, sound2) -> {
                    switch (sortOptionPerk) {
                        case COINS -> {
                            return Integer.compare((int) sound1.getPrice(), (int) sound2.getPrice());
                        }
                        case RANK -> {
                            return sound1.getPerkRankType().compareTo(sound2.getPerkRankType());
                        }
                        default -> {
                            return Comparator.comparing(BridgeSound::getPerkId).compare(sound1, sound2);
                        }
                    }
                })
                .toList();

        var pagifier = bridgePlayer.getSoundPagifier();
        pagifier.reset();
        sounds.forEach(pagifier::addItem);
        sounds.forEach(pagifier::addItem);


        var pageSounds = pagifier.getPage(page);

        if (pageSounds.size() >= 7 && pageSounds.size() <= 14) {
            size = 5;
        } else if (pageSounds.size() >= 15) {
            size = 6;
        }

        var inventorySize = size * 9;

        de.teamholy.core.bukkit.utils.Inventory holyInventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Sound Perks §e" + (page), inventorySize);


        de.teamholy.core.bukkit.utils.ItemBuilder sortPerk = new de.teamholy.core.bukkit.utils.ItemBuilder(Material.HOPPER).setName("§8» §6Sort");
        de.teamholy.core.bukkit.utils.ItemBuilder sortPlayer = new de.teamholy.core.bukkit.utils.ItemBuilder(Material.DIAMOND).setName("§8» §6Filter");
        de.teamholy.core.bukkit.utils.ItemBuilder sortType = new de.teamholy.core.bukkit.utils.ItemBuilder(Material.NOTE_BLOCK).setName("§8» §6Sound types");

        sortPerk.setLore(Arrays.stream(PerkManager.SortOptionPerk.values())
                .map(value -> (sortOptionPerk == value) ? "§a" + value.toString().toLowerCase(Locale.ROOT) : "§7" + value.toString().toLowerCase(Locale.ROOT))
                .collect(Collectors.toList()));

        sortPlayer.setLore(Arrays.stream(PerkManager.SortOptionPlayer.values())
                .map(value -> (sortOptionPlayer == value) ? "§a" + value.toString().toLowerCase(Locale.ROOT) : "§7" + value.toString().toLowerCase(Locale.ROOT))
                .collect(Collectors.toList()));

        sortType.setLore(Arrays.stream(BridgeSoundType.values())
                .map(value -> (bridgeSoundType == value) ? "§a" + value.toString().toLowerCase(Locale.ROOT) : "§7" + value.toString().toLowerCase(Locale.ROOT))
                .collect(Collectors.toList()));

        holyInventory.setItem(sortPerk.build(), inventorySize - 9, event -> {
            var ordinal = sortOptionPerk.ordinal();
            var length = PerkManager.SortOptionPerk.values().length;
            PerkManager.SortOptionPerk next = PerkManager.SortOptionPerk.values()[(ordinal + 1) % length];
            bridgePlayer.getPlayer().playSound(bridgePlayer.getPlayer().getLocation(), Sound.CLICK, 1.0F, 100.0F);
            bridgePlayer.getPlayer().openInventory(openSoundInventory(bridgePlayer, bridgeSoundType, next, sortOptionPlayer, 1).getInventory());
        });

        holyInventory.setItem(sortPlayer.build(), inventorySize - 8, event -> {
            var ordinal = sortOptionPlayer.ordinal();
            var length = PerkManager.SortOptionPlayer.values().length;
            PerkManager.SortOptionPlayer next = PerkManager.SortOptionPlayer.values()[(ordinal + 1) % length];
            bridgePlayer.getPlayer().playSound(bridgePlayer.getPlayer().getLocation(), Sound.CLICK, 1.0F, 100.0F);
            bridgePlayer.getPlayer().openInventory(openSoundInventory(bridgePlayer, bridgeSoundType, sortOptionPerk, next, 1).getInventory());
        });

        holyInventory.setItem(sortType.build(), inventorySize - 7, event -> {
            var ordinal = bridgeSoundType.ordinal();
            var length = BridgeSoundType.values().length;
            BridgeSoundType next = BridgeSoundType.values()[(ordinal + 1) % length];
            bridgePlayer.getPlayer().playSound(bridgePlayer.getPlayer().getLocation(), Sound.CLICK, 1.0F, 100.0F);
            bridgePlayer.getPlayer().openInventory(openSoundInventory(bridgePlayer, next, sortOptionPerk, sortOptionPlayer, 1).getInventory());
        });

        holyInventory.setItem(new de.teamholy.core.bukkit.utils.ItemBuilder(Material.BARRIER).setName("§8» §cReset all").build(), inventorySize - 5, event -> {
            bridgePlayer.setSoundPagifier(new Pagifier<>(MAX_SOUNDS_PER_PAGE));
            bridgePlayer.getPlayer().playSound(bridgePlayer.getPlayer().getLocation(), Sound.ANVIL_BREAK, 1.0F, 100.0F);
            bridgePlayer.getPlayer().openInventory(openSoundInventory(bridgePlayer, BridgeSoundType.ALL, PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL, 1).getInventory());
        });

        if (page > 1) {
            holyInventory.setItem(new de.teamholy.core.bukkit.utils.ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setAttributs().setName("§8» §6Previous page").setSkullMeta(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1Z" +
                            "GFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==", "").build(), inventorySize - 3, event -> {
                bridgePlayer.getPlayer().playSound(bridgePlayer.getPlayer().getLocation(), Sound.CHICKEN_EGG_POP, 2.0F, 2.0F);
                bridgePlayer.getPlayer().openInventory(openSoundInventory(bridgePlayer, bridgeSoundType, sortOptionPerk, sortOptionPlayer, page - 1).getInventory());
            });
        }

        if (pagifier.getPage(page + 1) != null) {
            holyInventory.setItem(new de.teamholy.core.bukkit.utils.ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setAttributs().setName("§8» §6Next page").setSkullMeta(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMT" +
                            "I2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19", "").build(), inventorySize - 1, event -> {
                bridgePlayer.getPlayer().playSound(bridgePlayer.getPlayer().getLocation(), Sound.CHICKEN_EGG_POP, 2.0F, 2.0F);
                bridgePlayer.getPlayer().openInventory(openSoundInventory(bridgePlayer, bridgeSoundType, sortOptionPerk, sortOptionPlayer, page + 1).getInventory());
            });
        }


        int slot = 10;

        for (BridgeSound sound : pageSounds) {
            List<String> lore = getLore(bridgePlayer, sound);

            holyInventory.setItem(
                    new ItemBuilder(sound.getMaterial()).amount(1).name(sound.getDisplayName())
                            .withGlow(bridgePlayer.getSettings().getSounds().contains(sound)).lore(lore).build(), slot, event -> {

                        var clickedItem = event.getCurrentItem();
                        if (clickedItem == null) return;

                        var clickedItemMeta = clickedItem.getItemMeta();
                        if (clickedItemMeta == null) return;

                        if (event.getClick() == ClickType.LEFT) {
                            for (BridgeSounds bridgeSounds : BridgeSounds.values()) {
                                if (bridgeSounds.getBridgeSound().getDisplayName().equalsIgnoreCase(clickedItemMeta.getDisplayName())) {
                                    var bridgeSound = bridgeSounds.getBridgeSound();

                                    if (bridgePlayer.getSettings().getSounds().contains(bridgeSound)) {
                                        switch (bridgeSound.getSoundType()) {
                                            case SONG ->
                                                    bridgePlayer.getSettings().getSoundEvents().putIfAbsent(bridgeSound, Settings.BridgeSoundEventType.NEW_RECORD); // default value
                                            case SAD ->
                                                    bridgePlayer.getSettings().getSoundEvents().putIfAbsent(bridgeSound, Settings.BridgeSoundEventType.DEATH); // default value
                                            case HAPPY ->
                                                    bridgePlayer.getSettings().getSoundEvents().putIfAbsent(bridgeSound, Settings.BridgeSoundEventType.WIN); // default value
                                            default ->
                                                    throw new IllegalStateException("Unexpected value: " + bridgeSound.getSoundType());
                                        }

                                        bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "You set the sound to §e" + bridgeSound.getDisplayName() + "§7!");
                                        bridgePlayer.getSettings().setCurrentSound(bridgeSound);
                                    } else {
                                        buyPerk(bridgePlayer, bridgeSound);
                                    }

                                    holyInventory.setOnClose(empty ->
                                            Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> bridgePlayer.getPlayer().openInventory(openSoundInventory(bridgePlayer, bridgeSound.getSoundType(), PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL, 0).getInventory()), 1L));

                                    bridgePlayer.getPlayer().closeInventory();

                                    return;
                                }
                            }
                        } else if (event.getClick() == ClickType.RIGHT) {
                            if (!bridgePlayer.getSettings().getSounds().contains(sound)) {
                                bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "§cYou don't own this sound!");
                                return;
                            }

                            /*
                            if (bridgePlayer.getSettings().getCurrentSound() != sound) {
                                bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "§cYou don't have selected this perk!");
                                return;
                            }*/
                            bridgePlayer.getPlayer().openInventory(openSoundEventTypeInventory(bridgePlayer, sound));
                        }

                    });
            slot++;
            if (slot > 16 && slot < 19) {
                slot = 19;
            } else if (slot > 25 && slot < 28) {
                slot = 28;
            }
        }

        return holyInventory;
    }

    public Inventory openSoundEventTypeInventory(BridgePlayer bridgePlayer, BridgeSound bridgeSound) {
        de.teamholy.core.bukkit.utils.Inventory holyInventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6" + bridgeSound.getDisplayName() + " Settings", 9 * 3);

        holyInventory.setOnClose(event -> Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> event.getPlayer().openInventory(openSoundInventory(bridgePlayer, bridgeSound.getSoundType(), PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL, 0).getInventory()), 1L));

        Settings.BridgeSoundEventType glowWin = bridgePlayer.getSettings().getSoundEvents().get(bridgeSound);

        holyInventory.setItem(new ItemBuilder(Material.INK_SACK).amount(1).durability(1).name("§c§lPlay on Death")
                .withGlow(glowWin == Settings.BridgeSoundEventType.DEATH)
                .lore(glowWin == Settings.BridgeSoundEventType.DEATH ? "§aSelected" : "§7Click to select").build(), 11, event -> {

            bridgePlayer.getSettings().getSoundEvents().entrySet().removeIf(entry -> entry.getValue() != null && entry.getValue() != Settings.BridgeSoundEventType.DEATH);
            bridgePlayer.getSettings().getSoundEvents().put(bridgeSound, Settings.BridgeSoundEventType.DEATH);

            bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "You set the event of §e" + bridgeSound.getDisplayName() + "§7 to §c§lDeath!");
            bridgePlayer.getPlayer().closeInventory();
        });
        holyInventory.setItem(new ItemBuilder(Material.INK_SACK).amount(1).durability(2).name("§a§lPlay on Win")
                .withGlow(glowWin == Settings.BridgeSoundEventType.WIN)
                .lore(glowWin == Settings.BridgeSoundEventType.WIN ? "§aSelected" : "§7Click to select").build(), 13, event -> {

            bridgePlayer.getSettings().getSoundEvents().entrySet().removeIf(entry -> entry.getValue() != null && entry.getValue() != Settings.BridgeSoundEventType.WIN);
            bridgePlayer.getSettings().getSoundEvents().put(bridgeSound, Settings.BridgeSoundEventType.WIN);

            bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "You set the event of §e" + bridgeSound.getDisplayName() + "§7 to §a§lWin!");

            bridgePlayer.getPlayer().closeInventory();
        });

        holyInventory.setItem(new ItemBuilder(Material.DIAMOND).amount(1).name("§b§lPlay on Record")
                .withGlow(glowWin == Settings.BridgeSoundEventType.NEW_RECORD)
                .lore(glowWin == Settings.BridgeSoundEventType.NEW_RECORD ? "§aSelected" : "§7Click to select").build(), 15, event -> {

            bridgePlayer.getSettings().getSoundEvents().entrySet().removeIf(entry -> entry.getValue() != null && entry.getValue() != Settings.BridgeSoundEventType.NEW_RECORD);
            bridgePlayer.getSettings().getSoundEvents().put(bridgeSound, Settings.BridgeSoundEventType.NEW_RECORD);

            bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "You set the event of §e" + bridgeSound.getDisplayName() + "§7 to §b§lRecord!");

            bridgePlayer.getPlayer().closeInventory();
        });

        return holyInventory.getInventory();
    }

    @Nonnull
    private static List<String> getLore(BridgePlayer bridgePlayer, BridgeSound sound) {
        List<String> lore = Lists.newArrayList();

        if (bridgePlayer.getSettings().getSounds().isEmpty() || !bridgePlayer.getSettings().getSounds().contains(sound)) {
            if (sound.isBuyable()) {
                lore.add("§7This sound costs §e" + sound.getPrice() + " §6coins");
            } else {
                if (bridgePlayer.getPlayer().hasPermission(sound.getPerkRankType().getPermission())) {
                    lore.add("§aYou own this sound!");
                } else {
                    lore.add("§7Available for " + sound.getPerkRankType().getRankName() + "§7 and above");
                }
            }
        } else {
            if (bridgePlayer.getSettings().getCurrentSound() == sound) {
                lore.add("§2Selected");
            } else {
                lore.add("§aYou own this sound!");
            }
            lore.add(" ");
            lore.add("§7§oRight click, to change the event");
            lore.add("§7§owhere this sound is played.");
            lore.add(" ");
            lore.add("§c§oOnly plays the sound if it is selected!");
        }
        return lore;
    }
}
