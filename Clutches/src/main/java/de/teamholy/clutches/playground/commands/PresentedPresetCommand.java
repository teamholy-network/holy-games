package de.teamholy.clutches.playground.commands;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.playground.model.Hit;
import de.teamholy.clutches.playground.model.HitPreset;
import de.teamholy.clutches.playground.model.PlaygroundPlayer;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;
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

                    Clutches.getInstance().getPlaygroundManager().getPresentedHits().removeIf(hitPreset1 -> hitPreset1.getUuid().equals(hitPreset.getUuid()));
                    player.closeInventory();

                    player.sendMessage("§cDeleted the preset §a" + hitPreset.getName() + " §cfrom the presented hitpresets");
                    deletePresetFromConfig(hitPreset.getUuid());

                });
            } else {
                inventory.setItem(new ItemBuilder(hitPreset.getIcon().getMaterial(),hitPreset.getHitMap().size(),hitPreset.getIcon().getSubId()).setName("§6" + hitPreset.getName())
                        .setLore("§aclick to import to presentedhits").build(),i.get(),event -> {

                    Clutches.getInstance().getPlaygroundManager().getPresentedHits().add(hitPreset);
                    player.closeInventory();

                    player.sendMessage("§aImported the preset §c" + hitPreset.getName() + " §ato the presented hitpresets");
                    savePresetToConfig(hitPreset);


                });
            }


            i.getAndIncrement();
        });

        player.openInventory(inventory.getInventory());

        return false;
    }


    public void deletePresetFromConfig(UUID presetUuid) {
        YamlConfiguration config = Clutches.getInstance().getPlaygroundManager().getYamlConfiguration();
        List<Map<?, ?>> presetsList = config.getMapList("presets");

        presetsList.removeIf(presetMap -> presetMap.get("uuid").equals(presetUuid.toString()));

        config.set("presets", presetsList);

        try {
            config.save(Clutches.getInstance().getPlaygroundManager().getCfgfFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void savePresetToConfig(HitPreset hitPreset) {
        YamlConfiguration config = Clutches.getInstance().getPlaygroundManager().getYamlConfiguration();
        List<Map<?, ?>> presetsList = config.getMapList("presets");

        Map<String, Object> presetMap = new HashMap<>();
        presetMap.put("uuid", hitPreset.getUuid().toString());
        presetMap.put("name", hitPreset.getName());
        presetMap.put("icon", hitPreset.getIcon().toString());
        presetMap.put("hitMap", saveHitMapToConfig(hitPreset.getHitMap()));
        presetMap.put("used", hitPreset.getUsed());
        presetMap.put("created", hitPreset.getCreated());
        presetMap.put("lastEdit", hitPreset.getLastEdit());
        presetMap.put("origin", hitPreset.getOrigin().toString());
        presetMap.put("shared", hitPreset.isShared());

        presetsList.add(presetMap);

        config.set("presets", presetsList);

        try {
            config.save(Clutches.getInstance().getPlaygroundManager().getCfgfFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Map<String, Object>> saveHitMapToConfig(Map<Integer, Hit> hitMap) {
        List<Map<String, Object>> hitMapList = new ArrayList<>();

        for (Map.Entry<Integer, Hit> entry : hitMap.entrySet()) {
            int slot = entry.getKey();
            Hit hit = entry.getValue();

            Map<String, Object> hitMapEntry = new HashMap<>();
            hitMapEntry.put("slot", slot);
            hitMapEntry.put("xKnock", hit.getXknock());
            hitMapEntry.put("yKnock", hit.getYknock());
            hitMapEntry.put("icon", hit.getIcon().toString());
            hitMapEntry.put("diagonalDirection", hit.getDiagonalDirection().toString());

            hitMapList.add(hitMapEntry);
        }

        return hitMapList;
    }

}
