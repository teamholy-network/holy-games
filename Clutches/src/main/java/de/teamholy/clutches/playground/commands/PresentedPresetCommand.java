package de.teamholy.clutches.playground.commands;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.playground.model.PlaygroundPlayer;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/* copyright by Yassino */
public class PresentedPresetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        if (!player.hasPermission("teamholy.presentedpreset")) return false;


        Inventory inventory = new Inventory("§6Your presets",9*3);

        PlaygroundPlayer playgroundPlayer = Clutches.getInstance().getPlayerEntryHandler().get(player.getUniqueId()).getPlaygroundPlayer();
        AtomicInteger i = new AtomicInteger();
        playgroundPlayer.getSettings().getHitPresetMap().forEach(hitPreset -> {


            if (Clutches.getInstance().getPlaygroundManager().getPresentedHits().stream().anyMatch(temp -> temp.getUuid().equals(hitPreset.getUuid()))) {

                inventory.setItem(new ItemBuilder(hitPreset.getIcon().getMaterial(),hitPreset.getHitMap().size(),hitPreset.getIcon().getSubId()).setName("§6" + hitPreset.getName())
                        .setLore("§cclick to delete preset from presentedhits").build(),i.get(),event -> {

                    Clutches.getInstance().getPlaygroundManager().getPresentedHits().remove(hitPreset);
                    player.closeInventory();

                    player.sendMessage("§cDeleted the preset §a" + hitPreset.getName() + " §cfrom the presented hitpresets");
                    Clutches.getInstance().getPlaygroundManager().getYamlConfiguration().set("presets",Clutches.getInstance().getPlaygroundManager().getPresentedHits());
                    try {
                        Clutches.getInstance().getPlaygroundManager().getYamlConfiguration().save(Clutches.getInstance().getPlaygroundManager().getCfgfFile());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                });
            } else {
                inventory.setItem(new ItemBuilder(hitPreset.getIcon().getMaterial(),hitPreset.getHitMap().size(),hitPreset.getIcon().getSubId()).setName("§6" + hitPreset.getName())
                        .setLore("§aclick to import to presentedhits").build(),i.get(),event -> {

                    Clutches.getInstance().getPlaygroundManager().getPresentedHits().add(hitPreset);
                    player.closeInventory();

                    player.sendMessage("§aImported the preset §c" + hitPreset.getName() + " §ato the presented hitpresets");
                    Clutches.getInstance().getPlaygroundManager().getYamlConfiguration().set("presets",Clutches.getInstance().getPlaygroundManager().getPresentedHits());
                    try {
                        Clutches.getInstance().getPlaygroundManager().getYamlConfiguration().save(Clutches.getInstance().getPlaygroundManager().getCfgfFile());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                });
            }


            i.getAndIncrement();
        });

        player.openInventory(inventory.getInventory());

        return false;
    }
}
