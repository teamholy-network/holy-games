package de.teamholy.bridge.tasks;

import com.google.common.collect.Lists;
import com.mongodb.Block;
import de.teamholy.bridge.Bridge;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/* copyright by Yassino */
public class GhostBlockRemover {

    /*@Getter
    private Queue<Block> blocksToRemove = Lists.newLinkedList();


    public GhostBlockRemover(Bridge bridge) {

        new BukkitRunnable() {
            @Override
            public void run() {

            }
        }.runTaskTimer(bridge, 0, 15);

    }


    private void removeGhostBlocks() {
        while (!blocksToRemove.isEmpty()) {

            var removeBlocksAtOnce = 1000;
            for (int i = 0; i < removeBlocksAtOnce; i++) {
                var block = blocksToRemove.poll();


            }

        }
    }

    public List<org.bukkit.block.Block> getBlocksInRadius(Location center, int radius) {
        return IntStream.rangeClosed(center.getBlockX() - radius, center.getBlockX() + radius)
                .boxed()
                .flatMap(x -> IntStream.rangeClosed(center.getBlockY() - radius, center.getBlockY() + radius)
                        .mapToObj(y -> Pair.of(x, y)))
                .flatMap(pair -> IntStream.rangeClosed(center.getBlockZ() - radius, center.getBlockZ() + radius)
                        .mapToObj(z -> center.getWorld().getBlockAt(pair.getKey(), pair.getValue(), z)))
                .filter(block -> block.getType() == Material.AIR)
                .collect(Collectors.toList());
    }

    public void scheduleBlockRemoval(Block block) {
        blocksToRemove.add(block);
    }  */

}
