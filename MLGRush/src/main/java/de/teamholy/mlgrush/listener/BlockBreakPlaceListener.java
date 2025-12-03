package de.teamholy.mlgrush.listener;

import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.enums.BlockResetType;
import de.teamholy.mlgrush.player.PlayerEntry;
import de.teamholy.mlgrush.player.PlayerState;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class BlockBreakPlaceListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        Player player = event.getPlayer();
        PlayerEntry playerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(player.getUniqueId());
        if (playerEntry.getPlayerState() == PlayerState.LOBBY || playerEntry.getPlayerState() == PlayerState.SPECTATE) {
            if (player.getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        } else if (playerEntry.getPlayerState() == PlayerState.INGAME) {
            if (event.getBlock().getType() == Material.BED_BLOCK || event.getBlock().getType() == Material.BED) {

                if (playerEntry.getGameEntry().getMapEntry().getBed1().distance(event.getBlock().getLocation()) < 3) {
                    if (playerEntry.getGameEntry().getPlayer(playerEntry) == 1) {
                        event.setCancelled(true);
                    } else {
                        playerEntry.getGameEntry().destroyBed(playerEntry, playerEntry.getGameEntry().getPlayerOne());
                    }
                }

                else if (playerEntry.getGameEntry().getMapEntry().getBed2().distance(event.getBlock().getLocation()) < 3) {
                    if (playerEntry.getGameEntry().getPlayer(playerEntry) == 2) {
                        event.setCancelled(true);
                    } else {
                        playerEntry.getGameEntry().destroyBed(playerEntry,playerEntry.getGameEntry().getPlayerTwo());
                    }
                }

                else if (playerEntry.getGameEntry().getMapEntry().getBed3().distance(event.getBlock().getLocation()) < 3) {
                    if (playerEntry.getGameEntry().getPlayer(playerEntry) == 3) {
                        event.setCancelled(true);
                    } else {
                        playerEntry.getGameEntry().destroyBed(playerEntry,playerEntry.getGameEntry().getPlayerThree());
                    }
                }

                else if (playerEntry.getGameEntry().getMapEntry().getBed4().distance(event.getBlock().getLocation()) < 3) {
                    if (playerEntry.getGameEntry().getPlayer(playerEntry) == 4) {
                        event.setCancelled(true);
                    } else {
                        playerEntry.getGameEntry().destroyBed(playerEntry,playerEntry.getGameEntry().getPlayerFour());
                    }
                }
                event.setCancelled(true);
            } else {
                event.setCancelled(true);
                if (playerEntry.getGameEntry().getPlacedBlocks().containsKey(event.getBlock().getLocation())) {
                    event.getBlock().setType(Material.AIR);
                    playerEntry.getGameEntry().getPlacedBlocks().remove(event.getBlock().getLocation());
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerEntry playerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(player.getUniqueId());
        if (playerEntry.getPlayerState() == PlayerState.LOBBY || playerEntry.getPlayerState() == PlayerState.SPECTATE) {
            if (player.getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        } else {
            playerEntry.getGameEntry().getPlacedBlocks().put(event.getBlock().getLocation(),player);
            playerEntry.setBlocksItems();
            Block b = event.getBlock();

            if (playerEntry.getGameEntry().getResetBlocksOnDeath() == BlockResetType.THREE_SECONDS) {

                BukkitTask bukkitRunnable = new BukkitRunnable() {
                    int i = 0;
                    final int random = new Random().nextInt(99999999);
                    @Override
                    public void run() {
                        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(random, new BlockPosition(b.getX(), b.getY(), b.getZ()), i);
                        if (i >= 0 && i <= 7) {
                            if (b.getType() == Material.AIR) cancel();
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                ((CraftPlayer) all).getHandle().playerConnection.sendPacket(packet);
                            }
                        } else {
                            b.setType(Material.AIR);
                            PacketPlayOutBlockBreakAnimation packet1 = new PacketPlayOutBlockBreakAnimation(random, new BlockPosition(b.getX(), b.getY(), b.getZ()), -1);
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                ((CraftPlayer) all).getHandle().playerConnection.sendPacket(packet1);
                            }
                            playerEntry.getGameEntry().getPlacedBlocks().remove(event.getBlock().getLocation());
                            cancel();
                        }
                        i++;
                    }
                }.runTaskTimer(MLGRush.getInstance(), 8, 8);
                playerEntry.getGameEntry().getBukkitRunnables().add(bukkitRunnable);
            }



        }
    }

}
