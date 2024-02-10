package de.teamholy.bridge.util;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

/* copyright by Yassino */
public class FireworkUtil {

    private static Method worldGetHandle;
    private static Method nmsWorldBroadcastEntityEffect;
    private static Method fireworkGetHandle;

    public static Firework playFirework(World world, Location location, FireworkEffect fireworkEffect) {
        Firework firework = world.spawn(location, Firework.class);

        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.clearEffects();
        fireworkMeta.setPower(1);
        fireworkMeta.addEffect(fireworkEffect);
        firework.setFireworkMeta(fireworkMeta);

        return firework;
    }

    public static FireworkEffect getRandomEffect() {
        return generateFireworkEffect(FireworkEffect.Type.BALL);
    }

    public static FireworkEffect getBlowupRandomEffect() {
        return generateFireworkEffect(FireworkEffect.Type.BALL_LARGE);
    }

    private static FireworkEffect generateFireworkEffect(FireworkEffect.Type effectType) {
        Color primaryColor = getRandomColor();
        Color fadeColor = getRandomColor();

        return FireworkEffect.builder()
                .with(effectType)
                .withColor(primaryColor)
                .withFade(fadeColor)
                .flicker(true)
                .build();
    }

    private static Color getRandomColor() {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return Color.fromRGB(red, green, blue);
    }

}
