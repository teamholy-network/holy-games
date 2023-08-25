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
import java.util.Random;
import java.util.UUID;

/* copyright by Yassino */
@Getter @Setter
public class TeamEntry {

    private String name, colorCode;
    private ArrayList<Player> players;
    private ArrayList<PlayerEntry> allPlayers;
    private int size;
    private boolean hasBed;
    private Color color;
    private String skullId;
    private int id;
    private Location bed, spawn, Shop;
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
        players = new ArrayList<Player>();
        allPlayers = new ArrayList<>();
        teamChest = Bukkit.createInventory(null, 27,"§8» " + colorCode + name);

        int index = new Random().nextInt(Bedwars.getInstance().getShopUuids().size());

        shopNPCUuid = Bedwars.getInstance().getShopUuids().get(index);

        Bedwars.getInstance().getShopUuids().remove(index);
    }


    public void addPlayer(Player player) {
        if (!this.players.contains(player))
            this.players.add(player);
    }

    public void removePlayer(Player player) {
        if (this.players.contains(player)) this.players.remove(player);
    }

}
