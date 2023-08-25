package de.teamholy.knockbackffa.models;

import lombok.Getter;

import java.util.ArrayList;

/* copyright by Yassino */
@Getter
public class TeamEntry {

    private String tag;
    private ArrayList<PlayerEntry> playerEntries = new ArrayList<>();
    private ArrayList<PlayerEntry> invites = new ArrayList<>();

    public TeamEntry(String tag) {
        this.tag = tag;
    }
}
