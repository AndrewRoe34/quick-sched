package com.planner.scripter;

public class CardInstance extends ClassInstance {

    private final String title;
    private final String color;

    public CardInstance(String title, String color) {
        this.title = title;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public String getColor() {
        return color;
    }
}
