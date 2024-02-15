package de.teamholy.bridge.player.management;

import com.google.common.collect.Lists;
import de.teamholy.bridge.Bridge;
import de.teamholy.bridge.map.skin.BridgeMapSkin;
import de.teamholy.bridge.map.skin.BridgeMapSkins;
import de.teamholy.bridge.map.BridgeMapType;
import de.teamholy.bridge.player.BridgePlayer;
import de.teamholy.bridge.util.ItemBuilder;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.utility.Pagifier;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.PerkManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/* copyright by Yassino */
public class BridgeMapSkinPerkManagment {

    public static final int MAX_MAPS_PER_PAGE = 21;

    public void buyPerk(BridgePlayer bridgePlayer, BridgeMapSkin bridgeMapSkin) {
        PerkPlayerProfile perkProfile = bridgePlayer.getPerkPlayerProfile();

        PlayerProfile playerProfile = BukkitCore.getInstance().getCoreAPI().getPlayerService().getEntity(bridgePlayer.getUuid(), () -> BukkitCore.getInstance().getCoreAPI().getPlayerService().getRepository().findFirstById(bridgePlayer.getUuid()));

        if (playerProfile == null) {
            return;
        }


        if (perkProfile.getOwnedPerks().contains(bridgeMapSkin.getId())) {
            bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "§cYou already own this perk!");
            return;
        }

