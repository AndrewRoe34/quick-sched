package com.agile.planner.scripter;

public class LabelInstance extends ClassInstance {

    private final String name;

    private final int color;

    public LabelInstance(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }
}
