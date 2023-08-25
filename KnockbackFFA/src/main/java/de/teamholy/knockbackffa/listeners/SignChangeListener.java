package de.teamholy.knockbackffa.listeners;

import de.teamholy.knockbackffa.KnockbackFFA;
import de.teamholy.knockbackffa.models.MapEntry;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/* copyright by Yassino */
public class SignChangeListener implements Listener {
    @EventHandler
    public void onChange(SignChangeEvent event) {
        Sign sign = (Sign) event.getBlock().getState();
        for (MapEntry mapEntry : KnockbackFFA.getInstance().getCacheHandler().getMapEntrys().values()) {
            if (event.getLine(0).equalsIgnoreCase(mapEntry.getMapName())) {
                event.getPlayer().sendMessage(KnockbackFFA.getInstance().getPrefix() + "Schild für die Map §e" + mapEntry.getMapName() + " §7gesetzt");
                mapEntry.setSign(sign);
            }
        }
    }
}
