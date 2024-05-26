package com.planner.models;

import com.planner.schedule.day.Day;

public class Event {

    private int id;
    private String name;
    private double hours;
    private Day.TimeStamp timeStamp;

    public Event(int id, String name, double hours, Day.TimeStamp timeStamp) {
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

    public Day.TimeStamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Day.TimeStamp timeStamp) {
        this.timeStamp = timeStamp;
    }
}
