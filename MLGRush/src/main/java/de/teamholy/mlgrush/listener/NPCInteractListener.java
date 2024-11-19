package de.teamholy.mlgrush.listener;

import de.teamholy.core.bukkit.npc.event.PlayerInteractAtNPCEvent;
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
        String displayName = event.getNpcEntry().getDisplayName();

        if (displayName.equals("§6§lQueue 4x1")) {
            processQueue(playerEntry, GameType.FOURxONE);
        } else if (displayName.equals("§6§lQueue 2x1")) {
            processQueue(playerEntry, GameType.TWOxONE);
        }
    }

    private void processQueue(PlayerEntry playerEntry, GameType gameType) {
        Player player = playerEntry.getPlayer();

        if (isOnCooldown(playerEntry)) {
            MLGRush.getInstance().getPlayerUtils().sendActionBar(player, "§c§lPlease wait!");
            return;
        }

        int requiredPlayers = gameType == GameType.FOURxONE ? 4 : 2;
        int currentQueueSize = getCurrentQueueSize(gameType);

        if (currentQueueSize >= requiredPlayers) {
            notifyQueueFull(player);
        } else {
            toggleQueueStatus(playerEntry, gameType);
            if (currentQueueSize + 1 == requiredPlayers) {
                startGame(gameType);
            }
        }
    }

    private boolean isOnCooldown(PlayerEntry playerEntry) {
        return playerEntry.getQueueCooldown() > System.currentTimeMillis();
    }

    private int getCurrentQueueSize(GameType gameType) {
        return (int) MLGRush.getInstance().getQueueHandler().getQueue().values().stream()
                .filter(type -> type == gameType).count();
    }

    private void notifyQueueFull(Player player) {
        player.sendMessage(MLGRush.getInstance().getPrefix() + "The Queue is full, please wait");
        player.playSound(player.getLocation(), Sound.NOTE_BASS, 50f, 50f);
    }

    private void toggleQueueStatus(PlayerEntry playerEntry, GameType gameType) {
        Player player = playerEntry.getPlayer();
        playerEntry.setQueueCooldown(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2L));

        if (MLGRush.getInstance().getQueueHandler().getQueue().containsKey(playerEntry)) {
            MLGRush.getInstance().getQueueHandler().getQueue().remove(playerEntry);
            player.sendMessage(MLGRush.getInstance().getPrefix() + "You left the queue!");
        } else {
            playerEntry.setChallengedPlayer(null);
            MLGRush.getInstance().getQueueHandler().getQueue().put(playerEntry, gameType);
            player.sendMessage(MLGRush.getInstance().getPrefix() + "You joined the queue!");
        }

        MLGRush.getInstance().getQueueHandler().updateQueue();
    }

    private void startGame(GameType gameType) {
        List<Map.Entry<PlayerEntry, GameType>> playersInQueue = MLGRush.getInstance().getQueueHandler().getQueue().entrySet().stream()
                .filter(entry -> entry.getValue() == gameType)
                .collect(Collectors.toList());

        GameEntry gameEntry;
        if (gameType == GameType.TWOxONE) {
            gameEntry = new GameEntry(playersInQueue.get(0).getKey(), playersInQueue.get(1).getKey(), null, null);
        } else {
            gameEntry = new GameEntry(playersInQueue.get(0).getKey(), playersInQueue.get(1).getKey(), playersInQueue.get(2).getKey(), playersInQueue.get(3).getKey());
        }

        playersInQueue.forEach(entry -> {
            PlayerEntry playerEntry = entry.getKey();
            playerEntry.setChallengedPlayer(null);
            playerEntry.getPlayer().playSound(playerEntry.getPlayer().getLocation(), Sound.CHEST_OPEN, 2f, 2f);
            playerEntry.setGameEntry(gameEntry);
            playerEntry.openMapSelection(gameType);
        });

        playersInQueue.forEach(entry -> MLGRush.getInstance().getQueueHandler().getQueue().remove(entry.getKey()));
        MLGRush.getInstance().getQueueHandler().updateQueue();
    }
}