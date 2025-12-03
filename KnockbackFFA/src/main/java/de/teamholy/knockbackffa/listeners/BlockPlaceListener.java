package de.teamholy.knockbackffa.listeners;

import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.enums.PlayerState;
import de.teamholy.knockbackffa.models.PlayerEntry;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/* copyright by Yassino */
public class BlockPlaceListener implements Listener {
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerEntry playerEntry = KnockbackFFA.getInstance().getCacheHandler().getPlayerEntrys().get(player.getUniqueId());
        Block b = event.getBlock();
        if (playerEntry.getPlayerState() == PlayerState.INGAME) {
            if (event.getBlock().getLocation().getBlockY() > playerEntry.getActiveMap().getSpawnHight()) {
                event.setCancelled(true);
                return;
            }
            new BukkitRunnable() {
                int i = 0;
                final int random = new Random().nextInt(99999999);
                @Override
                public void run() {
                    PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(random, new BlockPosition(b.getX(), b.getY(), b.getZ()), i);
                    if (b.getLocation().getBlock().getType() == Material.AIR) cancel();
                    if (i >= 0 && i <= 7) {
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            ((CraftPlayer) all).getHandle().playerConnection.sendPacket(packet);
                        }
                    } else {
                        b.setType(Material.AIR);
                        PacketPlayOutBlockBreakAnimation packet1 = new PacketPlayOutBlockBreakAnimation(random, new BlockPosition(b.getX(), b.getY(), b.getZ()), -1);
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            ((CraftPlayer) all).getHandle().playerConnection.sendPacket(packet1);
                        }
                        cancel();
                    }
                    i++;
                }
            }.runTaskTimer(KnockbackFFA.getInstance(), 10, 5);
        }
    }

}
