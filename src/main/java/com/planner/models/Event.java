package com.planner.models;

import java.util.Calendar;

public class Event {

    private int id;
    private String name;
    private double hours;
    private Calendar date;

    public Event(int id, String name, double hours, Calendar date) {
        this.id = id;
        this.name = name;
        this.hours = hours;
        this.date = date;
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

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
}
