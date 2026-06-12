package de.teamholy.gamescommon;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeFormatTest {

    @Test
    void formatSecondsZero() {
        assertEquals("00:00", TimeFormat.formatSeconds(0));
    }

    @Test
    void formatSecondsUnderOneMinute() {
        assertEquals("00:59", TimeFormat.formatSeconds(59));
    }

    @Test
    void formatSecondsExactMinute() {
        assertEquals("01:00", TimeFormat.formatSeconds(60));
    }

    @Test
    void formatSecondsMixed() {
        assertEquals("01:30", TimeFormat.formatSeconds(90));
    }

    @Test
    void formatSecondsOverAnHourKeepsMinutes() {
        // Bewusst kein Stunden-Feld: 61 Minuten werden als "61:xx" angezeigt (bestehendes Verhalten).
        assertEquals("61:01", TimeFormat.formatSeconds(3661));
    }
}
