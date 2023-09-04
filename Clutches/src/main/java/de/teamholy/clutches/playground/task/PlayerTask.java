package de.teamholy.clutches.playground.task;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.playground.model.Hit;
import de.teamholy.clutches.playground.model.HitPreset;
import de.teamholy.clutches.playground.model.PlaygroundPlayer;
import de.teamholy.clutches.utils.PlayerUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Comparator;

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
                    case CHAT -> playgroundPlayer.getPlayer().sendMessage(Clutches.PREFIX + "Clutch in §6§l" + countdown[0]);
                    case TITLE -> PlayerUtils.sendTitle(playgroundPlayer.getPlayer(), "","§6§l" + countdown[0],0,10,0);
                    case ACTIONBAR -> PlayerUtils.sendBar(playgroundPlayer.getPlayer(), "§7Clutch in §6§l" + countdown[0]);
                }

                playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(), Sound.NOTE_PLING,1,2);


                countdown[0]--;

                if (countdown[0] == 0) {
                    stopIfActive();
                    playHitPreset(hitPreset);
                }

            }
        }.runTaskTimerAsynchronously(Clutches.getInstance(),0,10);
    }

    private void playHitPreset(HitPreset hitPreset) {

        final int[] counter = {0};
        final int[] clutchCount = {0};
        final int[] nextClutch = {0};

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                counter[0]++;

                for (int i = counter[0]; i < 28; i++) {

                    if (hitPreset.getHitMap().containsKey(i)) {
                        Hit hit = hitPreset.getHitMap().get(i);

                        clutchCount[0]++;
                        playgroundPlayer.updateClutchCountScore(clutchCount[0]);

                        playgroundPlayer.getPlayer().damage(0);
                        playgroundPlayer.getPlayer().setVelocity(getVelocity(createVector(calculateBackYaw(),0),hit.getXknock(),hit.getYknock()));

                        playgroundPlayer.getPlayer().setLevel(0);
                        playgroundPlayer.getPlayer().setExp(0);

                        if (i == hitPreset.getHitMap().keySet().stream().max(Comparator.naturalOrder()).orElse(0)) {
                            stopIfActive();
                        }

                        break;

                    } else {
                        if (hitPreset.getHitMap().containsKey(i + 1)) {
                            nextClutch[0] = i + 1;

                            int countdown = nextClutch[0] - counter[0];
                            float expValue = (float) countdown / 27;


                            playgroundPlayer.getPlayer().setExp(expValue);
                            playgroundPlayer.getPlayer().setLevel(countdown);
                            break;
                        }
                    }
                }

            }
        }.runTaskTimerAsynchronously(Clutches.getInstance(), 10, 10);
    }

    public Vector getVelocity(Vector blick, double x, double y) {
        double multiplier = Math.sqrt((x * x) / (blick.getX() * blick.getX() + y * y + blick.getZ() * blick.getZ()));
        return blick.multiply(multiplier).setY(y);
    }

    private float calculateBackYaw() {
        float currentYaw = playgroundPlayer.getPlayer().getLocation().getYaw();

        // Add 180 degrees to the current yaw to get the back yaw
        float backYaw = currentYaw - 90;

        // Ensure that the back yaw stays within the range of -180 to 180 degrees
        if (backYaw > 180.0f) {
            backYaw -= 180;
        } else if (backYaw < -180.0f) {
            backYaw += 180;
        }
        return backYaw;
    }

    private Vector createVector(float yaw, float pitch) {
        // Wir verwenden trigonometrische Funktionen, um aus Yaw und Pitch X-, Y- und Z-Komponenten abzuleiten
        double x = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        double y = Math.sin(Math.toRadians(pitch));
        double z = Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));

        return new Vector(x, y, z);
    }

    public boolean stopIfActive() {
        if (bukkitTask != null) {
            Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId());
            System.out.println("TEST");
            playgroundPlayer.getPlayer().setExp(0);
            playgroundPlayer.getPlayer().setLevel(0);
            bukkitTask = null;
            return true;
        }
        return false;
    }

}
