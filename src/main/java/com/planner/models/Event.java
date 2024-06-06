package com.planner.models;

import com.planner.schedule.day.Day;
import com.planner.util.Time.TimeStamp;

import java.util.Calendar;

/**
 * Represents a user-created event.
 *
 * @author Abah Olotuche Gabriel
 */
public class Event implements Comparable<Event> {
    private int id;
    private String name;
    private TimeStamp timeStamp;
    private Card.Colors color;
    private boolean recurring;
    private String[] days;

    public Event(int id, String name, Card.Colors color, TimeStamp timeStamp, boolean recurring, String[] days) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.timeStamp = timeStamp;
        this.recurring = recurring;
        this.days = days;
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
        this.name = name;
    }

    public Card.Colors getColor() {
        return color;
    }

    public void setColor(Card.Colors color) { this.color = color; }

    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    public String getDateString() {
        return timeStamp.getStart().get(Calendar.DAY_OF_MONTH)
                + "-" +
                timeStamp.getStart().get(Calendar.MONTH)
                + "-" +
                timeStamp.getStart().get(Calendar.YEAR);
    }

    public void setTimeStamp(TimeStamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) { this.recurring = recurring; }

    public String[] getDays() {
        return days;
    }

    public void setDays(String[] days) { this.days = days; }

    @Override
    public int compareTo(Event o) {
        return this.timeStamp.getStart().compareTo(o.getTimeStamp().getStart());
    }
}
