package de.teamholy.mlgrush.maptemplate;

import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.enums.GameType;
import de.teamholy.mlgrush.manager.RegionManager;
import de.teamholy.mlgrush.map.MapEntry;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;

public class MapTemplateSetup implements Listener {

    private Player player;
    private int currentStep;
    private boolean running;
    private MapEntry mapEntry;
    private GameType gameType;

    public MapTemplateSetup(Player player, MapEntry mapEntry,GameType gameType) {
        this.player = player;
        this.gameType = gameType;
        Bukkit.getPluginManager().registerEvents(this, MLGRush.getInstance());
        this.currentStep = 0;
        running = true;
        this.mapEntry = mapEntry;
        player.sendMessage(MLGRush.getInstance().getPrefix() + "Du bearbeitest nun die Map §6" + mapEntry.getMapId() + "§7!");
        player.sendMessage(MLGRush.getInstance().getPrefix() + "Wenn du bei der Location bist die im Chat steht klicke");
        player.sendMessage(MLGRush.getInstance().getPrefix() + "rechtsklick auf den Netherstar");
        nextStep();
        player.getInventory().clear();
        player.getInventory().setItem(4,new ItemBuilder(Material.NETHER_STAR).setName("§8» §6Nächster Schritt §8(§7Rechtsklick§8)").build());
    }

    private void nextStep() {
        this.currentStep++;
        switch(this.currentStep) {
            case 1: case 2:
                player.sendMessage("");
                player.sendMessage(MLGRush.getInstance().getPrefix() + "Gehe zur " + currentStep + " Region Location");
                break;
            case 3:
                player.sendMessage("");
                player.sendMessage(MLGRush.getInstance().getPrefix() + "Gehe zum Spawn von Team 1 ");
                break;
            case 4:
                player.sendMessage("");
                player.sendMessage(MLGRush.getInstance().getPrefix() + "Stelle dich auf das Bett von Team 1 ");
                break;
            case 5:
                player.sendMessage("");
                player.sendMessage(MLGRush.getInstance().getPrefix() + "Gehe zum Spawn von Team 2 ");
                break;
            case 6:
                player.sendMessage("");
                player.sendMessage(MLGRush.getInstance().getPrefix() + "Stelle dich auf das Bett von Team 2 ");
                break;
            case 7:
                player.sendMessage("");
                player.sendMessage(MLGRush.getInstance().getPrefix() + "Flieg zur Todeshöhe");
                break;
            case 8:
                player.sendMessage("");
                player.sendMessage(MLGRush.getInstance().getPrefix() + "Gehe zum Spawn von Team 3 ");
                break;
            case 9:
                player.sendMessage("");
                player.sendMessage(MLGRush.getInstance().getPrefix() + "Stelle dich auf das Bett von Team 3");
                break;
            case 10:
                player.sendMessage("");
                player.sendMessage(MLGRush.getInstance().getPrefix() + "Gehe zum Spawn von Team 4");
                break;
            case 11:
                player.sendMessage("");
                player.sendMessage(MLGRush.getInstance().getPrefix() + "Stelle dich auf das Bett von Team 4");
                break;
        }
    }

    private void finishSetup() throws IOException {
        mapEntry.saveMapInConfig();
        running = false;
        player.sendMessage(MLGRush.getInstance().getPrefix() + "Du hast das Map Setup beendet");
        String[] map = mapEntry.getMapId().split("-");
        mapEntry.setId(Integer.parseInt(map[1]));
        mapEntry.setMapTemplate(MLGRush.getInstance().getMapTemplateEntryHandler().get(map[0]));
        mapEntry.getMapTemplate().getTemplatesCount().add(mapEntry);
        mapEntry.getMapTemplate().getFreeTemplatesCount().add(mapEntry);
        mapEntry.setRegionManager(new RegionManager(mapEntry.getRegion1(),mapEntry.getRegion2()));
        player.sendMessage("");
        player.sendMessage(MLGRush.getInstance().getPrefix() + "Map Setup von §6" + mapEntry.getMapId() + " §7abgeschlossen");
        HandlerList.unregisterAll(this);
        MLGRush.getInstance().getMapEntryHandler().put(mapEntry.getMapId(),mapEntry);
        MLGRush.getInstance().getMaps().add(mapEntry.getMapId());
        MLGRush.getInstance().getYamlConfiguration().set("Maps", MLGRush.getInstance().getMaps());
        MLGRush.getInstance().getYamlConfiguration().save(MLGRush.getInstance().getCfgfFile());
        player.getInventory().clear();
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncChat(PlayerInteractEvent event) {
        if(!running) return;
        try {
            Player player = event.getPlayer();
            if(player.getUniqueId().equals(this.player.getUniqueId())) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (event.getItem().getType() == Material.NETHER_STAR) {
                        switch(this.currentStep) {
                            case 1:
                                mapEntry.setRegion1(player.getLocation());
                                player.sendMessage(MLGRush.getInstance().getPrefix() + "Region 1 gesetzt!");
                                this.nextStep();
                                break;
                            case 2:
                                mapEntry.setRegion2(player.getLocation());
                                player.sendMessage(MLGRush.getInstance().getPrefix() + "Region 1 gesetzt!");
                                this.nextStep();
                                break;
                            case 3:
                                mapEntry.setSpawn1(player.getLocation());
                                player.sendMessage(MLGRush.getInstance().getPrefix() + "Spawn von Team 1 gesetzt!");
                                this.nextStep();
                                break;
                            case 4:
                                mapEntry.setBed1(player.getLocation());
                                player.sendMessage(MLGRush.getInstance().getPrefix() + "Bett von Team 1 gesetzt!");
                                this.nextStep();
                                break;
                            case 5:
                                mapEntry.setSpawn2(player.getLocation());
                                player.sendMessage(MLGRush.getInstance().getPrefix() + "Spawn von Team 2 gesetzt!");
                                this.nextStep();
                                break;
                            case 6:
                                mapEntry.setBed2(player.getLocation());
                                player.sendMessage(MLGRush.getInstance().getPrefix() + "Bett von Team 2 gesetzt!");
                                this.nextStep();
                                break;
                            case 7:
                                mapEntry.setDeathhight(player.getLocation().getY());
                                player.sendMessage(MLGRush.getInstance().getPrefix() + "Todes Höhe gesetzt");
                                if (gameType == GameType.TWOxONE) {
                                    finishSetup();
                                } else {
                                    nextStep();
                                }
                                break;
                            case 8:
                                mapEntry.setSpawn3(player.getLocation());
                                player.sendMessage(MLGRush.getInstance().getPrefix() + "Spawn von Team 3 gesetzt!");
                                this.nextStep();
                                break;
                            case 9:
                                mapEntry.setBed3(player.getLocation());
                                player.sendMessage(MLGRush.getInstance().getPrefix() + "Bett von Team 3 gesetzt!");
                                this.nextStep();
                                break;
                            case 10:
                                mapEntry.setSpawn4(player.getLocation());
                                player.sendMessage(MLGRush.getInstance().getPrefix() + "Spawn von Team 4 gesetzt!");
                                this.nextStep();
                                break;
                            case 11:
                                mapEntry.setBed4(player.getLocation());
                                player.sendMessage(MLGRush.getInstance().getPrefix() + "Bett von Team 4 gesetzt!");
                                finishSetup();
                                break;
                        }
                    }
                }
            }
        }catch (Exception e) {
            
        }
    }

}
