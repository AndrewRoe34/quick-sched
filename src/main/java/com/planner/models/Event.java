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
    /** ID of the Event */
    private int id;
    /** Name of the Event */
    private String name;
    /** timestamp of the Event */
    private TimeStamp timeStamp;
    /** Color of the Event's dard */
    private Card.Color color;
    /** Recurring status of the Event */
    private boolean recurring;
    /** Days of the Event */
    private DayOfWeek[] days;

    // this constructor is for individual events (second one is for recurring)

    /**
     * Constructor for individual events
     *
     * @param id ID of the Event
     * @param name Name of the Event
     * @param color Color of the Event
     * @param timeStamp Timestamp of the Event
     */
    public Event(int id, String name, Card.Color color, TimeStamp timeStamp) {
        setId(id);
        setName(name);
        setColor(color);
        setTimeStamp(timeStamp);
    }

    /**
     * Constructor for recurring events
     *
     * @param id ID of the Event
     * @param name Name of the Event
     * @param color Color of the Event
     * @param timeStamp Timestamp of the Event
     * @param days Days of the Event
     */
    public Event(int id, String name, Card.Color color, TimeStamp timeStamp, DayOfWeek[] days) {
        this(id, name, color, timeStamp);
        this.recurring = true;
        setDays(days);
    }

    /** Enum to store the values of week days */
    public enum DayOfWeek {
        SUN,
        MON,
        TUE,
        WED,
        THU,
        FRI,
        SAT
    }

    /**
     * Gets the ID of the Event
     *
     * @return Event ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the Event
     *
     * @param id ID of the Event
     */
    private void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the Name of the Event
     *
     * @return Name of the Event
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the Event
     *
     * @param name Name of the Event
     */
    public void setName(String name) {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name for Event cannot be empty or null");
        this.name = name;
    }

    /**
     * Gets the color of the Event
     *
     * @return Event's card color
     */
    public Card.Color getColor() {
        return color;
    }

    /**
     * Sets the color of the Event
     *
     * @param color Color of the Event's card
     */
    public void setColor(Card.Color color) {
        this.color = color;
    }

    /**
     * Gets the event timestamp
     *
     * @return Event's timestamp
     */
    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    /**
     * Gets Event's 24-hour timestamp in this format: <code>HH:mm-HH:mm</code>
     *
     * @return 24-hour timestamp String
     */
    public String get24HourTimeStampString() {
        StringBuilder output = new StringBuilder();

        Calendar start = timeStamp.getStart();
        Calendar end = timeStamp.getEnd();

        int startHour = start.get(Calendar.HOUR_OF_DAY);
        int startMinute = start.get(Calendar.MINUTE);
        int endHour = end.get(Calendar.HOUR_OF_DAY);
        int endMinute = end.get(Calendar.MINUTE);

        appendTime(output, startHour);

        output.append(":");

        appendTime(output, startMinute);

        output.append("-");

        appendTime(output, endHour);

        output.append(":");

        appendTime(output, endMinute);

        return output.toString();
    }

    /**
     * Appends time to timestamp
     *
     * @param output StringBuilder storing the timestamp
     * @param time Number representing the time (hours, minutes, days)
     */
    private void appendTime(StringBuilder output, int time)
    {
        if (time < 10)
            output.append("0");
        output.append(time);
    }

    /**
     * Sets the Event's timestamp
     *
     * @param timeStamp Event's timestamp
     * @throws IllegalArgumentException if the timestamp's start and end don't share the same date
     */
    public void setTimeStamp(TimeStamp timeStamp) {
        Calendar start = timeStamp.getStart();
        Calendar end = timeStamp.getEnd();

        if (areDatesDifferent(start, end)) {
            throw new IllegalArgumentException("Start and end times for timestamp don't share same date");
        }

        this.timeStamp = timeStamp;
    }

    /**
     * Checks if two dates are different
     *
     * @param start Start date
     * @param end End date
     * @return <code>true</code> if two dates are different;
     * and <code>false</code> if they share the same date
     */
    private boolean areDatesDifferent(Calendar start, Calendar end)
    {
        return start.get(Calendar.YEAR) != end.get(Calendar.YEAR) ||
                start.get(Calendar.MONTH) != end.get(Calendar.MONTH) ||
                start.get(Calendar.DAY_OF_MONTH) != end.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Gets the Event's date stamp in this format: <code>day-month-year</code>
     *
     * @return Event's datestamp
     */
    public String getDateStamp() {
        int day = timeStamp.getStart().get(Calendar.DAY_OF_MONTH);
        int year = timeStamp.getStart().get(Calendar.YEAR);

        // Calendar.MONTH is zero-indexed
        int month = timeStamp.getStart().get(Calendar.MONTH) + 1;

        StringBuilder dateStamp = new StringBuilder();

        appendTime(dateStamp, day);

        dateStamp.append("-");

        appendTime(dateStamp, month);

        dateStamp.append("-");

        dateStamp.append(year);

        return dateStamp.toString();
    }

    /**
     * Determines if Event is recurring
     *
     * @return <code>true</code> if Event is recurring and <code>false</code> if it's not
     */
    public boolean isRecurring() {
        return recurring;
    }

    /**
     * Gets the days of the Event as an Array
     *
     * @return Array containing the Event days
     */
    public DayOfWeek[] getDays() {
        return days;
    }

    /**
     * Gets the days of the Event as a String
     *
     * @return String containing the Event days
     */
    public String getDaysString() {
        String daysString = Arrays.toString(days);

        return daysString
                .substring(1, daysString.length() - 1)
                .replace(", ", " ");
    }

    /**
     * Sets the event days
     *
     * @param days Array containing the Event days
     * @throws IllegalArgumentException when days are repeated, empty, null, or more than 7
     */
    public void setDays(DayOfWeek[] days) {
        if (days == null || days.length == 0 || days.length > 7)
            throw new IllegalArgumentException("Invalid set of days provided to Event");

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

    /**
     * Compares two Events by the start of their timestamp
     *
     * @param anotherEvent the object to be compared.
     * @return the result of comparing their timestamp
     */
    @Override
    public int compareTo(Event anotherEvent) {
        return this.timeStamp
                .getStart()
                .compareTo(anotherEvent.getTimeStamp().getStart());
    }
}
