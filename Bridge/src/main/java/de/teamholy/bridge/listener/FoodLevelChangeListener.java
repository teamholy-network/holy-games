package de.teamholy.bridge.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class FoodLevelChangeListener implements Listener {

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }

}
