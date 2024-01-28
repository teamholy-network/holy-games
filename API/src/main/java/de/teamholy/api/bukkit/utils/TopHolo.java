package de.teamholy.api.bukkit.utils;


import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.google.common.collect.Maps;
import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
public class TopHolo {

    private Hologram hologram;
    private StatsType statsType = StatsType.ALLTIME;
    private long cooldown = System.currentTimeMillis();
    private Gamemodes gamemode;

    private final HashMap<StatsType, HashMap<Integer,TopPlayer>> top = new HashMap<>();

    public TopHolo(Location location, Gamemodes gamemode, ItemStack itemStack) {
        top.put(StatsType.ALLTIME, Maps.newHashMap());
        top.put(StatsType.MONTHLY, Maps.newHashMap());
        top.put(StatsType.DAILY, Maps.newHashMap());

        this.gamemode = gamemode;
        hologram = HologramsAPI.createHologram(BukkitHolyAPI.getInstance(), location);
        hologram.appendItemLine(itemStack);
        for (int i = 0; i < 15; i++) {
            hologram.appendTextLine("Loading...").setTouchHandler((player) -> {
                if (cooldown > System.currentTimeMillis()) {
                    player.sendMessage("§cPlease wait");
                    return;
                }

                cooldown = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 100);



                switch (statsType) {
                    case DAILY -> statsType = StatsType.MONTHLY;
                    case MONTHLY -> statsType = StatsType.ALLTIME;
                    case ALLTIME -> statsType = StatsType.DAILY;
                }


                updateHologram();
            });
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(BukkitHolyAPI.getInstance(), () -> {
            top.put(StatsType.ALLTIME, refreshTop(StatsType.ALLTIME));
            top.put(StatsType.MONTHLY, refreshTop(StatsType.MONTHLY));
            top.put(StatsType.DAILY, refreshTop(StatsType.DAILY));
            updateHologram();
        }, 10, 20 * 60 * 5);
    }


    private void updateHologram() {
        TextLine textLine = (TextLine) hologram.getLine(1);
        textLine.setText("§8§m----------§f§lTOP 10§8§m----------");
        for (int i = 2; i < 11; i++) {
            TopPlayer topPlayer = top.get(statsType).get(i -1);
            textLine = (TextLine) hologram.getLine(i);
            if (topPlayer != null) {
                textLine.setText("§7#" + topPlayer.getRank() + " §8» " + topPlayer.getName() + " §8» §a" + topPlayer.getValue() + " §cTrophies");
            } else {
                textLine.setText("§7-/-");
            }
        }

        TextLine lastLine = (TextLine) hologram.getLine(13);
        switch (statsType) {
            case DAILY -> lastLine.setText("§7Alltime §8︳ §7Monthly §8︳ §a§lDAILY");
            case MONTHLY -> lastLine.setText("§7Alltime §8︳ §e§lMONTHLY §8︳ §7Daily");
            case ALLTIME -> lastLine.setText("§c§lALLTIME §8︳ §7Monthly §8︳ §7Daily");
        }
        ((TextLine) hologram.getLine(14)).setText("§8§m----------§f§lTOP 10§8§m----------");

    }


    private void refreshTop(StatsType statsType) {
    }


    @Getter
    public class TopPlayer {
        private final String name;
        private final int value;
        private final int rank;

        public TopPlayer(String name, int value, int rank) {
            this.name = name;
            this.value = value;
            this.rank = rank;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

        public int getRank() {
            return rank;
        }
    }

}
