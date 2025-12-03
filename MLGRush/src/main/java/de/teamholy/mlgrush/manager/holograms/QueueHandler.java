package de.teamholy.mlgrush.manager.holograms;

import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.enums.GameType;
import de.teamholy.mlgrush.player.PlayerEntry;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import lombok.Getter;

import java.util.HashMap;

/* copyright by Yassino */
@Getter
public class QueueHandler {

    private final Hologram TwoxOne;
    private final Hologram FourxOne;

    private final HashMap<PlayerEntry, GameType> queue = new HashMap<>();


    public QueueHandler(MLGRush instance) {
        TwoxOne = HologramsAPI.createHologram(instance, BukkitCore.getInstance().getLocationManager().getLocation("queue").add(0,2.6,0));
        TwoxOne.appendTextLine("§e0 §7Players in queue");

        FourxOne = HologramsAPI.createHologram(instance, BukkitCore.getInstance().getLocationManager().getLocation("spectate").add(0,2.6,0));
        FourxOne.appendTextLine("§e0 §7Players in queue");
    }

    public void updateQueue() {
        int size1 = (int) queue.values().stream().filter(value -> value == GameType.TWOxONE).count();
        TextLine textLine1 = (TextLine) TwoxOne.getLine(0);
        textLine1.setText((size1 == 0 ? "§e" : "§a") + size1 + ((size1 == 1 ? " §7Player" : " §7Players")) + " in queue");

        int size2 = (int) queue.values().stream().filter(value -> value == GameType.FOURxONE).count();
        TextLine textLine2 = (TextLine) FourxOne.getLine(0);
        textLine2.setText((size2 == 0 ? "§e" : "§a") + size2 + ((size2 == 1 ? " §7Player" : " §7Players")) + " in queue");
    }

}
