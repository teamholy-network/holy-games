package de.teamholy.mlgrush.manager;

import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.enums.BlockResetType;
import de.teamholy.mlgrush.enums.GameType;
import de.teamholy.mlgrush.game.GameEntry;
import de.teamholy.mlgrush.game.GameState;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class InventoryManager {

    public Inventory spectateInv = Bukkit.createInventory(null, 9*3,"§8» §6Spectate");

    public void updateSpectator() {
        spectateInv.clear();
        for (GameEntry gameEntry : MLGRush.getInstance().getGameEntryHandler().values()) {
            if (gameEntry.getGameState() == GameState.INGAME) {
                if (gameEntry.getGameType() == GameType.TWOxONE) {
                    spectateInv.addItem(new ItemBuilder(gameEntry.getMapEntry().getMapTemplate().getMaterial()).setName("§8» §6" + gameEntry.getMapEntry().getMapId())
                            .setLore("",
                                    "§7Players §8»",
                                    " " + BukkitCore.getInstance().getPlayerColor(gameEntry.getPlayerOne().getPlayer().getUniqueId(),true) + gameEntry.getPlayerOne().getPlayer().getDisplayName() + " §8× §6" + gameEntry.getPlayerOne().getIngamePlayer().getBeds(),
                                    " " + BukkitCore.getInstance().getPlayerColor(gameEntry.getPlayerTwo().getPlayer().getUniqueId(),true)  + gameEntry.getPlayerTwo().getPlayer().getDisplayName() + " §8× §6" + gameEntry.getPlayerTwo().getIngamePlayer().getBeds(),
                                    " ",
                                    "§7Timer §8»",
                                    " §6" + MLGRush.getInstance().getPlayerUtils().formatSeconds(gameEntry.getTimeSinceStart()),
                                    " ",
                                    "§7Settings §8»",
                                    "§7 NoHitDelay §8× §6" + (gameEntry.isNoHitDelay() ? "§a✔" : "§c✘"),
                                    "§7 OnlyYKnock §8× §6" + (gameEntry.isOnlyVerticalKnockback() ? "§a✔" : "§c✘"),
                                    "§7 Reset Blocks on death §8× §6" + (gameEntry.getResetBlocksOnDeath() != BlockResetType.OFF ? "§a✔" : "§c✘"),
                                    ""
                            ).build());
                } else {
                    spectateInv.addItem(new ItemBuilder(gameEntry.getMapEntry().getMapTemplate().getMaterial()).setName("§8» §6" + gameEntry.getMapEntry().getMapId())
                            .setLore("",
                                    "§7Players §8»",
                                    " " + BukkitCore.getInstance().getPlayerColor(gameEntry.getPlayerOne().getPlayer().getUniqueId(),true) + gameEntry.getPlayerOne().getPlayer().getDisplayName() + " §8× §6" + gameEntry.getPlayerOne().getIngamePlayer().getBeds(),
                                    " " + BukkitCore.getInstance().getPlayerColor(gameEntry.getPlayerTwo().getPlayer().getUniqueId(),true) + gameEntry.getPlayerTwo().getPlayer().getDisplayName() + " §8× §6" + gameEntry.getPlayerTwo().getIngamePlayer().getBeds(),
                                    " " + BukkitCore.getInstance().getPlayerColor(gameEntry.getPlayerThree().getPlayer().getUniqueId(),true) + gameEntry.getPlayerThree().getPlayer().getDisplayName() + " §8× §6" + gameEntry.getPlayerThree().getIngamePlayer().getBeds(),
                                    " " + BukkitCore.getInstance().getPlayerColor(gameEntry.getPlayerFour().getPlayer().getUniqueId(),true) + gameEntry.getPlayerFour().getPlayer().getDisplayName() + " §8× §6" + gameEntry.getPlayerFour().getIngamePlayer().getBeds(),
                                    " ",
                                    "§7Timer §8»",
                                    " §6" + MLGRush.getInstance().getPlayerUtils().formatSeconds(gameEntry.getTimeSinceStart()),
                                    " ",
                                    "§7Settings §8»",
                                    "§7 NoHitDelay §8× §6" + (gameEntry.isNoHitDelay() ? "§a✔" : "§c✘"),
                                    "§7 OnlyYKnock §8× §6" + (gameEntry.isOnlyVerticalKnockback() ? "§a✔" : "§c✘"),
                                    "§7 Reset Blocks on death §8× §6" + (gameEntry.getResetBlocksOnDeath() != BlockResetType.OFF ? "§a✔" : "§c✘"),
                                    ""
                            ).build());
                }
            }
        }
    }

}
