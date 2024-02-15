package de.teamholy.bridge.map.loader;

import de.teamholy.bridge.map.BridgeMap;
import de.teamholy.bridge.map.skin.BridgeMapSkin;
import org.bukkit.Location;

import java.util.concurrent.CompletableFuture;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public interface BridgeMapLoader {

    void loadMap(BridgeMap bridgeMap, boolean firstPaste, Location location, BridgeMapSkin bridgeMapSkin, boolean pasteAir);

    CompletableFuture<BridgeMapSkin> loadMapAsync(BridgeMap bridgeMap, BridgeMapSkin bridgeMapSkin, boolean air);


}
