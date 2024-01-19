package dev.charon.bridge.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class FormatTime {

    private static final DecimalFormat df = new DecimalFormat("##0.00");

    public static String formatTimeManually(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;
        return df.format(hours) + ":" + df.format(minutes) + ":" + df.format(remainingSeconds);
    }

    public static String formatTimeManually(long millis) {
        double remainingSeconds = (millis / 1000d);
        return df.format(remainingSeconds);
    }
}
