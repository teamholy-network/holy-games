package de.teamholy.clutches.playground.model;

import com.google.common.collect.Lists;
import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.utils.InventoryUtils;
import de.teamholy.api.bukkit.utils.scoreboard.ScoreboardAPI;
import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.player.PlayerEntry;
import de.teamholy.clutches.player.PlayerState;
import de.teamholy.clutches.playground.enums.ArmorColor;
import de.teamholy.clutches.playground.enums.CountdownLocation;
import de.teamholy.clutches.playground.enums.PlaygroundItems;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.clutches.playground.task.PlayerTask;
import de.teamholy.clutches.utils.PlayerUtils;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.PerkManager;
import de.teamholy.core.bukkit.perks.PerkType;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import eu.koboo.en2do.repository.entity.Id;
import io.netty.handler.codec.spdy.SpdyHttpResponseStreamIdHandler;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class PlaygroundPlayer {


    private PlayerEntry playerEntry;
    private ScoreboardAPI scoreboardAPI;
    private PlaygroundWorld playgroundWorld;
    private Player player;
    private org.bukkit.inventory.Inventory inventory;
    private Settings settings = new Settings();
    private PlayerTask playerTask;

    private HitPreset currentEditPreset;
    private boolean chatEdit = false;


    public PlaygroundPlayer(PlayerEntry playerEntry) {
        this.playerEntry = playerEntry;
        this.scoreboardAPI = playerEntry.getScoreboardAPI();
        this.player = playerEntry.getPlayer();
        this.playerTask = new PlayerTask(this);
        settings.setUuid(player.getUniqueId());

        BukkitCore.getAPI().getExecutor().execute(() -> {

            Settings temp = Clutches.getInstance().getPlaygroundManager().getPlaygroundRepository().findFirstById(player.getUniqueId());
            if (temp != null) {
                settings = temp;
            } else Clutches.getInstance().getPlaygroundManager().getPlaygroundRepository().save(settings);

            this.inventory = InventoryUtils.inventoryFromString(settings.getInventoryString());
        });




    }


    public void setScoreboard() {
        scoreboardAPI.clearScoreboard();
        scoreboardAPI.setLine(8, " §8§m--------------- ");
        scoreboardAPI.setLine(7, "§7");
        scoreboardAPI.setLine(6, " §7Map§8: §b" + playgroundWorld.getName());
        scoreboardAPI.setLine(5, "§1");
        scoreboardAPI.setLine(4, settings.getSelectedPreset() == null ? " §cno clutch selected" : " §6" + settings.getSelectedPreset().getName());
        scoreboardAPI.setLine(3, " §7Clutch count§8: §b0");
        scoreboardAPI.setLine(2, "§5");
        scoreboardAPI.setLine(1, " §8§m--------------- ");
        scoreboardAPI.setLine(0, "§o" + Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName());
    }

    public void updateClutchSelectedScore() {
        scoreboardAPI.updateLine(4, settings.getSelectedPreset() == null ? " §cno clutch selected" : " §6" + settings.getSelectedPreset().getName());
    }

    public void saveData() {
        settings.setInventoryString(InventoryUtils.inventoryToString(inventory));
        BukkitCore.getAPI().getExecutor().execute(() -> Clutches.getInstance().getPlaygroundManager().getPlaygroundRepository().save(settings));
    }

    public void openArmorColor() {
        de.teamholy.core.bukkit.utils.Inventory inventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Armor Color", 9);
        PerkPlayerProfile perkPlayerProfile = BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().get(player.getUniqueId());
        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }
        int i = 0;
        for (ArmorColor armorColor : ArmorColor.values()) {
            ItemBuilder itemBuilder = new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherColor(armorColor.getColor());
            itemBuilder.setName("§8» §6" + armorColor.getName());

            if (settings.getArmorColor() == armorColor) {
                itemBuilder.setLore("§2selected");
                itemBuilder.setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                itemBuilder.setAttributs();
            } else {

                String owned = "§ayou own this perk, click to select";
                if ((ArmorColor.isBuyable(armorColor) && perkPlayerProfile.getOwnedPerks().contains(ArmorColor.getId(armorColor))
                        || (!ArmorColor.isBuyable(armorColor) && player.hasPermission(armorColor.getPerkRankType().getPermission())))) {
                    itemBuilder.setLore(owned);
                } else {
                    itemBuilder.setLore("§7This perk costs §e" + armorColor.getPrice() + " §6coins");
                }

            }
            inventory.setItem(itemBuilder.build(), i, event -> {
                boolean sucess = false;
                if (ArmorColor.isBuyable(armorColor)) {
                    if (perkPlayerProfile.getOwnedPerks().contains(ArmorColor.getId(armorColor))) {
                        sucess = true;
                    } else {
                        ArmorColor.buyPerk(player, perkPlayerProfile, armorColor, armorColor.getName());
                        return;
                    }
                } else {
                    sucess = player.hasPermission(armorColor.getPerkRankType().getPermission());
                }

                if (sucess) {
                    player.sendMessage(BukkitHolyAPI.getInstance().getPrefix() + "Armor color selected!");
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 2f, 2f);
                    settings.setArmorColor(armorColor);
                    player.closeInventory();
                }
            });
            i++;
        }

        player.openInventory(inventory.getInventory());
    }

    public void openFeaturedPresets() {
        List<HitPreset> hitPresets = Clutches.getInstance().getPlaygroundManager().getPresentedHits();

        de.teamholy.core.bukkit.utils.Inventory inventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Featured Hit§8-§6Presets", 9);


        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        int i = 0;
        for (HitPreset hitPreset : hitPresets) {

            inventory.setItem(new ItemBuilder(hitPreset.getIcon().getMaterial(), hitPreset.getHitMap().size(), (byte) hitPreset.getIcon().getSubId())
                            .setName("§8» §6" + hitPreset.getName())
                            .setLore(
                                    " ",
                                    " §7This hitpreset has §6" + hitPreset.getHitMap().size() + " §7hits",
                                    " §7leftclick to §aselect §7rightclick to §bimport"
                            )
                            .build(), i, event -> {
                if (event.isLeftClick()) {
                    player.playSound(player.getLocation(),Sound.NOTE_PLING,2,2);
                    settings.setSelectedPreset(hitPreset);
                    updateClutchSelectedScore();
                    player.closeInventory();
                    player.sendMessage(Clutches.PREFIX + "You selected the §6" + hitPreset.getName() + " §7hitpreset");
                    player.sendMessage(Clutches.PREFIX + "Press §6leftclick §7on the §csettings §7item to start clutching");
                } else if (event.isRightClick()) {

                    if (settings.getHitPresetMap().stream().anyMatch(temp -> temp.getUuid().equals(hitPreset.getUuid()))) {
                        player.closeInventory();
                        player.sendMessage(Clutches.PREFIX + "§cYou already have this preset");
                        return;
                    }

                    if (settings.getHitPresetMap().size() < 27) {
                        HitPreset temp = new HitPreset();
                        temp.setIcon(hitPreset.getIcon());
                        temp.setOrigin(HitPreset.Origin.IMPORTED);
                        temp.setHitMap(hitPreset.getHitMap());
                        temp.setName(hitPreset.getName());
                        temp.setUuid(hitPreset.getUuid());

                        player.closeInventory();
                        player.playSound(player.getLocation(),Sound.NOTE_PLING,2,2);
                        settings.getHitPresetMap().add(temp);
                        player.sendMessage(Clutches.PREFIX + "You imported the §6" + hitPreset.getName() + " §7hitpreset");


                    } else {

                        player.playSound(player.getLocation(),Sound.ITEM_BREAK,2,2);
                        player.closeInventory();
                        player.sendMessage(Clutches.PREFIX + "§cYou dont have enough space! delete a hitpreset");

                    }

                }

            });

            i++;
        }

        player.openInventory(inventory.getInventory());

    }

    public void openHitpresets() {

        de.teamholy.core.bukkit.utils.Inventory inventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Hitpresets", 4 * 9);

        int lastRowIndex = (inventory.getInventory().getSize() / 9) - 1;
        int startIndex = lastRowIndex * 9;

        for (int i = startIndex; i < startIndex + 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        int i = 0;
        for (HitPreset hitPreset : settings.getHitPresetMap().stream().sorted(Comparator.comparing(HitPreset::getCreated)).toList()) {

            inventory.setItem(new ItemBuilder(hitPreset.getIcon().getMaterial(), hitPreset.getHitMap().size(), (byte) hitPreset.getIcon().getSubId())
                    .setName("§8» §6" + hitPreset.getName())
                    .setLore(
                            settings.getSelectedPreset() == hitPreset ? "§aselected" : null,
                            hitPreset.getOrigin() == HitPreset.Origin.CREATED ? " §7created§8: §6" + PlayerUtils.convertTime(hitPreset.getCreated()) : " §b§lIMPORTED",
                            " §7last edited§8: §6" + PlayerUtils.convertTime(hitPreset.getLastEdit()),
                            " ",
                            " §7you used this preset §6" + hitPreset.getUsed() + " " + (hitPreset.getUsed() == 1 ? "§7time" : "§7times"),
                            " §7§lrightclick §r§7to §bedit§8, §7§lleftclick §r§7to §aselect",
                            " "
                    )
                    .build(), i, event -> {
                if (event.getClick().isRightClick()) Clutches.getInstance().getPlaygroundManager().getEditInventories().openHitPresetEdit(this,hitPreset);
                if (event.getClick().isLeftClick()) {
                    player.playSound(player.getLocation(),Sound.NOTE_PLING,2,2);
                    settings.setSelectedPreset(hitPreset);
                    updateClutchSelectedScore();
                    player.closeInventory();
                    player.sendMessage(Clutches.PREFIX + "You selected the §6" + hitPreset.getName() + " §7hitpreset");
                    player.sendMessage(Clutches.PREFIX + "Press §6leftclick §7on the §csettings §7item to start clutching");
                }
            });

            i++;
        }

        if (settings.getHitPresetMap().size() < 27) {
            inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setName("§8» §6Create new hitpreset")
                    .setSkullMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWEyZDg5MWM2YWU5ZjZiYWEwNDBkNzM2YWI4NGQ0ODM0NGJiNmI3MGQ3ZjFhMjgwZGQxMmNiYWM0ZDc3NyJ9fX0=","").build(), 31, event -> {

                HitPreset hitPreset = new HitPreset();
                settings.getHitPresetMap().add(hitPreset);
                Clutches.getInstance().getPlaygroundManager().getEditInventories().openHitPresetEdit(this,hitPreset);


            });
        } else {
            inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setName("§cDelete a hitpreset to create a new one")
                            .setLore("§7you reached the §cmax §7amount of hitpresets")
                    .setSkullMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==","").build(), 31);

        }

        player.openInventory(inventory.getInventory());

    }



    public void openSettings() {
        de.teamholy.core.bukkit.utils.Inventory inventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Settings", 6 * 9);

        currentEditPreset = null;
        chatEdit = false;
        playerTask.stopIfActive();

        for (int i = 0; i < 6 * 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }


        inventory.setItem(null, 16);
        inventory.setItem(null, 14);
        inventory.setItem(null, 12);

        if (settings.getSelectedPreset() == null) {
            inventory.setItem(new ItemBuilder(Material.BARRIER).setName("§cNo clutch selected").build(),10);
        } else {
            inventory.setItem(new ItemBuilder(settings.getSelectedPreset().getIcon().getMaterial(),settings.getSelectedPreset().getHitMap().size(),settings.getSelectedPreset().getIcon().subId).setName("§7Selected clutch§8: §6" +settings.getSelectedPreset().getName()).build(),10);
        }

        inventory.setItem(new ItemBuilder(Material.RED_SANDSTONE).setName("§8» §6Hit presets").setLore(" ", " §7here you can §esee §8& §acreate", " §7your own §cHit presets§7! ", " ").build(), 13, event -> openHitpresets());
        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setSkullMeta(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cm" +
                        "UvNzZjYmFlNzI0NmNjMmM2ZTg4ODU4NzE5OGM3OTU5OTc5NjY2YjRmNWE0MDg4ZjI0ZTI2ZTA3NWYxNDBhZTZjMyJ9fX0=", "").setName("§8» §6Featured hit presets").setLore(" ", " §7here you can §esee §8& §bcopy", " §7featured §cHit presets ", " §7of people like §52sa §8& §5derNOZE " , " ").build(), 15,event -> openFeaturedPresets());

        // perks
        inventory.setItem(null, 30);
        inventory.setItem(null, 34);

        inventory.setItem(new ItemBuilder(Material.DIAMOND).setName("§8» §6Perks").build(), 28);

        inventory.setItem(new ItemBuilder(Material.SANDSTONE).setName("§8» §6Blocks").build(), 31,
                event -> BukkitCore.getInstance().getPerkManager().openSecondPerkInventory(player, PerkType.BLOCK, PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL));
        inventory.setItem(new ItemBuilder(Material.LEATHER_CHESTPLATE).setName("§8» §6Armor").build(), 32, event -> openArmorColor());
        inventory.setItem(new ItemBuilder(Material.STICK).setName("§8» §6Sticks").build(), 33,
                event -> BukkitCore.getInstance().getPerkManager().openSecondPerkInventory(player, PerkType.STICK, PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL));


        //settings
        inventory.setItem(new ItemBuilder(Material.REDSTONE_COMPARATOR).setName("§8» §6Settings").build(), 37);
        inventory.setItem(new ItemBuilder(Material.IRON_SWORD).setName("§8» §6PvP §8(§cno damage§8)")
                .setLore("§7currently " + (settings.isPvpEnabled() ? "§aenabled" : "§cdisabled"))
                .build(), 39, event -> {

            settings.setPvpEnabled(!settings.isPvpEnabled());
            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2, 2);
            openSettings();
        });

        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setSkullMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV" +
                        "0L3RleHR1cmUvOTQzOGI1MTEzOGY4MGE0OWYyZDE5ZjliMWFiZWQ5OWUxZjMyMjVlYmNmZThjOGI0Nzk4NDkxZTFiY2RhOTVlMiJ9fX0=", "").setName("§8» §6Adjust hit direction")
                .setLore(
                        "§7currently " + (settings.isAdjustDirection() ? "§aenabled " : "§cdisabled "),
                        " ",
                        " §7if enabled you will get hit in a ",
                        " §efixed §7postion for your clutch, ",
                        " §7meaning 180°§8, §790°§8, §70°§8 & §7-90° ",
                        " "

                )
                .build(), 40, event -> {

            settings.setAdjustDirection(!settings.isAdjustDirection());
            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2, 2);
            openSettings();
        });

        inventory.setItem(new ItemBuilder(Material.ARMOR_STAND, 1, (byte) 0).setName("§8» §6Inventory sort").build(), 41, event -> {
            de.teamholy.core.bukkit.utils.Inventory sort = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Sort inventory", 9, false);
            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2, 2);


            sort.getInventory().setContents(getInventory().getContents());
            player.getInventory().clear();

            sort.setOnClose(inventoryCloseEvent -> {
                if (PlaygroundItems.correctInventory(sort.getInventory())) {
                    setInventory(sort.getInventory());
                    player.sendMessage(Clutches.PREFIX + "Your inventory sort was saved");
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 2f, 2f);
                } else {
                    setInventory(PlaygroundItems.newInventory());
                    player.sendMessage(Clutches.PREFIX + "Your inventory was not saved");
                    player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 2f, 2f);
                }
                Bukkit.getScheduler().runTaskLater(Clutches.getInstance(), this::setItems, 1);
            });


            playerEntry.getPlayer().openInventory(sort.getInventory());
        });

        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, settings.getCountdown(), (byte) 3).setSkullMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg2YjlkNThiY2QxYTU1NWY5M2U3ZDg2NTkxNTljZmQyNWI4ZGQ2ZTliY2UxZTk3MzgyMjgyNDI5MTg2MiJ9fX0=", "").setName("§8» §6Clutch countdown")
                .setLore("§7Currently selected §8» §b" + settings.getCountdown(), " ", " §crightclick §7-1 ", " §aleftclick §7+1 ", " ").build(), 42, event -> {

            if (event.getClick().isRightClick()) {
                if (!(settings.getCountdown() <= 2)) {
                    getSettings().setCountdown(settings.getCountdown() - 1);
                    openSettings();
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2, 2);
                }
            } else if (event.getClick().isLeftClick()) {
                if (!(settings.getCountdown() >= 12)) {
                    getSettings().setCountdown(settings.getCountdown() + 1);
                    openSettings();
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2, 2);
                }
            }

        });

        inventory.setItem(new ItemBuilder(Material.PAPER, 1).setName("§8» §6Clutch countdown location")
                .setLore(Arrays.stream(CountdownLocation.values()).map(value -> (settings.getCountdownLocation() == value) ? "§a" + value.toString().toLowerCase() : "§7" + value.toString().toLowerCase()).collect(Collectors.toList())
                ).build(), 43, event -> {

            switch (settings.getCountdownLocation()) {
                case TITLE -> settings.setCountdownLocation(CountdownLocation.ACTIONBAR);
                case ACTIONBAR -> settings.setCountdownLocation(CountdownLocation.CHAT);
                case CHAT -> settings.setCountdownLocation(CountdownLocation.TITLE);
            }

            openSettings();
            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2, 2);
        });


        player.openInventory(inventory.getInventory());
    }

    public void updateClutchCountScore(int count) {
        scoreboardAPI.updateLine(3, " §7Clutch count§8: §b" + count);
    }

    public void setItems() {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        int slot = 0;

        player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 1).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(settings.getArmorColor().getColor()).setUnbreakable().build());
        player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 1).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(settings.getArmorColor().getColor()).setUnbreakable().build());
        player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 1).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(settings.getArmorColor().getColor()).setUnbreakable().build());
        player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 1).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(settings.getArmorColor().getColor()).setUnbreakable().build());

        for (ItemStack itemStack : getInventory().getContents()) {

            if (itemStack != null && itemStack.getType() != null) {
                if (itemStack.getType() == Material.STICK) {
                    player.getInventory().setItem(slot, BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.STICK).setUnbreakable().setEnchantments(Enchantment.KNOCKBACK, 1).build());
                } else if (itemStack.getType() == Material.SANDSTONE) {
                    player.getInventory().setItem(slot, BukkitCore.getInstance().getPerkManager().getPerk(player, PerkType.BLOCK).setAmount(64).build());
                } else {
                    this.player.getInventory().setItem(slot, itemStack);
                }
            }

            slot++;
        }
    }

    public void join(PlaygroundWorld playgroundWorld) {
        this.playgroundWorld = playgroundWorld;
        playerEntry.setPlayerState(PlayerState.PLAYGROUND);
        setScoreboard();
        setItems();
        player.teleport(playgroundWorld.getSpawns().get(new Random().nextInt(playgroundWorld.getSpawns().size())));
        Clutches.getInstance().getHologramManager().updateHolograms();
    }


    public void quit() {
        setChatEdit(false);
        setCurrentEditPreset(null);
        playerTask.stopIfActive();
        playerEntry.setPlayerState(PlayerState.LOBBY);
        playgroundWorld = null;
        playerEntry.setItemsSpawn();
        player.teleport(BukkitHolyAPI.getInstance().getLocationManager().getLocation("lobby"));
        playerEntry.setScoreboard();
        Clutches.getInstance().getHologramManager().updateHolograms();
    }


}
