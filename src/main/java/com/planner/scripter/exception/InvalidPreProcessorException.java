package com.planner.scripter.exception;

public class InvalidPreProcessorException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidPreProcessorException} with a detailed message
     * @param s message for Exception
     */
    public InvalidPreProcessorException(String s) {
        super(s);
    }

    /**
     * Constructs a new {@code InvalidPreProcessorException} with {@code null} as its detailed message
     */
    public InvalidPreProcessorException() {
        super("Invalid action or property was included in PreProcessor for script");
    }
}
