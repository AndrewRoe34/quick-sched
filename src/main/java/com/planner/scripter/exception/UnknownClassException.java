package com.planner.scripter.exception;

public class UnknownClassException extends RuntimeException {

    /**
     * Constructs a new {@code UnknownClassException} with a detailed message
     * @param s message for Exception
     */
    public UnknownClassException(String s) {
        super(s);
    }

    /**
     * Constructs a new {@code UnknownClassException} with {@code null} as its detailed message
     */
    public UnknownClassException() {
        super("Unknown class or function being utilized");
    }
}
