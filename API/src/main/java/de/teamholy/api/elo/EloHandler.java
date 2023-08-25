package de.teamholy.api.elo;


/* copyright by Yassino */
public class EloHandler {

    public static EloRank getEloRank(int elo) {
        EloRank eloRank = null;
        if (elo <= EloRank.UNRANKED.getRangeMaxElo())
            eloRank = EloRank.UNRANKED;

        for (EloRank eloRanks : EloRank.values()) {
            if (elo >= eloRanks.getRangeMinElo()) eloRank = eloRanks;
        }
        return eloRank;
    }

    public static int handleEloDifference(int difference) {
        int i = 5;
        if (difference >= -5000)
            i = 6;
        if (difference >= -4000)
            i = 7;
        if (difference >= -3000)
            i = 8;
        if (difference >= -2000)
            i = 9;
        if (difference >= -1000)
            i = 10;
        if (difference >= 0)
            i = 11;
        if (difference >= 1000)
            i = 12;
        if (difference >= 2000)
            i = 13;
        if (difference >= 3000)
            i = 14;
        if (difference >= 4000)
            i = 15;
        if (difference >= 5000)
            i = 16;

        return i;
    }


}
