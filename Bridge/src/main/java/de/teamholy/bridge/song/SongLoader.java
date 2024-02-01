package de.teamholy.bridge.song;

import de.teamholy.bridge.player.settings.sounds.BridgeSong;
import de.teamholy.bridge.player.settings.sounds.BridgeSound;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class SongLoader {

    public SongLoader(SongManager songManager) throws IOException {
        File file = new File("plugins/Bridge/songs");
        if (!file.exists()) {
            file.mkdirs();
        }

        for (File songFile : Objects.requireNonNull(file.listFiles())) {
            if (songFile.getName().endsWith(".nbs")) {
                BridgeSong song = new BridgeSong(songFile.getName().replace(" ", "").replace(".nbs", ""), songFile);
                songManager.addSong(song);

                System.out.println("Loaded song " + song.getName());
            }
        }
    }

}
