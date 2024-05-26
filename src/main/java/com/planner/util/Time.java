package com.planner.util;

import com.planner.models.UserConfig;
import com.planner.schedule.day.Day;

import java.util.Calendar;
import java.util.List;

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
     * @return formatted Calendar instance with the proper increment provided
     */
    public static Calendar getFormattedCalendarInstance(Calendar date, int days) {
        Calendar curr = (Calendar) date.clone();
        curr.add(Calendar.DAY_OF_MONTH, days);
        return curr;
    }

    /**
     * Determines the closest available Calendar instance within a quarter of an hour (above or below)
     *
     * @param date current date being measured
     * @param isAbove whether Calendar instance will be above or below to nearest quarter
     * @return new instance of Calendar to nearest quarter
     */
    public static Calendar getNearestQuarterOfHour(Calendar date, boolean isAbove) {
        int min = date.get(Calendar.MINUTE);
        Calendar clone = (Calendar) date.clone();
        if (min == 0 || min == 15 || min == 30 || min == 45) return clone;

        if (isAbove) {
            if (min > 45) {
                clone.set(Calendar.MINUTE, 0);
                clone.add(Calendar.HOUR_OF_DAY, 1); //todo need to check and see if that also updates the 'Day of month' component as well
            } else if (min > 30) {
                clone.set(Calendar.MINUTE, 45);
            } else if (min > 15) {
                clone.set(Calendar.MINUTE, 30);
            } else {
                clone.set(Calendar.MINUTE, 15);
            }
        } else {
            if (min < 15) {
                clone.set(Calendar.MINUTE, 0);
            } else if (min <= 30) {
                clone.set(Calendar.MINUTE, 15);
            } else if (min <= 45) {
                clone.set(Calendar.MINUTE, 30);
            } else {
                clone.set(Calendar.MINUTE, 45);
            }
        }
        return clone;
    }

    public static double getTimeUntilEvent(Calendar curr, Day.TimeStamp event) {
        // todo determine how many hours or minutes exist between current time and upcoming event
        //   if the minutes is less than 30, return 0
        //     e.g. 1 hour and 27 minutes --> 1 hour
        //     e.g. 1 hour and 40 minutes --> 1.5 hours
        //   we can safely assume that the event is properly aligned within the quarter period of the day (0, 15, 30, 45)
        if (curr.get(Calendar.HOUR_OF_DAY) < event.getStartHour()) {
            // compute difference
            double hours = event.getStartHour() - curr.get(Calendar.HOUR_OF_DAY);
            int min = 0;
            if (event.getStartMin() >= curr.get(Calendar.MINUTE)) {
                min = event.getStartMin() - curr.get(Calendar.MINUTE);
                hours += min < 30 ? 0 : 0.5;
            } else {
                min = curr.get(Calendar.MINUTE) - event.getStartMin();
                if (min > 30) hours--;
                else hours -= 0.5;
            }
            return hours < 0 ? 0 : hours;
        } else {
            return 0;
        }
    }

    public static List<Double> computeDayTimeBlocks(Day day, UserConfig userConfig) {
        // todo this will help with the 'optimizeDay' config
        return null;
    }

}