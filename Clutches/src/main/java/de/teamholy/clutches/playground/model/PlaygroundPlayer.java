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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public enum CountdownLocation { CHAT , ACTIONBAR , TITLE }

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
                itemBuilder.setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,2);
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
            inventory.setItem(itemBuilder.build(),i,event -> {
                boolean sucess = false;
                if (ArmorColor.isBuyable(armorColor)) {
                    if (perkPlayerProfile.getOwnedPerks().contains(ArmorColor.getId(armorColor))) {
                        sucess = true;
                    } else {
                        ArmorColor.buyPerk(player,perkPlayerProfile,armorColor,armorColor.getName());
                        return;
                    }
                } else {
                    sucess = player.hasPermission(armorColor.getPerkRankType().getPermission());
                }

                if (sucess) {
                    player.sendMessage(BukkitHolyAPI.getInstance().getPrefix() + "Armor color selected!");
                    player.playSound(player.getLocation(), Sound.NOTE_PLING,2f,2f);
                    setArmorColor(armorColor);
                    player.closeInventory();
                }
            });
            i++;
        }

        player.openInventory(inventory.getInventory());
    }
    public void openSettings() {
        de.teamholy.core.bukkit.utils.Inventory inventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Settings", 6*9);


        for (int i = 0; i < 6*9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }



        // perks
        inventory.setItem(null,30);
        inventory.setItem(null,34);

        inventory.setItem(new ItemBuilder(Material.DIAMOND).setName("§8» §6Perks").build(),28);

        inventory.setItem(new ItemBuilder(Material.SANDSTONE).setName("§8» §6Blocks").build(),31,
                event -> BukkitCore.getInstance().getPerkManager().openSecondPerkInventory(player,PerkType.BLOCK, PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL));
        inventory.setItem(new ItemBuilder(Material.LEATHER_CHESTPLATE).setName("§8» §6Armor").build(),32, event -> openArmorColor());
        inventory.setItem(new ItemBuilder(Material.STICK).setName("§8» §6Sticks").build(),33,
                event -> BukkitCore.getInstance().getPerkManager().openSecondPerkInventory(player,PerkType.STICK, PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL));


        //settings
        inventory.setItem(new ItemBuilder(Material.REDSTONE_COMPARATOR).setName("§8» §6Settings").build(),37);


        player.openInventory(inventory.getInventory());
    }

    public void setItems() {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        int slot = 0;

        player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 1).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(armorColor.getColor()).build());
        player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 1).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(armorColor.getColor()).build());
        player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 1).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(armorColor.getColor()).build());
        player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setEnchantments(Enchantment.PROTECTION_PROJECTILE, 1).setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setLeatherColor(armorColor.getColor()).build());

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
