package de.teamholy.api.elo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EloRank {

    UNRANKED(0,"§7Unranked",-1,1099,""),
    B1(1,"§cBronze I",1100,1249,"§8[§cI§8]"),
    B2(2,"§cBronze II",1250,1499,"§8[§cII§8]"),
    B3(3,"§cBronze III",1500,1749,"§8[§cIII§8]"),
    S1(4,"§fSilver I",1750,1999,"§8[§fI§8]"),
    S2(5,"§fSilver II",2000,2249,"§8[§fII§8]"),
    S3(6,"§fSilver III",2250,2499,"§8[§fIII§8]"),
    G1(7,"§6Gold I",2500,2749,"§8[§6I§8]"),
    G2(8,"§6Gold II",2750,2999,"§8[§6II§8]"),
    G3(9,"§6Gold III",3000,3249,"§8[§6III§8]"),
    D1(10,"§bDiamond I",3250,3499,"§8[§bI§8]"),
    D2(11,"§bDiamond II",3500,3749,"§8[§bII§8]"),
    D3(12,"§bDiamond III", 3750,3999,"§8[§bIII§8]"),
    C1(13,"§1Champion I",4000,4499,"§8[§1I§8]"),
    C2(14,"§1Champion II",4500,4999,"§8[§1II§8]"),
    C3(15,"§1Champion III",5000,5999,"§8[§1III§8]"),
    M(16,"§4Master",6000,-1,"§8[§4☣§8]");

    private int id;
    private String name;
    private int rangeMinElo;
    private int rangeMaxElo;
    private String tabPrefix;
}
