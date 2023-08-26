package de.teamholy.sgffa.handlers;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/* copyright by Yassino */
@Getter
public class ItemHandler {
    
    private final ArrayList<ItemStack> itemStackArrayList = new ArrayList<>();
    
    public ItemHandler() {
        itemStackArrayList.add(new ItemStack(Material.STONE_SWORD));
        itemStackArrayList.add(new ItemStack(Material.STONE_SWORD));
        itemStackArrayList.add(new ItemStack(Material.STONE_SWORD));
        itemStackArrayList.add(new ItemStack(Material.STONE_SWORD));
        itemStackArrayList.add(new ItemStack(Material.STONE_SWORD));
        itemStackArrayList.add(new ItemStack(Material.IRON_SWORD));
        itemStackArrayList.add(new ItemStack(Material.IRON_SWORD));
        itemStackArrayList.add(new ItemStack(Material.DIAMOND_PICKAXE));
        itemStackArrayList.add(new ItemStack(Material.DIAMOND_PICKAXE));
        itemStackArrayList.add(new ItemStack(Material.DIAMOND_PICKAXE));
        itemStackArrayList.add(new ItemStack(Material.DIAMOND_PICKAXE));
        itemStackArrayList.add(new ItemStack(Material.IRON_AXE));
        itemStackArrayList.add(new ItemStack(Material.IRON_AXE));
        itemStackArrayList.add(new ItemStack(Material.IRON_AXE));
        itemStackArrayList.add(new ItemStack(Material.IRON_AXE));
        itemStackArrayList.add(new ItemStack(Material.IRON_AXE));
        itemStackArrayList.add(new ItemStack(Material.BOW));
        itemStackArrayList.add(new ItemStack(Material.BOW));
        itemStackArrayList.add(new ItemStack(Material.ARROW, (int)(Math.random() * 3.0D) + 3));
        itemStackArrayList.add(new ItemStack(Material.ARROW, (int)(Math.random() * 2.0D) + 2));
        itemStackArrayList.add(new ItemStack(Material.ARROW, (int)(Math.random() * 3.0D) + 5));
        itemStackArrayList.add(new ItemStack(Material.ARROW, (int)(Math.random() * 5.0D) + 3));
        itemStackArrayList.add(new ItemStack(Material.IRON_CHESTPLATE));
        itemStackArrayList.add(new ItemStack(Material.IRON_CHESTPLATE));
        itemStackArrayList.add(new ItemStack(Material.IRON_CHESTPLATE));
        itemStackArrayList.add(new ItemStack(Material.IRON_CHESTPLATE));
        itemStackArrayList.add(new ItemStack(Material.IRON_CHESTPLATE));
        itemStackArrayList.add(new ItemStack(Material.DIAMOND_CHESTPLATE));
        itemStackArrayList.add(new ItemStack(Material.IRON_LEGGINGS));
        itemStackArrayList.add(new ItemStack(Material.IRON_LEGGINGS));
        itemStackArrayList.add(new ItemStack(Material.GOLD_HELMET));
        itemStackArrayList.add(new ItemStack(Material.GOLD_HELMET));
        itemStackArrayList.add(new ItemStack(Material.IRON_HELMET));
        itemStackArrayList.add(new ItemStack(Material.IRON_HELMET));
        itemStackArrayList.add(new ItemStack(Material.IRON_HELMET));
        itemStackArrayList.add(new ItemStack(Material.IRON_HELMET));
        itemStackArrayList.add(new ItemStack(Material.IRON_HELMET));
        itemStackArrayList.add(new ItemStack(Material.DIAMOND_BOOTS));
        itemStackArrayList.add(new ItemStack(Material.GOLD_BOOTS));
        itemStackArrayList.add(new ItemStack(Material.GOLD_BOOTS));
        itemStackArrayList.add(new ItemStack(Material.GOLD_BOOTS));
        itemStackArrayList.add(new ItemStack(Material.GOLD_BOOTS));
        itemStackArrayList.add(new ItemStack(Material.FISHING_ROD));
        itemStackArrayList.add(new ItemStack(Material.FISHING_ROD));
        itemStackArrayList.add(new ItemStack(Material.DIAMOND_LEGGINGS));
        itemStackArrayList.add(new ItemStack(Material.GOLDEN_APPLE, (int)(Math.random() + 1.0D)));
    }
    
}
