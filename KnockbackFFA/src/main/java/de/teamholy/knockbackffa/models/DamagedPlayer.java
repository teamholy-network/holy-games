package de.teamholy.knockbackffa.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

/* copyright by Yassino */
@AllArgsConstructor
@Getter
public class DamagedPlayer {

    private PlayerEntry damager;
    private PlayerEntry entity;
    private Long hitTime;

}
