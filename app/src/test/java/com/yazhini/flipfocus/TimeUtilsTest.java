package com.yazhini.flipfocus;

import static org.junit.Assert.assertEquals;

import com.yazhini.flipfocus.util.TimeUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

public class TimeUtilsTest {

    @Before
    public void setUp() {
        Locale.setDefault(Locale.US);   // same result on every computer
    }

    @Test
    public void formatDuration_zero() {
        assertEquals("00:00", TimeUtils.formatDuration(0));
    }

    @Test
    public void formatDuration_minutesAndSeconds() {
        assertEquals("02:05", TimeUtils.formatDuration(125_000));
    }

    @Test
    public void formatDuration_ignoresMilliseconds() {
        assertEquals("00:59", TimeUtils.formatDuration(59_999));
    }

    @Test
    public void formatShort_seconds() {
        assertEquals("45s", TimeUtils.formatShort(45_000));
    }

    @Test
    public void formatShort_minutes() {
        assertEquals("12m", TimeUtils.formatShort(12 * 60_000L));
    }

    @Test
    public void formatShort_hoursAndMinutes() {
        assertEquals("2h 5m", TimeUtils.formatShort(125 * 60_000L));
    }
}
