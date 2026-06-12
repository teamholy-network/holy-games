package de.teamholy.knockbackffa.listeners;

import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.enums.PlayerState;
import de.teamholy.knockbackffa.models.PlayerEntry;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;

/* copyright by Yassino */
public class BlockListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE)
            return;
        if (!event.getInventory().getTitle().equalsIgnoreCase("§8» §6Inventory Sort"))
            event.setCancelled(true);
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
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.SOIL) event.setCancelled(true);
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
    public void drop(PlayerDropItemEvent e) {
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
        if (e.getEntity().getType() == EntityType.PLAYER && e.getCause() == EntityDamageEvent.DamageCause.DROWNING) e.setCancelled(true);
        if (e.getEntity().getType() == EntityType.PLAYER && e.getCause() == EntityDamageEvent.DamageCause.CONTACT) e.setCancelled(true);
    }
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(player.getUniqueId());
        // Vorher NPE bei fehlendem Cache-Eintrag (vom Event-Bus geschluckt, Handler brach ab) — gleiches Ergebnis per Early-Return
        if (playerEntry == null) return;
        if (playerEntry.getPlayerState() == PlayerState.LOBBY) {
            if (player.getGameMode() == GameMode.CREATIVE) return;
            event.setCancelled(true);
        }
    }

}
