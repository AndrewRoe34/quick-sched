package com.planner.util;

import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests Time functionality
 *
 * @author Andrew Roe
 */
public class TimeTest {

    /**
     * Tests determineRangeOfDays() functionality
     */
    @Test
    public void testDetermineRangeOfDays() {
        Calendar current = Calendar.getInstance();
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);

        Calendar future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_MONTH, 4324);
        future.set(Calendar.HOUR_OF_DAY, 0);
        future.set(Calendar.MINUTE, 0);
        future.set(Calendar.SECOND, 0);
        future.set(Calendar.MILLISECOND, 0);

        assertEquals(4324, Time.determineRangeOfDays(current, future));
    }

    /**
     * Tests getFormattedCalendarInstance() functionality
     */
    @Test
    public void testGetFormattedCalendarInstance() {
        Calendar future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_MONTH, 4324);
        future.set(Calendar.HOUR_OF_DAY, 0);
        future.set(Calendar.MINUTE, 0);
        future.set(Calendar.SECOND, 0);
        future.set(Calendar.MILLISECOND, 0);

        assertEquals(future, Time.getFormattedCalendarInstance(4324));
    }

}