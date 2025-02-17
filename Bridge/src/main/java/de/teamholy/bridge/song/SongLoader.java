package de.teamholy.bridge.song;

import de.teamholy.bridge.player.settings.sounds.BridgeSong;
import java.io.*;
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
