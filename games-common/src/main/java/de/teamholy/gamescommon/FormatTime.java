package de.teamholy.gamescommon;

import java.text.DecimalFormat;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class FormatTime {

    private static final DecimalFormat df = new DecimalFormat("#.###");

    public static long getTimeDiffMillis(final long millis) {
        return System.currentTimeMillis() - millis;
    }

    public static String formatTimeManually(final long millis) {
        final double time = millis / 1000d;
        return String.format("%.2f", time);
    }

    public static String formatTime(final long millis) {
        final double time = millis / 1000d;
        return String.format("%.2f", time);
    }

    public static String getFormatedString(long millis) {
        final double time = millis / 1000d;
        return df.format(time);
    }

}
