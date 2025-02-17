package de.teamholy.clutches.playground.task;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.playground.model.Hit;
import de.teamholy.clutches.playground.model.HitPreset;
import de.teamholy.clutches.playground.model.PlaygroundPlayer;
import de.teamholy.clutches.utils.PlayerUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
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


        BukkitTask oldTask = bukkitTask;
        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {

                if (oldTask != null) {
                    oldTask.cancel();
                }

                if (!playgroundPlayer.getPlayer().isOnline()) stopIfActive();
                if (playgroundPlayer == null) stopIfActive();
                if (playgroundPlayer.getPlayer() == null) stopIfActive();

                switch (playgroundPlayer.getSettings().getCountdownLocation()) {
                    case CHAT -> playgroundPlayer.getPlayer().sendMessage(Clutches.PREFIX + "Clutch in §6§l" + countdown[0]);
                    case TITLE -> PlayerUtils.sendTitle(playgroundPlayer.getPlayer(), "","§6§l" + countdown[0],0,10,0);
                    case ACTIONBAR -> PlayerUtils.sendBar(playgroundPlayer.getPlayer(), "§7Clutch in §6§l" + countdown[0]);
                }

                playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(), Sound.NOTE_PLING,1,2);


                countdown[0]--;

                if (countdown[0] <= 0) {
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

        hitPreset.setUsed(hitPreset.getUsed() + 1);

        BukkitTask oldTask = bukkitTask;
        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                counter[0]++;

                if (oldTask != null) {
                    oldTask.cancel();
                }

                if (!playgroundPlayer.getPlayer().isOnline()) stopIfActive();
                if (playgroundPlayer == null) stopIfActive();
                if (playgroundPlayer.getPlayer() == null) stopIfActive();
                PlayerUtils.sendBar(playgroundPlayer.getPlayer(), "§7Server §8» §c§lTeamholy.de");

                for (int i = counter[0]; i < 28; i++) {

                    if (hitPreset.getHitMap().containsKey(i)) {
                        Hit hit = hitPreset.getHitMap().get(i);

                        clutchCount[0]++;
                        playgroundPlayer.updateClutchCountScore(clutchCount[0]);

                        playgroundPlayer.getPlayer().damage(0);

                        float add = 0;
                        if (hit.getDiagonalDirection() == Hit.DiagonalDirection.LEFT) add = 47.5F;
                        if (hit.getDiagonalDirection() == Hit.DiagonalDirection.RIGHT) add = -47.5F;

                        if (playgroundPlayer.getSettings().isAdjustDirection()) {
                            playgroundPlayer.getPlayer().setVelocity( getVelocity(calculateHitInBack(calculateNearestCardinalDirection(vectorToYaw(calculateHitInBack(playgroundPlayer.getPlayer().getLocation().getYaw(),0))) + add,0) ,hit.getXknock(),hit.getYknock()));
                        } else {
                            playgroundPlayer.getPlayer().setVelocity( getVelocity(calculateHitInBack(playgroundPlayer.getPlayer().getLocation().getYaw() + add,0).normalize() ,hit.getXknock(),hit.getYknock()));
                        }

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


    private float vectorToYaw(Vector vector) {
        double x = vector.getX();
        double z = vector.getZ();

        double radians = Math.atan2(-x, z);
        double degrees = Math.toDegrees(radians);

        float yaw = (float) degrees;
        yaw = (yaw + 360) % 360;

        return yaw;
    }

    private Vector calculateHitInBack(float yaw, float pitch) {
        double yawRadians = Math.toRadians(yaw);
        double pitchRadians = Math.toRadians(pitch);

        double x = Math.sin(yawRadians) * Math.cos(pitchRadians);
        double y = -Math.sin(pitchRadians);
        double z = -Math.cos(yawRadians) * Math.cos(pitchRadians);

        return new Vector(x, y, z);
    }


    public float calculateNearestCardinalDirection(float yaw) {
        yaw = (yaw % 360 + 360) % 360;

        if (yaw >= 45 && yaw < 135) {
            return -90;
        } else if (yaw >= 135 && yaw < 225) {
            return 0;
        } else if (yaw >= 225 && yaw < 315) {
            return 90;
        } else {
            return -180;
        }
    }

    public boolean stopIfActive() {
        if (bukkitTask != null) {
            Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId());
            if (playgroundPlayer != null) {
                playgroundPlayer.getPlayer().setExp(0);
                playgroundPlayer.getPlayer().setLevel(0);
            }
            bukkitTask = null;
            return true;
        }
        return false;
    }

}
