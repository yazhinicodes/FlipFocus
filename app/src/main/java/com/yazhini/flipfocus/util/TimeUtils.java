package com.yazhini.flipfocus.util;

import java.util.Locale;

public final class TimeUtils {

    private TimeUtils() {
        // utility class, nobody should create an object of it
    }

    public static String formatDuration(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    /** Short text for chart labels: 45s, 12m, 2h 5m. */
    public static String formatShort(long millis) {
        long totalSeconds = millis / 1000;
        if (totalSeconds < 60) {
            return totalSeconds + "s";
        }
        long totalMinutes = totalSeconds / 60;
        if (totalMinutes < 60) {
            return totalMinutes + "m";
        }
        return (totalMinutes / 60) + "h " + (totalMinutes % 60) + "m";
    }
}
