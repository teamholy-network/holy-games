package de.teamholy.gamescommon;

/**
 * Zeitformatierung für Countdown- und Timer-Anzeigen.
 */
public final class TimeFormat {

    private TimeFormat() {
    }

    /**
     * Formatiert Sekunden als {@code MM:SS}, z.B. {@code 90 -> "01:30"}.
     */
    public static String formatSeconds(int seconds) {
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
