package de.teamholy.mlgrush.listener;

import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;

public class BlockListener implements Listener {
    @EventHandler
    public void onBlockPhysic(BlockPhysicsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        Player player = (Player) entityDamageByEntityEvent.getDamager();
        if (entityDamageByEntityEvent.getDamager() == null || entityDamageByEntityEvent.getEntity() == null) {
            return;
        }
        if (entityDamageByEntityEvent.getEntityType() == EntityType.ARMOR_STAND) {
            entityDamageByEntityEvent.setCancelled(true);
        }
        if (entityDamageByEntityEvent.getEntityType() == EntityType.ITEM_FRAME) {
            if (player.getGameMode() == GameMode.CREATIVE)
                return;
            entityDamageByEntityEvent.setCancelled(true);
        }
    }
    @EventHandler
    public void onInteractFRame(PlayerInteractEntityEvent event) {
        if (event.getPlayer() == null) return;
        if (event.getRightClicked() == null) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (event.getRightClicked().getType() == EntityType.ITEM_FRAME) event.setCancelled(true);
    }


    @EventHandler
    public void onHaning(HangingBreakEvent event) {
        event.setCancelled(true);
    }
    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {

        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM)
            return;

        e.setCancelled(true);
    }
    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        event.getInventory().setResult(new ItemBuilder(Material.AIR).build());
    }
    @EventHandler
    public void onGet(PlayerPickupItemEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }
    @EventHandler
    public void onArmorStand(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(true);
    }
    @EventHandler
    public void onBed(PlayerBedEnterEvent enterEvent) {
        enterEvent.setCancelled(true);
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
    public void onFoodChanger(FoodLevelChangeEvent e){ e.setCancelled(true); }
    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        e.setCancelled(true);
    }
    @EventHandler
    public void ach(PlayerAchievementAwardedEvent e) {
        e.setCancelled(true);
    }
    @EventHandler
    public void SpringDamage(EntityDamageEvent e) {
        if (e.getEntity().getType() == EntityType.PLAYER && e.getCause() == EntityDamageEvent.DamageCause.FALL) e.setCancelled(true);
        if (e.getEntity().getType() == EntityType.PLAYER && e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) e.setCancelled(true);
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL) event.setCancelled(true);
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            if (block.getType() == Material.CHEST) e.setCancelled(true);
            if (block.getType() == Material.FURNACE) e.setCancelled(true);
            if (block.getType() == Material.BURNING_FURNACE) e.setCancelled(true);
            if (block.getType() == Material.TRAP_DOOR) e.setCancelled(true);
            if (block.getType() == Material.ENCHANTMENT_TABLE) e.setCancelled(true);
            if (block.getType() == Material.BED_BLOCK) e.setCancelled(true);
            if (block.getType() == Material.BED) e.setCancelled(true);
            if (block.getType() == Material.ANVIL) e.setCancelled(true);
            if (block.getType() == Material.ENDER_CHEST) e.setCancelled(true);
            if (block.getType() == Material.HOPPER) e.setCancelled(true);
            if (block.getType() == Material.ITEM_FRAME) e.setCancelled(true);
        }
    }
}
