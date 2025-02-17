package de.teamholy.clutches.listeners;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.arena.ArenaType;
import de.teamholy.clutches.player.PlayerEntry;
import de.teamholy.clutches.player.PlayerState;
import de.teamholy.core.bukkit.npc.event.PlayerInteractAtNPCEvent;
import de.teamholy.core.bukkit.npc.event.action.InteractAction;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.TimeUnit;

public class NPCClickListener implements Listener {

    @EventHandler
    public void onNPCClick(PlayerInteractAtNPCEvent event) {
        Player player = event.getPlayer();
        PlayerEntry playerEntry = Clutches.getInstance().getPlayerEntryHandler().get(player.getUniqueId());
        if (playerEntry == null) return;
        if (playerEntry.getPlayerState() != PlayerState.LOBBY)
            return;
        if (playerEntry.getCooldown() > System.currentTimeMillis())
            return;

        if (event.getNpcEntry().getDisplayName().equalsIgnoreCase("§f§lREDUCE")) {
            playerEntry.setCooldown(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3L));
            playerEntry.getPlayer().playSound(playerEntry.getPlayer().getLocation(), Sound.CHEST_OPEN, 2f, 2f);
            playerEntry.openMapInventory(ArenaType.REDUCE);
        } else if (event.getNpcEntry().getDisplayName().equalsIgnoreCase("§f§lCLUTCH")) {
            playerEntry.setCooldown(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3L));
            playerEntry.getPlayer().playSound(playerEntry.getPlayer().getLocation(), Sound.CHEST_OPEN, 2f, 2f);
            playerEntry.openMapInventory(ArenaType.CLUTCH);
        } else if (event.getNpcEntry().getDisplayName().equalsIgnoreCase("§f§lMULTIREDUCE")) {
            playerEntry.setCooldown(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3L));
            playerEntry.getPlayer().playSound(playerEntry.getPlayer().getLocation(), Sound.CHEST_OPEN, 2f, 2f);
            playerEntry.openMapInventory(ArenaType.EXPERIMENTAL);
        } else if (event.getNpcEntry().getDisplayName().equalsIgnoreCase("§f§lDIAGONAL")) {
            playerEntry.setCooldown(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3L));
            playerEntry.getPlayer().playSound(playerEntry.getPlayer().getLocation(), Sound.CHEST_OPEN, 2f, 2f);
            playerEntry.openMapInventory(ArenaType.DIAGONAL_CLUTCH);
        } else if (event.getNpcEntry().getDisplayName().equalsIgnoreCase("§a§lPLAYGROUND")) {
            playerEntry.getPlaygroundPlayer().openPlaygroundWorlds();
        }

    }

    @EventHandler
    public void onAttack(PlayerInteractAtNPCEvent event) {
        PlayerEntry playerEntry = Clutches.getInstance().getPlayerEntryHandler().get(event.getPlayer().getUniqueId());
        if (playerEntry == null) return;
        if (event.getInteractAction().equals(InteractAction.RIGHT_CLICK))
            return;
        if (playerEntry.getAttackCooldown() > System.currentTimeMillis())
            return;
        if (playerEntry.getPlayerState() == PlayerState.INGAME && (playerEntry.getArenaType() == ArenaType.REDUCE || playerEntry.getArenaType() == ArenaType.EXPERIMENTAL)) {




            if (!playerEntry.getFirstHitDelay().isReceived()) {
                playerEntry.getFirstHitDelay().setReceived(true);
                playerEntry.getCountdown().set(0);
            }

            playerEntry.setAttackCooldown(System.currentTimeMillis() + 420);
            event.getNpcEntry().animation(playerEntry.getPlayer(), 1);
            playerEntry.getPlayer().playSound(event.getNpcEntry().getLocation(), Sound.HURT_FLESH, 0.4F, 5);
        }
    }

}
