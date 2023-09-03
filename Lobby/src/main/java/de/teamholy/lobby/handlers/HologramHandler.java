package de.teamholy.lobby.handlers;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.api.utility.TrophieLeague;
import de.teamholy.lobby.Lobby;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/* copyright by Yassino */
@Getter
public class HologramHandler {

    private final HashMap<String, Hologram> holograms = new HashMap<>();
    private final HashMap<String, Hologram> gameholograms = new HashMap<>();
    private final Lobby instance;

    public HologramHandler(Lobby instance) {
        this.instance = instance;
        holograms.put("Bedwars",createHologram(new ItemBuilder(Material.BED).build(), BukkitHolyAPI.getInstance().getLocationManager().getLocation("bedwars")));

        holograms.put("bw_spawn",createHologram(new ItemBuilder(Material.BED).build(), BukkitHolyAPI.getInstance().getLocationManager().getLocation("bw_spawn_bw")));
        holograms.put("rbw_spawn",createHologram(new ItemBuilder(Material.BLAZE_ROD).build(), BukkitHolyAPI.getInstance().getLocationManager().getLocation("bw_spawn_rbw")));


        Hologram hologram = HologramsAPI.createHologram(instance,BukkitHolyAPI.getInstance().getLocationManager().getLocation("bw_spawn_spec").add(0,3.35,0));
        hologram.appendItemLine(new ItemBuilder(Material.EYE_OF_ENDER).build());
        hologram.appendTextLine("§50 §7Games");
        holograms.put("spec",hologram);

        gameholograms.put("MLGRush",createHologram(new ItemBuilder(Material.STICK).build(), BukkitHolyAPI.getInstance().getLocationManager().getLocation("mlgrush")));
        gameholograms.put("KnockbackFFA",createHologram(new ItemBuilder(Material.SANDSTONE).build(), BukkitHolyAPI.getInstance().getLocationManager().getLocation("kbffa")));
        gameholograms.put("Clutches",createHologram(new ItemBuilder(Material.RED_SANDSTONE).build(), BukkitHolyAPI.getInstance().getLocationManager().getLocation("clutches")));
        gameholograms.put("SGFFA",createHologram(new ItemBuilder(Material.IRON_SWORD).build(), BukkitHolyAPI.getInstance().getLocationManager().getLocation("sgffa")));



        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("§8§m------------§f§lLEAGUES§8§m------------");
        List<TrophieLeague> ranks = new ArrayList<>(Arrays.stream(TrophieLeague.values()).toList());

        Collections.reverse(ranks);
        for (TrophieLeague eloRank : ranks) {
            String s = eloRank.getName() + " §8» §7" + eloRank.getMinRange() + " §a- §7" + eloRank.getMaxRange() + " §8» " + eloRank.getShortName();
            arrayList.add(s);
        }
        arrayList.add("§8§m------------§f§lLEAGUES§8§m------------");
        arrayList.add(null);
        arrayList.add("§f§lClick to see the §c§lleaderboards");


        Hologram eloholo = HologramsAPI.createHologram(instance,BukkitHolyAPI.getInstance().getLocationManager().getLocation("eloholo").add(0,7,0));
        eloholo.appendItemLine(new ItemBuilder(Material.DIAMOND_SWORD).setEnchantments(Enchantment.DAMAGE_ALL,1).build());
        arrayList.forEach(s -> eloholo.appendTextLine(s).setTouchHandler(player -> Lobby.getInstance().getLeaderboardInventory().open(player, Gamemodes.MLGRUSH)));


    }

    public void updateHolograms() {
        gameholograms.forEach((name, hologram) -> {
            TextLine textLine = (TextLine) hologram.getLine(1);
            int count = 0;
            count = Lobby.getInstance().getCloudCacheHandler().getOnlineCount(name);

            textLine.setText("§6" + count + " §7" + (count == 1 ? "Player" : "Players"));
        });

        updateBW();
        updateBwSpawn();
        updateRBWSpawn();
        updateSpec();

    }

    public void updateSpec() {
        int count = Lobby.getInstance().getBedwarsSpectateInventory().getIngameGames("BW").size()
                + Lobby.getInstance().getBedwarsSpectateInventory().getIngameGames("RBW").size();

        Hologram hologram = holograms.get("spec");
        TextLine textLine = (TextLine) hologram.getLine(1);
        textLine.setText("§5" + count + " §7" + (count == 1 ? "Game" : "Games"));
    }


    public void updateBW() {
        int count = Lobby.getInstance().getBedwarsServerInventory().getBedwarsPlayers() + Lobby.getInstance().getBedwarsServerInventory().getRushBWPlayers();
        Hologram hologram = holograms.get("Bedwars");
        TextLine textLine = (TextLine) hologram.getLine(1);
        textLine.setText("§6" + count + " §7" + (count == 1 ? "Player" : "Players"));
    }

    public void updateBwSpawn() {
        int count = Lobby.getInstance().getBedwarsServerInventory().getBedwarsPlayers();
        Hologram hologram = holograms.get("bw_spawn");
        TextLine textLine = (TextLine) hologram.getLine(1);
        textLine.setText("§6" + count + " §7" + (count == 1 ? "Player" : "Players"));
    }

    public void updateRBWSpawn() {
        int count = Lobby.getInstance().getBedwarsServerInventory().getRushBWPlayers();
        Hologram hologram = holograms.get("rbw_spawn");
        TextLine textLine = (TextLine) hologram.getLine(1);
        textLine.setText("§6" + count + " §7" + (count == 1 ? "Player" : "Players"));
    }


    public Hologram createHologram(ItemStack itemStack, Location location) {
        Hologram hologram = HologramsAPI.createHologram(instance,location.add(0,3.35,0));
        hologram.appendItemLine(itemStack);
        hologram.appendTextLine("§60 §76Players");
        return hologram;
    }

}
