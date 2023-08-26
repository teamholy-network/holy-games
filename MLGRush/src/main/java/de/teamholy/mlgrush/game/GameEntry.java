package de.teamholy.mlgrush.game;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.mlgrush.MLGRush;
import de.teamholy.mlgrush.enums.BlockResetType;
import de.teamholy.mlgrush.enums.GameType;
import de.teamholy.mlgrush.map.MapEntry;
import de.teamholy.mlgrush.map.MapState;
import de.teamholy.mlgrush.player.PlayerEntry;
import de.teamholy.mlgrush.player.PlayerState;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bukkit.BukkitCore;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class GameEntry {


    private GameType gameType;
    private MapEntry mapEntry;
    private PlayerEntry playerOne;
    private PlayerEntry playerTwo;

    // 4x1
    private PlayerEntry playerThree;
    private PlayerEntry playerFour;

    private ArrayList<PlayerEntry> playersInArena;
    private ArrayList<PlayerEntry> playersPlaying = new ArrayList<>();
    private HashMap<Location, Player> placedBlocks;
    private ArrayList<BukkitTask> bukkitRunnables = new ArrayList<>();
    private GameState gameState;
    private int timeSinceStart;

    private boolean noHitDelay;
    private boolean onlyVerticalKnockback;
    private BlockResetType resetBlocksOnDeath;

    public GameEntry(PlayerEntry playerOne, PlayerEntry playerTwo, PlayerEntry playerThree, PlayerEntry playerFour) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        if (playerThree != null) {
            this.playerThree = playerThree;
            this.playerFour = playerFour;
        }
        this.playersInArena = new ArrayList<>();
        this.placedBlocks = new HashMap<>();
        gameState = GameState.MAPSELECT;
        noHitDelay = false;
        onlyVerticalKnockback = false;
        resetBlocksOnDeath = BlockResetType.OFF;

        playersInArena.add(playerTwo);
        playersInArena.add(playerOne);
        playersPlaying.add(playerTwo);
        playersPlaying.add(playerOne);
        gameType = GameType.TWOxONE;

        if (playerThree != null) {
            gameType = GameType.FOURxONE;
            playersInArena.add(playerThree);
            playersInArena.add(playerFour);
            playersPlaying.add(playerThree);
            playersPlaying.add(playerFour);
        }
    }

    public void setupGame(MapEntry mapEntry) {
        this.mapEntry = mapEntry;

        gameState = GameState.INGAME;
        setNoHitDelay(playerOne.isNoHitDelay());
        setOnlyVerticalKnockback(playerOne.isOnlyVerticalKnockback());
        setResetBlocksOnDeath(playerOne.getBlockResetType());
        playersPlaying.forEach(playerEntry -> {
            teleportToSpawn(playerEntry);
            playerEntry.setIngameItems();
        });
        playersInArena.forEach(playerEntry -> {
            playerEntry.setPlayerState(PlayerState.INGAME);
            playerEntry.getPlayer().sendMessage(MLGRush.getInstance().getPrefix() + "Use /quit to leave the game");
            playerEntry.setGameEntry(this);
            playerEntry.setScoreboard();
            playerEntry.getPlayer().closeInventory();
            if (isNoHitDelay()) {
                playerEntry.getPlayer().setMaximumNoDamageTicks(0);
            } else {
                playerEntry.getPlayer().setMaximumNoDamageTicks(20);
            }
            playerEntry.getPlayer().setAllowFlight(false);
            playerEntry.getPlayer().setFlying(false);
        });
        timeSinceStart = 0;
        mapEntry.getMapTemplate().getFreeTemplatesCount().remove(mapEntry);
        mapEntry.setMapState(MapState.INUSE);
    }


    public void removeBlocks(Player player, BlockResetType blockResetType) {
        if (blockResetType == BlockResetType.INSTANT_ANYONE) {
            placedBlocks.keySet().forEach(location -> location.getBlock().setType(Material.AIR, true));
            placedBlocks.clear();
        } else if (blockResetType == BlockResetType.INSTANT_VICTIM) {

            List<Location> toRemove = new ArrayList<Location>();

            placedBlocks.forEach((location, player1) -> {
                if (player1 == player) {
                    location.getBlock().setType(Material.AIR, true);
                    toRemove.add(location);
                }
            });
            toRemove.forEach(location -> placedBlocks.remove(location));
        }

    }

    public void destroyBed(PlayerEntry playerEntry, PlayerEntry destroyed) {
        BukkitHolyAPI.getInstance().getStatsManager().addStat(Gamemodes.MLGRUSH.toString(),"destroyed_beds",playerEntry.getPlayer().getUniqueId());
        placedBlocks.keySet().forEach(location -> location.getBlock().setType(Material.AIR, true));
        placedBlocks.clear();
        bukkitRunnables.forEach(bukkitTask -> bukkitTask.cancel());
        bukkitRunnables.clear();
        playersInArena.forEach(playerEntry1 -> {
            if (!playerEntry1.isNoIngameMessage()) {
                playerEntry1.getPlayer().sendMessage(MLGRush.getInstance().getPrefix() + "The bed of §6" + destroyed.getPlayer().getDisplayName() + " §7was destroyed by §c" + playerEntry.getPlayer().getDisplayName());
            }
            playerEntry1.getPlayer().playSound(playerEntry1.getPlayer().getLocation(), Sound.ANVIL_BREAK, 2f, 2f);
        });
        playersPlaying.forEach(playerEntry1 -> {
            teleportToSpawn(playerEntry1);
            playerEntry1.setIngameItems();
        });
        playerEntry.getIngamePlayer().setBeds(playerEntry.getIngamePlayer().getBeds() + 1);
    }

    public void finishGame(boolean stopserver) {
        PlayerEntry winner = getWinner();
        mapEntry.setMapState(MapState.NOUSE);
        mapEntry.getMapTemplate().getFreeTemplatesCount().add(mapEntry);
        placedBlocks.keySet().forEach(location -> location.getBlock().setType(Material.AIR, true));
        placedBlocks.clear();
        bukkitRunnables.forEach(bukkitTask -> bukkitTask.cancel());
        bukkitRunnables.clear();
        playersPlaying.forEach(playerEntry -> {
            BukkitHolyAPI.getInstance().getStatsManager().addStat(Gamemodes.MLGRUSH.toString(),"played_games",playerEntry.getPlayer().getUniqueId());
        });
        if (!stopserver) {
            String winnerName = null;
            if (winner != null) winnerName = BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColor(winner.getPlayer().getUniqueId()) + winner.getPlayer().getName();

            String finalWinnerName = winnerName;
            playersInArena.forEach(playerEntry -> {

                playerEntry.getPlayer().sendMessage("");
                playerEntry.getPlayer().sendMessage("          §f§lMATCH OVERVIEW      ");
                playerEntry.getPlayer().sendMessage("");
                playerEntry.getPlayer().sendMessage(" §7Match time §8» §e" + MLGRush.getInstance().getPlayerUtils().formatSeconds(timeSinceStart));
                playerEntry.getPlayer().sendMessage(" §7Winner §8» " + (winner == null ? " §cno one §8(§eDRAW§8)" :
                        finalWinnerName + " §8(§e" + winner.getIngamePlayer().getBeds() + " BEDS§8)"));
                playerEntry.getPlayer().sendMessage("");
                if (playersPlaying.contains(playerEntry)) {
                    playerEntry.getPlayer().sendMessage(" §7Kills §8» §e" + playerEntry.getIngamePlayer().getKills());
                    playerEntry.getPlayer().sendMessage(" §7Beds §8» §e" + playerEntry.getIngamePlayer().getBeds());
                    playerEntry.getPlayer().sendMessage(" §7Deaths §8» §c" + playerEntry.getIngamePlayer().getDeaths());
                    playerEntry.getPlayer().sendMessage("");
                }


                playerEntry.performSpawn();
                playerEntry.setItemsSpawn();
                playerEntry.getPlayer().teleport(MLGRush.getInstance().getLobby());
                playerEntry.setGotLastHit(null);
                playerEntry.getIngamePlayer().reset();
                playerEntry.getPlayer().setMaximumNoDamageTicks(20);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.showPlayer(playerEntry.getPlayer());
                }
                for (PotionEffect potionEffectType : playerEntry.getPlayer().getActivePotionEffects())
                    playerEntry.getPlayer().removePotionEffect(potionEffectType.getType());
                playerEntry.getPlayer().setAllowFlight(false);
                playerEntry.getPlayer().setFlying(false);
                playerEntry.getPlayer().spigot().setCollidesWithEntities(true);
                playerEntry.setGameEntry(null);
                playerEntry.setPlayerState(PlayerState.LOBBY);
                playerEntry.setScoreboard();
            });

        }
        MLGRush.getInstance().getGameEntryHandler().remove(mapEntry.getMapId());
    }

    private PlayerEntry getWinner() {
        HashMap<PlayerEntry, Integer> map = new HashMap<>();
        playersPlaying.forEach(playerEntry -> map.put(playerEntry,playerEntry.getIngamePlayer().getBeds()));
        ArrayList<PlayerEntry> winners = MLGRush.getInstance().getPlayerUtils().getHighestValue(map);
        if (winners.size() == 1 && (winners.get(0).getIngamePlayer().getBeds() >= 3 )) {
            if (map.size() == 4) {
                BukkitCore.getAPI().getCoinManager().addCoins(winners.get(0).getPlayer().getUniqueId(),45,true);
            } else {
                BukkitCore.getAPI().getCoinManager().addCoins(winners.get(0).getPlayer().getUniqueId(),30,true);
            }
            BukkitHolyAPI.getInstance().getStatsManager().addStat(Gamemodes.MLGRUSH.toString(),"won_games",winners.get(0).getPlayer().getUniqueId());
            return winners.get(0);
        }
        return null;
    }


    public void teleportToSpawn(PlayerEntry playerEntry) {
        Player player = playerEntry.getPlayer();
        if (playerOne == playerEntry) {
            player.teleport(mapEntry.getSpawn1());
        } else if (playerTwo == playerEntry) {
            player.teleport(mapEntry.getSpawn2());
        } else if (playerThree == playerEntry) {
            player.teleport(mapEntry.getSpawn3());
        } else if (playerFour == playerEntry) {
            player.teleport(mapEntry.getSpawn4());
        }
    }

    public int getPlayer(PlayerEntry playerEntry) {
        if (playerEntry == playerOne) {
            return 1;
        } else if (playerEntry == playerTwo) {
            return 2;
        } else if (playerEntry == playerThree) {
            return 3;
        } else if (playerEntry == playerFour) {
            return 4;
        }
        return 0;
    }



}
