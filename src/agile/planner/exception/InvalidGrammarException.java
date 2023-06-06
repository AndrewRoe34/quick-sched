package agile.planner.exception;

public class InvalidGrammarException extends RuntimeException {

    public InvalidGrammarException(String s) {
        super(s);
    }

    public InvalidGrammarException() {
        super("Invalid grammar utilized for function or class");
    }
}
