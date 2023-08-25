package de.teamholy.bedwars.handlers;

import de.teamholy.bedwars.inventoriers.GoldVotingInventory;
import de.teamholy.bedwars.inventoriers.MapVotingInventory;
import de.teamholy.bedwars.inventoriers.ShopInventory;
import de.teamholy.bedwars.inventoriers.TeamSelectInventory;
import lombok.Getter;

/* copyright by Yassino */
@Getter
public class InventoryHandler {
    private TeamSelectInventory teamSelectInventory;
    private GoldVotingInventory goldVotingInventory;
    private MapVotingInventory mapVotingInventory;
    private ShopInventory shopInventory;

    public InventoryHandler() {
        shopInventory = new ShopInventory();
        mapVotingInventory = new MapVotingInventory();
        goldVotingInventory = new GoldVotingInventory();
        teamSelectInventory = new TeamSelectInventory();

    }
}
