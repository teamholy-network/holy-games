package de.teamholy.sgffa.handlers;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Getter
public class ItemHandler {

    private final Map<ItemStack, Double> itemProbabilities = new HashMap<>();
    private final Random random = new Random();

    public ItemHandler() {
        addItemWithProbability(new ItemStack(Material.STONE_SWORD), 0.15);
        addItemWithProbability(new ItemStack(Material.IRON_SWORD), 0.10);
        addItemWithProbability(new ItemStack(Material.DIAMOND_PICKAXE), 0.05);
        addItemWithProbability(new ItemStack(Material.IRON_AXE), 0.10);
        addItemWithProbability(new ItemStack(Material.BOW), 0.10);
        addItemWithProbability(new ItemStack(Material.ARROW, (int) (Math.random() * 3.0D) + 3), 0.10);
        addItemWithProbability(new ItemStack(Material.IRON_CHESTPLATE), 0.10);
        addItemWithProbability(new ItemStack(Material.DIAMOND_CHESTPLATE), 0.05);
        addItemWithProbability(new ItemStack(Material.IRON_LEGGINGS), 0.10);
        addItemWithProbability(new ItemStack(Material.GOLD_HELMET), 0.05);
        addItemWithProbability(new ItemStack(Material.IRON_HELMET), 0.10);
        addItemWithProbability(new ItemStack(Material.DIAMOND_BOOTS), 0.05);
        addItemWithProbability(new ItemStack(Material.GOLD_BOOTS), 0.05);
        addItemWithProbability(new ItemStack(Material.FISHING_ROD), 0.05);
        addItemWithProbability(new ItemStack(Material.DIAMOND_LEGGINGS), 0.05);
        addItemWithProbability(new ItemStack(Material.GOLDEN_APPLE, (int) (Math.random() + 1.0D)), 0.05);
    }

    private void addItemWithProbability(ItemStack item, double probability) {
        itemProbabilities.put(item, probability);
    }

    public ItemStack getRandomItem() {
        double totalProbability = itemProbabilities.values().stream().mapToDouble(Double::doubleValue).sum();
        double randomValue = random.nextDouble() * totalProbability;

        for (Map.Entry<ItemStack, Double> entry : itemProbabilities.entrySet()) {
            randomValue -= entry.getValue();
            if (randomValue <= 0) {
                return entry.getKey();
            }
        }
        return null; // Should never reach here if probabilities are set correctly
    }
}
