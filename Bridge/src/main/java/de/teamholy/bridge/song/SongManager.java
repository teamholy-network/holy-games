package de.teamholy.bridge.song;

import com.google.common.collect.Lists;
import de.teamholy.bridge.player.settings.sounds.BridgeSong;
import lombok.Getter;

import java.util.List;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class SongManager {

    private final List<BridgeSong> songs;

    private final SongLoader songLoader;

    public SongManager() {
        this.songs = Lists.newArrayList();

        try {
            this.songLoader = new SongLoader(this);
        } catch (Exception e) {
            throw new RuntimeException("Could not load songs", e);
        }
    }

    public void addSong(BridgeSong song) {
        this.songs.add(song);
    }
}
