package com.planner.scripter.exception;

public class DereferenceNullException extends RuntimeException {
    /**
     * Constructs a new {@code DereferenceNullException} with a detailed message
     * @param s message for Exception
     */
    public DereferenceNullException(String s) {
        super(s);
    }

    /**
     * Constructs a new {@code DereferenceNullException} with {@code null} as its detailed message
     */
    public DereferenceNullException() {
        super("Attempts to call attribute function with a null reference");
    }
}
