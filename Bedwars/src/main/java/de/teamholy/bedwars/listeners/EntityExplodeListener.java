package de.teamholy.bedwars.listeners;

import de.teamholy.bedwars.Bedwars;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/* copyright by Yassino */
public class EntityExplodeListener implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> block = event.blockList();
        event.setCancelled(true);
        for (Block b : block) {
            if (Bedwars.getInstance().getPlacedBlocks().contains(b.getLocation()) && !(b.getType() == Material.GLASS)) {
                b.getWorld().dropItem(b.getLocation(), new ItemStack(b.getType(), 1, b.getData()));
                b.setType(Material.AIR);
            }
        }
    }

}
