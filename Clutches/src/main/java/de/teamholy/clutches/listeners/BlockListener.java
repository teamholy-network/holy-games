package de.teamholy.clutches.listeners;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.player.PlayerEntry;
import de.teamholy.clutches.player.PlayerState;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.PerkType;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class BlockListener implements Listener {

    @EventHandler
    public void onBlockPhysic(BlockPhysicsEvent event) {
        event.setCancelled(true);
    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER) {
            PlayerEntry entity = Clutches.getInstance().getPlayerEntryHandler().get(event.getEntity().getUniqueId());
            PlayerEntry damager = Clutches.getInstance().getPlayerEntryHandler().get(event.getDamager().getUniqueId());

            if (entity.getPlayerState() == PlayerState.PLAYGROUND
            && entity.getPlaygroundPlayer().getSettings().isPvpEnabled() && damager.getPlaygroundPlayer().getSettings().isPvpEnabled()) {
                event.setDamage(0);
            } else {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByBlockEvent event) {
        event.setCancelled(true); //fix to prevent damage from blocks
    }

    @EventHandler
    public void onDamageEntity(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE)
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
    public void onArmorStand(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBed(PlayerBedEnterEvent enterEvent) {
        enterEvent.setCancelled(true);
    }

    @EventHandler
    public void onWeather(final WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void Hunger(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodChanger(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void drop(PlayerPickupItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void ach(PlayerAchievementAwardedEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL){
            e.setCancelled(true);
        }
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
            if (block.getType().toString().toLowerCase().contains("door")) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        PlayerEntry playerEntry = Clutches.getInstance().getPlayerEntryHandler().get(event.getPlayer().getUniqueId());
        if (playerEntry.getPlayerState() == PlayerState.LOBBY) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        } else if (playerEntry.getPlayerState() == PlayerState.INGAME){
            ItemBuilder perk = BukkitCore.getInstance().getPerkManager().getPerk(playerEntry.getPlayer(), PerkType.BLOCK);
            if (event.getBlock().getType() == perk.itemStack.getType()) {
                playerEntry.getBlocks().add(event.getBlock().getLocation());
                event.getPlayer().getInventory().setItem(event.getPlayer().getInventory().getHeldItemSlot(), perk.setAmount(64).build());
            } else {
                event.setCancelled(true);
            }
        } else if (playerEntry.getPlayerState() == PlayerState.PLAYGROUND) {

            int slot = 0;

            ItemBuilder perk = BukkitCore.getInstance().getPerkManager().getPerk(playerEntry.getPlayer(),PerkType.BLOCK);
            if (event.getBlock().getType() != perk.itemStack.getType()) {
                event.setCancelled(true);
                return;
            }


            if (event.getBlockReplacedState().getType() != Material.AIR) {
                event.setCancelled(true);
                return;
            }


            for (ItemStack itemStack : playerEntry.getPlaygroundPlayer().getInventory()) {

                if (itemStack != null && itemStack.getType() != null && itemStack.getType() == Material.SANDSTONE) {
                    playerEntry.getPlayer().getInventory().setItem(slot, BukkitCore.getInstance().getPerkManager().getPerk(playerEntry.getPlayer(), PerkType.BLOCK).setAmount(64).build());
                    break;
                }

                slot++;
            }
            new BukkitRunnable() {
                int i = 0;
                final int random = new Random().nextInt(99999999);
                @Override
                public void run() {
                    PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(random, new BlockPosition(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ()), i);
                    if (event.getBlock().getLocation().getBlock().getType() == Material.AIR) cancel();
                    if (i >= 0 && i <= 7) {
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            ((CraftPlayer) all).getHandle().playerConnection.sendPacket(packet);
                        }
                    } else {
                        event.getBlock().setType(Material.AIR);
                        PacketPlayOutBlockBreakAnimation packet1 = new PacketPlayOutBlockBreakAnimation(random, new BlockPosition(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ()), -1);
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            ((CraftPlayer) all).getHandle().playerConnection.sendPacket(packet1);
                        }
                        cancel();
                    }
                    i++;
                }
            }.runTaskTimer(Clutches.getInstance(), 10, 10);
        } else {
            event.setCancelled(true);
        }
    }

}
