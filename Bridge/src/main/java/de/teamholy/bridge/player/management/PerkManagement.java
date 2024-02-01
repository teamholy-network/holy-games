package de.teamholy.bridge.player.management;

import com.google.common.collect.Lists;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.player.BridgePlayer;
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
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PerkManagement {

    public void playSoundPerk(BridgePlayer bridgePlayer, boolean win) {
        Player bukkitPlayer = bridgePlayer.getPlayer();
        if (bukkitPlayer == null) {
            Bukkit.broadcastMessage("player is null");
            return;
        }

        var sound = bridgePlayer.getSettings().getCurrentSound();

        if (sound == null) {
            return;
        }

        if (win) {
            if (sound.getSoundType() == BridgeSoundType.SONG) {
                BridgeSong bridgeSong = Bridge.getInstance().getSongManager().getSong(sound.getName().split("_")[1].toLowerCase());
                Song song = NBSDecoder.parse(bridgeSong.getFile());

                RadioSongPlayer radioSongPlayer = new RadioSongPlayer(song);
                radioSongPlayer.addPlayer(bukkitPlayer);
                radioSongPlayer.setPlaying(true);

                Bukkit.getScheduler().runTaskLater(Bridge.getInstance(), radioSongPlayer::destroy, 20 * 10L);
            } else {
                bukkitPlayer.playSound(bukkitPlayer.getLocation(), sound.getBukkitSound(), sound.getVolume(), sound.getPitch());
            }
        } else {
            if (sound.getSoundType() != BridgeSoundType.DEATH) return;

            bukkitPlayer.playSound(bukkitPlayer.getLocation(), sound.getBukkitSound(), sound.getVolume(), sound.getPitch());
        }
    }

    public boolean buyPerk(BridgePlayer bridgePlayer, BridgeSound bridgeSound) {
        PerkPlayerProfile perkProfile = bridgePlayer.getPerkPlayerProfile();

        PlayerProfile playerProfile = (PlayerProfile) BukkitCore.getInstance().getCoreAPI().getPlayerService().getEntity(bridgePlayer.getUuid(), () -> {
            return (PlayerProfile)((PlayerRepository)BukkitCore.getInstance().getCoreAPI().getPlayerService().getRepository()).findFirstById(bridgePlayer.getUuid());
        });

        if (playerProfile == null) {
            return false;
        }

        if (perkProfile.getOwnedPerks().contains(bridgeSound.getPerkId())) {
            bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "§cYou already own this perk!");
            return false;
        }

        if (playerProfile.getCoins() < bridgeSound.getPrice()) {
            bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "§cYou don't have enough coins to buy this perk!");
            return false;
        }

        playerProfile.setCoins(playerProfile.getCoins() - bridgeSound.getPrice());
        perkProfile.getOwnedPerks().add(bridgeSound.getPerkId());
        BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().put(bridgePlayer.getUuid(), perkProfile);
        BukkitCore.getAPI().getPerkPlayerService().saveEntity(perkProfile, true, true);
        BukkitCore.getAPI().getPlayerService().saveEntity(playerProfile, true, true);

        bridgePlayer.getSettings().getSounds().add(bridgeSound);

        Player bukkitPlayer = bridgePlayer.getPlayer();
        if (bukkitPlayer != null) {
            bukkitPlayer.sendMessage(Bridge.PREFIX + "§7You successfully bought the §e" + bridgeSound.getDisplayName() + " §7perk for §e" + bridgeSound.getPrice() + " §6coins!");
            bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.LEVEL_UP, 2.0F, 2.0F);
            bukkitPlayer.closeInventory();
        }
        return true;
    }

    public Inventory openSoundInventory(BridgePlayer bridgePlayer) {
        Inventory inventory = Bukkit.createInventory(null, 9 * 4, "§8» §6Sound Settings");

        int slot = 0;

        for (BridgeSounds sounds : BridgeSounds.values()) {
            BridgeSound sound = sounds.getBridgeSound();
            List<String> lore = getLore(bridgePlayer, sound);

            inventory.setItem(slot,
                    new ItemBuilder((sound.getSoundType() == BridgeSoundType.SONG ? Material.RECORD_3 : Material.NOTE_BLOCK)).amount(1).name(sound.getDisplayName())
                            .withGlow(bridgePlayer.getSettings().getSounds().contains(sound)).lore(lore).build());
            slot++;
            if (slot == 26) {
                break;
            }
        }
        inventory.setItem(inventory.getSize() - 5, new ItemBuilder(Material.BARRIER).amount(1).name("§c§lClear").build());
        return inventory;
    }

    @Nonnull
    private static List<String> getLore(BridgePlayer bridgePlayer, BridgeSound sound) {
        List<String> lore = Lists.newArrayList();

        if (bridgePlayer.getSettings().getSounds().isEmpty() || !bridgePlayer.getSettings().getSounds().contains(sound)) {
            lore.add("§7This sound costs §e" + sound.getPrice() + " §6coins");
        } else {
            if (bridgePlayer.getSettings().getCurrentSound() == sound) {
                lore.add("§2selected");
            } else {
                lore.add("§ayou own this perk, click to select");
            }
        }
        return lore;
    }
}
