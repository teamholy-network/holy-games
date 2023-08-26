package de.teamholy.mlgrush.player;

import lombok.Getter;
import lombok.Setter;

/* copyright by Yassino */
@Getter
@Setter
public class IngamePlayer {
    private int beds = 0, kills = 0, deaths = 0;

    public void reset() {
        beds = 0;
        kills = 0;
        deaths = 0;
    }
}
