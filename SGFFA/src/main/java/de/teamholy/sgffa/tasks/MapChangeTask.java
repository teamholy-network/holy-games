package de.teamholy.sgffa.tasks;

import de.teamholy.sgffa.SGFFA;
import de.teamholy.sgffa.models.PlayerEntry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.Random;

/* copyright by Yassino */
public class MapChangeTask {

    private int countdown = 600;

    public MapChangeTask() {
        Bukkit.getScheduler().runTaskTimer(SGFFA.getInstance(),() -> {
            countdown--;
            for (PlayerEntry playerEntry : SGFFA.getInstance().getCacheHandler().getPlayerEntryHashMap().values()) {
                playerEntry.sendActionBar("§7Map change in §a" + formatSeconds(countdown) + " §8︳ §7Teaming? §7/§cteaming");
                if (SGFFA.getInstance().getActiveMapEntry().getDeathHight() > playerEntry.getPlayer().getLocation().getBlockY()) playerEntry.getPlayer().setHealth(0);
            }
            if (countdown == 0) changeMap();
        },0,20);
    }

    private void changeMap() {
        ArrayList tempMaps = new ArrayList<String>();
        tempMaps.addAll(SGFFA.getInstance().getCacheHandler().getMapEntryHashMap().keySet());
        tempMaps.remove(SGFFA.getInstance().getActiveMapEntry().getMapName());

        SGFFA.getInstance().getClickedChests().forEach(location -> location.getBlock().setType(Material.CHEST));
        SGFFA.getInstance().getClickedChests().clear();
        SGFFA.getInstance().setActiveMapEntry(SGFFA.getInstance().getCacheHandler().getMapEntryHashMap().get(tempMaps.get(new Random().nextInt(tempMaps.size()))));

        SGFFA.getInstance().getCacheHandler().getPlayerEntryHashMap().values().forEach(playerEntry -> {
            if (!playerEntry.isVanish()) {
                playerEntry.performSpawn();
            }
            playerEntry.updateMapScore();
            playerEntry.getPlayer().playSound(playerEntry.getPlayer().getLocation(), Sound.ANVIL_BREAK,50f,50f);
            playerEntry.getPlayer().sendTitle("§7§lNEW MAP","§a" + SGFFA.getInstance().getActiveMapEntry().getMapName());
        });
        countdown = 600;
    }

    private String formatSeconds(int seconds) {
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}
