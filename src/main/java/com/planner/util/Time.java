package com.planner.util;

import com.planner.models.Event;
import com.planner.models.UserConfig;
import com.planner.schedule.day.Day;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Handles all time operations involving Calendar instances
 *
 * @author Andrew Roe
 * @author Abah Olotuche Gabriel
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

    public static boolean doDatesMatch(Calendar date1, Calendar date2) {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) && date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
                && date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH);
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
                clone.add(Calendar.HOUR_OF_DAY, 1);
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
        Calendar currTemp = cloneAndResetSecMillis(curr);
        Calendar eventTemp = cloneAndResetSecMillis(event);
        long milliseconds = Math.abs(currTemp.getTimeInMillis() - eventTemp.getTimeInMillis());
        int min = (int) Math.round(milliseconds / 1000.0 / 60.0);
        double hours = (int) (min / 60.0);
        min %= 60;
        hours += min >= 30 ? 0.5 : 0;
        return hours;
    }

    // [COMPLETE]
    public static Calendar getFirstAvailableTimeInDay(List<TimeStamp> taskTimeStamps, List<TimeStamp> eventTimeStamps, UserConfig userConfig, Calendar time, boolean isToday) {
        Calendar startTime;
        if (isToday && time.get(Calendar.HOUR_OF_DAY) >= userConfig.getDailyHoursRange()[0] && taskTimeStamps.isEmpty() && !userConfig.isDefaultAtStart()) {
            startTime = Time.getNearestQuarterOfHour(time, true);
        } else if (taskTimeStamps.isEmpty()) {
            startTime = Time.getFormattedCalendarInstance(time, 0);
            startTime.set(Calendar.HOUR_OF_DAY, userConfig.getDailyHoursRange()[0]);
            startTime.set(Calendar.MINUTE, 0);
        } else {
            TimeStamp ts = taskTimeStamps.get(taskTimeStamps.size() - 1);
            startTime = (Calendar) ts.getEnd().clone();
        }

        if (eventTimeStamps.isEmpty()) {
            return startTime;
        }

        /*
        Steps for determining correct start time with events throughout day:
        1. Check whether startTime interferes with the event
        2. If it does, assign the end of the event to startTime
            a. If no more events, loop will break with correct value (so, do nothing here)
            b. If there are events, simply continue (so, do nothing here)
        3. If it doesn't, check whether it is before or after startTime
            a. If it's before, continue (if no more events, no changes to startTime)
            b. If it's after, check how much time there is between
                i. If enough, break
                ii. If not enough, assign end of event to startTime and loop again
         */
        for (TimeStamp eTS : eventTimeStamps) {
            if (isInsideEventBlock(startTime, eTS)) startTime = eTS.getEnd();
            else {
                if (isAfter(startTime, eTS.getEnd())) {
                    // do nothing here
                } else {
                    double hours = getTimeInterval(startTime, eTS.getStart());
                    if (hours > 0) break;
                    else startTime = eTS.getEnd();
                }
            }
        }

        return Time.getNearestQuarterOfHour(startTime, true);
    }

    // [COMPLETE]
    public static boolean isInsideEventBlock(Calendar startTime, TimeStamp eventTimeStamp) {
        return !isAfter(startTime, eventTimeStamp.getEnd()) && !isBefore(startTime, eventTimeStamp.getStart());
    }

    // [COMPLETE]
    public static boolean isAfter(Calendar startTime, Calendar eventEnd) {
        Calendar startTemp = cloneAndResetSecMillis(startTime);
        Calendar eventTemp = cloneAndResetSecMillis(eventEnd);
        return startTemp.compareTo(eventTemp) >= 0;
    }

    // [COMPLETE]
    public static boolean isBefore(Calendar startTime, Calendar eventStart) {
        Calendar startTemp = cloneAndResetSecMillis(startTime);
        Calendar eventTemp = cloneAndResetSecMillis(eventStart);
        return startTemp.compareTo(eventTemp) < 0;
    }

    // [COMPLETE]
    public static Calendar cloneAndResetSecMillis(Calendar date) {
        Calendar temp = (Calendar) date.clone();
        temp.set(Calendar.SECOND, 0);
        temp.set(Calendar.MILLISECOND, 0);
        return temp;
    }

    // [COMPLETE]
    public static boolean isConflictingEvent(Event curr, Event other) {
        Calendar currStart = cloneAndResetSecMillis(curr.getTimeStamp().getStart());
        Calendar currEnd = cloneAndResetSecMillis(curr.getTimeStamp().getEnd());
        Calendar otherStart = cloneAndResetSecMillis(other.getTimeStamp().getEnd());
        Calendar otherEnd = cloneAndResetSecMillis(other.getTimeStamp().getEnd());

        if (currStart.compareTo(otherStart) >= 0 && currStart.compareTo(otherEnd) < 0) return true;
        else return currEnd.compareTo(otherStart) > 0 && currEnd.compareTo(otherEnd) < 0;
    }

    // [COMPLETE]
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
        private final String strStamp;

        public TimeStamp(Calendar start, Calendar end) {
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);
            end.set(Calendar.SECOND, 0);
            end.set(Calendar.MILLISECOND, 0);
            if (start.compareTo(end) >= 0) throw new IllegalArgumentException("Start time is greater than or equal to end time");
            this.start = start;
            this.end = end;
            strStamp = buildStamp();
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

        private String buildStamp() {
            StringBuilder sb = new StringBuilder();

            if (getStartHour() == 0) {
                sb.append("12");
            } else {
                int _startHour = getStartHour() - 12;
                if (getStartHour() <= 9 || getStartHour() >= 13 && getStartHour() <= 21) sb.append("0");
                if (getStartHour() <= 12) sb.append(getStartHour());
                if (getStartHour() > 12) sb.append(_startHour);
            }
            sb.append(":");
            if (getStartMin() < 10) sb.append("0");
            sb.append(getStartMin());
            if (getStartHour() < 12) sb.append("am");
            else sb.append("pm");

            sb.append("-");

            if (getEndHour() == 0) {
                sb.append("12");
            } else {
                int _endHour = getEndHour() - 12;
                if (getEndHour() <= 9 || getEndHour() >= 13 && getEndHour() <= 21) sb.append("0");
                if (getEndHour() <= 12) sb.append(getEndHour());
                if (getEndHour() > 12) sb.append(_endHour);
            }
            sb.append(":");
            if (getEndMin() < 10) sb.append("0");
            sb.append(getEndMin());
            if (getEndHour() < 12) sb.append("am");
            else sb.append("pm");

            return sb.toString();
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