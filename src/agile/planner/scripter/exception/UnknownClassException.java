package agile.planner.scripter.exception;

public class UnknownClassException extends RuntimeException {

    public UnknownClassException(String s) {
        super(s);
    }

    public UnknownClassException() {
        super("Unknown class or function being utilized");
    }
}
