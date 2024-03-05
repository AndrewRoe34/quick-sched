package com.agile.planner.scripter;

public class StringInstance extends ClassInstance {

    private final String str;

    public StringInstance(String str) {
        this.str = str.substring(1, str.length() - 1);
    }

    public String getStr() {
        return str;
    }

}
