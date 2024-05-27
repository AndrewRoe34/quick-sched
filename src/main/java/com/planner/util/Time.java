package com.planner.util;

import com.planner.models.UserConfig;
import com.planner.schedule.day.Day;

import java.util.ArrayList;
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

    // [COMPLETE]
    public static double getTimeInterval(Calendar curr, Calendar event) {
        // determine how many hours or minutes exist between current time and upcoming event
        //   if the minutes is less than 30, return 0
        //     e.g. 1 hour and 27 minutes --> 1 hour
        //     e.g. 1 hour and 40 minutes --> 1.5 hours
        //   we can safely assume that the event is properly aligned within the quarter period of the day (0, 15, 30, 45)
        long milliseconds = Math.abs(curr.getTimeInMillis() - event.getTimeInMillis());
        int min = (int) Math.round(milliseconds / 1000.0 / 60.0);
        double hours = (int) (min / 60.0);
        min %= 60;
        hours += min >= 30 ? 0.5 : 0;
        return hours;
    }

    // todo once this piece is done, i'm going to get some sleep
    public static Calendar getFirstAvailableTimeInDay(List<TimeStamp> taskTimeStamps, List<TimeStamp> eventTimeStamps, UserConfig userConfig, Calendar time, boolean isToday) {
        Calendar startTime;
        if (isToday && time.get(Calendar.HOUR_OF_DAY) >= userConfig.getRange()[0] && taskTimeStamps.isEmpty() && !userConfig.isDefaultAtStart()) {
            startTime = Time.getNearestQuarterOfHour(time, true);
        } else if (taskTimeStamps.isEmpty()) {
            startTime = Time.getFormattedCalendarInstance(time, 0);
            startTime.set(Calendar.HOUR_OF_DAY, userConfig.getRange()[0]);
            startTime.set(Calendar.MINUTE, 0);
        } else {
            TimeStamp ts = taskTimeStamps.get(taskTimeStamps.size() - 1);
            startTime = (Calendar) ts.getEnd().clone();
        }

        if (eventTimeStamps.isEmpty()) {
            return startTime;
        }

        // todo there are events tho, so we need to use a loop that keeps on checking until we find a valid starting point
        for (TimeStamp eTS : eventTimeStamps) {
            isInsideEventBlock(startTime, eTS);
        }
        // not sure what to do here yet (might need to update the startTime inside the for loop
        return startTime;
    }

    private static boolean isInsideEventBlock(Calendar startTime, TimeStamp eventTimeStamp) {
        return false;
    }

    public static List<Double> computeTimeBlocks(Day day) {
        List<Double> intervals = new ArrayList<>();
        for (TimeStamp taskTimeStamp : day.getTaskTimeStamps()) {
            intervals.add(Time.getTimeInterval(taskTimeStamp.getStart(), taskTimeStamp.getEnd()));
        }
        return intervals;
    }

    /**
     * Manages the creation of a time interval along with properly formatting its display.
     * <p>
     * Example: 12:30pm-4:15pm
     *
     * @author Andrew Roe
     */
    public static class TimeStamp implements Comparable<TimeStamp> {
        private final Calendar start;
        private final Calendar end;
        private String strStamp;

        public TimeStamp(Calendar start, Calendar end) {
            this.start = start;
            this.end = end;
            buildStamp();
        }

        public Calendar getStart() {
            return start;
        }

        public Calendar getEnd() {
            return end;
        }

        public int getStartHour() {
            return start.get(Calendar.HOUR_OF_DAY);
        }

        public int getStartMin() {
            return start.get(Calendar.MINUTE);
        }

        public int getEndHour() {
            return end.get(Calendar.HOUR_OF_DAY);
        }

        public int getEndMin() {
            return end.get(Calendar.MINUTE);
        }

        private void buildStamp() {
            StringBuilder sb = new StringBuilder();

            int _startHour = getStartHour() - 12;
            if (getStartHour() <= 9 || getStartHour() >= 13 && getStartHour() <= 21) sb.append("0");
            if (getStartHour() <= 12) sb.append(getStartHour());
            if (getStartHour() > 12) sb.append(_startHour);
            sb.append(":");
            if (getStartMin() < 10) sb.append("0");
            sb.append(getStartMin());
            if (getStartHour() < 12) sb.append("am");
            else sb.append("pm");

            sb.append("-");

            int _endHour = getEndHour() - 12;
            if (getEndHour() <= 9 || getEndHour() >= 13 && getEndHour() <= 21) sb.append("0");
            if (getEndHour() <= 12) sb.append(getEndHour());
            if (getEndHour() > 12) sb.append(_endHour);
            sb.append(":");
            if (getEndMin() < 10) sb.append("0");
            sb.append(getEndMin());
            if (getEndHour() < 12) sb.append("am");
            else sb.append("pm");

            strStamp = sb.toString();
        }

        @Override
        public String toString() {
            return strStamp;
        }

        @Override
        public int compareTo(TimeStamp o) {
            if (this.getStartHour() < o.getStartHour()) return -1;
            else if (this.getStartHour() > o.getStartHour()) return 1;
            else {
                return Integer.compare(this.getStartMin(), o.getStartMin());
            }
        }
    }

}