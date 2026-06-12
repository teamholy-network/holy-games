package de.teamholy.mlgrush.utils;

import de.teamholy.gamescommon.PlayerMessages;
import de.teamholy.gamescommon.TimeFormat;
import de.teamholy.mlgrush.player.PlayerEntry;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerUtils {

    public String formatSeconds(int seconds) {
        return TimeFormat.formatSeconds(seconds);
    }

    public void sendActionBar(Player p, String nachricht) {
        PlayerMessages.sendActionBar(p, nachricht);
    }

    public ArrayList<PlayerEntry> getHighestValue(HashMap<PlayerEntry, Integer> hashMap) {
        int max = 0;
        for (int i : hashMap.values()) {
            if (i > max)
                max = i;
        }
        ArrayList<PlayerEntry> arrayList = new ArrayList<>();
        for (PlayerEntry all : hashMap.keySet()) {
            if (hashMap.get(all) == max) {
                arrayList.add(all);
            }
        }
        return arrayList;
    }

}
