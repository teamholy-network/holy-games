package de.teamholy.sgffa.listeners;

import de.teamholy.sgffa.SGFFA;
import de.teamholy.sgffa.models.PlayerEntry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;

/* copyright by Yassino */
public class PlayerInteractListener implements Listener {


        /*
    Dirty code weil zu faul
     */

    @EventHandler
    public void onChestClick(PlayerInteractEvent event) {
        try {
            if (event.getAction() == null && event.getClickedBlock() == null && Objects.requireNonNull(event.getClickedBlock()).getType() == null) return;
            if (event.getClickedBlock().getType() != Material.CHEST) return;

            Player player = event.getPlayer();
            player.getInventory().addItem(SGFFA.getInstance().getItemHandler().getItemStackArrayList().get(new Random().nextInt(SGFFA.getInstance().getItemHandler().getItemStackArrayList().size())));
            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 0.9F, 0.5F);

            event.getClickedBlock().setType(Material.COAL_BLOCK);
            SGFFA.getInstance().getClickedChests().add(event.getClickedBlock().getLocation());

            Bukkit.getScheduler().runTaskLater(SGFFA.getInstance(), () -> {
                event.getClickedBlock().setType(Material.CHEST);
                SGFFA.getInstance().getClickedChests().remove(event.getClickedBlock().getLocation());
            }, 300L);
        } catch (Exception ignored) {

        }
    }


    @EventHandler
    public void onInteractInventoryArmor(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        try {
            if (event.getItem().getType() == Material.COMPASS) {
                PlayerEntry playerEntry = SGFFA.getInstance().getCacheHandler().getPlayerEntryHashMap().get(player.getUniqueId());
                playerEntry.openVanishMenu();
                return;
            }
            ItemStack boots = player.getInventory().getBoots();
            ItemStack leggings = player.getInventory().getLeggings();
            ItemStack chestplate = player.getInventory().getChestplate();
            ItemStack helmet = player.getInventory().getHelmet();
            ItemStack hand = player.getItemInHand();
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (hand.getType().toString().toUpperCase().contains("BOOTS")) {
                    player.getInventory().setItemInHand(boots);
                    player.getInventory().setBoots(hand);
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 0.4F, 0.3F);
                    player.updateInventory();
                } else if (player.getItemInHand().getType().toString().toUpperCase().contains("LEGGINGS")) {
                    player.getInventory().setItemInHand(leggings);
                    player.getInventory().setLeggings(hand);
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 0.4F, 0.3F);
                    player.updateInventory();
                } else if (player.getItemInHand().getType().toString().toUpperCase().contains("CHESTPLATE")) {
                    player.getInventory().setItemInHand(chestplate);
                    player.getInventory().setChestplate(hand);
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 0.4F, 0.3F);
                    player.updateInventory();
                } else if (player.getItemInHand().getType().toString().toUpperCase().contains("HELMET")) {
                    player.getInventory().setItemInHand(helmet);
                    player.getInventory().setHelmet(hand);
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 0.4F, 0.3F);
                    player.updateInventory();
                }
            }
        } catch (Exception ignored) {}
    }

}
