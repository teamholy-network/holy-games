package de.teamholy.bedwars.inventoriers;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.model.MapEntry;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/* copyright by Yassino */
public class MapVotingInventory {


    public Inventory inventory = Bukkit.createInventory(null, 9 * 2,"§8» §6Map voting");

    public void updateInventory() {
        inventory.clear();
        for (int i = 0; i < 9 * 2; i++) {
            inventory.setItem(i,new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build());
        }
        int i1 = 0;
        for (MapEntry mapEntry : Bedwars.getInstance().getCacheHandler().getMapEntries().values()) {
            inventory.setItem(i1,new ItemBuilder(mapEntry.getMaterial(),getAmount(mapEntry.getVotes()),(byte) mapEntry.getMaterialSubId()).setLore("§7Votes §8× §c" + mapEntry.getVotes()).setName("§8» §6" + mapEntry.getName()).build());
            i1++;
        }
    }

    private int getAmount(int i) {
        if (i < 1) {
            return 1;
        } else {
            return i;
        }
    }

    public String getHighestValue() {
        int max = 0;
        Iterator<Integer> iterator;
        HashMap<String, Integer> voting = new HashMap();
        ArrayList<String> arrayList = new ArrayList<>();
        for (MapEntry mapEntry : Bedwars.getInstance().getCacheHandler().getMapEntries().values()) {
            voting.put(mapEntry.getName(), mapEntry.getVotes());
        }
        for (iterator = voting.values().iterator(); iterator.hasNext(); ) {
            int i = iterator.next();
            if (i > max)
                max = i;
        }
        for (String all : voting.keySet()) {
            if (voting.get(all) == max) {
                arrayList.add(all);
            }
        }
        return arrayList.get(new Random().nextInt(arrayList.size()));
    }

}
