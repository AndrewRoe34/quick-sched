package com.planner.models;

import com.planner.util.Time;
import com.planner.util.Time.TimeStamp;
import com.sun.tools.javac.Main;

import java.lang.reflect.Array;
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
    /** TimeStamp of the Event */
    private TimeStamp timeStamp;
    /** Card for Event */
    private Card card;
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
     * @param card Card for Event
     * @param timeStamp Timestamp of the Event
     */
    public Event(int id, String name, Card card, TimeStamp timeStamp) {
        setId(id);
        setName(name);
        setCard(card);
        setTimeStamp(timeStamp);
    }

    /**
     * Constructor for recurring events
     *
     * @param id ID of the Event
     * @param name Name of the Event
     * @param card Card for Event
     * @param timeStamp Timestamp of the Event
     * @param days Days of the Event
     */
    public Event(int id, String name, Card card, TimeStamp timeStamp, DayOfWeek[] days) {
        this(id, name, card, timeStamp);
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
     * Gets the card for the Event
     *
     * @return card for Event
     */
    public Card getCard() {
        return card;
    }

    /**
     * Sets the card for the Event
     *
     * @param card for Event
     */
    public void setCard(Card card) {
        this.card = card;
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
    private void appendTime(StringBuilder output, int time) {
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

        if (!Time.doDatesMatch(start, end)) {
            throw new IllegalArgumentException("Start and end times for timestamp don't share same date");
        }

        this.timeStamp = timeStamp;
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

        boolean[] weekDayExists = new boolean[7];

        // Ensures no week day is repeated
        for (DayOfWeek d : days) {
            if (!weekDayExists[d.ordinal()]) weekDayExists[d.ordinal()] = true;
            else throw new IllegalArgumentException("Repeats of days cannot occur");
        }

        // Sort days of the week (which is helpful for eventlog and displays)
        // example: days before sorting = {FRI, SUN, SAT}, after sorting = {SUN, FRI, SAT}
        Arrays.sort(days);

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
