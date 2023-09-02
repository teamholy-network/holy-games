package de.teamholy.sgffa.models;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.utils.scoreboard.ScoreboardAPI;
import de.teamholy.core.api.utility.TrophieLeague;
import de.teamholy.sgffa.SGFFA;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

/* copyright by Yassino */
@Getter @Setter
public class PlayerEntry {

    private Player player;
    private ScoreboardAPI scoreboardAPI;

    private TeamEntry teamEntry;

    private boolean grace;
    private boolean vanish = false;

    public int alltimeTrophies = 1000;

    public PlayerEntry(Player player) {
        this.player = player;


        GameProfile statsProfile = BukkitCore.getAPI().getGameService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()));


        if (!statsProfile.exists(Gamemodes.SGFFA.toString())) {
            for (StatsType time : StatsType.values()) {
                for (Gamemodes.StatKey statKey : Gamemodes.SGFFA.getStatKeys()) statsProfile.setStat(Gamemodes.SGFFA.toString(), time, statKey.getName(), statKey.getDefaultValue());
            }

            BukkitCore.getAPI().getGameService().saveEntity(statsProfile,true,true);
        } else {
            alltimeTrophies = (int) statsProfile.getStat(Gamemodes.SGFFA.toString(),StatsType.ALLTIME,"trophies");
        }


        scoreboardAPI = new ScoreboardAPI().createScoreboard(player,"§a");
        setScoreboard();
        performSpawn();
    }

    public void updateTeamScore() {
        if (teamEntry == null) {
            scoreboardAPI.updateLine(10," §7Team§8: §a-/-");
        } else {
            scoreboardAPI.updateLine(10," §7Team§8: §a" + teamEntry.getTag());
        }
    }



    public void updateMapScore() {
        scoreboardAPI.updateLine(11," §7Map§8: §a" + SGFFA.getInstance().getActiveMapEntry().getMapName());
    }

    public void setScoreboard() {
        scoreboardAPI.clearScoreboard();

        BukkitCore.getAPI().getGameService().getEntityAsync(player.getUniqueId(),() -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()),gameProfile -> {
            scoreboardAPI.setLine(14, " §8§m--------------- ");
            scoreboardAPI.setLine(13,"§4");
            scoreboardAPI.setLine(12," §7Map§8: §a" + SGFFA.getInstance().getActiveMapEntry().getMapName());
            scoreboardAPI.setLine(11," §7Team§8: §a-/-");
            scoreboardAPI.setLine(10,"§6");
            scoreboardAPI.setLine(9," §7Stats§8: " + StatsType.ALLTIME.toBeauty());
            scoreboardAPI.setLine(8,"§2");
            scoreboardAPI.setLine(7," §7Rank§8: §a#" + BukkitCore.getAPI().getRankingManager().getRankFromUUID(Gamemodes.SGFFA,StatsType.ALLTIME,player.getUniqueId()));
            int elo = (int) gameProfile.getStat(Gamemodes.SGFFA.toString(),StatsType.ALLTIME,"trophies");
            scoreboardAPI.setLine(6," §7Trophies§8: §a"  + elo + " " + TrophieLeague.getEloRank(elo).getShortName());
            scoreboardAPI.setLine(5," §7Kills§8: §a" + gameProfile.getStat(Gamemodes.SGFFA.toString(),StatsType.ALLTIME,"kills"));
            scoreboardAPI.setLine(4," §7Deaths§8: §a" + gameProfile.getStat(Gamemodes.SGFFA.toString(),StatsType.ALLTIME,"deaths"));
            scoreboardAPI.setLine(3," §7K/D§8: §a" + BukkitHolyAPI.getInstance().getStatsManager().calculateKD(gameProfile.getStat(Gamemodes.SGFFA.toString(),StatsType.ALLTIME,"kills"), gameProfile.getStat(Gamemodes.SGFFA.toString(),StatsType.ALLTIME,"deaths")));
            scoreboardAPI.setLine(2, "§5");
            scoreboardAPI.setLine(1, " §8§m--------------- ");
            scoreboardAPI.setLine(0,"§o"+Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName());
            Bukkit.getScheduler().runTask(SGFFA.getInstance(),() -> scoreboardAPI.build());
        });
    }



    public void updateScoreboard() {
        BukkitCore.getAPI().getGameService().getEntityAsync(player.getUniqueId(),() -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()),gameProfile -> {
            scoreboardAPI.updateLine(7," §7Rank§8: §a#" + BukkitCore.getAPI().getRankingManager().getRankFromUUID(Gamemodes.SGFFA,StatsType.ALLTIME,player.getUniqueId()));
            int elo = (int) gameProfile.getStat(Gamemodes.SGFFA.toString(),StatsType.ALLTIME,"trophies");
            scoreboardAPI.updateLine(6," §7Trophies§8: §a"  + elo + " " + TrophieLeague.getEloRank(elo).getShortName());
            scoreboardAPI.updateLine(5," §7Kills§8: §a" + gameProfile.getStat(Gamemodes.SGFFA.toString(),StatsType.ALLTIME,"kills"));
            scoreboardAPI.updateLine(4," §7Deaths§8: §a" + gameProfile.getStat(Gamemodes.SGFFA.toString(),StatsType.ALLTIME,"deaths"));
            scoreboardAPI.updateLine(3," §7K/D§8: §a" + BukkitHolyAPI.getInstance().getStatsManager().calculateKD(gameProfile.getStat(Gamemodes.SGFFA.toString(),StatsType.ALLTIME,"kills"), gameProfile.getStat(Gamemodes.SGFFA.toString(),StatsType.ALLTIME,"deaths")));
        });
    }

    public void vanish() {

        if (vanish) {
            vanish = false;
            player.spigot().setCollidesWithEntities(false);
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (!all.hasPermission("teamholy.team")) {
                    all.showPlayer(player);
                } else {
                    all.sendMessage(SGFFA.PREFIX + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColorWithoutNick(player.getUniqueId()) + player.getName() + " §7Ist nun nicht mehr im Vanish!");
                }
            }
            player.setAllowFlight(false);
            player.setFlying(false);
            performSpawn();
        } else {
            vanish = true;
            player.spigot().setCollidesWithEntities(false);
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (!all.hasPermission("teamholy.team")) {
                    all.hidePlayer(player);
                } else {
                    all.sendMessage(SGFFA.PREFIX + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColorWithoutNick(player.getUniqueId()) + player.getName() +  " §7Ist nun im Vanish!");
                }
            }
            player.setAllowFlight(true);
            player.setFlying(true);
            player.getInventory().setArmorContents(null);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,99999,1));
            giveVanishItems();
        }
    }

    public void openVanishMenu() {
        Inventory inventory = new Inventory("§8» §6Vanish Menü",9*6);
        int i = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            inventory.setItem(new ItemBuilder(Material.SKULL_ITEM,1, (byte) 3).setSkullOwner(player.getName()).setName(BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColorWithoutNick(player.getUniqueId()) + player.getDisplayName()).build(),i,inventoryClickEvent -> {
                if (player.isOnline()) this.player.teleport(player);
            });
            i++;
        }
        player.openInventory(inventory.getInventory());
    }

    public void giveVanishItems() {
        player.getInventory().clear();
        player.getInventory().setItem(4,new ItemBuilder(Material.COMPASS).setName("§8» §6Vanish Menü").build());
    }

    public void performSpawn() {
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setMaxHealth(20);
        player.getInventory().setItem(0,new ItemBuilder(Material.WOOD_AXE).build());
        grace = true;

        for (PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999, 0));

        this.player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setUnbreakable().build());
        this.player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setUnbreakable().build());
        this.player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setUnbreakable().build());
        this.player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setUnbreakable().build());

        if (SGFFA.getInstance().getActiveMapEntry() != null)
        Bukkit.getScheduler().runTaskLater(SGFFA.getInstance(), () -> player.teleport(SGFFA.getInstance().getActiveMapEntry().getSpawns().get(new Random().nextInt(SGFFA.getInstance().getActiveMapEntry().getSpawns().size()))),1);
        Bukkit.getScheduler().scheduleAsyncDelayedTask(SGFFA.getInstance(), () -> {
            if (!player.isOnline() || !grace || vanish) return;
            grace = false;
            player.sendMessage(SGFFA.PREFIX + "Your grace period finished!");
            for (PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
        },140);
    }

    public void sendActionBar(String message) {
        CraftPlayer cp = (CraftPlayer) player;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        cp.getHandle().playerConnection.sendPacket(ppoc);
    }

}
