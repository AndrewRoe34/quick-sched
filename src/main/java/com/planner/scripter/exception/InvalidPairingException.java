package com.planner.scripter.exception;

public class InvalidPairingException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidPairingException} with a detailed message
     * @param s message for Exception
     */
    public InvalidPairingException(String s) {
        super(s);
    }

    /**
     * Constructs a new {@code InvalidPairingException} with {@code null} as its detailed message
     */
    public InvalidPairingException() {
        super("Invalid pairing of classes provided for function call");
    }
}
