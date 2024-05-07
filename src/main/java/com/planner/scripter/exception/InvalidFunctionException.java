package com.planner.scripter.exception;

public class InvalidFunctionException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidFunctionException} with a detailed message
     * @param s message for Exception
     */
    public InvalidFunctionException(String s) {
        super(s);
    }

    /**
     * Constructs a new {@code InvalidFunctionException} with {@code null} as its detailed message
     */
    public InvalidFunctionException() {
        super("Attempts to call other custom functions, possesses incorrect parameters, offers invalid function name, or arguments do not match");
    }
}
