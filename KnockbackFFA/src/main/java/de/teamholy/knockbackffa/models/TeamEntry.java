package de.teamholy.knockbackffa.models;

import lombok.Getter;

import java.util.ArrayList;

/* copyright by Yassino */
@Getter
public class TeamEntry {

    private final String tag;
    private final ArrayList<PlayerEntry> playerEntries = new ArrayList<>();
    private final ArrayList<PlayerEntry> invites = new ArrayList<>();

    public TeamEntry(String tag) {
        this.tag = tag;
    }
}
