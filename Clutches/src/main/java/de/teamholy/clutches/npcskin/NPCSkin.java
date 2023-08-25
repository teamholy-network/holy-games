package de.teamholy.clutches.npcskin;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public enum NPCSkin {

    IAMSLOWLY(0,"iamSlowly",UUID.fromString("7f835fbc-0b45-4def-b5b2-d3bdfd3b23f8")),
    VEPX(1,"vepex",UUID.fromString("4e2654df-fa09-4c88-b3ae-b2377bcc487d")),
    TELLY(2,"ATellyBridger",UUID.fromString("b813ceac-1130-401d-9d8f-cbf4852392d0")),
    KLEINERCLUE(3,"kleinerclue",UUID.fromString("84043f3a-5a71-498b-8edb-16de44c98ab0")),
    LEBBYY(4,"Lebbyy",UUID.fromString("bef28b0b-cb18-412c-98ec-2c6b33ac3933")),
    JQRDEN(5,"Jqrden",UUID.fromString("2646aecf-ddcc-4f3a-bedd-3b2c8d386350")),
    GREGORR(6,"Gregorr",UUID.fromString("eecc3c44-eaaf-48fe-af23-3af762578446")),
    MIILLE(7,"miille",UUID.fromString("3c192f66-9c9f-4b5e-837d-a60cfdd6f96f")),
    KLEINERBLUE(8,"kleinerblue",UUID.fromString("a54e8818-845d-4f55-923b-38b334697096")),
    JOKY(9,"7joky",UUID.fromString("1dd0cc8f-5271-4d49-b774-16dc36877017")),
    YASSINO(10,"Yassino",UUID.fromString("fa44c187-80dd-4171-bb5a-2e694c4c8b4f")),
    CHARONXYZ(11,"charonxyz",UUID.fromString("d08cbbd7-5e65-43e1-ad58-6ed90a560818")),
    BEDLESSNOOB(12,"BedlessNoob",UUID.fromString("04042384-cf5e-4f58-a128-6a87ede461b4"));

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
