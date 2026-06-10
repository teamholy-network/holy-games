package de.teamholy.clutches.playground;

import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.playground.model.Hit;
import de.teamholy.clutches.playground.model.HitPreset;
import de.teamholy.clutches.playground.model.PlaygroundPlayer;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/* copyright by Yassino */
public class EditInventories implements Listener {


    private final DecimalFormat decimalFormat = new DecimalFormat("#.###");

    public EditInventories() {
        Clutches.getInstance().getServer().getPluginManager().registerEvents(this, Clutches.getInstance());
    }

    public void openHitPresetEdit(PlaygroundPlayer playgroundPlayer, HitPreset hitPreset) {
        de.teamholy.core.bukkit.utils.Inventory inventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Hitpreset edit §8(§7" + hitPreset.getName() + "§8)", 2 * 9);
        Player player = playgroundPlayer.getPlayer();

        AtomicBoolean openHitPresets = new AtomicBoolean(true);

        playgroundPlayer.setCurrentEditPreset(hitPreset);

        for (int i = 0; i < 18; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        inventory.setItem(new ItemBuilder(Material.PAPER).setName("§c§lNOTE").setLore(" ", " §7close your inventory to §asave ", " ").build(), 4);

        inventory.setItem(new ItemBuilder(Material.NAME_TAG).setName("§8» §6Edit name").setLore("§7current name§8: §6" + hitPreset.getName()).build(), 10, event -> {
            playgroundPlayer.setChatEdit(true);
            player.sendMessage(Clutches.PREFIX + "Write the name of your hitpreset in the chat");
            player.sendMessage(Clutches.PREFIX + "if you wanna abort type §c§lcancel");
            openHitPresets.set(false);
            player.closeInventory();
        });

        inventory.setItem(new ItemBuilder(hitPreset.getIcon().getMaterial(), 1, (byte) hitPreset.getIcon().getSubId()).setName("§8» §6Change icon").build(), 12, event -> {
            openHitPresets.set(false);
            openHitPresetIcon(playgroundPlayer, hitPreset);
        });

        inventory.setItem(new ItemBuilder(Material.GLASS).setAmount(hitPreset.getHitMap().size())
                .setLore((hitPreset.getHitMap().size() == 0 ? "§cYou dont have any hits yet, click to create" : "§7You currently have §6" + hitPreset.getHitMap().size() + " §7hits"))
                .setName("§8» §6Edit hits").build(), 14, event -> {

            openHitPresets.set(false);
            openHits(playgroundPlayer,hitPreset);

        });


        inventory.setItem(new ItemBuilder(Material.INK_SACK, 1, (byte) 1).setName("§cDelete preset").build(), 16, event -> {
            openHitPresets.set(false);
            openDelete(playgroundPlayer, hitPreset);
        });

        inventory.setOnClose(event -> Bukkit.getScheduler().runTaskLater(Clutches.getInstance(), () -> {
            hitPreset.setLastEdit(System.currentTimeMillis());
            if (playgroundPlayer.getSettings().getSelectedPreset() != null && playgroundPlayer.getSettings().getSelectedPreset().getUuid().equals(hitPreset.getUuid())) playgroundPlayer.getSettings().setSelectedPreset(hitPreset);
            playgroundPlayer.saveData();
            if (playgroundPlayer != null && playgroundPlayer.getPlayer().isOnline() && openHitPresets.get())
                playgroundPlayer.openHitpresets();
        }, 1));

        playgroundPlayer.getPlayer().openInventory(inventory.getInventory());
    }

    private void openHitPresetIcon(PlaygroundPlayer playgroundPlayer, HitPreset hitPreset) {
        de.teamholy.core.bukkit.utils.Inventory inventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Change icon §8(§7" + hitPreset.getName() + "§8)", 3 * 9);

        for (int i = 0; i < 3 * 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        int i = 0;
        for (HitPreset.Icon icon : HitPreset.Icon.values()) {
            inventory.setItem(new ItemBuilder(icon.getMaterial(), 1, (byte) icon.getSubId()).build(), i, event -> {
                hitPreset.setIcon(icon);
                playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(), Sound.HORSE_ARMOR, 2, 2);
                playgroundPlayer.getPlayer().closeInventory();
            });
            i++;
        }

        inventory.setOnClose(event -> Bukkit.getScheduler().runTaskLater(Clutches.getInstance(), () -> {
            if (playgroundPlayer != null && playgroundPlayer.getPlayer().isOnline())
                openHitPresetEdit(playgroundPlayer, hitPreset);
        }, 1));

        playgroundPlayer.getPlayer().openInventory(inventory.getInventory());
    }

    private void openHitEditor(PlaygroundPlayer playgroundPlayer, Hit hit) {
        Inventory inventory = new Inventory("§8» §6Hit editor", 5*9);

        for (int i = 0; i < 5*9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }



        for (int i = 9; i < 18; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setName("§8//").build(), i);
        }

        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM,1,3).setName("§6§lTemplates §8»").setSkullMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjlj" +
                "YjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19","").build(),0);

        AtomicBoolean goBackToHits = new AtomicBoolean(true);


        inventory.setItem(new ItemBuilder(Material.STAINED_GLASS,1, (byte) 5)
                .setName("§6Teamholy §aEasy")
                        .setLore(" " , " §7Teamholy x knockback on §aeasy §7is §a0.8 " , " §7Click to set §a0.8 §ex-knock §7on your hit! " , " ")
                .build(),2,event -> {

            goBackToHits.set(false);
            hit.setXknock(0.8);
            playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(), Sound.HORSE_ARMOR, 2, 2);
            openHitEditor(playgroundPlayer,hit);
        });

        inventory.setItem(new ItemBuilder(Material.STICK, 1).setName("§8» §6Hit direction §8(§ediagonal)§8")
                .setLore(
                        Arrays.stream(Hit.DiagonalDirection.values()).map(value -> (hit.getDiagonalDirection() == value) ? "§a" + value.getName() : "§7" + value.getName()).collect(Collectors.toList())
                ).build(), 34, event -> {

            switch (hit.getDiagonalDirection()) {
                case STRAIGHT -> hit.setDiagonalDirection(Hit.DiagonalDirection.LEFT);
                case LEFT -> hit.setDiagonalDirection(Hit.DiagonalDirection.RIGHT);
                case RIGHT -> hit.setDiagonalDirection(Hit.DiagonalDirection.STRAIGHT);
            }

            goBackToHits.set(false);
            playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(), Sound.HORSE_ARMOR, 2, 2);
            openHitEditor(playgroundPlayer,hit);
        });

        inventory.setItem(new ItemBuilder(Material.STAINED_GLASS,1, (byte) 7)
                .setName("§6Teamholy §6Medium")
                .setLore(" " , " §7Teamholy x knockback on §6medium §7is §61.2 " , " §7Click to set §61.2 §ex-knock §7on your hit! " , " ")
                .build(),3,event -> {

            goBackToHits.set(false);
            hit.setXknock(1.2);
            playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(), Sound.HORSE_ARMOR, 2, 2);
            openHitEditor(playgroundPlayer,hit);
        });

        inventory.setItem(new ItemBuilder(Material.STAINED_GLASS,1, (byte) 14)
                .setName("§6Teamholy §cHard")
                .setLore(" " , " §7Teamholy x knockback on §chard §7is §c1.6 " , " §7Click to set §c1.6 §ex-knock §7on your hit! " , " ")
                .build(),4,event -> {

            goBackToHits.set(false);
            hit.setXknock(1.6);
            playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(), Sound.HORSE_ARMOR, 2, 2);
            openHitEditor(playgroundPlayer,hit);
        });



        inventory.setItem(new ItemBuilder(Material.STICK,1, (byte) 0)

                .setName("§6Teamholy Reduce")
                .setLore(" " , " §7Teamholy y knockback on §6reducer §7is §60.353 " , " §7Click to set §60.353 §ey-knock §7on your hit! " , " ")
                .build(),6,event -> {

            goBackToHits.set(false);
            hit.setYknock(0.353);
            playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(), Sound.HORSE_ARMOR, 2, 2);
            openHitEditor(playgroundPlayer,hit);
        });

        inventory.setItem(new ItemBuilder(Material.SANDSTONE,1, (byte) 0)

                .setName("§6Teamholy Clutch §7/ §6Diagonal Clutch")
                .setLore(" " , " §7Teamholy y knockback on §6clutch §7is §60.373 " , " §7Click to set §60.373 §ey-knock §7on your hit! " , " ")
                .build(),7,event -> {

            goBackToHits.set(false);
            hit.setYknock(0.373);
            playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(), Sound.HORSE_ARMOR, 2, 2);
            openHitEditor(playgroundPlayer,hit);
        });

        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM,1, (byte) 3)

                .setName("§6Teamholy Multireduce")
                .setLore(" " , " §7Teamholy y knockback on §6multireduce §7is §60.363 " , " §7Click to set §60.363 §ey-knock §7on your hit! " , " ")
                .build(),8,event -> {

            goBackToHits.set(false);
            hit.setYknock(0.363);
            playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(), Sound.HORSE_ARMOR, 2, 2);
            openHitEditor(playgroundPlayer,hit);
        });

        String plus = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19";
        String minus = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=";


        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM,1, (byte) 3).setName("§c- §6to x")
                .setLore(" ", " §7leftclick§8: §c- 0.00§l1 "," §7rightclick§8: §c- 0.0§l1 "," §7shiftclick§8: §c- 0.§l1 "," §7middleclick§8: §c- §l1 ", " " , "§7current x§8-§7knock value§8: §6" + decimalFormat.format(hit.getXknock()))
                .setSkullMeta(minus,"")
                .build(),37, event -> {


            switch (event.getClick()) {
                case LEFT -> hit.setXknock(hit.getXknock() - 0.001);
                case RIGHT -> hit.setXknock(hit.getXknock() - 0.01);
                case SHIFT_LEFT, SHIFT_RIGHT -> hit.setXknock(hit.getXknock() - 0.1);
                case MIDDLE -> hit.setXknock(hit.getXknock() - 1);
            }

            if (hit.getXknock() >= Hit.MAX_XKNOCK) {
                playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(),Sound.NOTE_BASS,2,2);
                hit.setXknock(Hit.MAX_XKNOCK);
            } else if (hit.getXknock() <= Hit.MIN_XKNOCK) {
                playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(),Sound.NOTE_BASS,2,2);
                hit.setXknock(Hit.MIN_XKNOCK);
            }
            goBackToHits.set(false);


            openHitEditor(playgroundPlayer,hit);

        });


        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM,1, (byte) 3).setName("§cX Knockback §8» §6" + decimalFormat.format(hit.getXknock()))
                .setSkullMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzM4YWIxNDU3NDdiNGJkMDljZTA" +
                        "zNTQzNTQ5NDhjZTY5ZmY2ZjQxZDllMDk4YzY4NDhiODBlMTg3ZTkxOSJ9fX0=","")
                .build(),28);


        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM,1, (byte) 3).setName("§a+ §6to x")
                        .setLore(" ", " §7leftclick§8: §a+ 0.00§l1 "," §7rightclick§8: §a+ 0.0§l1 "," §7shiftclick§8: §a+ 0.§l1 ", " §7middleclick§8: §a+ §l1 ", " " , "§7current x§8-§7knock value§8: §6" + decimalFormat.format(hit.getXknock()))
                .setSkullMeta(plus,"")
                .build(),19, event -> {



            switch (event.getClick()) {
                case LEFT -> hit.setXknock(hit.getXknock() + 0.001);
                case RIGHT -> hit.setXknock(hit.getXknock() + 0.01);
                case SHIFT_LEFT, SHIFT_RIGHT -> hit.setXknock(hit.getXknock() + 0.1);
                case MIDDLE -> hit.setXknock(hit.getXknock() + 1);
            }

            if (hit.getXknock() >= Hit.MAX_XKNOCK) {
                playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(),Sound.NOTE_BASS,2,2);
                hit.setXknock(Hit.MAX_XKNOCK);
            } else if (hit.getXknock() <= Hit.MIN_XKNOCK) {
                playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(),Sound.NOTE_BASS,2,2);
                hit.setXknock(Hit.MIN_XKNOCK);
            }

            goBackToHits.set(false);
            openHitEditor(playgroundPlayer, hit);

        });

        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setName("§cY Knockback §8» §6" + decimalFormat.format(hit.getYknock()))
                .setSkullMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTcxMDcxYmVmNzMzZj" +
                        "Q3NzAyMWIzMjkxZGMzZDQ3ZjBiZGYwYmUyZGExYjE2NWExMTlhOGZmMTU5NDU2NyJ9fX0=", "")
                .build(), 30);


        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setName("§c- §6to Y")
                .setLore(" ", " §7leftclick§8: §c- 0.00§l1 ", " §7rightclick§8: §c- 0.0§l1 ", " §7shiftclick§8: §c- 0.§l1 ", " §7middleclick§8: §c- §l1 " ,  " ", "§7current y§8-§7knock value§8: §6" + decimalFormat.format(hit.getYknock()))
                .setSkullMeta(minus, "")
                .build(), 39, event -> {




            switch (event.getClick()) {
                case LEFT -> hit.setYknock(hit.getYknock() - 0.001);
                case RIGHT -> hit.setYknock(hit.getYknock() - 0.01);
                case SHIFT_LEFT, SHIFT_RIGHT -> hit.setYknock(hit.getYknock() - 0.1);
                case MIDDLE -> hit.setYknock(hit.getYknock() - 1);
            }

            if (hit.getYknock() >= Hit.MAX_YKNOCK) {
                playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(),Sound.NOTE_BASS,2,2);
                hit.setYknock(Hit.MAX_YKNOCK);
            } else if (hit.getYknock() <= Hit.MIN_YKNOCK) {
                playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(),Sound.NOTE_BASS,2,2);
                hit.setYknock(Hit.MIN_YKNOCK);
            }

            goBackToHits.set(false);
            openHitEditor(playgroundPlayer, hit);


        });


        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setName("§a+ §6to Y")
                .setLore(" ", " §7leftclick§8: §a+ 0.00§l1 ", " §7rightclick§8: §a+ 0.0§l1 ", " §7shiftclick§8: §a+ 0.§l1 "," §7middleclick§8: §a+ §l1 ", " ", "§7current y§8-§7knock value§8: §6" + decimalFormat.format(hit.getYknock()))
                .setSkullMeta(plus, "")
                .build(), 21, event -> {




            switch (event.getClick()) {
                case LEFT -> hit.setYknock(hit.getYknock() + 0.001);
                case RIGHT -> hit.setYknock(hit.getYknock() + 0.01);
                case SHIFT_LEFT, SHIFT_RIGHT -> hit.setYknock(hit.getYknock() + 0.1);
                case MIDDLE -> hit.setYknock(hit.getYknock() + 1);
            }

            if (hit.getYknock() >= Hit.MAX_YKNOCK) {
                playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(),Sound.NOTE_BASS,2,2);
                hit.setYknock(Hit.MAX_YKNOCK);
            } else if (hit.getYknock() <= Hit.MIN_YKNOCK) {
                playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(),Sound.NOTE_BASS,2,2);
                hit.setYknock(Hit.MIN_YKNOCK);
            }

            goBackToHits.set(false);
            openHitEditor(playgroundPlayer, hit);


        });

        inventory.setOnClose(event -> Bukkit.getScheduler().runTaskLater(Clutches.getInstance(), () -> {
            if (playgroundPlayer != null && playgroundPlayer.getPlayer().isOnline() && goBackToHits.get())
                openHits(playgroundPlayer, playgroundPlayer.getCurrentEditPreset());
        }, 1));

        inventory.setItem(new ItemBuilder(hit.getIcon().getMaterial(), 1, (byte) hit.getIcon().getSubId()).setName("§8» §6Change icon").build(), 32, event -> {
            goBackToHits.set(false);
            openHitIconChange(playgroundPlayer, hit);
        });


        playgroundPlayer.getPlayer().openInventory(inventory.getInventory());
    }

    public void openHitIconChange(PlaygroundPlayer playgroundPlayer, Hit hit) {

        de.teamholy.core.bukkit.utils.Inventory inventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Edit icon", 6 * 9);

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                if (row == 0 || row == 5 || col == 0 || col == 8) {
                    inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), row * 9 + col);
                }
            }
        }


        int i = 0;
        for (Hit.Icon value : Hit.Icon.values()) {
            while (inventory.getInventory().getItem(i) != null) {
                i++;
            }

            if (inventory.getInventory().getItem(i) == null) {
                inventory.setItem(new ItemBuilder(value.getMaterial(),1, (byte) value.getSubId()).build(),i,event -> {
                    hit.setIcon(value);
                    playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(), Sound.HORSE_ARMOR, 2, 2);
                    playgroundPlayer.getPlayer().closeInventory();
                });
            }

            i++;
        }

        inventory.setOnClose(event -> Bukkit.getScheduler().runTaskLater(Clutches.getInstance(), () -> {
            if (playgroundPlayer != null && playgroundPlayer.getPlayer().isOnline())
                openHitEditor(playgroundPlayer, hit);
        }, 1));


        playgroundPlayer.getPlayer().openInventory(inventory.getInventory());
    }

    private void openHits(PlaygroundPlayer playgroundPlayer, HitPreset hitPreset) {
        de.teamholy.core.bukkit.utils.Inventory inventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Edit hits §8(§7" + hitPreset.getName() + "§8)", 3 * 9);


        AtomicBoolean openHitpreset = new AtomicBoolean(true);

        for (int i = 0; i < 3 * 9; i++) {


            int j = (i + 1);

            Hit hit = hitPreset.getHitMap().get(j);



            ItemBuilder itemBuilder;
            if (hit != null) {
                itemBuilder = new ItemBuilder(hit.getIcon().getMaterial(),1, (byte) hit.getIcon().getSubId()).setName("§aClick to edit hit §7#§b" + j);
                itemBuilder.setLore("§6X §7value§8: §e" + decimalFormat.format(hit.getXknock()) + "§8, §6Y §7value§8: §e" + decimalFormat.format(hit.getYknock())," " , " §7leftclick to §aedit " , " §7rightclick to §cdelete " , " ");
            } else {
                itemBuilder = new ItemBuilder(Material.STAINED_GLASS).setName("§cClick to edit hit §7#§b" + j);
            }

            int finalI = j;
            inventory.setItem(itemBuilder.build(),i, event -> {
                if (hit != null) {
                    if (event.getClick().isRightClick()) {
                        hitPreset.getHitMap().remove(finalI);
                        playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(),Sound.ANVIL_BREAK,2,2);
                        openHitpreset.set(false);
                        openHits(playgroundPlayer,hitPreset);
                    } else if (event.getClick().isLeftClick()) {
                        openHitpreset.set(false);
                        openHitEditor(playgroundPlayer,hit);
                    }
                } else {
                    openHitpreset.set(false);
                    Hit newHit = new Hit();
                    hitPreset.getHitMap().put(finalI,newHit);
                    openHitEditor(playgroundPlayer,newHit);
                }
            });

        }

        inventory.setOnClose(event -> Bukkit.getScheduler().runTaskLater(Clutches.getInstance(), () -> {
            if (playgroundPlayer != null && playgroundPlayer.getPlayer().isOnline())
                if (openHitpreset.get()) openHitPresetEdit(playgroundPlayer, hitPreset);
        }, 1));

        playgroundPlayer.getPlayer().openInventory(inventory.getInventory());
    }

    private void openDelete(PlaygroundPlayer playgroundPlayer, HitPreset hitPreset) {
        Inventory inventory = new Inventory("§8» §6Delete §6" + hitPreset.getName(), 9);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        AtomicBoolean openAgain = new AtomicBoolean(true);

        inventory.setItem(new ItemBuilder(hitPreset.getIcon().getMaterial(), 1, (byte) hitPreset.getIcon().getSubId()).setLore("§7Do you want to delete your hitpreset named §6" + hitPreset.getName() + "§7?").build(), 4);


        inventory.setItem(new ItemBuilder(Material.INK_SACK, 1, (byte) 10).setName("§8» §aYes").build(), 2, event -> {
            openAgain.set(false);
            playgroundPlayer.getPlayer().closeInventory();
            playgroundPlayer.getPlayer().sendMessage(Clutches.PREFIX + "§cYou deleted the preset §6" + hitPreset.getName());
            playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(), Sound.ANVIL_BREAK, 2, 2);
            if (playgroundPlayer.getSettings().getSelectedPreset() != null && playgroundPlayer.getSettings().getSelectedPreset().getUuid().equals(hitPreset.getUuid())) playgroundPlayer.getSettings().setSelectedPreset(null);
            playgroundPlayer.updateClutchSelectedScore();
            playgroundPlayer.getSettings().getHitPresetMap().removeIf(temp -> temp.getUuid().equals(hitPreset.getUuid()));
        });

        inventory.setItem(new ItemBuilder(Material.INK_SACK, 1, (byte) 1).setName("§8» §cNo").build(), 6, event -> {
            playgroundPlayer.getPlayer().closeInventory();
        });

        inventory.setOnClose(event -> {

            Bukkit.getScheduler().runTaskLater(Clutches.getInstance(), () -> {
                if (playgroundPlayer != null && playgroundPlayer.getPlayer().isOnline())
                    if (openAgain.get()) openHitPresetEdit(playgroundPlayer, hitPreset);
            }, 1);

        });

        playgroundPlayer.getPlayer().openInventory(inventory.getInventory());

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlaygroundPlayer playgroundPlayer = Clutches.getInstance().getPlayerEntryHandler().get(player.getUniqueId()).getPlaygroundPlayer();
        if (!playgroundPlayer.isChatEdit()) return;
        event.setCancelled(true);

        if (playgroundPlayer.getCurrentEditPreset() == null) {
            player.sendMessage(Clutches.PREFIX + "Error while changing name");
            playgroundPlayer.setChatEdit(false);
            return;
        }

        if (event.getMessage().equalsIgnoreCase("cancel")) {
            openHitPresetEdit(playgroundPlayer, playgroundPlayer.getCurrentEditPreset());
            return;
        }

        if (event.getMessage().length() > 20) {
            player.sendMessage(Clutches.PREFIX + "The name can't be longer than 20 characters §8(§cspaces included§8)");
            return;
        }

        HitPreset hitPreset = playgroundPlayer.getCurrentEditPreset();
        hitPreset.setName(event.getMessage());
        playgroundPlayer.getPlayer().playSound(playgroundPlayer.getPlayer().getLocation(), Sound.HORSE_ARMOR, 2, 2);

        playgroundPlayer.setCurrentEditPreset(hitPreset);
        openHitPresetEdit(playgroundPlayer, hitPreset);

    }

}
