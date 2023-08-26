package de.teamholy.mlgrush.utils;

import de.teamholy.mlgrush.player.PlayerEntry;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PlayerUtils {

    public String formatSeconds(int seconds) {
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void sendActionBar(Player p, String nachricht) {
        CraftPlayer cp = (CraftPlayer) p;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + nachricht + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        cp.getHandle().playerConnection.sendPacket(ppoc);
    }

    public ArrayList<PlayerEntry> getHighestValue(HashMap<PlayerEntry, Integer> hashMap) {
        int max = 0;
        Iterator<Integer> iterator;
        ArrayList<PlayerEntry> arrayList = new ArrayList<>();

        for (iterator = hashMap.values().iterator(); iterator.hasNext(); ) {
            int i = iterator.next();
            if (i > max)
                max = i;
        }
        for (PlayerEntry all : hashMap.keySet()) {
            if (hashMap.get(all) == max) {
                arrayList.add(all);
            }
        }
        return arrayList;
    }


}
