package com.planner.scripter.exception;

public class InvalidGrammarException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidGrammarException} with a detailed message
     * @param s message for Exception
     */
    public InvalidGrammarException(String s) {
        super(s);
    }

    /**
     * Constructs a new {@code InvalidGrammarException} with {@code null} as its detailed message
     */
    public InvalidGrammarException() {
        super("Invalid grammar utilized for function or class");
    }
}
