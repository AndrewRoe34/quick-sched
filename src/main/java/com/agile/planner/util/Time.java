package com.agile.planner.util;

import java.util.Calendar;

/**
 * Handles all time operations involving Calendar instances
 *
 * @author Andrew Roe
 */
public class Time {

    /**
     * Determines the difference of days between two Calendar instances
     *
     * @param date1 first Calendar instance
     * @param date2 second Calendar instance
     * @return difference of days between two Calendar instances
     */
    public static int differenceOfDays(Calendar date1, Calendar date2) {
        long milliseconds = date1.getTimeInMillis() - date2.getTimeInMillis();
        return (int) Math.round(milliseconds / 1000.0 / 3600.0 / 24.0);
    }

    /**
     * Determines the range of days between two Calendar instances
     *
     * @param date1 first Calendar instance
     * @param date2 second Calendar instance
     * @return number of days between two Calendar instances
     */
    public static int determineRangeOfDays(Calendar date1, Calendar date2) {
        long milliseconds = Math.abs(date1.getTimeInMillis() - date2.getTimeInMillis());
        return (int) Math.round(milliseconds / 1000.0 / 3600.0 / 24.0);
    }

    /**
     * Gets a formatted instance of a Calendar object. Sets all time to 12:00:00 AM
     *
     * @param days number of days from current day
     * @return formatted Calendar instance
     */
    public static Calendar getFormattedCalendarInstance(int days) {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_MONTH, days);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date;
    }

    /**
     * Gets a formatted instance of a Calendar object utilizing the specified date as the starting parameter.
     *
     * @param date starting date
     * @param days number of days from current day
     * @return
     */
    public static Calendar getFormattedCalendarInstance(Calendar date, int days) {
        Calendar curr = (Calendar) date.clone();
        curr.add(Calendar.DAY_OF_MONTH, days);
        return curr;
    }

}