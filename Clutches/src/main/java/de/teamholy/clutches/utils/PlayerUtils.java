package de.teamholy.clutches.utils;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

public class PlayerUtils {
    public static void sendBar(Player player, String message) {
        IChatBaseComponent msg = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat packet = new PacketPlayOutChat(msg, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadein, int stay, int fadeout) {
        PacketPlayOutTitle timepacket = new PacketPlayOutTitle(fadein, stay, fadeout);
        PacketPlayOutTitle titlepacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\": \"" + title + "\"}"));
        PacketPlayOutTitle subtitlepacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\": \"" + subtitle + "\"}"));

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(timepacket);
        connection.sendPacket(titlepacket);
        connection.sendPacket(subtitlepacket);
    }

    public static String convertTime(long timestampInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("d. MMM yyyy HH:mm:ss");
        Date date = new Date(timestampInMillis);
        return sdf.format(date);
    }
    private int getLastKey(Map<Integer, ?> map) {
        return map.keySet().stream()
                .max(Comparator.naturalOrder())
                .orElse(0);
    }


    public static int checkInventorySize(int items) {
        int size = 0;
        if (items <= 9) {
            size = 9;
        } else if (items <= 18) {
            size = 18;
        } else if (items <= 27) {
            size = 27;
        } else if (items <= 36) {
            size = 36;
        } else if (items <= 45) {
            size = 45;
        } else {
            size = 54;
        }
        return size;
    }


}
