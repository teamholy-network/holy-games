package de.teamholy.clutches.npcskin;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public enum NPCSkin {

    _2SA(1,"2sa",UUID.fromString("6d40f495-d796-4244-9f45-964cdd7e685a")),
    DERNOZE(2,"derNOZE",UUID.fromString("03c55754-08fc-4a12-a451-e517c89a3f91")),
    CHRAMA(3,"Chrama",UUID.fromString("b5f244ed-f925-468e-952a-3eb4a4ba5bb4")),
    HIGHCOMBO(4,"HighCombo",UUID.fromString("e6800636-e215-49ad-9628-a6f4075989a7")),
    JQRDEN(5,"Jqrden",UUID.fromString("2646aecf-ddcc-4f3a-bedd-3b2c8d386350")),
    GREGORR(6,"Gregorr",UUID.fromString("eecc3c44-eaaf-48fe-af23-3af762578446")),
    MIILLE(7,"miille",UUID.fromString("3c192f66-9c9f-4b5e-837d-a60cfdd6f96f")),
    KLEINERBLUE(8,"kleinerblue",UUID.fromString("a54e8818-845d-4f55-923b-38b334697096")),
    JOKY(9,"7joky",UUID.fromString("1dd0cc8f-5271-4d49-b774-16dc36877017")),
    YASSINO(10,"Yassino",UUID.fromString("fa44c187-80dd-4171-bb5a-2e694c4c8b4f")),
    CHARONXYZ(11,"charonxyz",UUID.fromString("d08cbbd7-5e65-43e1-ad58-6ed90a560818")),
    STEVE(12,"Steve",UUID.fromString("726ad0d8-c5ed-4301-9f66-ec719c292278")),
    ALEX(13,"Alex",UUID.fromString("7bf10b6d-0a8e-4c2b-a567-2e6491a89da5")),
    SELTT(14,"seltt",UUID.fromString("7414ffe4-6355-4877-8103-1ff6e0432e61"));



    private int id;
    private String name;
    private UUID uuid;

    public static NPCSkin getNPCSkinFromId(int id) {
        for (NPCSkin value : NPCSkin.values()) {
            if (value.id == id) return value;
        }
        return null;
    }

}