        if (playerProfile.getCoins() < bridgeMapSkin.getPrice()) {
            bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "§cYou don't have enough coins to buy this perk!");
            return;
        }

        playerProfile.setCoins(playerProfile.getCoins() - bridgeMapSkin.getPrice());
        perkProfile.getOwnedPerks().add(bridgeMapSkin.getId());
        BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().put(bridgePlayer.getUuid(), perkProfile);
        BukkitCore.getAPI().getPerkPlayerService().saveEntity(perkProfile, true, true);
        BukkitCore.getAPI().getPlayerService().saveEntity(playerProfile, true, true);

        for (BridgeMapSkins bridgeMapSkins : Arrays.stream(BridgeMapSkins.values()).filter(map -> map.getBridgeMapSkin().getId() == bridgeMapSkin.getId()).toList()) {
            bridgePlayer.getBridgeSettings().getMapSkins().add(bridgeMapSkins.getBridgeMapSkin());
        }


        Player bukkitPlayer = bridgePlayer.getPlayer();
        if (bukkitPlayer != null) {
            bukkitPlayer.sendMessage(Bridge.PREFIX + "§7You successfully bought the §e" + bridgeMapSkin.getName() + " §7map for §e" + bridgeMapSkin.getPrice() + " §6coins!");
            bukkitPlayer.closeInventory();
            bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.LEVEL_UP, 2.0F, 2.0F);
        }
    }


    public de.teamholy.core.bukkit.utils.Inventory openMapInventory(BridgePlayer bridgePlayer, BridgeMapType bridgeMapType, PerkManager.SortOptionPerk sortOptionPerk, PerkManager.SortOptionPlayer sortOptionPlayer, int page) {
        var size = 4; // 4 rows by default

        List<BridgeMapSkin> maps = Arrays.stream(BridgeMapSkins.values())
                .map(BridgeMapSkins::getBridgeMapSkin)
                .filter(map -> Arrays.stream(map.getBridgeMapTypes()).anyMatch(type -> type == bridgeMapType))
                .filter(map ->
                        switch (sortOptionPlayer) {
                            case OWNED -> (map.isBuyable() && bridgePlayer.getBridgeSettings().getMapSkins().contains(map)
                                    || (map.isRank() && bridgePlayer.getPlayer().hasPermission(map.getRankType().getPermission()))
                                    || (map.isSpecial() && bridgePlayer.getBridgeSettings().getMapSkins().contains(map)
                                    || map.isDefault())
                            );
                            case UNOWNED ->
                                    (map.isBuyable() && !bridgePlayer.getBridgeSettings().getMapSkins().contains(map)
                                            || (map.isRank() && !bridgePlayer.getPlayer().hasPermission(map.getRankType().getPermission()))
                                            || (map.isSpecial() && !bridgePlayer.getBridgeSettings().getMapSkins().contains(map)));
                            default -> true;
                        }
                )
                .filter(map -> {
                    switch (sortOptionPerk) {
                        case COINS -> {
                            return map.isBuyable();
                        }
                        case RANK -> {
                            return map.isRank();
                        }
                        case SPECIAL -> {
                            return map.isSpecial();
                        }
                        default -> {
                            return true;
                        }
                    }
                })
                .sorted((map1, map2) -> {
                    switch (sortOptionPerk) {
                        case COINS -> {
                            return Integer.compare((int) map1.getPrice(), (int) map2.getPrice());
                        }
                        case RANK -> {
                            return map1.getRankType().compareTo(map2.getRankType());
                        }
                        default -> {
                            return Comparator.comparing(BridgeMapSkin::getId).compare(map1, map2);
                        }
                    }
                })
                .toList();

        var pagifier = new Pagifier<BridgeMapSkin>(MAX_MAPS_PER_PAGE);
        pagifier.reset();
        maps.forEach(pagifier::addItem);


        var pageMaps = pagifier.getPage(page);

        if (pageMaps != null) {
            if (pageMaps.size() >= 7 && pageMaps.size() <= 14) {
                size = 5;
            } else if (pageMaps.size() >= 15) {
                size = 6;
            }
        }



        var inventorySize = size * 9;

        de.teamholy.core.bukkit.utils.Inventory holyInventory = new de.teamholy.core.bukkit.utils.Inventory("§8» §6Island skins §e" + (page), inventorySize);


        de.teamholy.core.bukkit.utils.ItemBuilder sortPerk = new de.teamholy.core.bukkit.utils.ItemBuilder(Material.HOPPER).setName("§8» §6Sort");
        de.teamholy.core.bukkit.utils.ItemBuilder sortPlayer = new de.teamholy.core.bukkit.utils.ItemBuilder(Material.DIAMOND).setName("§8» §6Filter");

        sortPerk.setLore(Arrays.stream(PerkManager.SortOptionPerk.values())
                .map(value -> (sortOptionPerk == value) ? "§a" + value.toString().toLowerCase(Locale.ROOT) : "§7" + value.toString().toLowerCase(Locale.ROOT))
                .collect(Collectors.toList()));

        sortPlayer.setLore(Arrays.stream(PerkManager.SortOptionPlayer.values())
                .map(value -> (sortOptionPlayer == value) ? "§a" + value.toString().toLowerCase(Locale.ROOT) : "§7" + value.toString().toLowerCase(Locale.ROOT))
                .collect(Collectors.toList()));


        holyInventory.setItem(sortPerk.build(), inventorySize - 6, event -> {
            var ordinal = sortOptionPerk.ordinal();
            var length = PerkManager.SortOptionPerk.values().length;
            PerkManager.SortOptionPerk next = PerkManager.SortOptionPerk.values()[(ordinal + 1) % length];
            bridgePlayer.getPlayer().playSound(bridgePlayer.getPlayer().getLocation(), Sound.CLICK, 1.0F, 100.0F);
            bridgePlayer.getPlayer().openInventory(openMapInventory(bridgePlayer, bridgeMapType, next, sortOptionPlayer, 1).getInventory());
        });

        holyInventory.setItem(sortPlayer.build(), inventorySize - 7, event -> {
            var ordinal = sortOptionPlayer.ordinal();
            var length = PerkManager.SortOptionPlayer.values().length;
            PerkManager.SortOptionPlayer next = PerkManager.SortOptionPlayer.values()[(ordinal + 1) % length];
            bridgePlayer.getPlayer().playSound(bridgePlayer.getPlayer().getLocation(), Sound.CLICK, 1.0F, 100.0F);
            bridgePlayer.getPlayer().openInventory(openMapInventory(bridgePlayer, bridgeMapType, sortOptionPerk, next, 1).getInventory());
        });


        holyInventory.setItem(new de.teamholy.core.bukkit.utils.ItemBuilder(Material.BARRIER).setName("§8» §cReset all").build(), inventorySize - 5, event -> {
            bridgePlayer.getPlayer().playSound(bridgePlayer.getPlayer().getLocation(), Sound.ANVIL_BREAK, 1.0F, 100.0F);
            bridgePlayer.getPlayer().openInventory(openMapInventory(bridgePlayer, bridgeMapType, PerkManager.SortOptionPerk.NORMAL, PerkManager.SortOptionPlayer.ALL, 1).getInventory());
        });

        if (page > 1) {
            holyInventory.setItem(new de.teamholy.core.bukkit.utils.ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setAttributs().setName("§8» §6Previous page").setSkullMeta(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1Z" +
                            "GFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==", "").build(), inventorySize - 3, event -> {
                bridgePlayer.getPlayer().playSound(bridgePlayer.getPlayer().getLocation(), Sound.CHICKEN_EGG_POP, 2.0F, 2.0F);
                bridgePlayer.getPlayer().openInventory(openMapInventory(bridgePlayer, bridgeMapType, sortOptionPerk, sortOptionPlayer, page - 1).getInventory());
            });
        }

        if (pagifier.getPage(page + 1) != null) {
            holyInventory.setItem(new de.teamholy.core.bukkit.utils.ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setAttributs().setName("§8» §6Next page").setSkullMeta(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMT" +
                            "I2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19", "").build(), inventorySize - 2, event -> {
                bridgePlayer.getPlayer().playSound(bridgePlayer.getPlayer().getLocation(), Sound.CHICKEN_EGG_POP, 2.0F, 2.0F);
                bridgePlayer.getPlayer().openInventory(openMapInventory(bridgePlayer, bridgeMapType, sortOptionPerk, sortOptionPlayer, page + 1).getInventory());
            });
        }


        int slot = 10;

        for (BridgeMapSkin map : pageMaps) {

            holyInventory.setItem(
                    map.getItem().setName("§8» §6" + map.getName())
                            .withGlow(bridgePlayer.getSelectedSkins().get(bridgeMapType) == map)
                            .setLore(getLore(bridgePlayer, map, bridgeMapType))
                            .build(), slot, event -> {

                        if (doOwn(bridgePlayer, map)) {

                            var bridgeMap = bridgePlayer.getMap();
                            if (bridgeMap.getBridgeMapSkin() == map) {
                                return;
                            }

                            if (bridgePlayer.getCooldown() > System.currentTimeMillis()) {
                                bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "§cYou have to wait a few seconds before you can change your map skin again.");
                                return;
                            }



                            bridgeMap.setBridgeMapSkin(map);
                            bridgePlayer.getSelectedSkins().put(bridgeMapType, map);
                            bridgeMap.loadMap(false, bridgeMap.getSpawnLocation(), map, true);
                            bridgePlayer.getPlayer().playSound(bridgePlayer.getPlayer().getLocation(), Sound.NOTE_PLING, 2.0F, 2.0F);
                            bridgePlayer.setCooldown(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3));
                            bridgePlayer.getPlayer().teleport(bridgePlayer.getMapLocation());
                            bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "§aYou have successfully selected the map skin §e" + map.getName() + "§a.");
                            bridgePlayer.getPlayer().closeInventory();
                        } else {
                            if (event.isLeftClick()) {

                                if (map.isBuyable()) {
                                    buyPerk(bridgePlayer, map);
                                } else if (map.isRank()) {
                                    bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "§cYou need the rank " + map.getRankType().getRankName() + "§c to select this map skin.");
                                } else if (map.isSpecial()) {
                                    bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "§cThis map skin is not available for you.");
                                }
                            } else if (event.isRightClick()) {

                                if (bridgePlayer.getCooldown() > System.currentTimeMillis()) {
                                    bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "§cYou have to wait a few seconds before you preview again.");
                                    return;
                                }

                                bridgePlayer.setCooldown(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3));

                                var bridgeMap = bridgePlayer.getMap();
                                bridgeMap.loadMap(false, bridgeMap.getSpawnLocation(), map, true);

                                bridgePlayer.getPlayer().closeInventory();
                                bridgePlayer.getPlayer().sendMessage(Bridge.PREFIX + "§aYou can now preview the map skin §e" + map.getName() + "§a.");

                                bridgePlayer.getPlayer().setAllowFlight(true);
                                bridgePlayer.getPlayer().setFlying(true);
                                bridgePlayer.getPlayer().teleport(new Location(Bukkit.getWorld(bridgeMap.getMapType().name()), bridgeMap.getSpawnLocation().getX(), bridgeMap.getSpawnLocation().getY() + 5, bridgeMap.getSpawnLocation().getZ(), bridgeMap.getMapType().getSpawnYaw(), 0));
                                bridgePlayer.setPreview(true);

                                bridgePlayer.getPlayer().getInventory().clear();
                                bridgePlayer.getPlayer().getInventory().setItem(4, new ItemBuilder(Material.MAGMA_CREAM).name("§8» §6Leave preview").build());

                            }
                        }


                    });
            slot++;
            if (slot > 16 && slot < 19) {
                slot = 19;
            } else if (slot > 25 && slot < 28) {
                slot = 28;
            }
        }

        return holyInventory;
    }


    @Nonnull
    private static List<String> getLore(BridgePlayer bridgePlayer, BridgeMapSkin bridgeMapSkin, BridgeMapType bridgeMapType) {
        List<String> lore = Lists.newArrayList();


        if (!doOwn(bridgePlayer, bridgeMapSkin)) {
            if (bridgeMapSkin.isBuyable()) {
                lore.add("§7This map costs §e" + bridgeMapSkin.getPrice() + " §6coins");
            } else if (bridgeMapSkin.isRank()) {
                lore.add("§7Available for " + bridgeMapSkin.getRankType().getRankName() + "§7 and above");
            } else if (bridgeMapSkin.isSpecial()) {
                lore.add(bridgeMapSkin.getSpecialText());
            }

            lore.add(" ");
            lore.addAll(bridgeMapSkin.getDescription());
            lore.add(" ");
            lore.add("§7§oRight click, to preview");
            lore.add(" ");
        } else {

            if (bridgePlayer.getSelectedSkins().get(bridgeMapType).equals(bridgeMapSkin)) {
                lore.add("§2Selected");
            } else {
                lore.add("§aClick to select");
            }

            /*
            if (bridgePlayer.getSelectedSkins().get(bridgeMapSkin.getBridgeMapType()) == bridgeMapSkin){
                lore.add("§2Selected");
            } else {
                lore.add("§aClick to select");
            }*/

            lore.add(" ");
            lore.addAll(bridgeMapSkin.getDescription());
            lore.add(" ");
        }
        return lore;
    }

    private static boolean doOwn(BridgePlayer bridgePlayer, BridgeMapSkin bridgeMapSkin) {
        return  (bridgeMapSkin.isBuyable() && bridgePlayer.getBridgeSettings().getMapSkins().contains(bridgeMapSkin))
                || (bridgeMapSkin.isRank()) && bridgePlayer.getPlayer().hasPermission(bridgeMapSkin.getRankType().getPermission())
                || bridgeMapSkin.isDefault();
    }



}
