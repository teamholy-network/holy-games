package de.teamholy.bedwars.utils;

import de.teamholy.bedwars.Bedwars;
import de.teamholy.bedwars.model.TeamEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/* copyright by Yassino */
public class Utils {

    public static TeamEntry getHighestTeamCount() {
        int max = 0;
        Iterator<Integer> iterator;
        HashMap<TeamEntry, Integer> teams = new HashMap();
        ArrayList<TeamEntry> arrayList = new ArrayList<>();
        for (TeamEntry teamEntry : Bedwars.getInstance().getCacheHandler().getTeamEntries()) {
            teams.put(teamEntry, teamEntry.getSize());
        }
        for (iterator = teams.values().iterator(); iterator.hasNext(); ) {
            int i = iterator.next();
            if (i > max)
                max = i;
        }
        for (TeamEntry all : teams.keySet()) {
            if (teams.get(all) == max) {
                arrayList.add(all);
            }
        }
        return arrayList.get(new Random().nextInt(arrayList.size()));
    }

}
