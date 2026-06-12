package de.teamholy.clutches.utils;

import de.teamholy.gamescommon.PlayerMessages;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerUtils {
    public static void sendBar(Player player, String message) {
        PlayerMessages.sendActionBar(player, message);
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadein, int stay, int fadeout) {
        PlayerMessages.sendTitle(player, title, subtitle, fadein, stay, fadeout);
    }

    public static String convertTime(long timestampInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("d. MMM yyyy HH:mm:ss");
        Date date = new Date(timestampInMillis);
        return sdf.format(date);
    }
}
