package de.teamholy.lobby.lobbyplayer;

import de.teamholy.core.api.entities.friend.entry.Friend;
import de.teamholy.core.api.entities.friend.entry.FriendEntry;
import de.teamholy.core.api.manager.FriendManager;
import de.teamholy.lobby.Lobby;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/* copyright by Yassino */
@Getter
public class BukkitFriendEntry {

    private final UUID uuid;
    private final Player player;

    private final FriendEntry friendEntry;

    private int page = 1;
    private FriendManager.SortOption sortOption;

    public BukkitFriendEntry(FriendEntry friendEntry, Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();

        this.friendEntry = friendEntry;
        this.sortOption = friendEntry.getSortOption();
    }

    public void updateFriendEntry(UUID uuid, String data, String extra) {
        friendEntry.updateFriendEntry(uuid, data, extra);
    }

    public void updateFriendRequestEntry(UUID uuid, String data) {
        friendEntry.updateFriendRequestEntry(uuid, data);
    }

    public void openFriendGui(Player player, int page) {
        Inventory inventory = new Inventory("§8» §6Friends",9*5);
        if (page == 1) {
            this.page = 1;
        }

        ArrayList<Friend> friendArrayList = friendEntry.getFriendCache()
                .values()
                .stream()
                .sorted(sortOption.getComparator())
                .collect(Collectors.toCollection(ArrayList::new));



        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                if (row == 0 || row == 4 || col == 0 || col == 8) {
                    inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), row * 9 + col);
                }
            }
        }

        inventory.setItem(new ItemBuilder(Material.BOOK).setName("§8» §6Requests").build(),4, event -> openRequestGui(player));

        if (this.page != 1) {
            inventory.setItem(new ItemBuilder(Material.SKULL_ITEM,1, (byte) 3).setAttributs().setName("§8» §6Previous").setSkullMeta(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1Z" +
                            "GFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==","").build(),38,event -> {
                this.page -= 1;
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2F, 2F);
                openFriendGui(player,this.page);
            });
        } else {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), 38);
        }


        if (getListForPage(page + 1, friendArrayList,21).size() != 0) {
            inventory.setItem(new ItemBuilder(Material.SKULL_ITEM,1, (byte) 3).setAttributs().setName("§8» §6Next").setSkullMeta(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMT" +
                            "I2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19","").build(),42,event -> {
                this.page += 1;
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2F, 2F);
                openFriendGui(player,this.page);
            });
        } else {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), 42);
        }

        ItemBuilder sortItem = new ItemBuilder(Material.HOPPER).setName("§8» §6Sort");

        List<String> sortLore = new ArrayList<>();
        sortLore.add(" ");
        sortLore.addAll(Arrays.stream(FriendManager.SortOption.values())
                .map(value -> (sortOption == value) ? "§a" + value.getLore() : "§7" + value.getLore())
                .collect(Collectors.toList()));
        sortLore.add(" ");

        sortItem.setLore(sortLore);

        inventory.setItem(sortItem.build(),18,event -> {

            switch (sortOption) {
                case LASTONLINE_RECENTLY:
                    this.sortOption = FriendManager.SortOption.LASTONLINE_LONG;
                    break;
                case LASTONLINE_LONG:
                    this.sortOption = FriendManager.SortOption.NAME_A_TO_Z;
                    break;
                case NAME_A_TO_Z:
                    this.sortOption = FriendManager.SortOption.NAME_Z_TO_A;
                    break;
                case NAME_Z_TO_A:
                    this.sortOption = FriendManager.SortOption.RANK;
                    break;
                case RANK:
                    this.sortOption = FriendManager.SortOption.LASTONLINE_RECENTLY;
                    break;
            }

            friendEntry.setSortOption(sortOption);
            player.playSound(player.getLocation(), Sound.CLICK, 1F, 10F);
            openFriendGui(player,1);
        });


        getListForPage(page,friendArrayList,21).forEach(friend -> {
            ItemBuilder itemBuilder = new ItemBuilder(Material.SKULL_ITEM,1, (byte) 3)
                    .setSkullMeta(friend.getValue(),friend.getSignature())
                    .setName("§8» " + friend.getPlayerRank().getColorCode() + friend.getName())
                    .setLore("§aOnline §7on §6" + friend.getCurrentServer());

            if (!friend.isOnline()) {
                itemBuilder = new ItemBuilder(Material.SKULL_ITEM)
                        .setName("§8» " + friend.getPlayerRank().getColorCode() + friend.getName())
                        .setLore("§cOffline §7since §6" + convertTime(System.currentTimeMillis() - friend.getLastJoin()));
            }

            ItemBuilder finalItemBuilder = itemBuilder;
            inventory.setItem(itemBuilder.build(),inventory.getInventory().firstEmpty(), event -> {
                ItemStack itemStack = finalItemBuilder.itemStack;
                LobbyPlayer lobbyPlayer = Lobby.getInstance().getLobbyPlayerEntryHandler().get(uuid);
                Inventory subInventory = new Inventory(itemStack.getItemMeta().getDisplayName(), 9);
                for (int glass = 0; glass < 8; glass++) {
                    subInventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), glass);
                }
                subInventory.setItem(itemStack, 4);
                subInventory.setItem(new ItemBuilder(Material.BARRIER).setName("§8» §cDelete Friend").build(), 8, subEvent -> {
                    lobbyPlayer.executeBungeeCommand("friend remove " + friend.getName());
                    player.playSound(player.getLocation(), Sound.CLICK, 1F, 10F);
                    openFriendGui(player,1);
                });

                subInventory.setItem(new ItemBuilder(Material.ENDER_PEARL).setName("§8» §6Jump to friend").build(), 0, subEvent -> {
                    lobbyPlayer.executeBungeeCommand("friend jump " + friend.getName());
                    player.playSound(player.getLocation(), Sound.CLICK, 1F, 10F);
                    player.closeInventory();
                });

                subInventory.setItem(new ItemBuilder(Material.CAKE).setName("§8» §6Invite to party").build(), 1, subEvent -> {
                    lobbyPlayer.executeBungeeCommand("party invite " + friend.getName());
                    player.playSound(player.getLocation(), Sound.CLICK, 1F, 10F);
                    openFriendGui(player,1);
                });
                player.openInventory(subInventory.getInventory());
            });
        });

        inventory.setItem(new ItemBuilder(Material.SIGN).setName("§8» §6Informations")
                .setLore(
                        " "
                        , " §7Page §6" + page + " §7of §c" + getMaxPages(friendArrayList,21)
                        ," "
                        ," §aOnline friends§8: §6" + friendArrayList.stream().filter(Friend::isOnline).count()
                        ," §cOffline friends§8: §6" + friendArrayList.stream().filter(friend -> !friend.isOnline()).count()
                        , ""
                )
                .build(),40);


        player.openInventory(inventory.getInventory());
    }

    private void openRequestGui(Player player) {
        Inventory inventory = new Inventory("§8» §6Requests",9*5);
        if (page == 1) this.page = 1;

        ArrayList<Friend> friendArrayList = friendEntry.getFriendRequestCache()
                .values()
                .stream()
                .sorted((o1, o2) -> o1.isOnline() && !o2.isOnline() ? -1 : o1.isOnline() == o2.isOnline() ? 0 : 1)
                .collect(Collectors.toCollection(ArrayList::new));


        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                if (row == 0 || row == 4 || col == 0 || col == 8) {
                    inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), row * 9 + col);
                }
            }
        }

        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM,1, (byte) 3).setSkullMeta(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cm" +
                        "UvNzZjYmFlNzI0NmNjMmM2ZTg4ODU4NzE5OGM3OTU5OTc5NjY2YjRmNWE0MDg4ZjI0ZTI2ZTA3NWYxNDBhZTZjMyJ9fX0=","")
                .setName("§8» §6Friends").build(),4,event -> openFriendGui(player, 1));



        getListForPage(1,friendArrayList,21).forEach(friend -> {
            ItemBuilder itemBuilder = new ItemBuilder(Material.SKULL_ITEM,1, (byte) 3)
                    .setSkullMeta(friend.getValue(),friend.getSignature())
                    .setName("§8» " + friend.getName())
                    .setLore(""," §7leftclick to §aaccept ", " §7rightclick to §cdeny " , " ");


            inventory.setItem(itemBuilder.build(),inventory.getInventory().firstEmpty(), event -> {
                LobbyPlayer lobbyPlayer = Lobby.getInstance().getLobbyPlayerEntryHandler().get(uuid);
                if (event.isLeftClick()) {
                    lobbyPlayer.executeBungeeCommand("friend accept " + friend.getName());
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                } else if (event.isRightClick()) {
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 10);
                    lobbyPlayer.executeBungeeCommand("friend deny " + friend.getName());
                }
                player.closeInventory();
            });
        });



        player.openInventory(inventory.getInventory());
    }


    private List<Friend> getListForPage(int page, List<Friend> list, int slotsPerPage) {
        int startIndex = (page - 1) * slotsPerPage;
        int endIndex = startIndex + slotsPerPage;

        if (startIndex >= list.size()) {
            return new ArrayList<>();
        }

        if (endIndex > list.size()) {
            endIndex = list.size();
        }
        return list.subList(startIndex, endIndex);
    }

    private int getMaxPages(List<Friend> list, int slotsPerPage) {
        int totalItems = list.size();
        return (int) Math.ceil((double) totalItems / slotsPerPage);
    }

    private String convertTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        String result = "";

        if (days > 0) {
            result += days + "d ";
            hours = hours % 24;
        }
        if (hours > 0) {
            result += hours + "h ";
            minutes = minutes % 60;
        }
        if (minutes > 0 && days == 0) {
            result += minutes + "m";
        }

        return result.trim();
    }


}
