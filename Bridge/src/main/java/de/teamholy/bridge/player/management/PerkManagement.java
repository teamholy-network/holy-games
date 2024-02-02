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
import de.teamholy.core.api.entities.player.PlayerRepository;
import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PerkManagement {

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

            Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), radioSongPlayer::destroy, 20 * 10L);
        } else {
            player.playSound(player.getLocation(), sound.getBukkitSound(), sound.getVolume(), sound.getPitch());
        }
    }

    public void buyPerk(BridgePlayer bridgePlayer, BridgeSound bridgeSound) {
        PerkPlayerProfile perkProfile = bridgePlayer.getPerkPlayerProfile();

        PlayerProfile playerProfile = (PlayerProfile) BukkitCore.getInstance().getCoreAPI().getPlayerService().getEntity(bridgePlayer.getUuid(), () -> {
            return (PlayerProfile) ((PlayerRepository) BukkitCore.getInstance().getCoreAPI().getPlayerService().getRepository()).findFirstById(bridgePlayer.getUuid());
        });

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
                case DEATH ->
                        bridgePlayer.getSettings().getSoundEvents().putIfAbsent(bridgeSound, Settings.BridgeSoundEventType.DEATH); // default value
                case WIN ->
                        bridgePlayer.getSettings().getSoundEvents().putIfAbsent(bridgeSound, Settings.BridgeSoundEventType.WIN); // default value
                default -> throw new IllegalStateException("Unexpected value: " + bridgeSound.getSoundType());
            }

            bukkitPlayer.sendMessage(Bridge.PREFIX + "§7You successfully bought the §e" + bridgeSound.getDisplayName() + " §7perk for §e" + bridgeSound.getPrice() + " §6coins!");
            bukkitPlayer.sendMessage(Bridge.PREFIX + "§7Automatically selected the perk!");
            bridgePlayer.getSettings().setCurrentSound(bridgeSound);
            bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.LEVEL_UP, 2.0F, 2.0F);
        }
    }

    public Inventory openSoundsPerkInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§8» §6Sound Settings");

        inventory.setItem(11, new ItemBuilder(Material.SKULL_ITEM).amount(1).name("§7Type§8: §cSad Sounds").lore("§7Click to open the sad sound settings").build());
        inventory.setItem(13, new ItemBuilder(Material.NETHER_STAR).amount(1).name("§7Type§8: §aHappy Sounds").lore("§7Click to open the happy sound settings").build());
        inventory.setItem(15, new ItemBuilder(Material.NOTE_BLOCK).amount(1).name("§7Type§8: §fMusic").lore("§7Click to open the music settings").build());

        return inventory;
    }

    public de.teamholy.core.bukkit.utils.Inventory openSoundInventory(BridgePlayer bridgePlayer, BridgeSoundType bridgeSoundType) {
        int size = 4; // 4 rows by default

        int listSize = Arrays.stream(BridgeSounds.values()).filter(bridgeSounds -> bridgeSounds.getBridgeSound().getSoundType() == bridgeSoundType).toList().size();

        if (listSize > 9 && listSize <= 18) {
            size = 5; // 5 rows if there are more than 9 sounds
        } else if (listSize > 18) {
            size = 6; // 6 rows if there are more than 18 sounds
        }

        de.teamholy.core.bukkit.utils.Inventory holyInventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6" + bridgeSoundType.getName() + " Settings", size * 9);
        holyInventory.setOnClose(event -> Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> event.getPlayer().openInventory(openSoundsPerkInventory()), 1L));

        int slot = 10;

        for (BridgeSounds sounds : BridgeSounds.values()) {
            BridgeSound sound = sounds.getBridgeSound();
            if (sound.getSoundType() != bridgeSoundType) {
                continue;
            }
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
                                            case DEATH ->
                                                    bridgePlayer.getSettings().getSoundEvents().putIfAbsent(bridgeSound, Settings.BridgeSoundEventType.DEATH); // default value
                                            case WIN ->
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
                                            Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> bridgePlayer.getPlayer().openInventory(openSoundInventory(bridgePlayer, bridgeSound.getSoundType()).getInventory()), 1L));

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
                            holyInventory.setOnClose(empty -> {
                            });
                            bridgePlayer.getPlayer().openInventory(openSoundEventTypeInventory(bridgePlayer, sound));
                        }

                    });
            slot++;
            if (slot > 16 && slot < 19) {
                slot = 19;
            } else if (slot == 26) break;
        }
        holyInventory.setItem(new ItemBuilder(Material.BARRIER).amount(1).name("§c§lClear").build(), holyInventory.getInventory().getSize() - 5, event -> {
            bridgePlayer.getSettings().setCurrentSound(null);
            bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "You cleared the sound!");
        });

        return holyInventory;
    }

    public Inventory openSoundEventTypeInventory(BridgePlayer bridgePlayer, BridgeSound bridgeSound) {
        de.teamholy.core.bukkit.utils.Inventory holyInventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6" + bridgeSound.getDisplayName() + " Settings", 9 * 3);

        holyInventory.setOnClose(event -> Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), () -> event.getPlayer().openInventory(openSoundInventory(bridgePlayer, bridgeSound.getSoundType()).getInventory()), 1L));

        Settings.BridgeSoundEventType glowWin = bridgePlayer.getSettings().getSoundEvents().get(bridgeSound);

        holyInventory.setItem(new ItemBuilder(Material.INK_SACK).amount(1).durability(1).name("§c§lPlay on Death")
                .withGlow(glowWin == Settings.BridgeSoundEventType.DEATH).build(), 11, event -> {

            bridgePlayer.getSettings().getSoundEvents().entrySet().removeIf(entry -> entry.getValue() != null && entry.getValue() != Settings.BridgeSoundEventType.DEATH);
            bridgePlayer.getSettings().getSoundEvents().put(bridgeSound, Settings.BridgeSoundEventType.DEATH);

            bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "You set the event of §e" + bridgeSound.getDisplayName() + "§7 to §c§lDeath!");
            bridgePlayer.getPlayer().closeInventory();
        });
        holyInventory.setItem(new ItemBuilder(Material.INK_SACK).amount(1).durability(2).name("§a§lPlay on Win")
                .withGlow(glowWin == Settings.BridgeSoundEventType.WIN).build(), 13, event -> {

            bridgePlayer.getSettings().getSoundEvents().entrySet().removeIf(entry -> entry.getValue() != null && entry.getValue() != Settings.BridgeSoundEventType.WIN);
            bridgePlayer.getSettings().getSoundEvents().put(bridgeSound, Settings.BridgeSoundEventType.WIN);

            bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "You set the event of §e" + bridgeSound.getDisplayName() + "§7 to §a§lWin!");

            bridgePlayer.getPlayer().closeInventory();
        });

        holyInventory.setItem(new ItemBuilder(Material.DIAMOND).amount(1).name("§b§lPlay on Record")
                .withGlow(glowWin == Settings.BridgeSoundEventType.NEW_RECORD).build(), 15, event -> {

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
            lore.add("§7This sound costs §e" + sound.getPrice() + " §6coins");
        } else {
            if (bridgePlayer.getSettings().getCurrentSound() == sound) {
                lore.add("§2selected");
                lore.add(" ");
                lore.add("§7§oright click, to change the event");
                lore.add("§7§owhere this sound is played");
            } else {
                lore.add("§ayou own this sound");
            }
        }
        return lore;
    }
}
