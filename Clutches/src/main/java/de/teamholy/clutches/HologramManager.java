package de.teamholy.clutches;

/* copyright by Yassino */

import de.teamholy.clutches.arena.ArenaType;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import de.teamholy.clutches.player.PlayerState;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@Getter
public class HologramManager {

    private final HashMap<ArenaType, Hologram> hologramHashMap = new HashMap<>();
    private final Hologram playgroundHologram;


    public HologramManager() {
        hologramHashMap.put(ArenaType.EXPERIMENTAL,createHologram(new ItemBuilder(Material.SKULL_ITEM,1,(byte)3).build(), BukkitCore.getInstance().getLocationManager().getLocation("multireduce")));

        // multi reduce hats
        Hologram hologram2 = HologramsAPI.createHologram(Clutches.getInstance(), BukkitCore.getInstance().getLocationManager().getLocation("multireduce").clone().add(0 + 0.5,3.35,0));
        hologram2.appendItemLine(new ItemBuilder(Material.SKULL_ITEM,1,(byte)3).build());
        hologram2.appendTextLine(null);

        Hologram hologram3 = HologramsAPI.createHologram(Clutches.getInstance(),BukkitCore.getInstance().getLocationManager().getLocation("multireduce").clone().add(0 - 0.5,3.35,0));
        hologram3.appendItemLine(new ItemBuilder(Material.SKULL_ITEM,1,(byte)3).build());
        hologram3.appendTextLine(null);


        Hologram plholo = HologramsAPI.createHologram(Clutches.getInstance(),BukkitCore.getInstance().getLocationManager().getLocation("playground").clone().add(0,3.35,0));
        plholo.appendItemLine(new ItemBuilder(Material.SKULL_ITEM,1,(byte)3).setSkullMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjU5Z" +
                "ThkNDE5NmZlYTgyNzAyNWMyOTI3YTZmY2Q2ZTk4ZDAzMDA1NzM3MTIzOGE3N2FlNGNkZGViY2U4NjQ3NyJ9fX0=","").build());
        plholo.appendTextLine("§b0 §7Players");

        playgroundHologram = plholo;

        hologramHashMap.put(ArenaType.REDUCE,createHologram(new ItemBuilder(Material.STICK,1).build(), BukkitCore.getInstance().getLocationManager().getLocation("reduce")));
        hologramHashMap.put(ArenaType.CLUTCH,createHologram(new ItemBuilder(Material.SANDSTONE,1,(byte)1).build(), BukkitCore.getInstance().getLocationManager().getLocation("clutch")));
        hologramHashMap.put(ArenaType.DIAGONAL_CLUTCH,createHologram(new ItemBuilder(Material.SANDSTONE_STAIRS,1,(byte)1).build(), BukkitCore.getInstance().getLocationManager().getLocation("diagonalclutch")));
    }

    public void updateHolograms() {
        hologramHashMap.forEach((s, hologram) -> {
            TextLine textLine = (TextLine) hologram.getLine(1);
            int count = (int) Clutches.getInstance().getPlayerEntryHandler().values().stream()
                            .filter(playerEntry -> playerEntry.getArenaType() != null)
                                    .filter(playerEntry -> playerEntry.getArenaType() == s)
                                            .count();
            textLine.setText("§b" + count + " §7" + (count == 1 ? "Player" : "Players"));
        });


        TextLine textLine = (TextLine) playgroundHologram.getLine(1);
        int count = (int) Clutches.getInstance().getPlayerEntryHandler().values().stream()
                .filter(playerEntry -> playerEntry.getPlayerState() == PlayerState.PLAYGROUND)
                .count();
        textLine.setText("§b" + count + " §7" + (count == 1 ? "Player" : "Players"));

    }

    public Hologram createHologram(ItemStack itemStack, Location location) {
        Hologram hologram = HologramsAPI.createHologram(Clutches.getInstance(),location.add(0,3.35,0));
        hologram.appendItemLine(itemStack);
        hologram.appendTextLine("§b0 §7Players");
        return hologram;
    }

}
