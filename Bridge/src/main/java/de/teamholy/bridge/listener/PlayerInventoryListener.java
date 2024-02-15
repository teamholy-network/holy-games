package de.teamholy.bridge.listener;

import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.player.service.SoundPerkService;
import de.teamholy.bridge.player.settings.sounds.BridgeSoundType;
import de.teamholy.bridge.player.service.BridgePlayerService;
import de.teamholy.core.bukkit.perks.PerkManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PlayerInventoryListener implements Listener {

    private final BridgePlayerService bridgePlayerService = Bridge.getInstance().getBridgePlayerService();
    private final SoundPerkService soundPerkService = Bridge.getInstance().getSoundPerkService();

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        var view = event.getView();

        if (!view.getTitle().equalsIgnoreCase("§8» §6Inventory sort"))
            event.setCancelled(!player.isOp() && player.getGameMode() != GameMode.CREATIVE);

        if (view.getTitle().equals("§8» §6Sound Settings")) {
            event.setCancelled(true);
            var bridgePlayer = bridgePlayerService.getBridgePlayer(player);
            var soundPerkInventory = soundPerkService.openSoundInventory(bridgePlayer, BridgeSoundType.ALL, PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL, 1).getInventory();


            var clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            var clickedItemMeta = clickedItem.getItemMeta();
            if (clickedItemMeta == null) return;

            player.openInventory(soundPerkInventory);

        } else if (view.getTitle().equalsIgnoreCase("§8» §6Block Settings")) {
            event.setCancelled(true);

            var bridgePlayer = bridgePlayerService.getBridgePlayer(player);

            var clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            var clickedItemMeta = clickedItem.getItemMeta();
            if (clickedItemMeta == null) return;

            if (clickedItemMeta.getDisplayName().equalsIgnoreCase("§6Animation")) {
                player.openInventory(bridgePlayerService.blockSettingsInventory(bridgePlayer));
            }

        }
    }

}
