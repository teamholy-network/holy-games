package de.teamholy.bridge.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

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
        //final double roundedTime = Math.round(time * 20.0) / 20.0;
        return String.format("%.2f", time);
    }

    public static String formatTime(final long millis) {
        final double time = millis / 1000d;
        return String.format("%.2f", time);
    }

  //  getFormatedString
    public static String getFormatedString(long millis) {
        final double time = millis / 1000d;
        return df.format(time);
    }



}
