package de.teamholy.bedwars.model;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/* copyright by Yassino */
@Getter
public class MapEntry {

    private final Material material;
    private final int materialSubId;
    private final String name;
    private int votes;
    private final String world;

    private int schedBronze;
    private int schedIron;
    private int schedGold;

    public MapEntry(Material material, int materialSubId, String name,String world) {
        this.material = material;
        this.materialSubId = materialSubId;
        this.name = name;
        this.votes = 0;
        this.world = world;

        schedBronze = 0;
        schedIron = 0;
        schedGold = 0;
    }


    public void addVote() {
        votes++;
        Bedwars.getInstance().getInventoryHandler().getMapVotingInventory().updateInventory();
    }

    public void removeVote() {
        votes--;
        Bedwars.getInstance().getInventoryHandler().getMapVotingInventory().updateInventory();
    }

    public void startSpawner() {

        ArrayList<Location> bronze = new ArrayList(Bedwars.getInstance().getYamlConfiguration().getList(name + ".bronze-spawner"));
        ArrayList<Location> iron = new ArrayList(Bedwars.getInstance().getYamlConfiguration().getList(name + ".eisen-spawner"));
        ArrayList<Location> gold = new ArrayList(Bedwars.getInstance().getYamlConfiguration().getList(name + ".gold-spawner"));

        schedBronze = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bedwars.getInstance(),() -> bronze.forEach(bronzeLocation -> dropItem(bronzeLocation,new ItemBuilder(Material.CLAY_BRICK).setName("§cBronze").build())),10,10);

        if (!Bedwars.isRushMode()) {
            schedIron = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bedwars.getInstance(),() -> iron.forEach(itonLocation -> dropItem(itonLocation,new ItemBuilder(Material.IRON_INGOT).setName("§fIron").build())),15*20,15*20);

            if (Bedwars.getInstance().getInventoryHandler().getGoldVotingInventory().isGold()) {
                schedGold = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bedwars.getInstance(),() -> gold.forEach(itonLocation -> dropItem(itonLocation,new ItemBuilder(Material.GOLD_INGOT).setName("§6God").build())),60*20,60*20);
            }

        }


    }

    public void stopSpawner() {
        if (!Bedwars.isRushMode()) {
            Bukkit.getScheduler().cancelTask(schedIron);
            if (schedGold != 0) Bukkit.getScheduler().cancelTask(schedGold);
        }

        Bukkit.getScheduler().cancelTask(schedBronze);
    }


    private void dropItem(Location loc, ItemStack item) {
        if (loc.getChunk().getEntities().length > 64 * 4) return;
        EntityItem entity = new EntityItem(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ(), CraftItemStack.asNMSCopy(item));
        entity.pickupDelay = 10;
        entity.motX = 0.0D;
        entity.motY = 0.0D;
        entity.motZ = 0.0D;
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(entity);
    }
}
