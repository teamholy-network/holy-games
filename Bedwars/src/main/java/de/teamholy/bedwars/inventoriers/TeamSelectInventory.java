package de.teamholy.bedwars.inventoriers;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.model.TeamEntry;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/* copyright by Yassino */
public class TeamSelectInventory {

    public Inventory inventory = Bukkit.createInventory(null, 9,"§8» §6Team Selection");

    public void updateInventory() {
        inventory.clear();
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i,new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build());
        }
        int i1 = 0;
        for (TeamEntry teamEntry : Bedwars.getInstance().getCacheHandler().getTeamEntries()) {
            List list = new ArrayList<>();
            for (int i = 0; i < teamEntry.getPlayers().size(); i++) {
                list.add("§8» " + BukkitCore.getInstance().getPlayerColor(teamEntry.getPlayers().get(i).getUniqueId(), true) + teamEntry.getPlayers().get(i).getName());
            }
            inventory.setItem(i1,new ItemBuilder(Material.SKULL_ITEM,(teamEntry.getPlayers().size() < 1 ? (teamEntry.getPlayers().size() + 1) : teamEntry.getPlayers().size()),(byte) 3).setName("§8» " + teamEntry.getColorCode() + teamEntry.getName()).getSkull("http://textures.minecraft.net/texture/" + teamEntry.getSkullId()).setLore(list).build());
            i1++;
        }
    }

}
