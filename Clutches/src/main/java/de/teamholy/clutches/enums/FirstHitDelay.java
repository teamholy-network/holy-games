package de.teamholy.clutches.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum FirstHitDelay {

    COUNTDOWN(false, "After countdown"),
    AFTER(false, "After Player hit");

    @Setter
    private boolean received;

    private String name;

    FirstHitDelay(boolean received, String name) {
        this.received = received;
        this.name = name;
    }
}
