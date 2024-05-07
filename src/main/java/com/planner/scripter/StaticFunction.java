package com.planner.scripter;

public class StaticFunction {

    private final String funcName;

    private final String[] args;

    private final boolean builtInFunc;

    public StaticFunction(String funcName, String[] args, boolean builtInFunc) {
        this.funcName = funcName;
        this.args = args;
        this.builtInFunc = builtInFunc;
    }

    public String getFuncName() {
        return funcName;
    }

    public String[] getArgs() {
        return args;
    }

    public boolean getBuiltInFunc() {
        return builtInFunc;
    }
}
