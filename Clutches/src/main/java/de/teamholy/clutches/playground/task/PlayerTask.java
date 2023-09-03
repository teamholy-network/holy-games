package de.teamholy.clutches.playground.task;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.playground.model.HitPreset;
import de.teamholy.clutches.playground.model.PlaygroundPlayer;
import de.teamholy.clutches.utils.PlayerUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/* copyright by Yassino */
public class PlayerTask {

    @Getter
    private PlaygroundPlayer playgroundPlayer;
    private BukkitTask bukkitTask;

    public PlayerTask(PlaygroundPlayer playgroundPlayer) {
        this.playgroundPlayer = playgroundPlayer;
    }

    public void playCountdownTask(HitPreset hitPreset) {

        final int[] countdown = {playgroundPlayer.getSettings().getCountdown()};

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {

                switch (playgroundPlayer.getSettings().getCountdownLocation()) {
                    case CHAT -> playgroundPlayer.getPlayer().sendMessage(Clutches.PREFIX + "Clutch in §6" + countdown[0]);
                    case TITLE -> PlayerUtils.sendTitle(playgroundPlayer.getPlayer(), "","§6" + countdown[0],0,10,0);
                    case ACTIONBAR -> PlayerUtils.sendBar(playgroundPlayer.getPlayer(), "§7Clutch in §6" + countdown[0]);
                }

                playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(), Sound.NOTE_PLING,1,2);


                countdown[0]--;

                if (countdown[0] == 0) {
                    bukkitTask = null;
                    playHitPreset(hitPreset);
                    cancel();
                }

            }
        }.runTaskTimerAsynchronously(Clutches.getInstance(),0,10);
    }

    private void playHitPreset(HitPreset hitPreset) {

    }

    public boolean stopIfActive() {
        if (bukkitTask != null) {
            Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId());
            bukkitTask = null;
            return true;
        }
        return false;
    }

}
