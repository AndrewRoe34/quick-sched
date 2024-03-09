package com.agile.planner.models;

public class Label implements Linker {

    private int id;
    private String name;
    private int color;

    public Label(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public boolean add(Linker o) {
        return false;
    }

    @Override
    public boolean remove(Linker o) {
        return false;
    }

    @Override
    public String toString() {
        return name;
    }
}
