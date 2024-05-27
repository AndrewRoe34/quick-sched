package com.planner.models;

import com.planner.schedule.day.Day;
import com.planner.util.Time.TimeStamp;

public class Event {

    private int id;
    private String name;
    private double hours;
    private TimeStamp timeStamp;
    private Card.Colors color;

    public Event(int id, String name, double hours, TimeStamp timeStamp) {
        this.id = id;
        this.name = name;
        this.hours = hours;
        this.timeStamp = timeStamp;
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

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(TimeStamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Card.Colors getColor() {
        return color;
    }

    public void setColor(Card.Colors color) {
        this.color = color;
    }
}
