package de.teamholy.mlgrush.listener;

import de.teamholy.api.bukkit.npc.event.PlayerInteractAtNPCEvent;
import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.enums.GameType;
import de.teamholy.mlgrush.game.GameEntry;
import de.teamholy.mlgrush.player.PlayerEntry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class NPCInteractListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractAtNPCEvent event) {
        Player player = event.getPlayer();
        PlayerEntry playerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(player.getUniqueId());
        if (event.getNpcEntry().getDisplayName().equals("§6§lQueue 4x1")) {
            proceesQueue(playerEntry,GameType.FOURxONE);
        } else if (event.getNpcEntry().getDisplayName().equals("§6§lQueue 2x1")) {
            proceesQueue(playerEntry,GameType.TWOxONE);
        }
    }


    private void proceesQueue(PlayerEntry playerEntry, GameType gameType) {
        int size = (int) MLGRush.getInstance().getQueueHandler().getQueue().values().stream().filter(gameType1 -> gameType1 == gameType).count();
        Player player = playerEntry.getPlayer();
        if (playerEntry.getQueueCooldown() > System.currentTimeMillis()) {
            MLGRush.getInstance().getPlayerUtils().sendActionBar(player,"§c§lPlease wait!");
            return;
        }
        if ((gameType == GameType.FOURxONE ? 4 : 2) <= MLGRush.getInstance().getQueueHandler().getQueue().values().stream().filter(gameType1 -> gameType1 == gameType).count()) {
            player.sendMessage(MLGRush.getInstance().getPrefix() + "The Queue is full, please wait");
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 50f, 50f);
        } else {
            playerEntry.setQueueCooldown(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2L));
            if (MLGRush.getInstance().getQueueHandler().getQueue().containsKey(playerEntry)) {
                MLGRush.getInstance().getQueueHandler().getQueue().remove(playerEntry);
                MLGRush.getInstance().getQueueHandler().updateQueue();
                player.sendMessage(MLGRush.getInstance().getPrefix() + "You left the queue!");
            } else {
                playerEntry.setChallengedPlayer(null);
                MLGRush.getInstance().getQueueHandler().getQueue().put(playerEntry,gameType);
                MLGRush.getInstance().getQueueHandler().updateQueue();
                player.sendMessage(MLGRush.getInstance().getPrefix() + "You joined the queue!");
            }
            if ((gameType == GameType.FOURxONE ? 4 : 2) == MLGRush.getInstance().getQueueHandler().getQueue().values().stream().filter(gameType1 -> gameType1 == gameType).count()) {
                List<Map.Entry<PlayerEntry, GameType>> collection = MLGRush.getInstance().getQueueHandler().getQueue().entrySet().stream().filter(entry -> entry.getValue() == gameType).collect(Collectors.toList());
                GameEntry gameEntry;
                if (gameType == GameType.TWOxONE) {
                    gameEntry = new GameEntry(collection.get(0).getKey(),collection.get(1).getKey(),null,null);
                } else {
                    gameEntry = new GameEntry(collection.get(0).getKey(),collection.get(1).getKey(),collection.get(2).getKey(),collection.get(3).getKey());
                }
                collection.forEach(enty -> {
                    PlayerEntry playerEntry1 = enty.getKey();
                    playerEntry1.setChallengedPlayer(null);
                    playerEntry1.getPlayer().playSound(playerEntry.getPlayer().getLocation(),Sound.CHEST_OPEN,2f,2f);
                    playerEntry1.setGameEntry(gameEntry);
                    playerEntry1.openMapSelection(gameType);
                });
                MLGRush.getInstance().getQueueHandler().getQueue().forEach((playerEntry1, gameType2) -> {
                    if (gameType2 == gameType) {
                        MLGRush.getInstance().getQueueHandler().getQueue().remove(playerEntry1);
                    }
                });
                MLGRush.getInstance().getQueueHandler().updateQueue();
            }
        }

    }
}
