package de.teamholy.bedwars.inventoriers;

import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

/* copyright by Yassino */
@Getter
public class GoldVotingInventory {

    public Inventory inventory = Bukkit.createInventory(null, 9,"§8» §6Gold voting");
    private final ArrayList<Player> withGold;
    private final ArrayList<Player> withoutGold;

    public GoldVotingInventory() {
        withGold = new ArrayList<>();
        withoutGold = new ArrayList<>();
    }

    public void updateInventory() {
        inventory.clear();
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i,new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build());
        }
        inventory.setItem(2,new ItemBuilder(Material.INK_SACK,getAmount(withGold.size()), (byte) 10).setName("§8» §aYes").build());
        inventory.setItem(4,new ItemBuilder(Material.PAPER,1, (byte) 0).setName("§8» §6Tip").setLore("§7your vote is counted twice if you have §dVIP §7or higher").build());
        inventory.setItem(6,new ItemBuilder(Material.INK_SACK,getAmount(withoutGold.size()), (byte) 1).setName("§8» §cNo").build());
    }

    private int getAmount(int i) {
        if (i < 1) {
            return 1;
        } else {
            return i;
        }
    }

    public void removingVotes(Player player) {
        withGold.remove(player);
        withGold.remove(player);
        withoutGold.remove(player);
        withoutGold.remove(player);
    }

    public boolean isGold() {
        if (withGold.size() > withoutGold.size()) {
            return true;
        } else if (withoutGold.size() > withGold.size()) {
            return false;
        } else {
            return false;
        }
    }
}
