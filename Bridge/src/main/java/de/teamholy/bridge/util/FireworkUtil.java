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
        Firework firework = (Firework) world.spawn(location, Firework.class);
        Object nmsWorld = null;
        Object nmsFirework = null;
        try {
            nmsWorld = getNMSObject(world, worldGetHandle);
            nmsFirework = getNMSObject(firework, fireworkGetHandle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (nmsWorldBroadcastEntityEffect == null) {
            nmsWorldBroadcastEntityEffect = getMethod(nmsWorld.getClass(), "broadcastEntityEffect");
        }

        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.clearEffects();
        fireworkMeta.setPower(1);
        fireworkMeta.addEffect(fireworkEffect);
        firework.setFireworkMeta(fireworkMeta);

        try {
            nmsWorldBroadcastEntityEffect.invoke(nmsWorld, nmsFirework, (byte) 17);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return firework;
    }

    private static Object getNMSObject(Object obj, Method method) throws Exception {
        if (method == null) {
            method = getMethod(obj.getClass(), "getHandle");
        }
        return method.invoke(obj);
    }

    private static Method getMethod(Class<?> cl, String methodName) {
        for (Method method : cl.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    public static FireworkEffect getRandomEffect() {
        Random generator = new Random();
        int type = generator.nextInt(9) + 1;
        return generateFireworkEffect(type, FireworkEffect.Type.BALL);
    }

    public static FireworkEffect getBlowupRandomEffect() {
        Random generator = new Random();
        int type = generator.nextInt(6) + 1;
        return generateFireworkEffect(type, FireworkEffect.Type.BALL_LARGE);
    }

    private static FireworkEffect generateFireworkEffect(int type, FireworkEffect.Type effectType) {
        Color primaryColor = getRandomColor();
        Color fadeColor = getRandomColor();

        return FireworkEffect.builder()
                .with(effectType)
                .withColor(primaryColor)
                .withFade(fadeColor)
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
