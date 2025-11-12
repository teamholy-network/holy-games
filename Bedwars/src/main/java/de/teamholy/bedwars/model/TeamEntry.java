package de.teamholy.bedwars.model;

import de.teamholy.bedwars.Bedwars;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Represents a team entry in Bedwars.
 * Copyright by Yassino
 */
@Getter
@Setter
public class TeamEntry {

    private String name;
    private String colorCode;
    private List<Player> players;
    private List<PlayerEntry> allPlayers;
    private int size;
    private boolean hasBed;
    private Color color;
    private String skullId;
    private int id;
    private Location bed;
    private Location spawn;
    private Location shop;
    private Inventory teamChest;
    private UUID shopNPCUuid;

    public TeamEntry(String name, String colorCode, Color color, int size, String skullId, int id) {
        this.name = name;
        this.colorCode = colorCode;
        this.size = size;
        this.hasBed = true;
        this.skullId = skullId;
        this.color = color;
        this.id = id;
        this.players = new ArrayList<>();
        this.allPlayers = new ArrayList<>();
        this.teamChest = Bukkit.createInventory(null, 27, "§8» " + colorCode + name);

        int index = new Random().nextInt(Bedwars.getInstance().getShopUuids().size());
        this.shopNPCUuid = Bedwars.getInstance().getShopUuids().get(index);
        Bedwars.getInstance().getShopUuids().remove(index);
    }

    /**
     * Adds a player to the team.
     *
     * @param player the player to add
     */
    public void addPlayer(Player player) {
        if (!this.players.contains(player)) {
            this.players.add(player);
        }
    }

    /**
     * Removes a player from the team.
     *
     * @param player the player to remove
     */
    public void removePlayer(Player player) {
        this.players.remove(player);
    }
}
