package de.teamholy.gamescommon;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormatTimeTest {

    @Test
    void formatTimeConvertsMillisToSecondsWithTwoDecimals() {
        // Locale-abhängiges Dezimaltrennzeichen, daher beide Varianten zulassen
        String formatted = FormatTime.formatTime(1500);
        assertTrue(formatted.equals("1.50") || formatted.equals("1,50"), formatted);
    }

    @Test
    void formatTimeManuallyMatchesFormatTime() {
        assertEquals(FormatTime.formatTime(12345), FormatTime.formatTimeManually(12345));
    }

    @Test
    void getFormatedStringUsesUpToThreeDecimals() {
        String formatted = FormatTime.getFormatedString(1234);
        assertTrue(formatted.equals("1.234") || formatted.equals("1,234"), formatted);
    }

    @Test
    void getTimeDiffMillisIsNonNegativeForPastTimestamps() {
        assertTrue(FormatTime.getTimeDiffMillis(System.currentTimeMillis() - 1000) >= 1000);
    }
}
