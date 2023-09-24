package de.teamholy.clutches.task;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.npc.models.NPCEntry;
import de.teamholy.clutches.Clutches;
import de.teamholy.clutches.arena.ArenaEntry;
import de.teamholy.clutches.arena.ArenaType;
import de.teamholy.clutches.enums.FirstHitDelay;
import de.teamholy.clutches.enums.HitType;
import de.teamholy.clutches.player.PlayerEntry;
import de.teamholy.clutches.player.PlayerState;
import de.teamholy.clutches.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class ClutchTask {


    public ClutchTask() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Clutches.getInstance(),() -> {

            for (PlayerEntry playerEntry : Clutches.getInstance().getPlayerEntryHandler().values()) {


                if (playerEntry.getPlayerState() == PlayerState.INGAME) {
                    Player player = playerEntry.getPlayer();
                    ArenaEntry arenaEntry = playerEntry.getArenaEntry();

                    if (player.getLocation().getBlockY() <= arenaEntry.getDeath()) {
                        playerEntry.resetMap();
                        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 50);
                        playerEntry.getClutchCount().set(0);
                        playerEntry.getNpcAirHit().set(playerEntry.getNpcAirHits());
                        playerEntry.getFirstHitDelay().setReceived(false);
                    } else {

                        if (player.getOpenInventory() != null && player.getOpenInventory().getTitle().equalsIgnoreCase("§8» §6Settings")) {
                            playerEntry.setPause(true);
                        }

                        if (!playerEntry.isPause()) {
                            if (playerEntry.getPre().get() == 0 || playerEntry.isSecondRound()) {

                                PlayerUtils.sendBar(player, "");


                                if (playerEntry.getArenaType() == ArenaType.REDUCE) {
                                    if (playerEntry.getFirstHitDelay() == FirstHitDelay.AFTER && !playerEntry.getFirstHitDelay().isReceived()) {
                                        continue;
                                    }
                                }


                                if (playerEntry.getCountdown().get() == 0) {
                                    PlayerUtils.sendBar(player, "§fServer §8» §c§lTeamholy.de");

                                    if (playerEntry.getArenaType() == ArenaType.REDUCE) {

                                        for (NPCEntry npcEntry : BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()).getNpcs().values()) {
                                            if (npcEntry.getDisplayName().equalsIgnoreCase("§6§lteamholy.de")) {
                                                if (npcEntry.getLocation().distance(player.getLocation()) <= 3.7) {
                                                    npcEntry.animation(player, 0);
                                                    player.damage(0);
                                                    player.setVelocity(playerEntry.getVelocity(npcEntry.getLocation().getDirection(), 0.8 * playerEntry.getNpcHit().getKnockback()));
                                                } else if (playerEntry.getNpcAirHit().get() != 0) {
                                                    npcEntry.animation(player, 0);
                                                    player.damage(0);
                                                    player.setVelocity(playerEntry.getVelocity(npcEntry.getLocation().getDirection(), 0.8 * playerEntry.getNpcHit().getKnockback()));
                                                    playerEntry.getNpcAirHit().getAndDecrement();
                                                }
                                            }
                                        }

                                    } else if (playerEntry.getArenaType() == ArenaType.EXPERIMENTAL) {

                                        for (NPCEntry npcEntry : BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId()).getNpcs().values()) {
                                            if (npcEntry.getDisplayName().equalsIgnoreCase("§6§lteamholy.de")) {
                                                if (npcEntry.getLocation().distance(player.getLocation()) <= 2.5) {
                                                    npcEntry.animation(player, 0);
                                                    player.damage(0);
                                                    player.setVelocity(playerEntry.getVelocity(npcEntry.getLocation().getDirection(), 0.8 * playerEntry.getNpcHit().getKnockback()));
                                                }
                                            }
                                        }

                                    } else if (playerEntry.getArenaType() == ArenaType.CLUTCH || playerEntry.getArenaType() == ArenaType.DIAGONAL_CLUTCH) {

                                        player.damage(0);
                                        Location direction = arenaEntry.getNpc().clone();
                                        if (playerEntry.getArenaType() == ArenaType.DIAGONAL_CLUTCH) {
                                            direction.setYaw(47.5F);
                                        }

                                        if (playerEntry.getClutchCount().get() == 0) {
                                            player.setVelocity(playerEntry.getVelocity(direction.getDirection(), 0.8 * playerEntry.getFirstHit().getKnockback()));
                                            playerEntry.getClutchCount().getAndIncrement();
                                            if (playerEntry.getSecondHit() == HitType.NONE) {
                                                resetClutch(playerEntry);
                                            }
                                        } else
                                        if (playerEntry.getClutchCount().get() == 1 && (playerEntry.getSecondHit() != HitType.NONE)) {
                                            player.setVelocity(playerEntry.getVelocity(direction.getDirection(), 0.8 * playerEntry.getSecondHit().getKnockback()));
                                            playerEntry.getClutchCount().getAndIncrement();
                                            if (playerEntry.getThirdHit() == HitType.NONE) {
                                                resetClutch(playerEntry);
                                            }
                                        } else
                                        if (playerEntry.getClutchCount().get() == 2 && (playerEntry.getThirdHit() != HitType.NONE)) {
                                            player.setVelocity(playerEntry.getVelocity(direction.getDirection(), 0.8 * playerEntry.getThirdHit().getKnockback()));
                                            playerEntry.getClutchCount().getAndIncrement();
                                            if (playerEntry.getFirstHit() == HitType.NONE) {
                                                resetClutch(playerEntry);
                                            }
                                        } else
                                        if (playerEntry.getClutchCount().get() == 3 && (playerEntry.getFourthHit() != HitType.NONE)) {
                                            player.setVelocity(playerEntry.getVelocity(direction.getDirection(), 0.8 * playerEntry.getFourthHit().getKnockback()));
                                            playerEntry.getClutchCount().getAndIncrement();
                                            resetClutch(playerEntry);
                                        }

                                    }


                                } else {
                                    playerEntry.getCountdown().getAndDecrement();
                                    player.setLevel(playerEntry.getCountdown().get());
                                    player.setExp((float) playerEntry.getCountdown().get() / playerEntry.getDelay());
                                }
                            } else {
                                playerEntry.getPre().getAndDecrement();
                                PlayerUtils.sendBar(player, "§fPrecooldown §8» §d" + playerEntry.getPre().get());
                            }
                        } else {
                            PlayerUtils.sendBar(player, "§c§lPAUSE");
                        }


                    }
                }


            }
        },0,10);
    }

    private void resetClutch(PlayerEntry playerEntry) {
        playerEntry.getPre().set(6);
        playerEntry.getCountdown().set(playerEntry.getDelay() + 1);
        playerEntry.getClutchCount().set(0);
        playerEntry.getFirstHitDelay().setReceived(false);
        playerEntry.getNpcAirHit().set(playerEntry.getNpcAirHits());
        playerEntry.getPlayer().setLevel(0);
        playerEntry.getPlayer().setExp(0);
    }
}
