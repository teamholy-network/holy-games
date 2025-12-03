package de.teamholy.sgffa.listeners;


import de.teamholy.sgffa.SGFFA;
import de.teamholy.sgffa.models.PlayerEntry;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.potion.PotionEffectType;

/* copyright by Yassino */
public class BlockListener implements Listener {
    @EventHandler
    public void onHaning(HangingBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onArrowDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow arrow)) return;
      if (!(arrow.getShooter() instanceof Player)) return;
        PlayerEntry playerEntry = SGFFA.getInstance().getCacheHandler().getPlayerEntryHashMap().get(event.getEntity().getUniqueId());
        PlayerEntry attackerEntry = SGFFA.getInstance().getCacheHandler().getPlayerEntryHashMap().get(((Player) arrow.getShooter()).getUniqueId());
        if (playerEntry == attackerEntry) return;
        if (playerEntry.getTeamEntry() != null && attackerEntry.getTeamEntry() != null && playerEntry.getTeamEntry() == attackerEntry.getTeamEntry()) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
          Player entity = (Player) event.getEntity();
            PlayerEntry playerEntry = SGFFA.getInstance().getCacheHandler().getPlayerEntryHashMap().get(player.getUniqueId());
            PlayerEntry entityEntry = SGFFA.getInstance().getCacheHandler().getPlayerEntryHashMap().get(entity.getUniqueId());
            if (entityEntry.isVanish()) {
                event.setCancelled(true);
            }
            if (entityEntry.isGrace()) {
                event.setCancelled(true);
            }

            if (playerEntry.isGrace()) {
                playerEntry.setGrace(false);
                player.sendMessage(SGFFA.PREFIX + "Your grace period has ended!");
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
            if (event.getEntity().getType() == EntityType.ITEM_FRAME)
                event.setCancelled(true);
            if (event.getEntity().getType() == EntityType.PAINTING)
                event.setCancelled(true);

            if (playerEntry.getTeamEntry() != null && entityEntry.getTeamEntry() != null && playerEntry.getTeamEntry() == entityEntry.getTeamEntry()) {
                event.setCancelled(true);
            }


        }
    }

    @EventHandler
    public void onDestroy(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }
    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        event.getInventory().setResult(new ItemBuilder(Material.AIR).build());
    }
    @EventHandler
    public void onWeather(final WeatherChangeEvent e){
        e.setCancelled(true);
    }
    @EventHandler
    public void Hunger(FoodLevelChangeEvent e){
        e.setCancelled(true);
    }
    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        e.getItemDrop().remove();
    }
    @EventHandler
    public void ach(PlayerAchievementAwardedEvent e) {
        e.setCancelled(true);
    }
    @EventHandler
    public void SpringDamage(EntityDamageEvent e) { if (e.getEntity().getType() == EntityType.PLAYER && e.getCause() == EntityDamageEvent.DamageCause.FALL) e.setCancelled(true); }
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL)
            event.setCancelled(true);
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) { if (e.getAction() == Action.RIGHT_CLICK_BLOCK) { Block block = e.getClickedBlock();if (block.getType() == Material.CHEST) e.setCancelled(true);if (block.getType() == Material.FURNACE) e.setCancelled(true);if (block.getType() == Material.BURNING_FURNACE) e.setCancelled(true);if (block.getType() == Material.TRAP_DOOR) e.setCancelled(true);if (block.getType() == Material.ENDER_CHEST) e.setCancelled(true); } }

}
