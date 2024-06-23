package com.planner.models;

import com.planner.util.Time.TimeStamp;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Represents a user-created event.
 *
 * @author Abah Olotuche Gabriel
 * @author Andrew Roe
 */
public class Event implements Comparable<Event> {
    private int id;
    private String name;
    private TimeStamp timeStamp;
    private Card.Colors color;
    private boolean recurring;
    private DayOfWeek[] days;

    // this constructor is for individual events (second one is for recurring)
    public Event(int id, String name, Card.Colors color, TimeStamp timeStamp) {
        setId(id);
        setName(name);
        setColor(color);
        setTimeStamp(timeStamp);
    }

    public Event(int id, String name, Card.Colors color, TimeStamp timeStamp, DayOfWeek[] days) {
        setId(id);
        setName(name);
        setColor(color);
        setTimeStamp(timeStamp);
        this.recurring = true;
        setDays(days);
    }

    public enum DayOfWeek {
        SUN,
        MON,
        TUE,
        WED,
        THU,
        FRI,
        SAT
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name for Event cannot be empty or null");
        this.name = name;
    }

    public Card.Colors getColor() {
        return color;
    }

    public void setColor(Card.Colors color) {
        this.color = color;
    }

    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    public String get24HourTimeStampString() {
        StringBuilder output = new StringBuilder();

        Calendar start = timeStamp.getStart();
        Calendar end = timeStamp.getEnd();

        output.append(start.get(Calendar.HOUR_OF_DAY) == 0 ? "00" : start.get(Calendar.HOUR_OF_DAY));
        output.append(":");
        output.append(start.get(Calendar.MINUTE) == 0 ? "00" : start.get(Calendar.MINUTE));

        output.append("-");

        output.append(end.get(Calendar.HOUR_OF_DAY) == 0 ? "00" : end.get(Calendar.HOUR_OF_DAY));
        output.append(":");
        output.append(end.get(Calendar.MINUTE) == 0 ? "00" : end.get(Calendar.MINUTE));

        return output.toString();
    }

    public void setTimeStamp(TimeStamp timeStamp) {
        Calendar start = timeStamp.getStart();
        Calendar end = timeStamp.getEnd();
        if (start.get(Calendar.YEAR) != end.get(Calendar.YEAR) || start.get(Calendar.MONTH) != end.get(Calendar.MONTH)
                || start.get(Calendar.DAY_OF_MONTH) != end.get(Calendar.DAY_OF_MONTH)) {
            throw new IllegalArgumentException("Start and end times for timestamp don't share same date");
        }

        this.timeStamp = timeStamp;
    }

    public String getDateStamp() {
        int day = timeStamp.getStart().get(Calendar.DAY_OF_MONTH);
        int year = timeStamp.getStart().get(Calendar.YEAR);

        // Calendar.MONTH is zero-indexed, so we have to increment the
        // month value to get the correct month.
        // I hate this.
        int month = timeStamp.getStart().get(Calendar.MONTH) + 1;

        StringBuilder dateStamp = new StringBuilder();
        if (day < 10) dateStamp.append("0");
        dateStamp.append(day);
        dateStamp.append("-");
        if (month < 10) dateStamp.append("0");
        dateStamp.append(month);
        dateStamp.append("-");
        dateStamp.append(year);

        return dateStamp.toString();
    }

    public boolean isRecurring() {
        return recurring;
    }

    public DayOfWeek[] getDays() {
        return days;
    }

    public String getDaysString() {
        String daysString = Arrays.toString(days);

        return daysString.substring(1, daysString.length() - 1);
    }

    public void setDays(DayOfWeek[] days) {
        if (days == null || days.length == 0 || days.length > 7) throw new IllegalArgumentException("Invalid set of days provided to Event");
        boolean[] week = new boolean[7];
        for (DayOfWeek d : days) {
            if (!week[d.ordinal()]) week[d.ordinal()] = true;
            else throw new IllegalArgumentException("Repeats of days cannot occur");
        }

        // this code below ensures that days of week are in sorted order (which is helpful for eventlog and displays)
        int count = 0;
        for (int i = 0; i < week.length; i++) {
            if (week[i]) {
                switch (i) {
                    case 0:
                        days[count++] = DayOfWeek.SUN;
                        break;
                    case 1:
                        days[count++] = DayOfWeek.MON;
                        break;
                    case 2:
                        days[count++] = DayOfWeek.TUE;
                        break;
                    case 3:
                        days[count++] = DayOfWeek.WED;
                        break;
                    case 4:
                        days[count++] = DayOfWeek.THU;
                        break;
                    case 5:
                        days[count++] = DayOfWeek.FRI;
                        break;
                    case 6:
                        days[count++] = DayOfWeek.SAT;
                        break;
                }
            }
        }
        this.days = days;
    }

    @Override
    public int compareTo(Event o) {
        return this.timeStamp
                .getStart()
                .compareTo(o.getTimeStamp().getStart());
    }
}
