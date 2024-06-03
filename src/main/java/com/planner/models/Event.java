package com.planner.models;

import com.planner.schedule.day.Day;
import com.planner.util.Time.TimeStamp;

public class Event {

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

    // Copy constructor to avoid "reference"-based issues when modifying event time..
    public Event (Event e) {
        this.id = e.id;
        this.name = e.name;
        this.color = e.color;
        this.timeStamp = new TimeStamp(e.timeStamp);
        this.recurring = e.recurring;
        this.days = e.days;
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
}
