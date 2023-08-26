package de.teamholy.mlgrush.listener;

import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.game.GameState;
import de.teamholy.mlgrush.player.PlayerEntry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerVelocityEvent;

public class PlayerVelocityListener implements Listener {

    @EventHandler
    public void onhandle(PlayerVelocityEvent event) {
        PlayerEntry playerEntry = MLGRush.getInstance().getPlayerEntryHandler().get(event.getPlayer().getUniqueId());
        if (playerEntry.getGameEntry() != null && playerEntry.getGameEntry().getGameState() == GameState.INGAME) {
            if (playerEntry.getGameEntry().isOnlyVerticalKnockback()) {
                Double x100 = Double.valueOf(event.getVelocity().getX());
                Double z100 = Double.valueOf(event.getVelocity().getZ());
                Double x1 = Double.valueOf(x100.doubleValue() / 100.0D);
                Double z1 = Double.valueOf(z100.doubleValue() / 100.0D);
                Double xFinal = Double.valueOf(x1.doubleValue() * 1);
                Double zFinal = Double.valueOf(z1.doubleValue() * 1);
                event.setVelocity(event.getVelocity().setX(xFinal.doubleValue()).setY(0.45).setZ(zFinal.doubleValue()));
            }
        }
    }

}
