package de.teamholy.clutches.playground.enums;

import de.teamholy.clutches.Clutches;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.PerkRankType;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public enum ArmorColor {

    GREY("Grey", PerkRankType.PLAYER, 0,Color.GRAY),
    ORANGE("Orange",PerkRankType.PREMIUM,500,Color.ORANGE),
    BLUE("Blue",PerkRankType.PREMIUM,0,Color.BLUE),
    GREEN("Green",PerkRankType.PREMIUM,0,Color.GREEN),
    RED("Red",PerkRankType.VIP,500,Color.RED),
    BLACK("Black",PerkRankType.VIP,750,Color.BLACK),
    WHITE("White",PerkRankType.HOLY,0,Color.WHITE),
    RAINBOW("Rainbow",PerkRankType.HOLY,2500,Color.GRAY);

    private final String name;
    private PerkRankType perkRankType;
    private int price;
    private Color color;

    public static boolean isBuyable(ArmorColor armorColor) {
        return armorColor.getPrice() != 0;
    }

    public static int getId(ArmorColor armorColor) {
        return (armorColor.ordinal() + 8000);
    }

    public static void buyPerk(Player player, PerkPlayerProfile perkPlayerProfile, ArmorColor armorColor, String name) {
        Inventory inventory = new Inventory("§8» §6Buy Armorcolor", 9);

        for(int i = 0; i < 9; ++i) {
            inventory.setItem((new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte)15)).setName("§8//").build(), i);
        }

        ItemBuilder itemBuilder = new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherColor(armorColor.getColor());

        itemBuilder.setName(name);
        List<String> list = new ArrayList<>();
        list.add("");
        list.add("§7Do you want to buy this perk?");
        list.add("§7price §8» §e" + armorColor.getPrice());
        list.add("");

        itemBuilder.setLore(list);
        itemBuilder.setName("§8» §6" + name);
        inventory.setItem(itemBuilder.build(), 4);
        inventory.setItem((new ItemBuilder(Material.INK_SACK, 1, (byte)10)).setName("§8» §aYes").build(), 2, (event) -> {
            player.closeInventory();
            PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId()));
            if (playerProfile.getCoins() < armorColor.getPrice()) {
                player.sendMessage(Clutches.PREFIX + "§cYou dont have enough coins!");
                player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 2.0F, 2.0F);
            } else {
                playerProfile.setCoins(playerProfile.getCoins() - armorColor.price);
                String var10001 = Clutches.PREFIX;
                player.sendMessage(var10001 + "§aYou successfully bought the §e" + armorColor.getName() + " §aArmorcolor for §a" + armorColor.getPrice() + " §6coins!");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0F, 2.0F);
                perkPlayerProfile.getOwnedPerks().add(getId(armorColor));
                BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().put(player.getUniqueId(), perkPlayerProfile);
                BukkitCore.getAPI().getPerkPlayerService().saveEntity(perkPlayerProfile, true, true);
                BukkitCore.getAPI().getPlayerService().saveEntity(playerProfile, true, true);
            }
        });
        inventory.setItem((new ItemBuilder(Material.INK_SACK, 1, (byte)1)).setName("§8» §cNo").build(), 6, (event) -> {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 50.0F, 50.0F);
            player.sendMessage("§cAborted!");
        });

        itemBuilder.setLore(list);
        player.openInventory(inventory.getInventory());
    }

}
