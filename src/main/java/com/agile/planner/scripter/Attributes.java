package com.agile.planner.scripter;

public class Attributes {

    private String varName;

    private String attr;

    private String[] arguments;

    public Attributes(String varName, String attr, String[] arguments) {
        this.varName = varName;
        this.attr = attr;
        this.arguments = arguments;
    }

    public String getVarName() {
        return varName;
    }

    public String getAttr() {
        return attr;
    }

    public String[] getArgs() {
        return arguments;
    }
}
