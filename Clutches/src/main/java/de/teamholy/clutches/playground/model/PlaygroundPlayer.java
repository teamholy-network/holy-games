package de.teamholy.clutches.playground.model;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.utils.scoreboard.ScoreboardAPI;
import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.player.PlayerEntry;
import de.teamholy.clutches.player.PlayerState;
import de.teamholy.clutches.playground.enums.ArmorColor;
import de.teamholy.clutches.playground.enums.PlaygroundItems;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.PerkManager;
import de.teamholy.core.bukkit.perks.PerkType;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Getter
@Setter
public class PlaygroundPlayer {

    private boolean pvpEnabled = false; // setting
    private boolean adjustDirection = true; // setting
    private int countdown = 3; // setting
    private Inventory inventory = PlaygroundItems.newInventory(); // setting
    private CountdownLocation countdownLocation = CountdownLocation.TITLE; // setting
    private ArmorColor armorColor = ArmorColor.GREY; // setting
    private List<HitPreset> hitPresets = new ArrayList<>();

    private PlayerEntry playerEntry;
    private ScoreboardAPI scoreboardAPI;
    private PlaygroundWorld playgroundWorld;
    private Player player;

    public enum CountdownLocation {TITLE,ACTIONBAR,CHAT}

    public PlaygroundPlayer(PlayerEntry playerEntry, GameProfile gameProfile) {
        this.playerEntry = playerEntry;
        this.scoreboardAPI = playerEntry.getScoreboardAPI();
        this.player = playerEntry.getPlayer();
    }

    public void setScoreboard() {
        scoreboardAPI.clearScoreboard();
        scoreboardAPI.setLine(8, " §8§m--------------- ");
        scoreboardAPI.setLine(7, "§7");
        scoreboardAPI.setLine(6, " §7Map§8: §b" + playgroundWorld.getName());
        scoreboardAPI.setLine(5, "§1");
        scoreboardAPI.setLine(4, " §7Selected§8: §cnone");
        scoreboardAPI.setLine(3, " §7Clutch count§8: §b0");
        scoreboardAPI.setLine(2, "§5");
        scoreboardAPI.setLine(1, " §8§m--------------- ");
        scoreboardAPI.setLine(0, "§o" + Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName());
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

            if (getArmorColor() == armorColor) {
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
                    setArmorColor(armorColor);
                    player.closeInventory();
                }
            });
            i++;
        }

        player.openInventory(inventory.getInventory());
    }

    public void openSettings() {
        de.teamholy.core.bukkit.utils.Inventory inventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Settings", 6 * 9);


        for (int i = 0; i < 6 * 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }


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
                .setLore("§7currently " + (isPvpEnabled() ? "§aenabled" : "§cdisabled"))
                .build(), 39, event -> {

            setPvpEnabled(!isPvpEnabled());
            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2, 2);
            openSettings();
        });

        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setSkullMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV" +
                        "0L3RleHR1cmUvOTQzOGI1MTEzOGY4MGE0OWYyZDE5ZjliMWFiZWQ5OWUxZjMyMjVlYmNmZThjOGI0Nzk4NDkxZTFiY2RhOTVlMiJ9fX0=", "").setName("§8» §6Adjust hit direction")
                .setLore(
                        "§7currently " + (isAdjustDirection() ? "§aenabled " : "§cdisabled "),
                        " ",
                        " §7if enabled you will get hit in a ",
                        " §efixed §7postion for your clutch, ",
                        " §7meaning 180°§8, §790°§8, §70°§8 & §7-90° ",
                        " ",
                        " §cnote §7if disabled diagonal clutches wont work ",
                        " §8(§7they do but you need to stand diagonal§8) ",
                        " "

                )
                .build(), 40, event -> {

            setAdjustDirection(!isAdjustDirection());
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

        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, countdown, (byte) 3).setSkullMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg2YjlkNThiY2QxYTU1NWY5M2U3ZDg2NTkxNTljZmQyNWI4ZGQ2ZTliY2UxZTk3MzgyMjgyNDI5MTg2MiJ9fX0=","").setName("§8» §6Clutch countdown")
                .setLore("§7Currently selected §8» §b" + countdown," ", " §crightclick §7-1 ", " §aleftclick §7+1 " , " ").build(), 42, event -> {

            if (event.getClick().isRightClick()) {
                if (!(countdown <= 2)) {
                    setCountdown(countdown - 1);
                    openSettings();
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2, 2);
                }
            } else if (event.getClick().isLeftClick()) {
                if (!(countdown >= 12)) {
                    setCountdown(countdown + 1);
                    openSettings();
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2, 2);
                }
            }

        });

        inventory.setItem(new ItemBuilder(Material.PAPER, 1).setName("§8» §6Clutch countdown location")
                .setLore(
                        Arrays.stream(CountdownLocation.values()).map(value -> (countdownLocation == value) ? "§a" + value.toString().toLowerCase() : "§7" + value.toString().toLowerCase()).collect(Collectors.toList())
                ).build(), 43, event -> {

            switch (countdownLocation) {
                case TITLE -> countdownLocation = CountdownLocation.ACTIONBAR;
                case ACTIONBAR -> countdownLocation = CountdownLocation.CHAT;
                case CHAT -> countdownLocation = CountdownLocation.TITLE;
            }

            openSettings();
            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2, 2);
        });





        player.openInventory(inventory.getInventory());
    }

    public void setItems() {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        int slot = 0;

        player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 1).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(armorColor.getColor()).setUnbreakable().build());
        player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 1).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(armorColor.getColor()).setUnbreakable().build());
        player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 1).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(armorColor.getColor()).setUnbreakable().build());
        player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 1).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(armorColor.getColor()).setUnbreakable().build());

        for (ItemStack itemStack : inventory.getContents()) {

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
        playerEntry.setPlayerState(PlayerState.LOBBY);
        playgroundWorld = null;
        playerEntry.setItemsSpawn();
        player.teleport(BukkitHolyAPI.getInstance().getLocationManager().getLocation("lobby"));
        playerEntry.setScoreboard();
        Clutches.getInstance().getHologramManager().updateHolograms();
    }


}
