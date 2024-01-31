package de.teamholy.bridge.player.management;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.player.settings.sounds.BridgeSound;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.player.PlayerRepository;
import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PerkManagement {

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

}
