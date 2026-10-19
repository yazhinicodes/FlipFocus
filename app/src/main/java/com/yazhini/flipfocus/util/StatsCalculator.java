package com.yazhini.flipfocus.util;

import com.yazhini.flipfocus.data.Session;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class StatsCalculator {

    public static final int DAYS = 7;

    private StatsCalculator() {
    }

    /** Midnight of each of the last 7 days. Index 0 = 6 days ago, index 6 = today. */
    public static long[] dayStarts(long nowMs) {
        long[] starts = new long[DAYS];
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(nowMs);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        for (int i = DAYS - 1; i >= 0; i--) {
            starts[i] = c.getTimeInMillis();
            c.add(Calendar.DAY_OF_YEAR, -1);
        }
        return starts;
    }

    /** Total focus time in milliseconds for each of the last 7 days. */
    public static long[] dailyTotals(List<Session> sessions, long nowMs) {
        long[] totals = new long[DAYS];
        long[] starts = dayStarts(nowMs);
        for (Session s : sessions) {
            for (int i = DAYS - 1; i >= 0; i--) {
                if (s.startTimeMs >= starts[i]) {
                    totals[i] += s.durationMs;
                    break;
                }
            }
        }
        return totals;
    }

    /** Short day names such as Mon, Tue... for the last 7 days. */
    public static String[] dayLabels(long nowMs) {
        long[] starts = dayStarts(nowMs);
        SimpleDateFormat format = new SimpleDateFormat("EEE", Locale.getDefault());
        String[] labels = new String[DAYS];
        for (int i = 0; i < DAYS; i++) {
            labels[i] = format.format(new Date(starts[i]));
        }
        return labels;
    }
}