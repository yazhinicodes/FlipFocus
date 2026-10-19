package com.yazhini.flipfocus;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.yazhini.flipfocus.data.Session;
import com.yazhini.flipfocus.util.StatsCalculator;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StatsCalculatorTest {

    private static final long MINUTE = 60_000L;
    private static final long DAY = 24 * 60 * MINUTE;

    private long now;   // Sunday, 18 Oct 2026, 15:30

    @Before
    public void setUp() {
        Locale.setDefault(Locale.US);
        Calendar c = Calendar.getInstance();
        c.set(2026, Calendar.OCTOBER, 18, 15, 30, 0);
        c.set(Calendar.MILLISECOND, 0);
        now = c.getTimeInMillis();
    }

    @Test
    public void dayStarts_returnsSevenMidnightsInOrder() {
        long[] starts = StatsCalculator.dayStarts(now);

        assertEquals(7, starts.length);
        for (int i = 1; i < 7; i++) {
            assertTrue(starts[i] > starts[i - 1]);
        }

        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(starts[6]);
        assertEquals(18, today.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, today.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, today.get(Calendar.MINUTE));
    }

    @Test
    public void dailyTotals_noSessions_allZero() {
        long[] totals = StatsCalculator.dailyTotals(Collections.<Session>emptyList(), now);
        assertArrayEquals(new long[7], totals);
    }

    @Test
    public void dailyTotals_sessionToday_countsInLastSlot() {
        List<Session> sessions = Arrays.asList(new Session(now - 60 * MINUTE, 10 * MINUTE));
        long[] totals = StatsCalculator.dailyTotals(sessions, now);
        assertArrayEquals(new long[]{0, 0, 0, 0, 0, 0, 10 * MINUTE}, totals);
    }

    @Test
    public void dailyTotals_twoSessionsSameDay_areAdded() {
        List<Session> sessions = Arrays.asList(
                new Session(now - 60 * MINUTE, 10 * MINUTE),
                new Session(now - 120 * MINUTE, 20 * MINUTE));
        long[] totals = StatsCalculator.dailyTotals(sessions, now);
        assertEquals(30 * MINUTE, totals[6]);
    }

    @Test
    public void dailyTotals_sessionYesterday_countsInSecondToLastSlot() {
        List<Session> sessions = Arrays.asList(new Session(now - DAY, 25 * MINUTE));
        long[] totals = StatsCalculator.dailyTotals(sessions, now);
        assertArrayEquals(new long[]{0, 0, 0, 0, 0, 25 * MINUTE, 0}, totals);
    }

    @Test
    public void dailyTotals_sixDaysAgo_countsInFirstSlot() {
        List<Session> sessions = Arrays.asList(new Session(now - 6 * DAY, 15 * MINUTE));
        long[] totals = StatsCalculator.dailyTotals(sessions, now);
        assertArrayEquals(new long[]{15 * MINUTE, 0, 0, 0, 0, 0, 0}, totals);
    }

    @Test
    public void dailyTotals_olderThanSevenDays_isIgnored() {
        List<Session> sessions = Arrays.asList(new Session(now - 7 * DAY, 15 * MINUTE));
        long[] totals = StatsCalculator.dailyTotals(sessions, now);
        assertArrayEquals(new long[7], totals);
    }

    @Test
    public void dailyTotals_midnightBoundary_goesToTheCorrectDay() {
        long[] starts = StatsCalculator.dayStarts(now);
        List<Session> sessions = Arrays.asList(
                new Session(starts[6], 5 * MINUTE),        // exactly midnight -> today
                new Session(starts[6] - 1, 7 * MINUTE));   // 1 ms earlier -> yesterday
        long[] totals = StatsCalculator.dailyTotals(sessions, now);
        assertEquals(7 * MINUTE, totals[5]);
        assertEquals(5 * MINUTE, totals[6]);
    }

    @Test
    public void dayLabels_areMondayToSunday() {
        String[] labels = StatsCalculator.dayLabels(now);
        assertArrayEquals(new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}, labels);
    }
}
