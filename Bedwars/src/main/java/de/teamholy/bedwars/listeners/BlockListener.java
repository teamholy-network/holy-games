package de.teamholy.bedwars.listeners;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.enums.GameState;
import de.teamholy.bedwars.model.PlayerEntry;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
        if (Bedwars.getInstance().getGameState() == GameState.LOBBY || Bedwars.getInstance().getGameState() == GameState.END) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.BED_BLOCK) {
            if (!event.getPlayer().isSneaking() || event.getItem() == null || !event.getItem().getType().isBlock()) {
                event.setUseInteractedBlock(Event.Result.DENY);
            }
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
        if (Bedwars.getInstance().getGameState() == GameState.INGAME) {
        } else {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent event) { if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL) event.setCancelled(true); }
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
        if (Bedwars.getInstance().getGameState() == GameState.LOBBY || Bedwars.getInstance().getGameState() == GameState.END)
            e.setCancelled(true);
        if (Bedwars.getInstance().getSpectatePlayers().contains(Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(e.getEntity().getUniqueId())))
            e.setCancelled(true);
    }
    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        if (Bedwars.getInstance().getGameState() != GameState.INGAME) e.setCancelled(true);
        if (Bedwars.getInstance().getSpectatePlayers().contains(Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(e.getPlayer().getUniqueId()))) e.setCancelled(true);
    }
    @EventHandler
    public void ach(PlayerAchievementAwardedEvent e) {
        e.setCancelled(true);
    }
    @EventHandler
    public void SpringDamage(EntityDamageEvent e) {
        PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(e.getEntity().getUniqueId());
        if (Bedwars.getInstance().getGameState() == GameState.LOBBY || Bedwars.getInstance().getGameState() == GameState.END || Bedwars.getInstance().getSpectatePlayers().contains(playerEntry)) {
            if (e.getEntity().getType() == EntityType.PLAYER && e.getCause() == EntityDamageEvent.DamageCause.FALL) e.setCancelled(true);
            if (e.getEntity().getType() == EntityType.PLAYER && e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) e.setCancelled(true);
            if (e.getEntity().getType() == EntityType.PLAYER && e.getCause() == EntityDamageEvent.DamageCause.DROWNING) e.setCancelled(true);
            if (e.getEntity().getType() == EntityType.PLAYER && e.getCause() == EntityDamageEvent.DamageCause.CONTACT) e.setCancelled(true);
            if (e.getEntity().getType() == EntityType.PLAYER && e.getCause() == EntityDamageEvent.DamageCause.VOID) e.setCancelled(true);
        }
    }
    @EventHandler
    public void onBedInteract(PlayerInteractEvent event) {
        try {
            if (event.getClickedBlock().getType() == Material.BED_BLOCK || event.getClickedBlock().getType() == Material.BED) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                    if (event.getPlayer().getItemInHand() == null)
                    event.setCancelled(true);
            }
        } catch (Exception e) {

        }
    }
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer() == null) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (Bedwars.getInstance().getGameState() == GameState.LOBBY || Bedwars.getInstance().getGameState() == GameState.END) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamae(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(event.getDamager().getUniqueId());
        if (Bedwars.getInstance().getGameState() == GameState.LOBBY || Bedwars.getInstance().getGameState() == GameState.END || Bedwars.getInstance().getSpectatePlayers().contains(playerEntry)) {
            event.setCancelled(true);
        } else if (Bedwars.getInstance().getGameState() == GameState.INGAME) {
            PlayerEntry entityDamage = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(event.getEntity().getUniqueId());
            if (Bedwars.isRushMode()) {
                if (playerEntry.getPlayer().getItemInHand().getType() == Material.WOOD_PICKAXE) {
                    event.setDamage(0);
                }
            }
            if (entityDamage.getTeamEntry() == playerEntry.getTeamEntry()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamaeArrow(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow arrow)) return;
      if (!(arrow.getShooter() instanceof Player)) return;
        PlayerEntry playerEntry = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(((Player) arrow.getShooter()).getUniqueId());
        PlayerEntry entityDamage = Bedwars.getInstance().getCacheHandler().getPlayerEntries().get(event.getEntity().getUniqueId());
        if (entityDamage.getTeamEntry() == playerEntry.getTeamEntry()) {
            event.setCancelled(true);
        }
    }


}
